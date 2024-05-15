package com.ulla.config;

import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import com.ulla.producer.MqProducer;

import lombok.Data;

/**
 * @author zhuyongdong
 * @Description TODO
 * @since 2023/2/11 16:54
 */
@Data
@Component
@ConfigurationProperties(prefix = "rocketmq.producer")
public class MqProducerConfig {

    private List<MqProducer> producerList;

}
