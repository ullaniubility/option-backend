package com.ulla.rocketmq.configuration;

/**
 * @author zhuyongdong
 * @Description
 * @since 2023/3/7 14:55
 */
public interface ExceptionIgnore {

    /**
     * @param throwable
     *            异常
     * @return {@code true} 忽略异常 {@code false} 其他
     */
    boolean ignorable(Throwable throwable);

}
