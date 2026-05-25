package com.express.yto.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * 账单计算异步任务线程池配置
 *
 * @author Detective
 * @date Created in 2026/5/24
 */
@Configuration
public class BillAsyncExecutorConfig {

    /**
     * 账单计算专用线程池
     *
     * 配置说明：
     * - 核心线程数：5（适合数据库连接池，避免耗尽连接）
     * - 最大线程数：10（高峰期可以扩展）
     * - 队列容量：100（缓冲任务，防止内存溢出）
     * - 线程名前缀：bill-async-（方便日志追踪）
     */
    @Bean(name = "asyncExecutor")
    public ThreadPoolTaskExecutor asyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();

        // 核心线程数
        executor.setCorePoolSize(5);

        // 最大线程数
        executor.setMaxPoolSize(10);

        // 队列容量
        executor.setQueueCapacity(100);

        // 线程名前缀
        executor.setThreadNamePrefix("bill-async-");

        // 拒绝策略：当线程池满时，由调用线程执行
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());

        // 初始化
        executor.initialize();

        return executor;
    }
}
