package com.ov.service.impl;

import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.ov.pojo.vo.CoteriePage;
import com.ov.common.exception.BusinessException;
import com.ov.constant.CoterieConstant;
import com.ov.pojo.enums.StatusCode;
import com.ov.pojo.Coterie;
import com.ov.pojo.User;
import com.ov.pojo.UserCoterie;
import com.ov.pojo.enums.CoterieStatusEnums;
import com.ov.pojo.request.CoterieAddRequest;
import com.ov.pojo.request.CoterieUpdateRequest;
import com.ov.pojo.vo.UserCommonVO;
import com.ov.service.CoterieService;
import com.ov.mapper.CoterieMapper;
import com.ov.service.UserCoterieService;
import com.ov.service.UserService;
import org.apache.commons.lang3.StringUtils;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;

import javax.servlet.http.HttpServletRequest;

import static com.ov.constant.CoterieConstant.*;
import static com.ov.constant.UserConstant.MAX_COTERIE_SIZE;

/**
* @author Ovta~
* @description 针对表【coterie】的数据库操作Service实现
* @createDate 2023-08-22 18:46:22
*/
@Service
public class CoterieServiceImpl extends ServiceImpl<CoterieMapper, Coterie>
    implements CoterieService{

    @Autowired
    private CoterieMapper coterieMapper;
    @Autowired
    private UserService userService;
    @Autowired
    private UserCoterieService userCoterieService;
    @Autowired
    private RedissonClient redissonClient;

    @Override
    @Transactional
    public Boolean addCoterie(CoterieAddRequest addCoterie, HttpServletRequest request) {
        User curUser = userService.getCurrentUser(request);
        if (curUser == null)
            throw new BusinessException(StatusCode.NO_LOGIN,"未登录");
        if (curUser.getId() == null || curUser.getId() <= 0)
            throw new BusinessException(StatusCode.NO_AUTH,"无权限");
        /*
         因为表中有查询语句，避免同一时间多个创建请求，超出创建个数限制，加锁
         */
        RLock lock = redissonClient.getLock(ADD_COTERIE_KEY_PRE + curUser.getId());
        boolean getLock = false;
        try {
            getLock = lock.tryLock(30, TimeUnit.SECONDS);
            if(getLock) {
                Coterie coterie = new Coterie();
                BeanUtils.copyProperties(addCoterie,coterie);
                /*
                  对圈子属性校验合法性
                 */
                checkCoterieProperties(coterie);
                // 用户创建队伍数量上限
                LambdaQueryWrapper<Coterie> lqw = new LambdaQueryWrapper<>();
                lqw.eq(Coterie::getUserId, curUser.getId());
                long count = count(lqw);
                if (count >= MAX_COTERIE_SIZE)
                    throw new BusinessException(StatusCode.NO_AUTH, "已达创建队伍上限");
                String password = coterie.getPassword();
                if (!StringUtils.isAnyBlank(password)) {
                    coterie.setIsEncrypted(COTERIE_ENCRYPTION);
                    // 对密码加密
                    coterie.setPassword(DigestUtils.md5DigestAsHex((CoterieConstant.COTERIE_PASSWORD_SECRET + password).getBytes(StandardCharsets.UTF_8)));
                } else
                    coterie.setIsEncrypted(COTERIE_NO_ENCRYPTION);
                //  设置圈号
                String coterieNumStr = UUID.randomUUID().toString().replace("-", "").substring(0, 10);
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < coterieNumStr.length(); i++) {
                    char ch = coterieNumStr.charAt(i);
                    if (ch >= 'a' && ch <= 'z') {
                        sb.append((char) (((ch - 'a') % 10) + '0'));
                    } else
                        sb.append(ch);
                }
                coterie.setAvatarUrl(curUser.getAvatarUrl());
                coterie.setCoterieNum(String.valueOf(sb));
                coterie.setUserId(curUser.getId());
                coterie.setCurNum(1);
                //  添加圈子成功，则更新用户圈子表
                if (coterieMapper.insert(coterie) > 0) {
                    UserCoterie userCoterie = new UserCoterie();
                    userCoterie.setUserId(curUser.getId());
                    userCoterie.setCoterieId(coterie.getId());
                    userCoterie.setRole(USER_COTERIE_ADMIN_ROLE);
                    return userCoterieService.save(userCoterie);
                }
            }
        } catch (InterruptedException e) {
            log.error(e.getMessage());
            throw new BusinessException(StatusCode.DB_ERROR,"创建队伍失败");
        }finally {
            if(lock.isHeldByCurrentThread())
                lock.unlock();
        }
        return false;
    }

    private void checkCoterieProperties(Coterie coterie) {
        String title = coterie.getTitle(),
                description = coterie.getDescription(),
                password = coterie.getPassword();
        Integer isEncrypted = coterie.getIsEncrypted(), maxNum = coterie.getMaxNum();
        if(title==null || title.getBytes(StandardCharsets.UTF_8).length>36)
            throw new BusinessException(StatusCode.PARAMS_ERROR,"圈名限制长度 12");
        if(description!=null && description.length()>200)
            throw new BusinessException(StatusCode.PARAMS_ERROR,"描述长度限制 200 字");
        if((!StringUtils.isAnyBlank(password) && isEncrypted!=COTERIE_ENCRYPTION)
                || (StringUtils.isAnyBlank(password) && isEncrypted!=COTERIE_NO_ENCRYPTION))
            throw new BusinessException(StatusCode.PARAMS_ERROR,"是否加密不匹配");
        if(isEncrypted==COTERIE_ENCRYPTION && (password.length() > 20 || password.length() < 6))
            throw new BusinessException(StatusCode.PARAMS_ERROR,"密码长度限制 6~20");
        if(CoterieStatusEnums.getStatus(coterie.getStatus()) == null)
            throw new BusinessException(StatusCode.PARAMS_ERROR,"圈子状态异常");
        if(maxNum==null || maxNum<1 || maxNum >COTERIE_MAX_NUMBER_OF_PEOPLE)
            throw new BusinessException(StatusCode.PARAMS_ERROR,"最大人数限制 1~"+COTERIE_MAX_NUMBER_OF_PEOPLE);
        // 校验标签
        try {
            String tagStr = coterie.getTags();
            if(tagStr != null){
                Gson gson = new Gson();
                gson.fromJson(tagStr, new TypeToken<ArrayList<String>>() {}.getType());
            }
        } catch (JsonSyntaxException e) {
            throw new BusinessException(StatusCode.PARAMS_ERROR,"标签格式错误");
        }
    }

    @Override
    public CoteriePage getCurUserCoterie(Long pageNum, Long pageSize, HttpServletRequest request) {
        User curUser = userService.getCurrentUser(request);
        List<Coterie> resCoterie = new ArrayList<>();
        if(curUser == null)
            return new CoteriePage(resCoterie,0L,pageNum,pageSize);
        LambdaQueryWrapper<UserCoterie> lqw = new LambdaQueryWrapper<>();
        lqw.eq(UserCoterie::getUserId,curUser.getId());
        Page<UserCoterie> page = userCoterieService.page(Page.of(pageNum, pageSize), lqw);
        //  根据查到的 用户圈子 列表数据，提取圈子 id 去数据库查询并添加到结果列表中
        for(UserCoterie userCoterie : page.getRecords()){
            long id = userCoterie.getCoterieId();
            Coterie byId = getById(id);
            resCoterie.add(safeCoterie(byId));
        }
        return new CoteriePage(resCoterie,page.getTotal(),page.getCurrent(),page.getSize());
    }

    private Coterie safeCoterie(Coterie coterie) {
        coterie.setPassword(null);
        return coterie;
    }

    @Override
    public CoteriePage getRecommendCoterie(Long pageNum, Long pageSize, HttpServletRequest request) {
        User curUser = userService.getCurrentUser(request);
        //  先找出当前用户加入的圈子
        LambdaQueryWrapper<UserCoterie> lqw = new LambdaQueryWrapper<>();
        Set<Long> joinedCoterieId = new HashSet<>();
        if(curUser != null) {
            lqw.select(UserCoterie::getCoterieId);
            lqw.eq(UserCoterie::getUserId, curUser.getId());
            joinedCoterieId = userCoterieService.list(lqw)
                    .stream().map(UserCoterie::getCoterieId)
                    .collect(Collectors.toSet());
        }
        LambdaQueryWrapper<Coterie> wrapper = new LambdaQueryWrapper<>();
        if(!joinedCoterieId.isEmpty())
            wrapper.notIn(Coterie::getId,joinedCoterieId);
//        //推荐公开、未加入过的圈子
        Page<Coterie> page = page(Page.of(pageNum, pageSize), wrapper);
        return new CoteriePage(page.getRecords().stream().map(this::safeCoterie).collect(Collectors.toList())
                , page.getTotal(),page.getCurrent(),page.getSize());
    }

    @Override
    public Boolean updateCoterie(CoterieUpdateRequest updateCoterie, HttpServletRequest request) {
        Long updateCoterieId = updateCoterie.getId();
        if(updateCoterieId == null || updateCoterieId <= 0)
            throw new BusinessException(StatusCode.PARAMS_ERROR);
        RLock lock = redissonClient.getLock(UPDATE_COTERIE_KEY_PRE+updateCoterie.getId());
        try {
            boolean getLock = lock.tryLock(30, TimeUnit.SECONDS);
            if(getLock) {
                User user = userService.getCurrentUser(request);
                if (user == null)
                    throw new BusinessException(StatusCode.NO_LOGIN,"未登录");
                Long curLoginUserId = user.getId();
                //  从数据库中搜索该条圈子记录
                Coterie dbCoterie = getById(updateCoterieId);
                if(dbCoterie == null)
                    throw new BusinessException(StatusCode.PARAMS_ERROR);
                //  若修改的圈子不是当前登录用户的 并且 登录用户也不是管理员，则抛出无权限异常
                if (!curLoginUserId.equals(dbCoterie.getUserId()) && !userService.isAdmin(user))
                    throw new BusinessException(StatusCode.NO_AUTH);
                //  若修改的最大人数小于当前人数，也抛出异常
                if (updateCoterie.getMaxNum() < dbCoterie.getCurNum())
                    throw new BusinessException(StatusCode.PARAMS_ERROR,"最大人数过小");
                BeanUtils.copyProperties(updateCoterie, dbCoterie);
                // 校验圈子属性
                checkCoterieProperties(dbCoterie);

                // 为密码加密
                if (dbCoterie.getIsEncrypted() == COTERIE_ENCRYPTION) {
                    String password = dbCoterie.getPassword();
                    dbCoterie.setPassword(DigestUtils.md5DigestAsHex((COTERIE_PASSWORD_SECRET + password).getBytes(StandardCharsets.UTF_8)));
                }
                return updateById(dbCoterie);
            }
        } catch (InterruptedException e) {
            log.error(e.getMessage());
            throw new BusinessException(StatusCode.DB_ERROR,"修改圈子失败");
        } finally {
            if(lock.isHeldByCurrentThread())
                lock.unlock();
        }
        return false;
    }

    @Override
    public CoteriePage getSearchCoterie(String text, Long pageNum, Long pageSize) {
        // 从 圈号、标题、标签、描述 四个方面搜索圈子
        LambdaQueryWrapper<Coterie> lqw = new LambdaQueryWrapper<>();
        lqw.eq(Coterie::getStatus,COTERIE_PUBLIC_STATUS).and((wrapper) -> {
            wrapper.eq(Coterie::getCoterieNum,text).or()
                    .like(Coterie::getTags,"\""+text+"\"").or()
                    .like(Coterie::getDescription,text).or()
                    .like(Coterie::getTitle,text);
        });
        Page<Coterie> page = page(Page.of(pageNum, pageSize), lqw);
        return new CoteriePage(page.getRecords().stream().map(this::safeCoterie).collect(Collectors.toList()),
                page.getTotal(),page.getCurrent(),page.getSize());
    }

    @Override
    @Transactional
    public Boolean checkJoinCoterie(Long coterieId, User curUser,String password) {
        RLock lock = redissonClient.getLock(UPDATE_COTERIE_KEY_PRE+coterieId);
        try {
            boolean getLock = lock.tryLock(30, TimeUnit.SECONDS);
            if(getLock) {
                Coterie coterie = getCoterie(coterieId);
                checkJoinWithPassword(coterie, curUser, password);
                return true;
            }
        } catch (InterruptedException e) {
            log.error(e.getMessage());
            throw new BusinessException(StatusCode.DB_ERROR,"审核失败");
        } finally {
            if(lock.isHeldByCurrentThread())
                lock.unlock();
        }
        return false;
    }

    @Override
    @Transactional
    public boolean executeJoinCoterie(Long userId, Coterie coterie) {
        UserCoterie userCoterie = new UserCoterie();
        userCoterie.setUserId(userId);
        userCoterie.setCoterieId(coterie.getId());
        userCoterie.setRole(0); //  数据库默认也是为 0，即默认普通参与者
        boolean flag = userCoterieService.save(userCoterie);
        // 若添加 用户圈子 数据成功则更新圈子数据
        if(flag){
            coterie.setCurNum(coterie.getCurNum()+1);
            flag = updateById(coterie);
        }
        return flag;
    }

    @Override
    public void checkJoinWithPassword(Coterie coterie, User curUser, String password) {
        checkJoinNoPassword(coterie, curUser.getId());
        // 校验密码
        if (coterie.getIsEncrypted() == COTERIE_ENCRYPTION) {
            if (password == null || !DigestUtils.md5DigestAsHex((COTERIE_PASSWORD_SECRET + password)
                    .getBytes(StandardCharsets.UTF_8)).equals(coterie.getPassword()))
                throw new BusinessException(StatusCode.PARAMS_ERROR,"密码错误");
        }
    }

    @Override
    public void checkJoinNoPassword(Coterie coterie, Long userId) {
        // 校验圈子是否未满、公开
        if (coterie.getMaxNum() <= coterie.getCurNum())
            throw new BusinessException(StatusCode.PARAMS_ERROR,"圈子已满");
        if(coterie.getStatus() != COTERIE_PUBLIC_STATUS)
            throw new BusinessException(StatusCode.PARAMS_ERROR,"圈子未公开");
        //  判断是否已加入
        LambdaQueryWrapper<UserCoterie> lqw = new LambdaQueryWrapper<>();
        lqw.eq(UserCoterie::getCoterieId, coterie.getId()).eq(UserCoterie::getUserId, userId);
        boolean isJoined = userCoterieService.count(lqw) > 0;
        if (isJoined)
            throw new BusinessException(StatusCode.PARAMS_ERROR,"已加入该圈子");
    }

    @Override
    public Coterie getCoterie(Long coterieId) {
        if (coterieId == null || coterieId <= 0)
            throw new BusinessException(StatusCode.PARAMS_ERROR);
        // 校验圈子是否存在
        Coterie coterie = getById(coterieId);
        if (coterie == null)
            throw new BusinessException(StatusCode.PARAMS_ERROR,"圈子不存在");
        return coterie;
    }

    @Override
    @Transactional
    public Boolean exitCoterie(Long coterieId, User curUser) {
        RLock lock = redissonClient.getLock(UPDATE_COTERIE_KEY_PRE+coterieId);
        try {
            boolean getLock = lock.tryLock(30, TimeUnit.SECONDS);
            //  1、从 用户圈子 表中查询该用户是否加入了该圈子
            LambdaQueryWrapper<UserCoterie> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(UserCoterie::getCoterieId,coterieId).eq(UserCoterie::getUserId,curUser.getId());
            UserCoterie userCoterie = userCoterieService.getOne(queryWrapper);
            if(userCoterie == null)
                return false;
            //  2、删除 用户圈子 记录
            boolean isDelete = userCoterieService.removeById(userCoterie);
            if(!isDelete)
                throw new BusinessException(StatusCode.DB_ERROR);
            //  3、根据圈子 id 查询圈子具体信息，判断是否为圈子的最后一位
            Coterie coterie = getById(coterieId);
            int curNum = coterie.getCurNum();
            if(curNum <= 1){
                //  4、若是最后一位：删除 圈子 记录
                if(!removeById(coterieId))
                    throw new BusinessException(StatusCode.DB_ERROR);
                return true;
            }
            //  5、若非最后一位：
            //    是否为圈主：是则直接解散
            if(coterie.getUserId().equals(curUser.getId())){
                //  删除所有 用户-圈子 关系
                LambdaQueryWrapper<UserCoterie> wrapper = new LambdaQueryWrapper<>();
                wrapper.eq(UserCoterie::getCoterieId,coterie.getId());
                if(!userCoterieService.remove(wrapper))
                    throw new BusinessException(StatusCode.DB_ERROR);
                // 删除圈子
                return removeById(coterieId);
            }
            //  6.2、更新圈子数据（curNum、userId）
            coterie.setCurNum(coterie.getCurNum()-1);
            if(!updateById(coterie))
                throw new BusinessException(StatusCode.DB_ERROR);
        } catch (InterruptedException e) {
            log.error(e.getMessage());
            throw new BusinessException(StatusCode.DB_ERROR);
        } finally {
            if(lock.isHeldByCurrentThread())
                lock.unlock();
        }
        return true;
    }

    @Override
    public List<UserCommonVO> getUserByCoterie(Long coterieId) {
        List<Long> userIdList = getUserIdByCoterie(coterieId);
        List<User> userList = new ArrayList<>();
        for(Long userId : userIdList){
            User searchUser = userService.getById(userId);
            if(searchUser != null)
                userList.add(searchUser);
        }
        return userService.commendVOUserList(userList);
    }

    @Override
    public List<Long> getUserIdByCoterie(Long coterieId) {
        if(coterieId == null || coterieId <= 0)
            return null;
        //  找出加入该圈子的用户 id
        LambdaQueryWrapper<UserCoterie> lqw = new LambdaQueryWrapper<>();
        lqw.select(UserCoterie::getUserId).eq(UserCoterie::getCoterieId,coterieId);
        return userCoterieService.list(lqw)
                .stream().map(UserCoterie::getUserId).collect(Collectors.toList());
    }
}




