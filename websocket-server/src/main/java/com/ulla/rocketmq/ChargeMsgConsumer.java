package com.ulla.rocketmq;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONObject;
import com.ulla.cache.Cache;
import com.ulla.common.enums.WebsocketDateEnum;
import com.ulla.constant.NumberConstant;
import com.ulla.constant.RocketMqConstants;
import com.ulla.modules.payment.entity.MoneyPaymentTransactionEntity;
import com.ulla.modules.payment.mapper.MoneyPaymentTransactionMapper;
import com.ulla.rocketmq.annotation.RocketConsumer;
import com.ulla.rocketmq.annotation.RocketListener;
import com.ulla.service.WebSocketServer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * @author chen
 * @Description 充值消息提醒消费
 * @since 2023/3/7 14:55
 */
@Slf4j
@RocketConsumer(nameServer = "${rocketmq.namesrvAddr}", topic = RocketMqConstants.CHARGE_MSG)
@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ChargeMsgConsumer {

    final Cache cache;

    final MoneyPaymentTransactionMapper moneyPaymentTransactionMapper;

    @LogAspect
    @RocketListener(consumerGroup = "binance-producer")
    public ConsumeConcurrentlyStatus listenAndSendChargeMsg(String message) {
        Map<String, String> map = JSONObject.parseObject(message, Map.class);
        sendNotification(map.get("openId"), map.get("amount"), map.get("moneyPaymentTransactionId"));
        log.info("消费成功!!!!!!!!!!!!!!!!!!!!!!!!!");
        return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
    }

    public void sendNotification(String userCode, String amount, String moneyPaymentTransactionId) {
        try {
            List<Object> keys = cache.keys("*" + userCode + "-*");
            MoneyPaymentTransactionEntity moneyPaymentTransactionEntity =
                moneyPaymentTransactionMapper.selectById(moneyPaymentTransactionId);
            if (keys.size() > 0) {
                StringBuffer buf = new StringBuffer("{\"dataType\":");
                buf.append(WebsocketDateEnum.CHARGE_MSG.getColumnType());
                buf.append(",\"amount\":\"").append(amount);
                buf.append("\"}");
                keys.forEach(item -> {
                    try {
                        String userCodeKey = cache.getString(item);
                        if (WebSocketServer.queryUserIsOnline(userCodeKey)) {
                            WebSocketServer.sendInfo(buf.toString(), userCodeKey);
                            log.info("发送成功!!!!!!!!!!!!!!!!!!!!!!!!!");
                            moneyPaymentTransactionEntity.setIfRead(NumberConstant.TWO);
                        }
                    } catch (IOException e) {
                        log.error(e.getMessage());
                    }
                });
                // 设置为已读
                moneyPaymentTransactionMapper.updateById(moneyPaymentTransactionEntity);
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

}
