package com.ov.pojo;

import com.baomidou.mybatisplus.annotation.*;

import java.io.Serializable;
import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 
 * @TableName user_coterie
 */
@TableName(value ="user_coterie")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserCoterie implements Serializable {
    /**
     * 
     */
    @TableId
    private Long id;

    /**
     * 
     */
    private Long userId;

    /**
     * 
     */
    private Long coterieId;

    /**
     * 1: 圈主；0: 普通
     */
    private Integer role;

    /**
     * 
     */
    private Date joinTime;

    /**
     * 
     */
    private Date updateTime;

    private Date createTime;

    @TableLogic
    private Integer isDelete;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}