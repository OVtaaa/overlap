package com.ov.pojo;

import com.baomidou.mybatisplus.annotation.*;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 
 * @TableName tag
 */
@TableName(value ="tag")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Tag implements Serializable {
    /**
     * 
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 
     */
    private String tagName;

    @TableField(exist = false)
    private List<Tag> subTag;

    /**
     * 
     */
    private Long userId;

    /**
     * 父标签（属于哪一行）
     */
    private Long parentId;

    /**
     * 分类标签（属于哪一列）
     */
    private Long classifyId;

    /**
     * 0：不是父标签；1：是父标签
     */
    private Integer isParent;

    /**
     * 
     */
    private Date createTime;

    /**
     * 
     */
    private Date updateTime;

    /**
     * 0: 未删除；1：已删除
     */
    @TableLogic
    private Integer isDelete;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}