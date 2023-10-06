package com.ov.pojo.request;

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
@NoArgsConstructor
@AllArgsConstructor
public class UserUpdateRequest implements Serializable {

    @TableId
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    private String originalPassword;
    private String newPassword;
    private String retypePassword;

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
     * 0：未发帖
     * 1：已发帖
     */
    private Integer isPost;

    /**
     * Json 格式的标签列表数据
     */
    private String tags;

    @Serial
    private static final long serialVersionUID = -6530257449055805652L;
}
