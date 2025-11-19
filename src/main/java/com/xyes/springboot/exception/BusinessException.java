package com.xyes.springboot.exception;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class BusinessException extends RuntimeException {

    private Integer code;
    private String message;

    public BusinessException(ErrorCode resultCode) {
        super(resultCode.getMessage());
        this.code = resultCode.getCode();
        this.message = resultCode.getMessage();
    }

    public BusinessException(ErrorCode resultCode, String message) {
        super(message);
        this.code = resultCode.getCode();
        // 替换resultCodeMessage中的{messages}占位符
        this.message = replaceMessages(resultCode, message);
    }

    private static String replaceMessages(ErrorCode iErrCode, String message) {
        if (message == null || message.isEmpty()) {
            return iErrCode.getMessage().replace("{message}", "Know Error");
        } else {
            return iErrCode.getMessage().replace("{message}", message);
        }
    }

}