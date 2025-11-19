package com.xyes.springboot.exception;


public enum ErrorCode {

    SUCCESS(0, "ok"),
    PARAMS_ERROR(40000, "请求参数错误"),
    BAD_REQUEST(40001, "错误的请求参数"),
    NOT_LOGIN_ERROR(40100, "未登录"),
    NO_AUTH_ERROR(40101, "无权限"),
    FORBIDDEN_ERROR(40300, "禁止访问"),
    NOT_FOUND_ERROR(40400, "请求数据不存在"),
    NOT_FOUND(40401, "请求资源不存在"),
    METHOD_NOT_ALLOWED(40500, "请求方法不允许"),
    UNSUPPORTED_MEDIA_TYPE(41500, "不支持的媒体类型"),
    TOO_MANY_REQUEST(42900, "请求过于频繁，请稍后再试"),
    SYSTEM_ERROR(50000, "系统内部异常"),
    OPERATION_ERROR(50001, "操作失败"),
    USER_BALANCE_NOT_ENOUGH(50002,"用户余额不足，无法调用 AI"),
    ERROR(50003, "系统异常"  ),
    INTERNAL_SERVER_ERROR(50004, "服务器内部错误"),
    WORD_FORBIDDEN_ERROR(422200, "包含违禁词，多次违禁将封禁账号"),
    VALIDATION_ERROR(423230, "参数校验错误" );
    /**
     * 状态码
     */
    private final int code;

    /**
     * 信息
     */
    private final String message;
    ErrorCode(int code, String message) {
        this.code = code;
        this.message = message;
    }
    public int getCode() {
        return code;
    }
    public String getMessage() {
        return message;
    }
}