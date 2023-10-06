package com.ov.common.exception;

import com.ov.pojo.enums.StatusCode;

import java.io.Serial;

public class BusinessException extends RuntimeException {

    private Integer code;
    private String message;
    private String description;

    public BusinessException(StatusCode statusCode){
        code = statusCode.getCode();
        message = statusCode.getMessage();
        description = statusCode.getDescription();
    }

    public BusinessException(StatusCode statusCode,String description){
        code = statusCode.getCode();
        message = statusCode.getMessage();
        this.description = description;
    }

    public Integer getCode() {
        return code;
    }

    @Override
    public String getMessage() {
        return message;
    }

    public String getDescription() {
        return description;
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    @Serial
    private static final long serialVersionUID = -557581779444383409L;
}
