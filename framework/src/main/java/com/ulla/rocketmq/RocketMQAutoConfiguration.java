package com.ulla.rocketmq;

import org.springframework.context.annotation.Bean;

/**
 * @author zhuyongdong
 * @Description rocketMq自动装配
 * @since 2023/3/7 14:55
 */
public class RocketMQAutoConfiguration {

    @Bean
    public RocketAnnotationBeanPostProcessor rocketAnnotationBeanPostProcessor() {
        return new RocketAnnotationBeanPostProcessor();
    }

    @Bean
    public RocketConsumerSpringLifecycle rocketConsumerLifecycle() {
        return new RocketConsumerSpringLifecycle();
    }

}
