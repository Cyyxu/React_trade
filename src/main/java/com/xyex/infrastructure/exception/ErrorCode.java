package com.xyex.infrastructure.exception;

import lombok.Getter;

@Getter
public enum ErrorCode {

    SUCCESS(0, "Success"),
    ERROR(500, "Operation failed"),

    // Client errors
    BAD_REQUEST(400, "Bad request:{message}"),
    NOT_FOUND(404, "Not found"),
    METHOD_NOT_ALLOWED(405, "Method not allowed"),
    CONFLICT(409, "Conflict"),
    UNSUPPORTED_MEDIA_TYPE(415, "Unsupported media type"),
    VALIDATION_ERROR(422, "Validation failed"),

    // Server errors
    INTERNAL_SERVER_ERROR(500, "Internal server error :{message}"),
    BAD_GATEWAY(502, "Bad gateway"),
    SERVICE_UNAVAILABLE(503, "Service unavailable"),
    GATEWAY_TIMEOUT(504, "Gateway timeout"),

    // File errors
    FILE_ERROR(700, "File error: {message}"),
    FILE_NOT_FOUND(701, "File not found"),
    ;

    private final Integer code;
    private final String message;

    ErrorCode(Integer code, String message) {
        this.code = code;
        this.message = message;
    }
}