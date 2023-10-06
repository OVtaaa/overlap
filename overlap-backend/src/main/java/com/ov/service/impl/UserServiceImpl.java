package com.ov.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.ov.pojo.vo.UserPage;
import com.ov.common.exception.BusinessException;
import com.ov.pojo.enums.StatusCode;
import com.ov.constant.TagConstant;
import com.ov.constant.UserConstant;
import com.ov.mapper.TagMapper;
import com.ov.pojo.Tag;
import com.ov.pojo.User;
import com.ov.pojo.request.UserUpdateRequest;
import com.ov.pojo.vo.UserCommonVO;
import com.ov.service.UserService;
import com.ov.mapper.UserMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.DigestUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static com.ov.constant.UserConstant.*;

/**
 * @author Ovta~
 */
@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
        implements UserService {

    @Resource
    private StringRedisTemplate stringRedisTemplate;
    @Autowired
    private TagMapper tagMapper;
    @Autowired
    private RedissonClient redissonClient;


    @Override
    public long registerUser(String userAccount,String email, String password, String checkPassword, HttpServletRequest request) {
        //  验证账户、密码是否 非空（"" || null）
        if (StringUtils.isAnyBlank(userAccount,email, password, checkPassword)) {
            throw new BusinessException(StatusCode.PARAMS_NULL, "部分栏为空");
        }
        //  验证 密码和校验密码是否一致
        if (!password.equals(checkPassword))
            throw new BusinessException(StatusCode.PARAMS_ERROR, "密码和校验密码不一致");

        //  账号不包含特殊字符，并有长度限制
        String verifyAccount = "^\\w{1,15}$";

        if (!userAccount.matches(verifyAccount))
            throw new BusinessException(StatusCode.PARAMS_ERROR, "账号不合法");
        if(!email.matches(EMAIL_REGEX))
            throw new BusinessException(StatusCode.PARAMS_ERROR,"邮箱不合法");
        if (password.length() < 6 || password.length() > 20)
            throw new BusinessException(StatusCode.PARAMS_ERROR, "密码长度不合法");

        //  账户、邮箱不能重复
        LambdaQueryWrapper<User> lqw = new LambdaQueryWrapper<>();
        lqw.eq(User::getUserAccount, userAccount).or().eq(User::getEmail, email);
        User one = this.getOne(lqw);
        if (one != null)
            throw new BusinessException(StatusCode.PARAMS_ERROR, "账号或邮箱重复");

        /*
          密码加密，"ovta"是加盐，增加破解难度
         */
        String encryptPassword = DigestUtils.md5DigestAsHex((SECRET + password).getBytes());
        User user = new User();

        //  保存至数据库
        user.setUserAccount(userAccount);
        user.setUsername(UserConstant.DEFAULT_USERNAME_PRE + UUID.randomUUID().toString().split("-")[4]);
        user.setPassword(encryptPassword);
        user.setEmail(email);
        user.setTags("[]");
        user.setAvatarUrl(UserConstant.DEFAULT_AVATAR_URL);
        user.setCreateTime(new Date());
        boolean saveResult = this.save(user);
        if (!saveResult)
            throw new BusinessException(StatusCode.DB_ERROR, "保存账号错误");
        //  注册成功，直接登录
        afterLogin(request,user);
        return user.getId();
    }

//      发送邮箱信息，避免邮箱被拉黑，于是舍去
//    public boolean sendEmail(String toEmail) {
//        String code = generateCode();
//        Boolean isSucceeded = stringRedisTemplate.opsForValue().setIfAbsent(USER_REGISTER_CODE_KEY_PRE + toEmail, code,
//                USER_REGISTER_CODE_EXPIRE, USER_REGISTER_CODE_TIMEUNIT);
//        if(isSucceeded==null || !isSucceeded)
//            return false;
//        SimpleMailMessage message = new SimpleMailMessage();
//        message.setFrom(qqEmail);
//        message.setSubject("overlap 验证码");
//        message.setTo(toEmail);
//        message.setText("注册验证码为："+code);
//        javaMailSender.send(message);
//        return true;
//    }

    private String generateCode() {
        return UUID.randomUUID().toString().split("-")[4].substring(0,6);
    }

    @Override
    public User loginUser(String userAccount, String password, HttpServletRequest request) {
        //  验证账户、密码是否 非空（"" || null）
        if (StringUtils.isAnyBlank(userAccount, password)) {
            throw new BusinessException(StatusCode.PARAMS_NULL, "账户或 密码为空");
        }

        //  账号不包含特殊字符，并有长度限制
        String verifyAccount = "^\\w{1,15}$";

        if (!userAccount.matches(verifyAccount))
            throw new BusinessException(StatusCode.PARAMS_ERROR, "账号不合法");
        if (password.length() < 6 || password.length() > 20)
            throw new BusinessException(StatusCode.PARAMS_ERROR, "密码长度不合法");

        /*
          密码加密，"ovta"是加盐，增加破解难度
         */
        String encryptPassword = DigestUtils.md5DigestAsHex((SECRET + password).getBytes());

        LambdaQueryWrapper<User> lqw = new LambdaQueryWrapper<>();
        lqw.eq(User::getUserAccount, userAccount).eq(User::getPassword, encryptPassword);
        User user = this.getOne(lqw);

        if (user == null)
            throw new BusinessException(StatusCode.PARAMS_ERROR, "账号或密码错误");

        User safeUser = safeUser(user);
        afterLogin(request, safeUser);
        return safeUser;
    }

    private void afterLogin(HttpServletRequest request, User safeUser) {
        // 登录成功则将该用户化为 热用户
        setHotUser(safeUser.getId());
        //  记录用户的登录态
        saveCurrentUser(request, safeUser);
    }

    private void setHotUser(Long id) {
        ListOperations<String,String> operations = stringRedisTemplate.opsForList();
        List<String> range = operations.range(HOT_USER_KEY, 0, -1);
        //  若已经是热用户则直接返回
        if(range!=null && range.contains(id.toString()))
            return;
        // 添加为 热用户
        operations.rightPush(HOT_USER_KEY,id.toString());
        stringRedisTemplate.expire(HOT_USER_KEY,HOT_USER_TIMEOUT,HOT_USER_TIMEUNIT);
        // 若热用户数量大于指定值，则删除一个
        if(range!=null && range.size() >= HOT_USER_SIZE)
            operations.leftPop(HOT_USER_KEY,1);
    }

    @Override
    public User safeUser(User user) {
        if (user == null)
            return null;

        //  创建 脱敏 后的用户信息
        User safeUser = new User();
        safeUser.setId(user.getId());
        safeUser.setUserAccount(user.getUserAccount());
        safeUser.setUsername(user.getUsername());
        safeUser.setGender(user.getGender());
        safeUser.setAge(user.getAge());
        safeUser.setIntroduction(user.getIntroduction());
        safeUser.setAvatarUrl(user.getAvatarUrl());
        safeUser.setPhone(user.getPhone());
        safeUser.setEmail(user.getEmail());
        safeUser.setIsPost(user.getIsPost());
        safeUser.setUserStatus(user.getUserStatus());
        safeUser.setUserRole(user.getUserRole());
        safeUser.setCreateTime(user.getCreateTime());
        safeUser.setTags(user.getTags());

        return safeUser;
    }

    @Override
    public UserPage searchUserByTags(List<String> tags, Integer pageNum, Integer pageSize) {
        if (CollectionUtils.isEmpty(tags))
            tags = new ArrayList<>();
        LambdaQueryWrapper<User> lqw = new LambdaQueryWrapper<>();
        lqw.eq(User::getIsPost, UserConstant.POSTED_STATUS);
        for (String s : tags)
            lqw.like(User::getTags, "\"" + s + "\"");

        Page<User> page = page(Page.of(pageNum, pageSize), lqw);
        //  对查询到的用户列表 进行脱敏
        return new UserPage(commendVOUserList(page.getRecords()),
                page.getTotal(), page.getCurrent(), page.getSize());
    }

    //  根据搜索框中的文本信息 从用户表中搜寻 用户名 / 手机号 / 邮箱 / 标签
    @Override
    public UserPage searchUserByInfo(String text, Integer pageNum, Integer pageSize) {
        if (StringUtils.isBlank(text)) {
            return null;
        }
        LambdaQueryWrapper<User> lqw = new LambdaQueryWrapper<>();
        lqw.like(User::getUsername, text).or().eq(User::getPhone, text).or()
                .eq(User::getEmail, text).or().like(User::getTags, "\"" + text + "\"");
        Page<User> page = page(Page.of(pageNum, pageSize), lqw);

        return new UserPage(commendVOUserList(page.getRecords()),
                page.getTotal(), page.getCurrent(), page.getSize());
    }

    @Override
    public List<Tag> searchTagsOnInit() {
        LambdaQueryWrapper<Tag> lqw = new LambdaQueryWrapper<>();
        lqw.eq(Tag::getIsParent, TagConstant.TAG_IS_PARENT).isNull(Tag::getClassifyId);
        List<Tag> list = tagMapper.selectList(lqw);
        return list.stream().map(tag -> {
            if (StringUtils.isNotBlank(tag.getTagName()) && tag.getTagName().equals("段位"))
                return tag;
            LambdaQueryWrapper<Tag> lqwTemp = new LambdaQueryWrapper<>();
            lqwTemp.eq(Tag::getParentId, tag.getId());
            tag.setSubTag(new ArrayList<>(tagMapper.selectList(lqwTemp)));
            return tag;
        }).collect(Collectors.toList());
    }

    @Override
    public List<Tag> searchSubTagsByTag(String tagName) {
        if (StringUtils.isAnyBlank(tagName))
            return null;
        LambdaQueryWrapper<Tag> lqw = new LambdaQueryWrapper<>();
        lqw.eq(Tag::getTagName, tagName);
        Tag tag = tagMapper.selectList(lqw).get(0);
        lqw = new LambdaQueryWrapper<>();
        lqw.eq(Tag::getClassifyId, tag.getId());
        return tagMapper.selectList(lqw);
    }

    @Override
    public User getCurrentUser(HttpServletRequest request) {
        Object object = request.getSession().getAttribute(USER_LOGIN_STATUS);
        if (object == null)
            return null;
        Gson gson = new Gson();
        return gson.fromJson((String) object, User.class);
    }

    @Override
    public Boolean saveCurrentUser(HttpServletRequest request, User user) {
        if (request == null || user == null)
            return false;
        Gson gson = new Gson();
        String s = gson.toJson(safeUser(user));
        request.getSession().setAttribute(USER_LOGIN_STATUS, s);
        return true;
    }

    @Override
    public Boolean isAdmin(User user) {
        if (user == null || user.getUserRole() == null)
            return false;
        return user.getUserRole() == ADMIN_ROLE;
    }

    @Override
    @Transactional
    public Boolean updateUser(UserUpdateRequest updateUser, HttpServletRequest request) {
        User loginUser = getCurrentUser(request);
        boolean res = false;
        if(loginUser == null)
            throw new BusinessException(StatusCode.NO_LOGIN);
        if(!loginUser.getId().equals(updateUser.getId()) && !isAdmin(loginUser))
            throw new BusinessException(StatusCode.NO_AUTH);

        User user = new User();
        BeanUtils.copyProperties(updateUser, user);
        // 校验用户属性
        checkUserProperties(user);

        RLock lock = redissonClient.getLock(USER_UPDATE_LOCK_PRE + loginUser.getId());
        try {
            boolean getLock = lock.tryLock(10, TimeUnit.SECONDS);
            if(getLock) {
                //  修改 user 内的性别信息,并且如果性别信息有变,则标签处也对应更改
                updateUserGender(user);
                //  修改 user 内的密码信息
                updateUserPassword(updateUser, user);

                res = updateById(user);

                Integer oldIsPost = loginUser.getIsPost();
                //  修改后,如果修改的用户是当前登录用户本身,则 redis 处信息也需要修改
                if (loginUser.getId().equals(user.getId()))
                    res &= saveCurrentUser(request, getById(user.getId()));
                //  若修改了发帖信息 isPost，则更新首页推荐数据
                if(user.getIsPost()!=null && !user.getIsPost().equals(oldIsPost))
                    cacheUserRecommend();
                return res;
            }
        } catch (InterruptedException e) {
            log.error(e.getMessage());
            throw new BusinessException(StatusCode.DB_ERROR,"修改失败");
        } finally {
            if(lock.isHeldByCurrentThread())
                lock.unlock();
        }
        return false;
    }

    @Override
    public void cacheUserRecommend() {
        // 由于更改了 isPost 属性，首页数据会受到影响，可以直接调动一次定时任务
        cacheCommonUserRecommend();
        cacheHotUserRecommend();
    }

    @Override
    public void cacheHotUserRecommend() {
        RLock lock = redissonClient.getLock(UserConstant.RECOMMEND_HOT_USER_LOCK);
        try {
            /*
              tryLock 参数：最大等待时间（由于只需要执行一次，所以置为 0 不需要等待）
              锁自动释放时间（避免出现执行时间过长，锁提前释放的可能，采用看门狗机制，不手动设置释锁时间）
              时间单位
             */
            boolean isLock = lock.tryLock(0, TimeUnit.SECONDS);
            if(isLock) {
                //  按 redis 中缓存的热用户数据 更新热用户的首页信息
                List<String> range = stringRedisTemplate.opsForList().range(UserConstant.HOT_USER_KEY, 0, -1);
                if (CollectionUtils.isEmpty(range))
                    return;
                for (String idStr : range) {
                    List<UserCommonVO> userList = getRecommendHotUserBySQL(Long.parseLong(idStr));
                    saveCommonVOUserListToRedis(userList, UserConstant.RECOMMEND_HOT_USER_KEY + idStr);
                }
            }
        } catch (Exception exception) {
            log.error("schedule error",exception);
        } finally {
            //  释放锁
            if(lock!=null && lock.isHeldByCurrentThread())
                lock.unlock();
        }
    }

    @Override
    public void cacheCommonUserRecommend()  {
        RLock lock = redissonClient.getLock(UserConstant.RECOMMEND_COMMON_USER_LOCK);
        try {
            /*
              tryLock 参数：最大等待时间（由于只需要执行一次，所以置为 0 不需要等待）
              锁自动释放时间（避免出现执行时间过长，锁提前释放的可能，采用看门狗机制，不手动设置释锁时间）
              时间单位
             */
            boolean isLock = lock.tryLock(0, TimeUnit.SECONDS);
            if(isLock) {
                //  更新 redis 首页展示数据
                List<UserCommonVO> userList = getRecommendCommonUserBySQL();
                saveCommonVOUserListToRedis(userList,UserConstant.RECOMMEND_COMMON_USER_KEY);
            }
        } catch (InterruptedException e) {
            System.out.println(e.getMessage());
        } finally {
            //  释放锁
            if(lock!=null && lock.isHeldByCurrentThread())
                lock.unlock();
        }
    }

    private void updateUserPassword(UserUpdateRequest updateUser, User user) {
        String originalPassword = updateUser.getOriginalPassword(),
                newPassword = updateUser.getNewPassword(), retypePassword = updateUser.getRetypePassword();
        //  至少有一个 非 blank
        if (!StringUtils.isAllBlank(originalPassword, newPassword, retypePassword)) {
            if (StringUtils.isAnyBlank(originalPassword, newPassword, retypePassword))
                throw new BusinessException(StatusCode.PARAMS_ERROR);
            // 新密码与重复新密码是否一致
            if (!newPassword.equals(retypePassword))
                throw new BusinessException(StatusCode.PARAMS_ERROR, "重复新密码不一致");
            //  校验新密码的格式
            if (newPassword.length() < 6 || newPassword.length() > 20)
                throw new BusinessException(StatusCode.PARAMS_ERROR, "密码长度不合法");
            User oldUser = getById(updateUser.getId());
            if (oldUser == null)
                throw new BusinessException(StatusCode.PARAMS_ERROR);
            String encryptPassword = DigestUtils.md5DigestAsHex((SECRET + updateUser.getOriginalPassword()).getBytes(StandardCharsets.UTF_8));
            if (!encryptPassword.equals(oldUser.getPassword()))
                throw new BusinessException(StatusCode.PARAMS_ERROR);
            user.setPassword(DigestUtils.md5DigestAsHex((SECRET + updateUser.getNewPassword()).getBytes(StandardCharsets.UTF_8)));
        }
    }

    private void updateUserGender(User user) {
        String gender = user.getGender();
        if (gender != null) {
            // 查询该用户的标签,更改标签中的性别属性
            User oldUser = getById(user.getId());
            if (oldUser == null)
                throw new BusinessException(StatusCode.PARAMS_ERROR);
            String tags = oldUser.getTags();
            //  该用户标签不为空，并且有性别属性，则更改
            if (!StringUtils.isAnyBlank(tags) && (tags.contains("\"男\"") || tags.contains("\"女\""))) {
                tags = tags.replaceAll("\"男\"", "\"" + gender + "\"");
                tags = tags.replaceAll("\"女\"", "\"" + gender + "\"");
            } else {
                Gson gson = new Gson();
                List<String> strings = new ArrayList<>();
                if (!StringUtils.isAnyBlank(tags))
                    strings = gson.fromJson(tags, new TypeToken<ArrayList<String>>() {
                    }.getType());
                strings.add(gender);
                tags = gson.toJson(strings);
            }
            user.setTags(tags);
        }
    }

    private void checkUserProperties(User user) {
        //  年龄判断
        if(user.getAge()!=null && (user.getAge()>120 || user.getAge()<0))
            throw new BusinessException(StatusCode.PARAMS_ERROR,"年龄范围应在 0~120 之间");
        //  昵称判断
        if(user.getUsername()!=null && user.getUsername().length()>6)
            throw new BusinessException(StatusCode.PARAMS_ERROR,"昵称长度限制为 6");
        //  简介判断
        if(user.getIntroduction()!=null && user.getIntroduction().length()>200)
            throw new BusinessException(StatusCode.PARAMS_ERROR,"简介长度限制 200 字");
        //  手机号判断
        if(user.getPhone()!=null && !Pattern.matches("^[1][3-9]\\d{9}", user.getPhone()))
            throw new BusinessException(StatusCode.PARAMS_ERROR,"手机号格式错误");
        //  邮箱判断
        if(user.getEmail()!=null &&
                !Pattern.matches(EMAIL_REGEX, user.getEmail()))
            throw new BusinessException(StatusCode.PARAMS_ERROR,"邮箱格式错误");
        //  密码判断
        if (user.getPassword()!=null && (user.getPassword().length() < 6 || user.getPassword().length() > 20))
            throw new BusinessException(StatusCode.PARAMS_ERROR, "密码长度不合法");
        // 性别判断
        if(user.getGender()!=null && (!user.getGender().equals("男") && !user.getGender().equals("女")))
            throw new BusinessException(StatusCode.PARAMS_ERROR,"性别格式不合法");
    }

    @Override
    public UserPage searchRecommendUser(Integer pageNum, Integer pageSize,HttpServletRequest request) {
        User currentUser = getCurrentUser(request);
        boolean isLogin = currentUser != null;
        //  若未登录则从 redis 中读取首页信息
        if(!isLogin){
            return searchRecommendUserForUnLogin(pageNum,pageSize);
        }
        //  已登录，部分用户根据标签推荐展示
        return searchRecommendUserForLogin(pageNum,pageSize,currentUser);
    }

    private UserPage searchRecommendUserForLogin(Integer pageNum, Integer pageSize, User currentUser) {
        long id = currentUser.getId();
        String userJson = stringRedisTemplate.opsForValue().get(RECOMMEND_HOT_USER_KEY + id);
        List<UserCommonVO> userList = null;
        if(StringUtils.isAnyBlank(userJson)){
            //  redis 暂无缓存 或是 缓存空数据（非热用户或是还未缓存），读取公共首页信息
            return searchRecommendUserForUnLogin(pageNum,pageSize);
        }
        //  redis 中有数据，读取数据
        Gson gson = new Gson();
        try {
            userList = gson.fromJson(userJson, new TypeToken<ArrayList<UserCommonVO>>() {}.getType());
        } catch (JsonSyntaxException e) {
            // Json 解析失败，也是读取公共信息页
            log.error("Json 解析异常", e);
            return searchRecommendUserForUnLogin(pageNum,pageSize);
        }
        userList = userList==null ? new ArrayList<>() : userList;
        //  根据获取的 userList 数据，按分页返回
        return pageUser(userList,pageNum,pageSize);
    }

    private UserPage searchRecommendUserForUnLogin(Integer pageNum, Integer pageSize) {
        // 从 redis 中读取首页公共用户展示信息
        String userJson = stringRedisTemplate.opsForValue().get(UserConstant.RECOMMEND_COMMON_USER_KEY);
        List<UserCommonVO> userList = null;
        // 是否需要缓存数据至 redis 中
        boolean flag = false;
        if(StringUtils.isAnyBlank(userJson)){
            //  redis 暂无缓存 或是 缓存空数据，直接读取数据库，并缓存至 redis 中
            userList = getRecommendCommonUserBySQL();
            flag = true;
        }else {
            //  redis 中有数据，读取数据
            Gson gson = new Gson();
            try {
                userList = gson.fromJson(userJson, new TypeToken<ArrayList<UserCommonVO>>() {}.getType());
            } catch (JsonSyntaxException e) {
                // Json 解析失败，直接读取数据库，并缓存至 redis 中
                log.error("Json 解析异常", e);
                userList = getRecommendCommonUserBySQL();
                flag = true;
            }
        }
        userList = userList==null ? new ArrayList<>() : userList;
        // 根据 flag 将数据缓存至 redis 中
        if(flag){
            saveCommonVOUserListToRedis(userList,UserConstant.RECOMMEND_COMMON_USER_KEY);
        }
        //  根据获取的 userList 数据，按分页返回
        return pageUser(userList,pageNum,pageSize);
    }

    @Override
    public void saveUserListToRedis(List<User> userList, String key) {
        Gson gson = new Gson();
        String json = gson.toJson(userList);
        stringRedisTemplate.opsForValue().set(key,json,UserConstant.RECOMMEND_USER_TIMEOUT,RECOMMEND_USER_TIMEUNIT);
    }

    @Override
    public void saveCommonVOUserListToRedis(List<UserCommonVO> userList, String key) {
        Gson gson = new Gson();
        String json = gson.toJson(userList);
        stringRedisTemplate.opsForValue().set(key,json,UserConstant.RECOMMEND_USER_TIMEOUT,RECOMMEND_USER_TIMEUNIT);
    }

    @Override
    public List<UserCommonVO> getRecommendHotUserBySQL(Long id) {
        User user = getById(id);
        Gson gson = new Gson();
        List<String> tags = gson.fromJson(user.getTags(), new TypeToken<ArrayList<String>>() {}.getType());
        // 若标签为空，则按普通用户展示首页
        if(CollectionUtils.isEmpty(tags))
            return getRecommendCommonUserBySQL();
        // 将热用户 tags 用 set 记录
        Set<String> set = new HashSet<>();
        for(String s : tags){
            //  不考虑 男、女 标签
            if(s.equals("男") || s.equals("女"))
                continue;
            set.add(s);
        }
        LambdaQueryWrapper<User> lqw = new LambdaQueryWrapper<>();
        //  选择发帖、排除自己
        lqw = lqw.eq(User::getIsPost, UserConstant.POSTED_STATUS)
                .ne(User::getId,id);
        List<User> list = list(lqw);
        PriorityQueue<Pair<User,Double>> queue = new PriorityQueue<>(new Comparator<Pair<User, Double>>() {
            @Override
            public int compare(Pair<User, Double> o1, Pair<User, Double> o2) {
                int compareValue = Double.compare(o2.getValue(), o1.getValue());
                /*
                    相似度：有相同的标签个数
                    两个相似度相同时（去除了性别标签）
                        若相似度为 0 或者 标签长度一致，直接比较更新时间
                        若相似度不为 0，判断标签长度
                */
                if(compareValue == 0) {
                    String o1Tags = o1.getKey().getTags(), o2Tags = o2.getKey().getTags();
                    if(o1.getValue()==0 || o1Tags.length()==o2Tags.length())
                        return o2.getKey().getUpdateTime().compareTo(o1.getKey().getUpdateTime());
                    return Integer.compare(o2Tags.length(),o1Tags.length());
                }
                return compareValue;
            }
        });

        //  将从数据库中查询到的用户进行相似度算分
        for(User curUser : list){
            String curTags = curUser.getTags();
            queue.add(Pair.of(curUser,getSimilarityValueByTags(curTags,set)));
            while (queue.size() > RECOMMEND_USER_SIZE)
                queue.poll();
        }
        // 保存结果
        List<User> resUser = new ArrayList<>();
        while(!queue.isEmpty()){
            resUser.add(queue.poll().getKey());
        }
        return commendVOUserList(resUser);
    }

    private Double getSimilarityValueByTags(String tags, Set<String> set) {
        if(tags == null)
            return 0.0;
        Gson gson = new Gson();
        try {
            List<String> tagList = gson.fromJson(tags, new TypeToken<ArrayList<String>>() {}.getType());
            if(tagList.size() == 0)
                return 0.0;
            double goodELe = 0;
            for (String tagEle : tagList) {
                if(tagEle.equals("男") || tagEle.equals("女"))
                    continue;
                if(set.contains(tagEle))
                    goodELe++;
            }
            return goodELe;
        } catch (JsonSyntaxException e) {
            throw new BusinessException(StatusCode.DB_ERROR);
        }

    }

    @Override
    public UserCommonVO commendVOUser(User user){
        UserCommonVO commendVO = new UserCommonVO();
        BeanUtils.copyProperties(user,commendVO);
        return commendVO;
    }

    @Override
    public List<UserCommonVO> commendVOUserList(List<User> userList){
        List<UserCommonVO> list = new ArrayList<>();
        for(User user : userList){
            UserCommonVO userCommonVO = new UserCommonVO();
            BeanUtils.copyProperties(user,userCommonVO);
            list.add(userCommonVO);
        }
        return list;
    }

    private UserPage pageUser(List<UserCommonVO> userList, Integer pageNum, Integer pageSize) {
        UserPage userPage = new UserPage();
        int startIndex = (pageNum-1)*pageSize ,
                endIndex = Math.min(userList.size(),startIndex+pageSize);
        List<UserCommonVO> subList = null;
        if(startIndex < userList.size())
            subList = userList.subList(startIndex,Math.min(endIndex,userList.size()));
        userPage.setList(subList);
        userPage.setTotal((long) userList.size());
        userPage.setPageSize((long)pageSize);
        userPage.setCurPageNum((long)pageNum);
        return userPage;
    }

    @Override
    public List<UserCommonVO> getRecommendCommonUserBySQL() {
        LambdaQueryWrapper<User> lqw = new LambdaQueryWrapper<>();
        // 按更新时间分页查询数据
        lqw.eq(User::getIsPost, UserConstant.POSTED_STATUS)
                .orderByDesc(User::getUpdateTime)
                .last("limit " + UserConstant.RECOMMEND_USER_SIZE);
        List<User> list = list(lqw);
        return commendVOUserList(list);
    }
}




