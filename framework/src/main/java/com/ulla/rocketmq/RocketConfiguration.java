package com.ulla.rocketmq;

import org.apache.rocketmq.acl.common.AclClientRPCHook;

import com.ulla.rocketmq.configuration.ConsumerConfig;
import com.ulla.rocketmq.configuration.ExceptionIgnore;

/**
 * @author zhuyongdong
 * @Description rocketMq自动装配实现
 * @since 2023/3/7 14:55
 */
public class RocketConfiguration {

    private String nameServer = ConsumerConfig.DEFAULT_NAME_SERVER;

    private int retryTimes = 16;

    private int delayTimeLevel = 0;

    private int suspendTimeMillis = -1;

    private Class<? extends Throwable>[] ignorableExceptions = new Class[0];

    private Class<? extends ExceptionIgnore>[] exceptionIgnores = new Class[0];

    private AclClientRPCHook aclHook;

    public RocketConfiguration() {

    }

    public String getNameServer() {
        return nameServer;
    }

    public void setNameServer(String nameServer) {
        this.nameServer = nameServer;
    }

    public int getRetryTimes() {
        return retryTimes;
    }

    public void setRetryTimes(int retryTimes) {
        this.retryTimes = retryTimes;
    }

    public int getDelayTimeLevel() {
        return delayTimeLevel;
    }

    public void setDelayTimeLevel(int delayTimeLevel) {
        this.delayTimeLevel = delayTimeLevel;
    }

    public int getSuspendTimeMillis() {
        return suspendTimeMillis;
    }

    public void setSuspendTimeMillis(int suspendTimeMillis) {
        this.suspendTimeMillis = suspendTimeMillis;
    }

    public Class<? extends Throwable>[] getIgnorableExceptions() {
        return ignorableExceptions;
    }

    public void setIgnorableExceptions(Class<? extends Throwable>[] ignorableExceptions) {
        this.ignorableExceptions = ignorableExceptions;
    }

    public Class<? extends ExceptionIgnore>[] getExceptionIgnores() {
        return exceptionIgnores;
    }

    public void setExceptionIgnores(Class<? extends ExceptionIgnore>[] exceptionIgnores) {
        this.exceptionIgnores = exceptionIgnores;
    }

    public AclClientRPCHook getAclHook() {
        return aclHook;
    }

    public void setAclHook(AclClientRPCHook aclHook) {
        this.aclHook = aclHook;
    }

}
