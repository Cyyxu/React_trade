package com.xyes.springboot.exception;

import com.xyes.springboot.model.Result;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.NoHandlerFoundException;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.Collections;
import java.util.stream.Collectors;

/**
 * 全局异常处理器
 * 用于统一处理系统中抛出的各种异常，提供标准化的错误响应格式
 *
 * * @author xujun
 * @since 1.0.0
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 处理业务异常
     * 业务逻辑中主动抛出的异常，通常包含业务错误码和错误信息
     *
     * @param e 业务异常对象
     * @return 标准化错误响应结果
     */
    @ExceptionHandler(BusinessException.class)
    @ResponseStatus(HttpStatus.OK)
    public Result<Void> handleBusinessException(BusinessException e) {
        log.error("Business exception: {} - {}", e.getCode(), e.getMessage(), e);
        return withTraceId(Result.error(e.getCode(), e.getMessage()));
    }

    /**
     * 处理参数校验异常 - @Valid注解触发
     * 当使用@Valid注解对请求体进行校验失败时触发
     *
     * @param e 参数校验异常对象
     * @return 标准化错误响应结果，包含具体的校验失败字段信息
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result<Void> handleValidationException(MethodArgumentNotValidException e) {
        String message = e.getBindingResult().getFieldErrors().stream().map(FieldError::getDefaultMessage).collect(Collectors.joining(", "));
        log.error("Parameter validation exception: {}", message, e);
        return withTraceId(Result.error(ErrorCode.VALIDATION_ERROR.getCode(), message));
    }

    /**
     * 处理参数绑定异常
     * 当请求参数绑定到对象时发生错误时触发
     *
     * @param e 参数绑定异常对象
     * @return 标准化错误响应结果，包含具体的绑定失败字段信息
     */
    @ExceptionHandler(BindException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result<Void> handleBindException(BindException e) {
        String message = e.getBindingResult().getFieldErrors().stream().map(FieldError::getDefaultMessage).collect(Collectors.joining(", "));
        log.error("Parameter binding exception: {}", message, e);
        return withTraceId(Result.error(ErrorCode.VALIDATION_ERROR.getCode(), message));
    }

    /**
     * 处理约束校验异常 - @Validated注解触发
     * 当使用@Validated注解对方法参数进行校验失败时触发
     *
     * @param e 约束校验异常对象
     * @return 标准化错误响应结果，包含具体的校验失败信息
     */
    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result<Void> handleConstraintViolationException(ConstraintViolationException e) {
        String message = e.getConstraintViolations().stream().map(ConstraintViolation::getMessage).collect(Collectors.joining(", "));
        log.error("Constraint validation exception: {}", message, e);
        return withTraceId(Result.error(ErrorCode.VALIDATION_ERROR.getCode(), message));
    }

    /**
     * 处理请求参数缺失异常
     * 当必需的请求参数未提供时触发
     *
     * @param e 请求参数缺失异常对象
     * @return 标准化错误响应结果，包含缺失的参数名称
     */
    @ExceptionHandler(MissingServletRequestParameterException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result<Void> handleMissingParameterException(MissingServletRequestParameterException e) {
        String message = String.format("Missing required request parameter: %s", e.getParameterName());
        log.error("Missing request parameter: {}", message, e);
        return withTraceId(Result.error(ErrorCode.BAD_REQUEST.getCode(), message));
    }

    /**
     * 处理参数类型不匹配异常
     * 当请求参数类型与方法参数类型不匹配时触发
     *
     * @param e 参数类型不匹配异常对象
     * @return 标准化错误响应结果，包含类型不匹配的参数名称
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result<Void> handleTypeMismatchException(MethodArgumentTypeMismatchException e) {
        String message = String.format("Parameter type mismatch: %s", e.getName());
        log.error("Parameter type mismatch: {}", message, e);
        return withTraceId(Result.error(ErrorCode.BAD_REQUEST.getCode(), message));
    }

    /**
     * 处理HTTP消息不可读异常
     * 当请求体格式错误或无法解析时触发
     *
     * @param e HTTP消息不可读异常对象
     * @return 标准化错误响应结果
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result<Void> handleMessageNotReadableException(HttpMessageNotReadableException e) {
        log.error("HTTP message not readable exception: {}", e.getMessage());
        return withTraceId(Result.error(ErrorCode.BAD_REQUEST.getCode(), "Request body format error"));
    }

    /**
     * 处理请求方法不支持异常
     * 当使用不支持的HTTP方法访问接口时触发
     *
     * @param e 请求方法不支持异常对象
     * @return 标准化错误响应结果，包含不支持的HTTP方法
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
    public Result<Void> handleMethodNotSupportedException(HttpRequestMethodNotSupportedException e) {
        String message = String.format("Request method not supported: %s", e.getMethod());
        log.error("Request method not supported: {}", message, e);
        return withTraceId(Result.error(ErrorCode.METHOD_NOT_ALLOWED.getCode(), message));
    }

    /**
     * 处理媒体类型不支持异常
     * 当请求的Content-Type不支持时触发
     *
     * @param e 媒体类型不支持异常对象
     * @return 标准化错误响应结果，包含不支持的媒体类型
     */
    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    @ResponseStatus(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
    public Result<Void> handleMediaTypeNotSupportedException(HttpMediaTypeNotSupportedException e) {
        String message = String.format("Unsupported media type: %s", e.getContentType());
        log.error("Media type not supported: {}", message, e);
        return withTraceId(Result.error(ErrorCode.UNSUPPORTED_MEDIA_TYPE.getCode(), message));
    }

    /**
     * 处理文件上传大小超限异常
     * 当上传文件大小超出限制时触发
     *
     * @param e 文件上传大小超限异常对象
     * @return 标准化错误响应结果
     */
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result<Void> handleMaxUploadSizeExceededException(MaxUploadSizeExceededException e) {
        log.error("File upload size exceeded: {}", e.getMessage());
        return withTraceId(Result.error(ErrorCode.BAD_REQUEST.getCode(), "File size exceeds limit"));
    }

    /**
     * 处理404异常
     * 当访问不存在的资源路径时触发
     *
     * @param e 404异常对象
     * @return 标准化错误响应结果
     */
    @ExceptionHandler(NoHandlerFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Result<Void> handleNotFoundException(NoHandlerFoundException e) {
        String message = String.format("Request path not found: %s %s", e.getHttpMethod(), e.getRequestURL());
        log.error("404 exception: {}", message, e);
        return withTraceId(Result.error(ErrorCode.NOT_FOUND.getCode(), "Request path not found"));
    }

    /**
     * 处理系统异常（兜底异常处理）
     * 处理所有未被其他异常处理器捕获的异常
     *
     * @param e 异常对象
     * @return 标准化错误响应结果
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Result<Void> handleException(Exception e) {
        log.error("System exception: ", e);
        return withTraceId(Result.error(ErrorCode.INTERNAL_SERVER_ERROR.getCode(), "System error, please contact administrator"));
    }

    /**
     * 获取链路追踪ID
     * 从MDC中获取当前请求的链路追踪ID，用于日志追踪
     *
     * @return 链路追踪ID
     */
    private Result<Void> withTraceId(Result<Void> response) {
        String traceId = MDC.get(MDC_TRACE_ID_KEY);
        if (traceId != null) {
            response.setExtra(Collections.singletonMap("traceId", traceId));
        }
        return response;
    }

    private static final String MDC_TRACE_ID_KEY = "traceId";
}