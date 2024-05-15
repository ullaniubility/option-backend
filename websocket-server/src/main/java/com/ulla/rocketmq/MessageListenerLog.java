package com.ulla.rocketmq;

import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @Description
 * @Author galaxy-captain
 * @Date 2019-06-24 15:09
 **/
@Aspect
public class MessageListenerLog {

    private static final Logger logger = LoggerFactory.getLogger(MessageListenerLog.class);

    @Pointcut("@annotation(com.ulla.rocketmq.LogAspect)")
    public void cutLogAspect() {

    }

    public MessageListenerLog() {

    }

    @Before("cutLogAspect()")
    public void before() {
        // logger.info("开始消费");
    }

    @After("cutLogAspect()")
    public void after() {
        // logger.info("结束消费");
    }
}
