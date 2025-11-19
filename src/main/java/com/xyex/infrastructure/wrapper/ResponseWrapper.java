package com.xyex.infrastructure.wrapper;

import java.lang.annotation.*;

@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ResponseWrapper {

    /**
     * 是否包装响应
     */
    boolean value() default true;

    /**
     * 忽略包装的条件
     */
    Class<?>[] ignore() default {};
}