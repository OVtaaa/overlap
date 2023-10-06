package com.ov.common;

import com.ov.common.exception.BusinessException;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 *  为响应返回统一 json 格式
 * @param <T> 返回数据的泛型
 */

@Data
public class BaseResponse<T> implements Serializable {

    private Integer code;

    private T data;

    private String message;

    private String description;

    @Serial
    private static final long serialVersionUID = 2295973541908991835L;

    public BaseResponse() {
    }

    public BaseResponse(Integer code, T data, String message, String description) {
        this.code = code;
        this.data = data;
        this.message = message;
        this.description = description;
    }

    public BaseResponse(BusinessException e) {
        this.code = e.getCode();
        this.data = null;
        this.message = e.getMessage();
        this.description = e.getDescription();
    }
}
