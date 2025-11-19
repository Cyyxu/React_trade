package com.xyex.infrastructure.wrapper;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.xyex.infrastructure.filter.RequestMdcFilter;
import com.xyex.infrastructure.model.Result;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.core.MethodParameter;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.util.Objects;

@Slf4j
@RestControllerAdvice(basePackages = "com.jyxy.stella.wind.controller")
public class ResponseWrapperHandler implements ResponseBodyAdvice<Object> {

    private final ObjectMapper objectMapper;

    public ResponseWrapperHandler(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        // 检查是否需要包装响应
        return shouldWrapResponse(returnType);
    }

    @Override
    public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType,
                                  Class<? extends HttpMessageConverter<?>> selectedConverterType,
                                  ServerHttpRequest request, ServerHttpResponse response) {
        // 如果已经是Result类型，直接返回
        if (body instanceof Result) {
            return setTraceId((Result<?>) body);
        }

        // 处理特殊类型
        if (isSpecialType(body)) {
            return body;
        }

        // 包装为Result
        Result<Object> result = Result.success(body);
        setTraceId(result);

        if (returnType.getParameterType() == String.class) {
            try {
                // 设置正确的Content-Type
                response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
                return objectMapper.writeValueAsString(result);
            } catch (JsonProcessingException e) {
                log.error("序列化响应数据失败", e);
                return Result.error("序列化响应失败");
            }
        }
        return result;
    }

    /**
     * 判断是否需要包装响应
     */
    private boolean shouldWrapResponse(MethodParameter returnType) {
        // 检查方法级别的注解
        if (AnnotatedElementUtils.hasAnnotation(Objects.requireNonNull(returnType.getMethod()), IgnoreResponseWrapper.class)) {
            return false;
        }

        // 检查类级别的注解
        if (AnnotatedElementUtils.hasAnnotation(returnType.getDeclaringClass(), IgnoreResponseWrapper.class)) {
            return false;
        }

        // 检查是否有ResponseWrapper注解，如果有且value为false，则不包装
        ResponseWrapper methodWrapper = AnnotatedElementUtils.findMergedAnnotation(returnType.getMethod(), ResponseWrapper.class);
        if (methodWrapper != null && !methodWrapper.value()) {
            return false;
        }

        ResponseWrapper classWrapper = AnnotatedElementUtils.findMergedAnnotation(returnType.getDeclaringClass(), ResponseWrapper.class);
        if (classWrapper != null && !classWrapper.value()) {
            return false;
        }

        // 检查特殊返回类型
        Class<?> returnClass = returnType.getParameterType();
        return !isSpecialReturnType(returnClass);
    }

    /**
     * 判断是否为特殊返回类型（不需要包装）
     */
    private boolean isSpecialReturnType(Class<?> returnType) {
        return StreamingResponseBody.class.isAssignableFrom(returnType) ||
                SseEmitter.class.isAssignableFrom(returnType) ||
                ResponseEntity.class.isAssignableFrom(returnType) ||
                returnType.getName().startsWith("org.springframework.web.servlet.mvc.method.annotation.") ||
                returnType.getName().startsWith("reactor.core.publisher.") ||
                returnType.getName().contains("Resource") ||
                returnType.getName().contains("InputStreamResource") ||
                returnType.getName().contains("ByteArrayResource");
    }

    /**
     * 判断是否为特殊类型的响应体
     */
    private boolean isSpecialType(Object body) {
        if (body == null) {
            return false;
        }

        return body instanceof StreamingResponseBody ||
                body instanceof SseEmitter ||
                body instanceof ResponseEntity ||
                body.getClass().getName().contains("Resource") ||
                body.getClass().getName().contains("InputStreamResource") ||
                body.getClass().getName().contains("ByteArrayResource");
    }

    /**
     * 设置链路追踪ID
     */
    private Result<?> setTraceId(Result<?> result) {
        result.setTraceId(MDC.get(RequestMdcFilter.MDC_TRACE_ID));
        return result;
    }
}