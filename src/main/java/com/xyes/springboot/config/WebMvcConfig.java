package com.xyes.springboot.config;

import com.xyes.springboot.aop.AuthInterceptorHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.ContentNegotiationConfigurer;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.ArrayList;
import java.util.List;

/**
 * Web MVC 配置
 * 用于配置消息转换器和内容协商策略
 * <p>
 * 主要功能：
 * <ul>
 *   <li>配置 JSON 消息转换器，支持带 charset 的 Content-Type</li>
 *   <li>配置内容协商策略，更宽松地接受 Content-Type</li>
 * </ul>
 *
 * @author xujun
 */
@Configuration
@RequiredArgsConstructor
public class WebMvcConfig implements WebMvcConfigurer {

    private final AuthInterceptorHandler authInterceptorHandler;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(authInterceptorHandler)
                .addPathPatterns("/**");
    }

    @Override
    public void configureContentNegotiation(ContentNegotiationConfigurer configurer) {
        // 配置内容协商，更宽松地接受 Content-Type
        configurer
                .favorParameter(false)
                .ignoreAcceptHeader(false)
                .defaultContentType(MediaType.APPLICATION_JSON);
    }

    @Override
    public void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
        // 扩展 JSON 消息转换器，支持带 charset 的 Content-Type
        for (HttpMessageConverter<?> converter : converters) {
            if (converter instanceof MappingJackson2HttpMessageConverter jsonConverter) {

                // 创建新的支持列表，包含所有可能的 JSON Content-Type 变体
                List<MediaType> supportedMediaTypes = new ArrayList<>();
                
                // 添加标准的 application/json
                supportedMediaTypes.add(MediaType.APPLICATION_JSON);
                
                // 添加带 charset 的变体（大小写不敏感）
                supportedMediaTypes.add(MediaType.valueOf("application/json;charset=UTF-8"));
                supportedMediaTypes.add(MediaType.valueOf("application/json;charset=utf-8"));
                supportedMediaTypes.add(MediaType.valueOf("application/json;charset=utf8"));
                supportedMediaTypes.add(MediaType.valueOf("application/json;charset=UTF8"));
                
                // 保留原有的支持类型（如果有的话）
                for (MediaType existingType : jsonConverter.getSupportedMediaTypes()) {
                    if (!supportedMediaTypes.contains(existingType)) {
                        supportedMediaTypes.add(existingType);
                    }
                }
                
                jsonConverter.setSupportedMediaTypes(supportedMediaTypes);
                
                // 打印日志确认配置生效
                System.out.println("JSON Converter supported media types: " + supportedMediaTypes);
                break; // 只处理第一个 JSON 转换器
            }
        }
    }
}

