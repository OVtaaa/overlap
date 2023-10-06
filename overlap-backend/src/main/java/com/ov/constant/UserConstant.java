package com.ov.constant;

import java.util.concurrent.TimeUnit;

public interface UserConstant {

    //  盐值：混淆密码
    String SECRET = "ovta";

    //  用户登录态的 Key
    String USER_LOGIN_STATUS = "user_login_status";

    //  已发帖（isPost 值）
    int POSTED_STATUS = 1;

    //  默认普通用户 role 值
    int DEFAULT_ROLE = 0;

    //  管理员 role 值
    int ADMIN_ROLE = 1;

    String DEFAULT_AVATAR_URL = "http://easyimage.ovta.love/i/2023/08/16/ugbnva.webp";

    String DEFAULT_USERNAME_PRE = "ov";

    String RECOMMEND_COMMON_USER_KEY = "user:recommend:common";
    String RECOMMEND_HOT_USER_KEY = "user:recommend:hot:";

    String RECOMMEND_COMMON_USER_LOCK = "user:recommend:commonLock";
    String RECOMMEND_HOT_USER_LOCK = "user:recommend:HotLock";

    // 用户修改信息 分布式锁
    String USER_UPDATE_LOCK_PRE = "user:update:";

    // 首页用户数据缓存时间 45 min
    long RECOMMEND_USER_TIMEOUT = 45;
    TimeUnit RECOMMEND_USER_TIMEUNIT = TimeUnit.MINUTES;
    //  首页用户数据定时更新时间 30 min
    long RECOMMEND_USER_SCHEDULE_TIME = 30*60*1000;

    // 存入 redis 中的首页数据最大值
    long RECOMMEND_USER_SIZE = 150;

    //  记录登录热用户的 key
    String HOT_USER_KEY = "user:hot";
    //  热用户的过期时间
    long HOT_USER_TIMEOUT = 3;
    TimeUnit HOT_USER_TIMEUNIT = TimeUnit.DAYS;
    //  热用户的最大数量
    int HOT_USER_SIZE = 30;

    // 用户创建圈子上限
    int MAX_COTERIE_SIZE = 6;

    //  验证码校验键值前缀
    String USER_REGISTER_CODE_KEY_PRE = "user:register:code:";
    //  验证码有效期
    int USER_REGISTER_CODE_EXPIRE = 5;
    TimeUnit USER_REGISTER_CODE_TIMEUNIT = TimeUnit.MINUTES;

    //  邮箱校验正则表达式
    String EMAIL_REGEX = "^[a-zA-Z0-9_.+-]+@[a-zA-Z0-9-]+.[a-zA-Z0-9-.]+$";
}
