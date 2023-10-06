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
public class ChatSendCoterieSystemRequest implements Serializable {

    @JsonSerialize(using = ToStringSerializer.class)
    private Long toId;

    @JsonSerialize(using = ToStringSerializer.class)
    private Long coterieId;

    @JsonSerialize(using = ToStringSerializer.class)
    private Long userId;

    private Integer type;

    @Serial
    private static final long serialVersionUID = -2401929930136241476L;
}
