package com.ulla.config;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import com.ulla.producer.MqProducer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * @author zhuyongdong
 * @Description TODO
 * @since 2023/2/11 18:49
 */
@Slf4j
@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class InitMqProducer implements ApplicationListener<ApplicationReadyEvent> {

    final MqProducerConfig mqProducerConfig;

    /**
     * 存放所有生产者
     */
    public static Map<String, DefaultMQProducer> producerMap = new HashMap<>();

    @Override
    public void onApplicationEvent(ApplicationReadyEvent applicationReadyEvent) {
        List<MqProducer> producerList = mqProducerConfig.getProducerList();
        log.info("配置数量为：{}", producerList.size());
        if (CollectionUtils.isEmpty(producerList)) {
            log.info("无MQ生产者---------------------------------------");
        } else {
            for (MqProducer vo : producerList) {
                try {
                    DefaultMQProducer producer = new DefaultMQProducer(vo.getGroupName(),
                        vo.getEnableMsgTrace().isEmpty(), vo.getCustomizedTraceTopic());
                    producer.setNamesrvAddr(vo.getNamesrvAddr());
                    producer.setVipChannelEnabled(false);
                    producer.setProducerGroup(vo.getGroupName());
                    producer.setMaxMessageSize(vo.getMaxMessageSize());
                    producer.setSendMsgTimeout(vo.getSendMsgTimeOut());
                    producer.setRetryTimesWhenSendAsyncFailed(vo.getRetryTimesWhenSendFailed());
                    producer.setRetryTimesWhenSendFailed(vo.getRetryTimesWhenSendFailed());
                    producer.start();
                    producerMap.put(vo.getProducerId(), producer);
                    log.info("mq生产者{},{}启动成功", vo.getGroupName(), vo.getNamesrvAddr());
                } catch (MQClientException e) {
                    log.error("mq生产者{},{}启动失败", vo.getGroupName(), vo.getNamesrvAddr(), e);
                }
            }
        }
    }
}
