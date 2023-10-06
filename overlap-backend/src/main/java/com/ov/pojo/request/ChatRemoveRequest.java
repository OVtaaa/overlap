package com.ov.pojo.request;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChatRemoveRequest implements Serializable {

    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    private Integer type;

    @Serial
    private static final long serialVersionUID = -7519519433927946974L;
}
