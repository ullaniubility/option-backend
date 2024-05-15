package com.ulla.annotation;

import java.lang.annotation.*;

/**
 * @Description 防重复提交注解
 * @author zhuyongdong
 * @since 2023-04-22 12:39:28
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface RepeatedSubmitAspect {

    /**
     * 过期时间 默认3秒，即3秒内无法重复点击。
     */
    long expire() default 3;

}
