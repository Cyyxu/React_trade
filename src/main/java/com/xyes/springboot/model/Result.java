package com.xyes.springboot.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.xyes.springboot.exception.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Result<T> implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 状态码
     */
    private Integer errorCode;

    /**
     * 消息
     */
    private String errorMsg;

    /**
     * 数据
     */
    private T data;

    /**
     * 时间戳
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime timestamp;

    /**
     * 请求ID（用于链路追踪）
     */
    private String traceId;

    /**
     * 额外数据
     */
    private Map<String, Object> extra;

    public Result(Integer code, String message) {
        this.errorCode = code;
        this.errorMsg = message;
        this.timestamp = LocalDateTime.now();
    }

    public Result(Integer code, String message, T data) {
        this.errorCode = code;
        this.errorMsg = message;
        this.data = data;
        this.timestamp = LocalDateTime.now();
    }

    public static <T> Result<T> success() {
        return new Result<>(ErrorCode.SUCCESS.getCode(), ErrorCode.SUCCESS.getMessage());
    }

    public static <T> Result<T> success(T data) {
        return new Result<>(ErrorCode.SUCCESS.getCode(), ErrorCode.SUCCESS.getMessage(), data);
    }

    public static <T> Result<T> success(String message, T data) {
        return new Result<>(ErrorCode.SUCCESS.getCode(), message, data);
    }

    public static <T> Result<T> error(String message) {
        return new Result<>(ErrorCode.ERROR.getCode(), message);
    }

    public static <T> Result<T> error(Integer code, String message) {
        return new Result<>(code, message);
    }

    public static <T> Result<T> error(ErrorCode resultCode) {
        return new Result<>(resultCode.getCode(), resultCode.getMessage());
    }

    public Result<T> traceId(String traceId) {
        this.traceId = traceId;
        return this;
    }

    public Result<T> extra(Map<String, Object> extra) {
        this.extra = extra;
        return this;
    }
}