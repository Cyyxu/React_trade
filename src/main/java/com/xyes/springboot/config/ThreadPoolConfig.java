package com.xyes.springboot.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;

@Slf4j
@Configuration
@EnableAsync
public class ThreadPoolConfig {

    /**
     * 审计日志异步线程池
     * 根据CPU核心数动态配置线程池参数
     */
    @Bean("auditLogExecutor")
    public ThreadPoolTaskExecutor auditLogExecutor() {
        // 获取CPU核心数
        int cpuCores = Runtime.getRuntime().availableProcessors();
        ThreadPoolTaskExecutor executor = createExecutor(
                cpuCores,
                cpuCores * 2,
                cpuCores * 10,
                60,
                "audit-log-",
                new ThreadPoolExecutor.CallerRunsPolicy(),
                60
        );
        log.info("审计日志线程池配置完成 - 核心线程数: {}, 最大线程数: {}, 队列容量: {}", cpuCores, cpuCores * 2, cpuCores * 10);
        return executor;
    }

    /**
     * 通用异步任务线程池
     * 用于其他异步任务
     */
    @Bean("businessAsyncExecutor")
    public ThreadPoolTaskExecutor businessAsyncExecutor() {
        int cpuCores = Runtime.getRuntime().availableProcessors();
        int corePoolSize = Math.max(1, cpuCores / 2);
        int queueCapacity = corePoolSize * 20;
        ThreadPoolTaskExecutor executor = createExecutor(
                corePoolSize,
                cpuCores,
                queueCapacity,
                300,
                "business-async-",
                new ThreadPoolExecutor.DiscardOldestPolicy(),
                120
        );
        log.info("通用异步线程池配置完成 - 核心线程数: {}, 最大线程数: {}, 队列容量: {}", corePoolSize, cpuCores, queueCapacity);
        return executor;
    }

    private ThreadPoolTaskExecutor createExecutor(int corePoolSize,
                                                  int maxPoolSize,
                                                  int queueCapacity,
                                                  int keepAliveSeconds,
                                                  String threadNamePrefix,
                                                  RejectedExecutionHandler rejectedExecutionHandler,
                                                  int awaitTerminationSeconds) {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(corePoolSize);
        executor.setMaxPoolSize(maxPoolSize);
        executor.setQueueCapacity(queueCapacity);
        executor.setKeepAliveSeconds(keepAliveSeconds);
        executor.setThreadNamePrefix(threadNamePrefix);
        executor.setRejectedExecutionHandler(rejectedExecutionHandler);
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.setAwaitTerminationSeconds(awaitTerminationSeconds);
        executor.initialize();
        return executor;
    }
}