package com.ulla.binance.rocketmq.util;

import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.springframework.util.CollectionUtils;

import com.ulla.binance.rocketmq.config.InitMqProducer;

/**
 * @author zhuyongdong
 * @Description TODO
 * @since 2023/2/13 10:11
 */
public class MqUtil {

    /**
     * 根据生成者唯一标识获取发送实例
     * 
     * @param producerId
     * @return
     */
    public static DefaultMQProducer getProducer(String producerId) {
        if (CollectionUtils.isEmpty(InitMqProducer.producerMap)) {
            return null;
        } else {
            return InitMqProducer.producerMap.get(producerId);
        }
    }

    public static void main(String[] args) {
        // TODO 打包启动入口，禁止删除
    }
}
