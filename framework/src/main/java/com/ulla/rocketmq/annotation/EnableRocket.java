package com.ulla.rocketmq.annotation;

import java.lang.annotation.*;

import org.springframework.context.annotation.Import;

import com.ulla.rocketmq.RocketMQAutoConfiguration;

/**
 * @author zhuyongdong
 * @Description 开启rocketMq注解
 * @since 2023/3/7 14:55
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.ANNOTATION_TYPE})
@Import(RocketMQAutoConfiguration.class)
public @interface EnableRocket {

}
