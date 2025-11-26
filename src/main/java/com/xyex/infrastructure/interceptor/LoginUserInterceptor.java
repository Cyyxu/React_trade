package com.xyex.infrastructure.interceptor;

import com.xyex.infrastructure.exception.BusinessException;
import com.xyex.infrastructure.exception.ErrorCode;
import com.xyex.infrastructure.utils.JwtUtils;
import com.xyex.infrastructure.utils.LoginUserContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

/**
 * 拦截请求，解析 Token 并把用户信息写入 ThreadLocal
 */
@Component
@RequiredArgsConstructor
public class LoginUserInterceptor implements HandlerInterceptor {

    private final JwtUtils jwtUtils;

    @Override
    public boolean preHandle(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull Object handler) {
        String token = extractToken(request);
        if (!StringUtils.hasText(token)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "未登录");
        }

        if (!jwtUtils.validateToken(token)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "token无效或已过期");
        }

        Long userId = jwtUtils.getUserIdFromToken(token);
        LoginUserContext.set(userId);
        request.setAttribute("loginUserId", userId);
        return true;
    }

    @Override
    public void afterCompletion(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull Object handler, @Nullable Exception ex) {
        LoginUserContext.clear();
    }

    private String extractToken(HttpServletRequest request) {
        String headerName = jwtUtils.getTokenHeader();
        String token = request.getHeader(headerName);
        if (!StringUtils.hasText(token)) {
            token = request.getHeader("Authorization");
        }

        if (!StringUtils.hasText(token)) {
            return null;
        }

        String tokenHead = jwtUtils.getTokenHead();
        if (StringUtils.hasText(tokenHead) && token.startsWith(tokenHead)) {
            token = token.substring(tokenHead.length()).trim();
        }
        return token;
    }
}
