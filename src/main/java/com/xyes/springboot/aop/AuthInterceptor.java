package com.xyes.springboot.aop;

import com.xyes.springboot.annotation.RequireRole;
import com.xyes.springboot.exception.ErrorCode;
import com.xyes.springboot.exception.BusinessException;
import com.xyes.springboot.model.entity.User;
import com.xyes.springboot.model.enums.UserRoleEnum;
import com.xyes.springboot.service.UserService;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import lombok.RequiredArgsConstructor;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

/**
 * 权限拦截器
 * 通过AOP拦截带有 {@link RequireRole} 注解的方法，进行权限验证
 *
 * @author xujun
 */
@Aspect
@Component
@RequiredArgsConstructor
public class AuthInterceptor {
    private final UserService userService;

    /**
     * 定义切点：拦截所有带有 {@link RequireRole} 注解的方法
     */
    @Pointcut("@annotation(com.xyes.springboot.annotation.RequireRole)")
    public void requireRolePointcut() {
    }

    /**
     * 执行权限拦截
     * 检查用户是否具有访问该方法所需的角色权限
     *
     * @param joinPoint 连接点
     * @param requireRole 权限注解
     * @return 方法执行结果
     * @throws Throwable 如果权限验证失败或方法执行异常
     */
    @Around("@annotation(requireRole)")
    public Object doInterceptor(ProceedingJoinPoint joinPoint, RequireRole requireRole) throws Throwable {
        String mustRole = requireRole.value();
        RequestAttributes requestAttributes = RequestContextHolder.currentRequestAttributes();
        HttpServletRequest request = ((ServletRequestAttributes) requestAttributes).getRequest();
        UserRoleEnum mustRoleEnum = UserRoleEnum.getEnumByValue(mustRole);
        User loginUser = userService.getLoginUser(request);
        
        // 如果没有指定角色，只需要登录即可
        if (mustRoleEnum == null) {
            return joinPoint.proceed();
        }
        
        // 检查用户是否登录
        if (loginUser == null) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        
        UserRoleEnum userRoleEnum = UserRoleEnum.getEnumByValue(loginUser.getUserRole());
        
        // 检查用户是否被禁用
        if (UserRoleEnum.BAN.equals(userRoleEnum)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        
        // 如果需要管理员权限
        if (UserRoleEnum.ADMIN.equals(mustRoleEnum)) {
            // 用户没有管理员权限，拒绝
            if (!UserRoleEnum.ADMIN.equals(userRoleEnum)) {
                throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
            }
        }
        
        return joinPoint.proceed();
    }
}


