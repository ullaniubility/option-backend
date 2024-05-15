package com.ulla.rocketmq.listener;

import java.lang.reflect.Method;
import java.util.List;

import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeOrderlyContext;
import org.apache.rocketmq.common.message.MessageExt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.classify.BinaryExceptionClassifier;

import com.ulla.rocketmq.configuration.ConsumerConfig;
import com.ulla.rocketmq.configuration.ExceptionIgnore;
import com.ulla.rocketmq.exception.IgnorableExceptions;

/**
 * @author zhuyongdong
 * @Description
 * @since 2023/3/7 14:55
 */
public abstract class IgnoredExceptionListener extends AbstractMessageListener {

    private static final Logger logger = LoggerFactory.getLogger(IgnoredExceptionListener.class);

    protected IgnorableExceptions ignorableExceptions;

    protected BinaryExceptionClassifier classifier;

    public IgnoredExceptionListener(Object consumerClass, Method consumerMethod, ConsumerConfig config) {

        super(consumerClass, consumerMethod, config);

        this.ignorableExceptions = buildIgnorableExceptions(config.getIgnorableExceptions());

        // 使用classify()方法时，需要被忽略的异常为true，其他异常为false
        // ignorableExceptions是需要被忽略异常的集合
        this.classifier = new BinaryExceptionClassifier(this.ignorableExceptions, false);
    }

    @Override
    protected Object consumeMessage(List<MessageExt> messageExtList, ConsumeConcurrentlyContext concurrentlyContext,
        ConsumeOrderlyContext orderlyContext) {

        try {
            // 执行下层业务逻辑
            return ignoredExceptionConsumerMessageWrapper(messageExtList, concurrentlyContext, orderlyContext);
        } catch (Throwable throwable) {

            // 可以被忽略的异常
            if (this.classifier.classify(throwable)) {
                return true;
            }

            // 根据异常的内容判断是否可以忽略
            if (this.exceptionIgnores.length > 0 && ignorable(throwable)) {
                return true;
            }

            // 异常导致消费失败
            return false;
        }

    }

    private IgnorableExceptions buildIgnorableExceptions(Class<? extends Throwable>[] exceptions) {
        return new IgnorableExceptions(exceptions);
    }

    /**
     * 判断是否可以忽略异常
     * 
     * @param throwable
     *            异常信息
     * @return {@code true} 忽略异常 {@code false} 其他
     */
    private boolean ignorable(Throwable throwable) {
        for (ExceptionIgnore exceptionIgnore : exceptionIgnores) {
            if (exceptionIgnore.ignorable(throwable)) {
                return true;
            }
        }
        return false;
    }

    protected abstract Object ignoredExceptionConsumerMessageWrapper(List<MessageExt> messageList,
        ConsumeConcurrentlyContext concurrentlyContext, ConsumeOrderlyContext orderlyContext) throws Throwable;

}
