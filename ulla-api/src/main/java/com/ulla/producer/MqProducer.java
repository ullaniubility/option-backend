package com.ulla.producer;

import lombok.Data;

/**
 * @author zhuyongdong
 * @Description 生产者模型
 * @since 2023/2/11 17:01
 */
@Data
public class MqProducer {

    /**
     * 生产者组名，规定在一个应用里面必须唯一
     */
    String groupName;

    /**
     * 生产者实例的命名空间。可以理解为 MQ生产者的名称
     */
    String producerId;

    /**
     * nameServer地址，分号分割
     */
    String namesrvAddr;

    /**
     * 消息最大长度 默认 1024 * 4 (4M)
     */
    Integer maxMessageSize;

    /**
     * 是否开启消息追踪
     */
    String enableMsgTrace;

    /**
     * 消息追踪日志使用的队列名字
     */
    String customizedTraceTopic;

    /**
     * 异步消息发送失败重试的次数
     */
    String retryTimesWhenSendAsyncFailed;

    /**
     * 同步消息发送失败重试次数
     */
    Integer retryTimesWhenSendFailed;

    /**
     * 消息发送的超时时间，默认为3000ms
     */
    Integer sendMsgTimeOut;

}
