package com.ov.pojo.request;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatSendCommonRequest implements Serializable {

    @JsonSerialize(using = ToStringSerializer.class)
    private Long toId;

    /**
     * 发送内容
     */
    private String content;

    /**
     * 己方头像路径
     */
    private String avatarUrl;

    private Integer type;

    @Serial
    private static final long serialVersionUID = -2401929930136241476L;
}
