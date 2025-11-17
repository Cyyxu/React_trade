package com.xyes.springboot.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 全局跨域配置
 * 用于支持前后端分离架构，特别是 React 前端应用
 * <p>
 * 配置说明：
 * <ul>
 *   <li>允许所有路径的跨域请求</li>
 *   <li>支持开发环境（localhost）和生产环境的 React 前端</li>
 *   <li>允许携带 Cookie 和认证信息</li>
 *   <li>支持常用的 HTTP 方法</li>
 * </ul>
 *
 * @author xujun
 */
@Configuration
public class CorsConfig implements WebMvcConfigurer {
    
    
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowCredentials(true)
                .allowedOriginPatterns(
                        "http://localhost:*",
                        "http://127.0.0.1:*",
                        "https://*.yourdomain.com"  // 生产环境域名，根据实际情况修改
                )
                // 允许的 HTTP 方法
                .allowedMethods("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS")
                // 允许的请求头（包括自定义的 Authorization 等）
                .allowedHeaders("*")
                // 暴露的响应头（前端可以访问的响应头）
                .exposedHeaders(
                        "Authorization",
                        "Content-Type",
                        "X-Total-Count",
                        "Access-Control-Allow-Origin",
                        "Access-Control-Allow-Credentials"
                )
                .maxAge(3600);
    }
}

