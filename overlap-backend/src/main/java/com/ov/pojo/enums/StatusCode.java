package com.ov.pojo.enums;

/**
 * 通过枚举类来定义各个状态码，对于异常的响应也只需要从此中调用特定的枚举类即可
 */
public enum StatusCode {

    SUCCESS(20000,"OK",""),
    PARAMS_NULL(40001,"请求参数为空",""),
    PARAMS_ERROR(40002,"请求参数错误",""),
    NO_LOGIN(40102,"未登录",""),
    NO_AUTH(40101,"无权限",""),
    DB_ERROR(50001,"数据库错误",""),
    PARSE_JSON_ERROR(50002,"JSON处理异常","");


    /**
     *  因为枚举主要针对于异常返回，且只是对状态做出解释，所以不需要有 data
     */
    private final Integer code;
    private final String message;
    private final String description;


    StatusCode(Integer code, String message, String description) {
        this.code = code;
        this.message = message;
        this.description = description;
    }

    public Integer getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public String getDescription() {
        return description;
    }
}
