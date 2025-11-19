package com.xyes.springboot.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.IOException;
import java.util.Collections;
import java.util.Enumeration;

/**
 * Content-Type 规范化过滤器
 * 在请求到达 Spring MVC 之前，规范化 Content-Type 头，移除 charset 参数
 * 这样可以确保无论前端发送什么格式的 Content-Type，都能被正确处理
 *
 * @author xujun
 */
@Slf4j
@Component
@Order(1) // 确保在其他过滤器之前执行
public class ContentTypeFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        if (request instanceof HttpServletRequest httpRequest) {
            String contentType = httpRequest.getContentType();
            
            // 如果 Content-Type 包含 charset，规范化它
            if (contentType != null && contentType.toLowerCase().contains("application/json")) {
                // 规范化 Content-Type：移除 charset 参数，只保留 application/json
                String normalizedContentType = normalizeContentType(contentType);
                if (!normalizedContentType.equals(contentType)) {
                    log.info("Normalizing Content-Type from '{}' to '{}' for request: {}", 
                            contentType, normalizedContentType, httpRequest.getRequestURI());
                    request = new ContentTypeRequestWrapper(httpRequest, normalizedContentType);
                }
            }
        }
        
        chain.doFilter(request, response);
    }

    /**
     * 规范化 Content-Type，移除 charset 参数
     *
     * @param contentType 原始 Content-Type
     * @return 规范化后的 Content-Type
     */
    private String normalizeContentType(String contentType) {
        if (contentType == null) {
            return null;
        }
        
        // 如果包含 application/json，移除 charset 参数
        String lowerContentType = contentType.toLowerCase();
        if (lowerContentType.contains("application/json")) {
            // 移除 charset 参数（包括大小写变体）
            String normalized = contentType
                    .replaceAll("(?i);\\s*charset\\s*=\\s*[^;]+", "")
                    .replaceAll("(?i);\\s*charset\\s*=\\s*[^;]+", "") // 再次处理，确保移除所有
                    .trim();
            
            // 如果末尾有分号，移除它
            if (normalized.endsWith(";")) {
                normalized = normalized.substring(0, normalized.length() - 1).trim();
            }
            
            return normalized.isEmpty() ? "application/json" : normalized;
        }
        
        return contentType;
    }

    /**
     * HttpServletRequest 包装类，用于修改 Content-Type 头
     */
    private static class ContentTypeRequestWrapper extends HttpServletRequestWrapper {
        private final String contentType;

        public ContentTypeRequestWrapper(HttpServletRequest request, String contentType) {
            super(request);
            this.contentType = contentType;
        }

        @Override
        public String getHeader(String name) {
            if ("content-type".equalsIgnoreCase(name)) {
                return contentType;
            }
            return super.getHeader(name);
        }

        @Override
        public Enumeration<String> getHeaders(String name) {
            if ("content-type".equalsIgnoreCase(name)) {
                return Collections.enumeration(Collections.singletonList(contentType));
            }
            return super.getHeaders(name);
        }

        @Override
        public String getContentType() {
            return contentType;
        }

        @Override
        public int getIntHeader(String name) {
            if ("content-type".equalsIgnoreCase(name)) {
                throw new IllegalArgumentException("Content-Type is not an integer header");
            }
            return super.getIntHeader(name);
        }
    }
}


