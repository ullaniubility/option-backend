package com.ulla.binance.config;

import java.util.concurrent.*;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;

import com.google.common.util.concurrent.ThreadFactoryBuilder;

/**
 * @author zhuyongdong
 * @Description TODO
 * @since 2023/2/14 17:52
 */
@EnableAsync
@Configuration
public class AsyncTaskConfig {

    /**
     * 1、corePoolSize：核心线程数 * 核心线程会一直存活，及时没有任务需要执行 * 当线程数小于核心线程数时，即使有线程空闲，线程池也会优先创建新线程处理 *
     * 设置allowCoreThreadTimeout=true（默认false）时，核心线程会超时关闭
     *
     * 2、queueCapacity：任务队列容量（阻塞队列） * 当核心线程数达到最大时，新任务会放在队列中排队等待执行
     *
     * 3、maxPoolSize：最大线程数 * 当线程数>=corePoolSize，且任务队列已满时。线程池会创建新线程来处理任务 * 当线程数=maxPoolSize，且任务队列已满时，线程池会拒绝处理任务而抛出异常
     *
     * 4、 keepAliveTime：线程空闲时间 * 当线程空闲时间达到keepAliveTime时，线程会退出，直到线程数量=corePoolSize *
     * 如果allowCoreThreadTimeout=true，则会直到线程数量=0
     *
     * 5、allowCoreThreadTimeout：允许核心线程超时 6、rejectedExecutionHandler：任务拒绝处理器 * 两种情况会拒绝处理任务： -
     * 当线程数已经达到maxPoolSize，切队列已满，会拒绝新任务 -
     * 当线程池被调用shutdown()后，会等待线程池里的任务执行完毕，再shutdown。如果在调用shutdown()和线程池真正shutdown之间提交任务，会拒绝新任务 *
     * 线程池会调用rejectedExecutionHandler来处理这个任务。如果没有设置默认是AbortPolicy，会抛出异常 * ThreadPoolExecutor类有几个内部实现类来处理这类情况： -
     * AbortPolicy 丢弃任务，抛运行时异常 - CallerRunsPolicy 执行任务 - DiscardPolicy 忽视，什么都不会发生 - DiscardOldestPolicy
     * 从队列中踢出最先进入队列（最后一个执行）的任务 * 实现RejectedExecutionHandler接口，可自定义处理器
     */

    /**
     * com.google.guava中的线程池
     * 
     * @return
     */
    @Bean("async-executor-guava")
    public Executor GuavaAsyncExecutor() {
        ThreadFactory threadFactory = new ThreadFactoryBuilder().setNameFormat("async-executor-guava").build();
        // 当前可用cpu数
        // 最佳线程数可通过计算得出http://ifeve.com/how-to-calculate-threadpool-size/
        int curSystemThreads = Runtime.getRuntime().availableProcessors() * 2;
        /**
         * int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue,
         * ThreadFactory threadFactory
         */
        ThreadPoolExecutor threadPool = new ThreadPoolExecutor(curSystemThreads, 100, 200, TimeUnit.SECONDS,
            new LinkedBlockingQueue<>(), threadFactory);
        // 允许核心线程超时
        threadPool.allowsCoreThreadTimeOut();
        return threadPool;
    }

}
