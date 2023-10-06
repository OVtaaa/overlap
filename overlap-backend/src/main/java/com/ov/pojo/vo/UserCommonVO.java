package com.ov.pojo.vo;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserCommonVO implements Serializable {

    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    /**
     * 账号
     */
    private String userAccount;

    private String username;

    private String gender;

    private String introduction;

    /**
     * 头像地址
     */
    private String avatarUrl;

    /**
     * 0：表示普通用户
     * 1：表示管理员
     */
    private Integer userRole;

    /**
     * 0：未发帖
     * 1：已发帖
     */
    private Integer isPost;

    /**
     * 0: 表示正常
     */
    private Integer userStatus;

    /**
     * Json 格式的标签列表数据
     */
    private String tags;

    @Serial
    private static final long serialVersionUID = 8714524562664898906L;
}
