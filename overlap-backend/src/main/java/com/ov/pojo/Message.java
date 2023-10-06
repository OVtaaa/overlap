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
 * @TableName message
 */
@TableName(value ="message")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Message implements Serializable {
    /**
     * 
     */
    @TableId
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    /**
     * 发送方
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long fromId;

    /**
     * 接收方
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long toId;

    @JsonSerialize(using = ToStringSerializer.class)
    private Long coterieId;

    private String username;

    /*
    在单聊中是对方头像路径；在群聊中是己方头像路径
     */
    private String avatarUrl;

    /**
     * 发送内容
     */
    private String content;

    /**
     * 0：文本
     */
    private Integer type;

    /**
     * 0：未读；1：已读
     */
    private Integer isRead;

    /**
     * 
     */
    private Date updateTime;

    /**
     * 
     */
    private Date createTime;

    /**
     * 0：未删除；1：逻辑删除
     */
    @TableLogic
    private Integer isDelete;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}