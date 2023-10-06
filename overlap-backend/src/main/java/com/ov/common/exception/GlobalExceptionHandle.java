package com.ov.common.exception;

import com.ov.common.BaseResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 *  全局异常处理器：将自定义异常信息返回给前端
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandle {

    @ExceptionHandler(BusinessException.class)
    public BaseResponse handleException(BusinessException e){
        log.error("businessException: " + e.getMessage(), e);
        return new BaseResponse(e);
    }

}
