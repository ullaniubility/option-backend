package com.ulla.rocketmq.listener.wrapper;

import java.util.List;

import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.common.message.MessageExt;

import com.ulla.rocketmq.listener.AbstractMessageListener;

/**
 * @author zhuyongdong
 * @Description
 * @since 2023/3/7 14:55
 */
public class ConcurrentlyListenerWrapper implements MessageListenerConcurrently {

    private AbstractMessageListener listener;

    public ConcurrentlyListenerWrapper(AbstractMessageListener listener) {
        this.listener = listener;
    }

    @Override
    public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs, ConsumeConcurrentlyContext context) {
        return this.listener.consumeMessage(msgs, context);
    }

}
