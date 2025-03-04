package com.ulla.rocketmq.listener;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeOrderlyContext;
import org.apache.rocketmq.common.message.MessageExt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONException;
import com.ulla.rocketmq.configuration.ConsumerConfig;
import com.ulla.rocketmq.exception.DetectGenericTypeException;
import com.ulla.rocketmq.exception.JSONConvertException;
import com.ulla.rocketmq.exception.NoMethodParameterException;
import com.ulla.rocketmq.utils.MessageConverter;

/**
 * @author zhuyongdong
 * @Description
 * @since 2023/3/7 14:55
 */
public class ConvertMessageListener extends IgnoredExceptionListener {

    private static final Logger logger = LoggerFactory.getLogger(ConvertMessageListener.class);

    private Class<?> convertToClass = null;

    private boolean isConvertToList = false;

    private boolean hasMessageExt = false;

    private boolean isConcurrently = false;

    private boolean isOrderly = false;

    public ConvertMessageListener(Object consumerClass, Method consumerMethod, ConsumerConfig config)
        throws DetectGenericTypeException {
        super(consumerClass, consumerMethod, config);

        // 初始化调用方法
        this.initMethodInvokeAction(consumerMethod);
    }

    @Override
    protected Object ignoredExceptionConsumerMessageWrapper(List<MessageExt> messageList,
        ConsumeConcurrentlyContext concurrentlyContext, ConsumeOrderlyContext orderlyContext) throws Throwable {
        return invokeMethod(consumerClass, consumerMethod, messageList, concurrentlyContext, orderlyContext);
    }

    private Object invokeMethod(Object clazz, Method method, List<MessageExt> messageList,
        ConsumeConcurrentlyContext concurrentlyContext, ConsumeOrderlyContext orderlyContext) throws Throwable {

        // 注入方法的数据
        Object injectObject;

        if (this.isConvertToList) {

            List<Object> realList = new ArrayList<>(messageList.size());

            for (MessageExt messageExt : messageList) {
                String msg = new String(messageExt.getBody());
                realList.add(convertToClass(msg));
            }

            injectObject = realList;

        } else {
            String msg = new String(messageList.get(0).getBody());
            injectObject = convertToClass(msg);
        }

        return invokeMethodWithAllParameters(clazz, method, injectObject, messageList, concurrentlyContext,
            orderlyContext);
    }

    /**
     * 调用方法
     */
    private Object invokeMethodWithAllParameters(Object clazz, Method method, Object injectObject,
        List<MessageExt> messageList, ConsumeConcurrentlyContext concurrentlyContext,
        ConsumeOrderlyContext orderlyContext) throws Throwable {

        try {

            if (isConvertToList) {
                if (hasMessageExt) {
                    if (isConcurrently) {
                        return method.invoke(clazz, injectObject, messageList, concurrentlyContext);
                    } else if (isOrderly) {
                        return method.invoke(clazz, injectObject, messageList, orderlyContext);
                    } else {
                        return method.invoke(clazz, injectObject, messageList);
                    }
                } else {
                    if (isConcurrently) {
                        return method.invoke(clazz, injectObject, concurrentlyContext);
                    } else if (isOrderly) {
                        return method.invoke(clazz, injectObject, orderlyContext);
                    } else {
                        return method.invoke(clazz, injectObject);
                    }
                }
            } else {
                if (hasMessageExt) {
                    if (isConcurrently) {
                        return method.invoke(clazz, injectObject, messageList.get(0), concurrentlyContext);
                    } else if (isOrderly) {
                        return method.invoke(clazz, injectObject, messageList.get(0), orderlyContext);
                    } else {
                        return method.invoke(clazz, injectObject, messageList.get(0));
                    }
                } else {
                    if (isConcurrently) {
                        return method.invoke(clazz, injectObject, concurrentlyContext);
                    } else if (isOrderly) {
                        return method.invoke(clazz, injectObject, orderlyContext);
                    } else {
                        return method.invoke(clazz, injectObject);
                    }
                }
            }

        } catch (IllegalAccessException e) {

            String em = String.format("%s：方法", simpleName);

            logger.error(em);

            return false;

        } catch (InvocationTargetException e) {
            throw e.getTargetException();
        }

    }

    /**
     * 初始化调用方法需要的动作
     */
    private void initMethodInvokeAction(Method method) throws DetectGenericTypeException {

        Class<?>[] parameterTypes = method.getParameterTypes();

        if (parameterTypes.length == 0) {
            throw new NoMethodParameterException(String.format("初始化%s[%s]失败，方法参数必须包含需要的数据类型",
                this.consumerClass.getClass().getName(), this.consumerMethod.getName()));
        }

        // 获取需要注入的数据类型
        detectConvertClass(method);

        // 方法参数中只有消息类型，直接调用
        if (parameterTypes.length == 1) {
            return;
        }

        // 第二个参数是context
        if (parameterTypes[1] == ConsumeConcurrentlyContext.class) {
            this.isConcurrently = true;
            return;
        } else if (parameterTypes[1] == ConsumeOrderlyContext.class) {
            this.isOrderly = true;
            return;
        }

        // 第二个参数是MessageExt
        this.hasMessageExt = true;

        // 只有两个参数
        if (parameterTypes.length == 2) {
            return;
        }

        // 共有三个参数
        if (parameterTypes[2] == ConsumeConcurrentlyContext.class) {
            this.isConcurrently = true;
        } else if (parameterTypes[2] == ConsumeOrderlyContext.class) {
            this.isOrderly = true;
        }

    }

    /**
     * 将消息转换为需要的类型
     */
    private Object convertToClass(String msg) {

        if (this.convertToClass == String.class) {
            return msg;
        }

        try {
            return MessageConverter.convertJSONToObject(msg, this.convertToClass);
        } catch (JSONException e) {
            throw new JSONConvertException(
                String.format("%s: 消息转换为[%s]失败. Message=[%s]", simpleName, this.convertToClass.getName(), msg), e);
        }
    }

    /**
     * 获取需要转换的类型
     */
    private void detectConvertClass(Method method) throws DetectGenericTypeException {
        Class<?> parameterType = method.getParameterTypes()[0];
        if (parameterType == List.class) {
            this.isConvertToList = true;
            this.convertToClass = detectListClassType(method);
        } else {
            this.convertToClass = parameterType;
        }
    }

    /**
     * 获取List中泛型的类型
     */
    private Class<?> detectListClassType(Method method) throws DetectGenericTypeException {
        try {
            Type[] methodType = method.getGenericParameterTypes();
            Type[] genericType = ((ParameterizedType)methodType[0]).getActualTypeArguments();
            return (Class<?>)genericType[0];
        } catch (Exception e) {

            String em = String.format("在%s[%s]中，获取泛型类型失败", this.consumerClass.getClass().getName(),
                this.consumerMethod.getName());

            logger.error(em);

            throw new DetectGenericTypeException(em, e);
        }

    }

}
