package com.ov.pojo;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *
 * @TableName user
 */
@TableName(value ="user")
@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class User implements Serializable {

    @TableId
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    /**
     * 账号
     */
    private String userAccount;

    private String password;

    private String username;

    private String gender;

    private Integer age;

    private String introduction;

    /**
     * 头像地址
     */
    private String avatarUrl;

    private String phone;

    private String email;

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

    private Date createTime;

    private Date updateTime;

    /**
     * Json 格式的标签列表数据
     */
    private String tags;

    /**
     * 逻辑删除
     */
    @TableLogic
    private Integer isDelete;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}