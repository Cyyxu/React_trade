package com.xyex.infrastructure.filter;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 请求 MDC 过滤器 - Spring Boot 3.5.0 优化版
 * <p>
 * 功能说明：
 * 1. 为每个 HTTP 请求生成或提取唯一的请求 ID (requestId/traceId)
 * 2. 将请求 ID 存储到 MDC 中，实现日志链路追踪
 * 3. 支持分布式链路追踪，可从请求头中传递 traceId
 * 4. 自动清理 MDC，避免内存泄漏和线程污染
 * <p>
 * 使用场景：
 * - 微服务链路追踪
 * - 请求日志关联分析
 * - 问题排查和性能监控
 * - 分布式系统调用链跟踪
 * <p>
 * 性能优化：
 * 1. 使用高效的 UUID 生成算法
 * 2. 请求计数器监控
 * 3. 异常安全处理，确保 MDC 清理
 * 4. 支持多种 traceId 头部格式
 *
 * @author stella-team
 * @version 2.0 (Spring Boot 3.5.0 适配版)
 * @since 2025/7/15
 */
@Slf4j
public class RequestMdcFilter implements HandlerInterceptor {

    // ==================== 常量定义 ====================

    /**
     * MDC 中存储请求 ID 的键名
     */
    public static final String MDC_TRACE_ID = "traceId";

    /**
     * HTTP 请求头中的 trace ID 字段名（支持多种格式）
     */
    public static final String HEADER_TRACE_ID = "X-Trace-Id";
    public static final String HEADER_REQUEST_ID = "X-Request-Id";
    public static final String HEADER_CORRELATION_ID = "X-Correlation-Id";
    public static final String HEADER_SPAN_ID = "X-Span-Id";

    /**
     * 用户相关头部
     */
    public static final String HEADER_USER_ID = "X-User-Id";
    public static final String HEADER_CLIENT_IP = "X-Real-IP";

    /**
     * MDC 扩展字段
     */
    public static final String MDC_USER_ID = "userId";
    public static final String MDC_CLIENT_IP = "clientIp";
    public static final String MDC_REQUEST_URI = "requestUri";
    public static final String MDC_HTTP_METHOD = "httpMethod";
    public static final String MDC_REQUEST_START_TIME = "requestStartTime";

    // ==================== 统计信息 ====================

    /**
     * 请求计数器
     */
    private static final AtomicLong REQUEST_COUNTER = new AtomicLong(0);

    /**
     * 异常计数器
     */
    private static final AtomicLong EXCEPTION_COUNTER = new AtomicLong(0);

    // ==================== 核心方法 ====================

    /**
     * 请求处理前：设置 MDC 上下文
     *
     * @param request  HTTP 请求
     * @param response HTTP 响应
     * @param handler  处理器
     * @return true 继续处理，false 中断处理
     */
    @Override
    public boolean preHandle(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull Object handler) {
        try {
            // 1. 增加请求计数
            final long requestSequence = REQUEST_COUNTER.incrementAndGet();

            // 2. 提取或生成 Trace ID
            final String traceId = extractOrGenerateTraceId(request);

            // 3. 设置核心 MDC 信息
            MDC.put(MDC_TRACE_ID, traceId);

            // 4. 设置扩展 MDC 信息
            setExtendedMdcInfo(request, requestSequence);

            // 5. 在响应头中返回 Trace ID（便于客户端追踪）
            response.setHeader(HEADER_TRACE_ID, traceId);
            return true;

        } catch (Exception e) {
            EXCEPTION_COUNTER.incrementAndGet();
            log.error("Failed to set MDC context for request: {}", request.getRequestURI(), e);

            // 异常情况下也要设置基础的 traceId，确保日志可追踪
            if (!StringUtils.hasText(MDC.get(MDC_TRACE_ID))) {
                MDC.put(MDC_TRACE_ID, UUID.randomUUID().toString());
            }

            return true; // 即使异常也继续处理请求
        }
    }

