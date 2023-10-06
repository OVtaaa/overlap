package com.ov.pojo.request;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Data
public class UserLoginRequest implements Serializable {

    private String userAccount;
    private String password;

    @Serial
    private static final long serialVersionUID = -4529118639023356480L;
}
