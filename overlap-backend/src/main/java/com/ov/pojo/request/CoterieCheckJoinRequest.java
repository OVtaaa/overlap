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
public class CoterieCheckJoinRequest implements Serializable {

    @JsonSerialize(using = ToStringSerializer.class)
    private Long coterieId;

    private String password;

    @Serial
    private static final long serialVersionUID = -8855157882741422L;
}
