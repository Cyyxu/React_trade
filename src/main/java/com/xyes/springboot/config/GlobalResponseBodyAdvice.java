package com.xyes.springboot.config;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.xyes.springboot.model.Result;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

/**
 * 全局响应包装
 */
@Slf4j
@RequiredArgsConstructor
@RestControllerAdvice
public class GlobalResponseBodyAdvice implements ResponseBodyAdvice<Object> {

    private final ObjectMapper objectMapper;

    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        // 获取方法所在的类
        Class<?> declaringClass = returnType.getDeclaringClass();
        
        // 排除 Swagger 相关接口
        String packageName = declaringClass.getPackage().getName();
        if (packageName.startsWith("springfox") || packageName.startsWith("com.github.xiaoymin")) {
            return false;
        }
        
        // 排除 Spring Boot 内置接口
        return !packageName.startsWith("org.springframework.boot");
    }

    @Override
    public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType,
                                  Class<? extends HttpMessageConverter<?>> selectedConverterType,
                                  ServerHttpRequest request, ServerHttpResponse response) {
        if (body == null) {
            return Result.success();
        }
        if (body instanceof Result) {
            return body;
        }
        if (body instanceof String) {
            try {
                return objectMapper.writeValueAsString(Result.success(body));
            } catch (JsonProcessingException e) {
                log.error("Failed to serialize response", e);
                return body;
            }
        }
        return Result.success(body);
    }
}

