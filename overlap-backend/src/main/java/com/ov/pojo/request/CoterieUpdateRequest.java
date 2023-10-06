package com.ov.pojo.request;

import com.baomidou.mybatisplus.annotation.TableField;
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
public class CoterieUpdateRequest implements Serializable {

    @Serial
    private static final long serialVersionUID = 6829744452126192936L;

    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    /**
     *
     */
    private String title;


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
}
