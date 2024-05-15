package com.ulla.rocketmq.annotation;

import java.lang.annotation.*;

import org.apache.rocketmq.remoting.RPCHook;

import com.ulla.rocketmq.configuration.ExceptionIgnore;

/**
 * @author zhuyongdong
 * @Description rocketMq消费者注解
 * @since 2023/3/7 14:55
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface RocketConsumer {

    String nameServer() default "";

    String topic() default "";

    int retryTimes() default Integer.MIN_VALUE;

    int delayTimeLevel() default Integer.MIN_VALUE;

    int suspendTimeMillis() default Integer.MIN_VALUE;

    Class<? extends Throwable>[] ignoredExceptions() default {};

    Class<? extends ExceptionIgnore>[] exceptionIgnores() default {};

    Class<? extends RPCHook>[] hook() default {};

}
