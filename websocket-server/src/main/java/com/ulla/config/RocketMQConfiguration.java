package com.ulla.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import com.ulla.rocketmq.MessageListenerLog;
import com.ulla.rocketmq.annotation.EnableRocket;

/**
 * @author zhuyongdong
 * @Description TODO
 * @since 2023/3/9 13:19
 */
@EnableRocket
@Component
public class RocketMQConfiguration {

    @Value("${rocketmq.namesrvAddr}")
    private String nameServer;

    /*@Bean
    public RocketConfiguration rocketConfiguration() {
        RocketConfiguration rocketConfiguration = new RocketConfiguration();
        rocketConfiguration.setRetryTimes(3);
        rocketConfiguration.setDelayTimeLevel(1);
        rocketConfiguration.setNameServer(nameServer);
        return rocketConfiguration;
    }*/

    /*@Bean
    public DefaultMQProducer defaultMQProducer() {
    
        DefaultMQProducer producer = new DefaultMQProducer();
        producer.setNamesrvAddr(nameServer);
        producer.setProducerGroup("producerGroup");
    
        return producer;
    }*/

    /*@Bean
    public RocketTemplate rocketTemplate(DefaultMQProducer producer) {
        return new RocketTemplate(producer);
    }*/

    @Bean
    public MessageListenerLog messageListenerLog() {
        return new MessageListenerLog();
    }

}
