package com.ov.pojo;

import com.baomidou.mybatisplus.annotation.*;

import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 
 * @TableName coterie
 */
@TableName(value ="coterie")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Coterie implements Serializable {
    /**
     * 
     */
    @TableId
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    /**
     * 圈号
     */
    private String coterieNum;

    /**
     * 
     */
    private String title;

    @JsonSerialize(using = ToStringSerializer.class)
    private Long userId;

    /**
     * 
     */
    private String description;

    /**
     * 
     */
    private String tags;

    /**
     * 
     */
    private String avatarUrl;

    /**
     * 
     */
    private String password;

    /**
     * 是否加密
     */
    private Integer isEncrypted;

    /**
     * 圈子状态，0：公开；1：私密
     */
    private Integer status;

    /**
     * 最大人数限制
     */
    private Integer maxNum;

    /**
     * 当前人数
     */
    private Integer curNum;

    /**
     * 逻辑删除
     */
    @TableLogic
    private Integer isDelete;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}