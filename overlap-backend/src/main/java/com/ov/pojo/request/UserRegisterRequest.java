package com.ov.pojo.request;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Data
public class UserRegisterRequest implements Serializable {

    private String userAccount;

    private String email;

    private String password;

    private String checkPassword;


    @Serial
    private static final long serialVersionUID = -814829402347456815L;


}
