package com.ov.constant;

public interface CoterieConstant {

    //  密码盐值
    String COTERIE_PASSWORD_SECRET = "ovta";

    // 圈子公开状态
    int COTERIE_PUBLIC_STATUS = 0;

    //  用户-圈子 表 圈主 role
    int USER_COTERIE_ADMIN_ROLE = 1;

    // 圈子最大人数
    int COTERIE_MAX_NUMBER_OF_PEOPLE = 50;

    // 圈子加密
    int COTERIE_ENCRYPTION = 1;
    // 圈子未加密
    int COTERIE_NO_ENCRYPTION = 0;

    // 创建圈子分布式锁 键值
    String ADD_COTERIE_KEY_PRE = "coterie:add:";
    // 加入 / 修改 / 退出圈子分布式锁 键值
    String UPDATE_COTERIE_KEY_PRE = "coterie:update:";
}
