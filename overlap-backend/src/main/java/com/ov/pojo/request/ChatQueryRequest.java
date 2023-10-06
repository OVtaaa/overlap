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
public class ChatQueryRequest implements Serializable {

    @JsonSerialize(using = ToStringSerializer.class)
    private Long to;

    // 0：单聊；1：群聊
    private Integer type;

    private Long pageNum;
    private Long pageSize;

    @Serial
    private static final long serialVersionUID = 894168689657466385L;
}
