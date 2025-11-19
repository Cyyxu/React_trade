package com.xyex.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 权限检查注解
 * 用于标记需要特定角色权限才能访问的方法
 * <p>
 * 使用示例：
 * <pre>
 * // 需要管理员角色
 * {@code @RequireRole("admin")}
 * public void adminMethod() { }
 *
 * // 需要登录（不指定角色）
 * {@code @RequireRole}
 * public void loginRequiredMethod() { }
 * </pre>
 *
 * @author xujun
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RequireRole {
    /**
     * 必需的角色名称
     * 如果为空字符串，表示只需要登录即可，不检查具体角色
     * 如果不为空，则要求用户必须具有该角色才能访问
     *
     * @return 角色名称，默认为空字符串（仅需登录）
     */
    String value() default "";
}

