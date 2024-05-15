package com.ulla.rocketmq;

import java.lang.reflect.Method;
import java.util.Map;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.core.MethodIntrospector;

import com.ulla.rocketmq.annotation.RocketConsumer;
import com.ulla.rocketmq.annotation.RocketListener;

/**
 * @author zhuyongdong
 * @Description 工厂方法提取
 * @since 2023/3/7 14:55
 */
public class RocketAnnotationDetector {

    public static Map<Method, RocketListener> rocketListenerAnnotatedMethod(Class<?> beanClass) {

        return MethodIntrospector.selectMethods(beanClass, new MethodIntrospector.MetadataLookup<RocketListener>() {
            @Override
            public RocketListener inspect(Method method) {
                return method.getAnnotation(RocketListener.class);
            }
        });

    }

    public static RocketConsumer getRocketMQAnnotation(Object object) {

        if (object == null) {
            return null;
        }

        return object.getClass().getAnnotation(RocketConsumer.class);
    }

    public static RocketConfiguration getRocketConfiguration(BeanFactory beanFactory) {

        RocketConfiguration configuration = null;

        try {
            configuration = beanFactory.getBean(RocketConfiguration.class);
        } catch (BeansException e) {
            // do nothing
        }

        return configuration;

    }

}