    /**
     * 请求完成后：清理 MDC 上下文
     *
     * @param request  HTTP 请求
     * @param response HTTP 响应
     * @param handler  处理器
     * @param ex       异常信息（如果有）
     */
    @Override
    public void afterCompletion(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull Object handler, @Nullable Exception ex) {
        try {
            // 记录异常信息（如果有）
            if (ex != null) {
                log.error("Request completed with exception - URI: {}, Method: {}, Error: {}", request.getRequestURI(), request.getMethod(), ex.getMessage(), ex);
                EXCEPTION_COUNTER.incrementAndGet();
            }
        } catch (Exception e) {
            log.error("Failed to log request completion", e);
        } finally {
            // 确保清理 MDC，避免内存泄漏和线程污染
            clearMdc();
        }
    }

    // ==================== 工具方法 ====================

    /**
     * 提取或生成 Trace ID
     * 优先级：X-Trace-Id > X-Request-Id > X-Correlation-Id > 生成新ID
     */
    @NonNull
    private String extractOrGenerateTraceId(@NonNull HttpServletRequest request) {
        // 1. 尝试从各种可能的头部中提取 trace ID
        String traceId = extractTraceIdFromHeaders(request);

        // 2. 如果没有找到，生成新的 trace ID
        if (!StringUtils.hasText(traceId)) {
            traceId = UUID.randomUUID().toString();
        }

        return traceId;
    }

    /**
     * 从请求头中提取 Trace ID
     */
    @Nullable
    private String extractTraceIdFromHeaders(@NonNull HttpServletRequest request) {
        // 按优先级顺序检查各种可能的头部
        final String[] traceHeaders = {HEADER_TRACE_ID, HEADER_REQUEST_ID, HEADER_CORRELATION_ID, HEADER_SPAN_ID};

        for (String headerName : traceHeaders) {
            final String headerValue = request.getHeader(headerName);
            if (StringUtils.hasText(headerValue)) {
                return headerValue.trim();
            }
        }

        return null;
    }

    /**
     * 设置扩展的 MDC 信息
     */
    private void setExtendedMdcInfo(@NonNull HttpServletRequest request, long requestSequence) {
        try {
            // 请求基本信息
            MDC.put(MDC_REQUEST_URI, request.getRequestURI());
            MDC.put(MDC_HTTP_METHOD, request.getMethod());
            MDC.put(MDC_REQUEST_START_TIME, String.valueOf(System.currentTimeMillis()));

            // 用户信息（如果有）
            final String userId = request.getHeader(HEADER_USER_ID);
            if (StringUtils.hasText(userId)) {
                MDC.put(MDC_USER_ID, userId);
            }

            // 客户端 IP
            final String clientIp = extractClientIp(request);
            if (StringUtils.hasText(clientIp)) {
                MDC.put(MDC_CLIENT_IP, clientIp);
            }

            // 请求序号（用于排序）
            MDC.put("requestSequence", String.valueOf(requestSequence));
        } catch (Exception e) {
            log.debug("Failed to set extended MDC info", e);
        }
    }

    /**
     * 提取客户端真实 IP
     */
    @Nullable
    private String extractClientIp(@NonNull HttpServletRequest request) {
        // 优先级：X-Real-IP > X-Forwarded-For > Remote Address
        String clientIp = request.getHeader(HEADER_CLIENT_IP);
        if (!StringUtils.hasText(clientIp)) {
            clientIp = request.getHeader("X-Forwarded-For");
            if (StringUtils.hasText(clientIp) && clientIp.contains(",")) {
                clientIp = clientIp.split(",")[0].trim();
            }
        }
        if (!StringUtils.hasText(clientIp)) {
            clientIp = request.getRemoteAddr();
        }
        return clientIp;
    }

    /**
     * 清理 MDC 上下文
     * 确保所有 MDC 数据都被清理，避免内存泄漏
     */
    private void clearMdc() {
        try {
            MDC.clear();
        } catch (Exception e) {
            log.debug("Failed to clear MDC", e);
        }
    }
}