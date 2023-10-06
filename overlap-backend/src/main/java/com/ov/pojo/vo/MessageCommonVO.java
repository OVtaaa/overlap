package com.ov.pojo.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.models.auth.In;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MessageCommonVO implements Serializable {

    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    @JsonSerialize(using = ToStringSerializer.class)
    private Long toId;

    @JsonSerialize(using = ToStringSerializer.class)
    private Long fromId;

    @JsonSerialize(using = ToStringSerializer.class)
    private Long coterieId;

    private String username;

    private String content;

    private String avatarUrl;

    private Integer type;

    private Integer isRead;

    private Date createTime;

    @Serial
    private static final long serialVersionUID = 1379606606570909093L;
}
