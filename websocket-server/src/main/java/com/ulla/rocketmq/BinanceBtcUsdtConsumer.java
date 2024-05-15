package com.ulla.rocketmq;

import java.io.IOException;
import java.util.List;

import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.common.message.MessageExt;
import org.springframework.beans.factory.annotation.Autowired;

import com.ulla.cache.Cache;
import com.ulla.common.enums.WebsocketDateEnum;
import com.ulla.modules.binance.mapper.QuotationKLineProductMapper;
import com.ulla.rocketmq.annotation.RocketListener;
import com.ulla.service.WebSocketServer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * @author zhuyongdong
 * @Description 币安比特币消费
 * @since 2023/3/7 14:55
 */
@Slf4j
/*@RocketConsumer(nameServer = "${rocketmq.namesrvAddr}", topic = "kline-BTCUSDT")
@Component*/
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class BinanceBtcUsdtConsumer {

    final Cache cache;

    final QuotationKLineProductMapper quotationKLineProductMapper;

    @LogAspect
    @RocketListener(consumerGroup = "binance-producer")
    public ConsumeConcurrentlyStatus binanceKline(String message, MessageExt messageExt) {
        String tag = messageExt.getTags();
        binanceKlineBytag(message, tag);
        return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
    }

    public void binanceKlineBytag(String message, String tag) {
        List<Object> keys = cache.keys("openKlineQuotation:BTCUSDT:" + tag + ":*");
        if (keys.size() > 0) {
            sendQuotation(keys, message, WebsocketDateEnum.K_ACTUAL);
        }
    }

    private void sendQuotation(List<Object> keys, String msg, WebsocketDateEnum messageType) {
        StringBuffer buf = new StringBuffer("{\"dataType\":" + messageType.getColumnType() + ",\"data\":");
        buf.append(msg);
        buf.append("}");
        keys.stream().forEach(item -> {
            try {
                String userCode = cache.getString(item);
                if (WebSocketServer.queryUserIsOnline(userCode)) {
                    WebSocketServer.sendInfo(buf.toString(), userCode);
                } else {
                    closeQuotation(userCode, item.toString());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    public void closeQuotation(String userCode, String key) {
        log.debug("userCode:{}", userCode);
        log.debug("key:{}", key);
        cache.remove(key);
    }

}
