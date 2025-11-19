package com.xyes.springboot.common;

import java.io.Serial;
import java.io.Serializable;
import java.util.Collections;
import java.util.Map;

/**
 * 通用响应封装
 *
 * @param <T> 数据类型
 */
public class BaseResponse<T> implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 业务状态码
     */
    private int code;

    /**
     * 返回数据
     */
    private T data;

    /**
     * 描述信息
     */
    private String message;

    /**
     * 详细描述
     */
    private String description;

    /**
     * 动态扩展字段
     */
    private Map<String, Object> extra;

    public BaseResponse() {
    }

    public BaseResponse(int code, T data, String message) {
        this(code, data, message, null, null);
    }

    public BaseResponse(int code, T data, String message, String description) {
        this(code, data, message, description, null);
    }

    public BaseResponse(int code, T data, String message, String description, Map<String, Object> extra) {
        this.code = code;
        this.data = data;
        this.message = message;
        this.description = description;
        this.extra = extra == null ? null : Collections.unmodifiableMap(extra);
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Map<String, Object> getExtra() {
        return extra;
    }

    public void setExtra(Map<String, Object> extra) {
        this.extra = extra == null ? null : Collections.unmodifiableMap(extra);
    }
}

