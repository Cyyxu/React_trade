package com.xyex.infrastructure.exception;

/**
 * 错误码枚举
 */
public enum ErrorCode {

    SUCCESS("0000", "成功"),
    SYSTEM_ERROR("9999", "系统错误"),
    PARAM_ERROR("1001", "参数错误"),
    NOT_FOUND("1002", "资源不存在"),
    UNAUTHORIZED("1003", "未授权"),
    FORBIDDEN("1004", "禁止访问");

    private final String code;
    private final String message;

    ErrorCode(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
