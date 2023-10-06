package com.ov.common;

import com.ov.pojo.enums.StatusCode;

/**
 *  BaseResponse 的工具类，便于返回
 */

public class ResultUtil {

    public static <T> BaseResponse<T> success(T data){
        return new BaseResponse<>(StatusCode.SUCCESS.getCode(),
                data,StatusCode.SUCCESS.getMessage(),
                StatusCode.SUCCESS.getDescription());
    }


}
