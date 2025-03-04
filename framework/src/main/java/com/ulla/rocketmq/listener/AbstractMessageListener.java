package com.ulla.rocketmq.listener;

import java.lang.reflect.Method;
import java.util.List;

import org.apache.rocketmq.client.consumer.listener.*;
import org.apache.rocketmq.common.message.MessageExt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ulla.rocketmq.configuration.ConsumerConfig;
import com.ulla.rocketmq.configuration.ExceptionIgnore;

/**
 * @author zhuyongdong
 * @Description
 * @since 2023/3/7 14:55
 */
public abstract class AbstractMessageListener implements MessageListenerConcurrently, MessageListenerOrderly {

    private static final Logger logger = LoggerFactory.getLogger(AbstractMessageListener.class);

    protected String simpleName;

    protected Object consumerClass;

    protected Method consumerMethod;

    protected Class<? extends Throwable>[] ignorableExceptions;

    protected ExceptionIgnore[] exceptionIgnores;

    protected String topic;

    protected String tag;

    protected boolean orderly;

    /**
     * MessageListenerConcurrently消费端控制延迟
     */
    private int delayLevelWhenNextConsume;

    /**
     * ConsumeOrderlyContext消费端控制暂停时间
     */
    private long suspendCurrentQueueTimeMillis;

    public AbstractMessageListener(Object consumerClass, Method consumerMethod, ConsumerConfig config) {

        this.simpleName = config.getSimpleName();
        this.consumerClass = consumerClass;
        this.consumerMethod = consumerMethod;
        this.ignorableExceptions = config.getIgnorableExceptions();
        this.exceptionIgnores = this.createExceptionIgnores(config.getExceptionIgnores());
        this.delayLevelWhenNextConsume = config.getDelayTimeLevel();
        this.suspendCurrentQueueTimeMillis = config.getSuspendTimeMillis();
        this.topic = config.getTopic();
        this.tag = config.getTag();
        this.orderly = config.isOrderly();

        this.consumerMethod.setAccessible(true);
    }

    private ExceptionIgnore[] createExceptionIgnores(Class<? extends ExceptionIgnore>[] classes) {

        int size = (classes == null) ? 0 : classes.length;

        ExceptionIgnore[] exceptionIgnores = new ExceptionIgnore[size];

        for (int i = 0; i < size; i++) {

            try {
                exceptionIgnores[i] = classes[i].newInstance();
            } catch (InstantiationException | IllegalAccessException e) {

                if (logger.isErrorEnabled()) {
                    logger.error(String.format("初始化%s[%s]时，创建[%s]失败", this.consumerClass.getClass().getName(),
                        this.consumerMethod.getName(), classes[i].getName()));
                } else {
                    e.printStackTrace();
                }
            }

        }

        return exceptionIgnores;
    }

    @Override
    public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> messages, ConsumeConcurrentlyContext context) {

        context.setDelayLevelWhenNextConsume(this.delayLevelWhenNextConsume);

        int msgLength = messages.size();

        // 执行下层逻辑
        Object result = consumeMessage(messages, context, null);

        return convertConcurrentlyConsumeResult(result, msgLength, context);
    }

    @Override
    public ConsumeOrderlyStatus consumeMessage(List<MessageExt> messages, ConsumeOrderlyContext context) {

        context.setSuspendCurrentQueueTimeMillis(this.suspendCurrentQueueTimeMillis);

        // 执行下层逻辑
        Object result = consumeMessage(messages, null, context);

        return convertOrderlyConsumeResult(result);
    }

    private ConsumeConcurrentlyStatus convertConcurrentlyConsumeResult(Object result, int size,
        ConsumeConcurrentlyContext context) {

        // 返回值为void，表示消费成功，错误以异常抛出
        if (result == null) {
            return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
        }

        // 返回值为布尔型，true为成功，false为失败
        if (result instanceof Boolean) {
            return ((boolean)result) ? ConsumeConcurrentlyStatus.CONSUME_SUCCESS
                : ConsumeConcurrentlyStatus.RECONSUME_LATER;
        }

        // 返回值为ConsumeConcurrentlyStatus
        if (result instanceof ConsumeConcurrentlyStatus) {
            return (ConsumeConcurrentlyStatus)result;
        }

        // 其他情况为异常情况，返回结果失败
        return ConsumeConcurrentlyStatus.RECONSUME_LATER;
    }

    private ConsumeOrderlyStatus convertOrderlyConsumeResult(Object result) {

        // 返回值为void，表示消费成功，错误以异常抛出
        if (result == null) {
            return ConsumeOrderlyStatus.SUCCESS;
        }

        // 返回值为布尔型，true为成功，false为失败
        if (result instanceof Boolean) {
            return ((boolean)result) ? ConsumeOrderlyStatus.SUCCESS
                : ConsumeOrderlyStatus.SUSPEND_CURRENT_QUEUE_A_MOMENT;
        }

        // 返回值为ConsumeOrderlyStatus
        if (result instanceof ConsumeOrderlyStatus) {
            return (ConsumeOrderlyStatus)result;
        }

        return ConsumeOrderlyStatus.SUSPEND_CURRENT_QUEUE_A_MOMENT;
    }

    /**
     *
     * @param messageExtList
     * @param concurrentlyContext
     * @param orderlyContext
     * @return
     */
    protected abstract Object consumeMessage(List<MessageExt> messageExtList,
        ConsumeConcurrentlyContext concurrentlyContext, ConsumeOrderlyContext orderlyContext);

}
