package com.ov.service;

import com.ov.pojo.vo.UserPage;
import com.ov.pojo.Tag;
import com.ov.pojo.User;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ov.pojo.request.UserUpdateRequest;
import com.ov.pojo.vo.UserCommonVO;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
* @author Ovta~
* createTime 2022-10-10 20:55:44
*/
public interface UserService extends IService<User> {

    /**
     *  用户注册
     * @return  用户ID
     */
    long registerUser(String userAccount,String email,String password,String checkPassword, HttpServletRequest request);

    /**
     *  用户登录
     * @return 用户信息
     */
    User loginUser(String userAccount, String password, HttpServletRequest request);

    /**
     *  用户脱敏
     * @param user  需要脱敏的用户信息
     * @return  脱敏后的用户信息
     */
    User safeUser(User user);

    /**
     * 按照标签查询用户
     * @param tags  标签名
     * @return  用户列表
     */
    UserPage searchUserByTags(List<String> tags, Integer pageNum, Integer pageSize);

    /**
     * 根据搜索框中的文本信息 从用户表中搜寻 用户名 / 手机号 / 邮箱 / 标签
     * @param text 要搜索的文本信息
     * @return 用户列表
     */
    UserPage searchUserByInfo(String text, Integer pageNum,Integer pageSize);

    /**
     * 查询标签（初始展示时，除去了段位标签）
     * @return  标签信息
     */
    List<Tag> searchTagsOnInit();

    /**
     * 查询标签的名下标签（非子标签）
     * @param tagName   标签名
     * @return 名下标签
     */
    List<Tag> searchSubTagsByTag(String tagName);

    /**
     * 获取当前登录用户信息
     * @param request   请求参数，获取session
     * @return 当前用户
     */
    User getCurrentUser(HttpServletRequest request);

    /**
     * 保存当前用户信息到 redis 中
     * @param request,user 请求信息以及要保存的用户信息
     * @return  是否保存成功
     */
    Boolean saveCurrentUser(HttpServletRequest request, User user);

    /**
     * 判断用户是否为管理员
     * @param user 要判断的用户信息
     * @return 是否为管理员
     */
    Boolean isAdmin(User user);

    /**
     * 修改用户信息
     * @param updateUser 要修改的用户信息
     * @param request 请求参数，用于获取当前登录用户信息
     * @return 修改后的用户信息
     */
    Boolean updateUser(UserUpdateRequest updateUser, HttpServletRequest request);

    /**
     * 搜索用户数据用于推荐页面展示
     * @param pageNum   搜索的页码
     * @param pageSize 页大小
     * @return 获取的页用户数据信息
     */
    UserPage searchRecommendUser(Integer pageNum, Integer pageSize,HttpServletRequest request);

    /**
     * 从数据库中查询首页普通用户展示信息
     * @return 页面数据
     */
    List<UserCommonVO> getRecommendCommonUserBySQL();

    /**
     * 将用户列表信息按 key 值存入 redis 中
     * @param userList 用户列表信息
     * @param key redis 键值
     */
    void saveUserListToRedis(List<User> userList, String key);

    /**
     * 将用户列表 VO 信息按 key 值存入 redis 中
     * @param userList 用户列表信息
     * @param key redis 键值
     */
    void saveCommonVOUserListToRedis(List<UserCommonVO> userList, String key);

    /**
     * 对热用户进行首页特色搜索展示
     * @return 展示的用户数据
     */
    List<UserCommonVO> getRecommendHotUserBySQL(Long id);

    /**
     * 缓存热用户首页数据
     */
    void cacheHotUserRecommend();

    /**
     * 缓存普通用户首页数据
     */
    void cacheCommonUserRecommend();

    /**
     * 缓存用户方法，其中包含了缓存热用户以及普通用户的首页数据
     */
    void cacheUserRecommend();

    /**
     * user 对象转 commendVOUser 对象
     * @param user 要转换的 user 对象
     * @return 转换后的对象
     */
    UserCommonVO commendVOUser(User user);

    /**
     * user 列表对象转 commendVOUser 对象
     * @param userList 要转换的列表对象
     * @return 转换后的对象
     */
    List<UserCommonVO> commendVOUserList(List<User> userList);
}
