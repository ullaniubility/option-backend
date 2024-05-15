package com.ulla.rocketmq.listener.wrapper;

import java.util.List;

import org.apache.rocketmq.client.consumer.listener.ConsumeOrderlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeOrderlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerOrderly;
import org.apache.rocketmq.common.message.MessageExt;

import com.ulla.rocketmq.listener.AbstractMessageListener;

/**
 * @author zhuyongdong
 * @Description
 * @since 2023/3/7 14:55
 */
public class OrderlyListenerWrapper implements MessageListenerOrderly {

    private AbstractMessageListener listener;

    public OrderlyListenerWrapper(AbstractMessageListener listener) {
        this.listener = listener;
    }

    @Override
    public ConsumeOrderlyStatus consumeMessage(List<MessageExt> msgs, ConsumeOrderlyContext context) {
        return this.listener.consumeMessage(msgs, context);
    }

}
