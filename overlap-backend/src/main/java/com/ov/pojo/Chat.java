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
 * @TableName chat
 */
@TableName(value ="chat")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Chat implements Serializable {
    /**
     *
     */
    @TableId
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    /**
     *
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long side1;

    /**
     *
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long side2;

    /**
     * 0：单聊
     * 1：群聊
     */
    private Integer type;

    /**
     *
     */
    private Date createTime;

    /**
     *
     */
    private Date updateTime;

    /**
     *
     */
    @TableLogic
    private Integer isDelete;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}