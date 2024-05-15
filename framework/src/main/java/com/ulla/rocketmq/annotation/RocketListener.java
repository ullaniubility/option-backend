package com.ulla.rocketmq.annotation;

import java.lang.annotation.*;

import org.apache.rocketmq.remoting.RPCHook;

import com.ulla.rocketmq.configuration.ExceptionIgnore;

/**
 * @author zhuyongdong
 * @Description rocketMq消费者方法注解
 * @since 2023/3/7 14:55
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface RocketListener {

    boolean orderly() default false;

    String nameServer() default "";

    String instance() default "";

    String consumerGroup();

    String topic() default "";

    String tag() default "";

    int maxBatchSize() default 1;

    int retryTimes() default Integer.MIN_VALUE;

    int delayTimeLevel() default Integer.MIN_VALUE;

    int suspendTimeMillis() default Integer.MIN_VALUE;

    Class<? extends Throwable>[] ignoredExceptions() default {};

    Class<? extends ExceptionIgnore>[] exceptionIgnores() default {};

    Class<? extends RPCHook>[] hook() default {};

}
