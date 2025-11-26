package com.xyex.infrastructure.filter;

import com.xyex.annotation.RequireRole;
import com.xyex.infrastructure.exception.BusinessException;
import com.xyex.infrastructure.exception.ErrorCode;
import com.xyex.infrastructure.utils.JwtUtils;
import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * RequireRole 注解的 AOP 处理
 * 用于检查用户权限
 *
 * @author xujun
 */
@Aspect
@Component
public class RequireRoleAspect {

    private final JwtUtils jwtUtils;

    public RequireRoleAspect(JwtUtils jwtUtils) {
        this.jwtUtils = jwtUtils;
    }

    @Before("@annotation(requireRole)")
    public void checkRole(JoinPoint joinPoint, RequireRole requireRole) {
        // 获取当前请求
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "无法获取请求信息");
        }

        HttpServletRequest request = attributes.getRequest();
        
        // 从请求头获取 token
        String token = request.getHeader("Authorization");
        if (token == null || token.isEmpty()) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "未登录");
        }
        
        // 验证 token 是否有效
        if (!jwtUtils.validateToken(token)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "token无效或已过期");
        }
        
        // 如果指定了角色，则检查用户角色
        String requiredRole = requireRole.value();
        if (requiredRole != null && !requiredRole.isEmpty()) {
            String userRole = jwtUtils.getUserRoleFromToken(token);
            if (userRole == null || !userRole.equals(requiredRole)) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "权限不足");
            }
        }
    }
}
