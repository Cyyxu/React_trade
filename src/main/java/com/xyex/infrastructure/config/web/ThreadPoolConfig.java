package com.xyex.infrastructure.config.web;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;
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
    public Executor auditLogExecutor() {
        // 获取CPU核心数
        int cpuCores = Runtime.getRuntime().availableProcessors();
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        // 核心线程数 = CPU核心数
        executor.setCorePoolSize(cpuCores);
        // 最大线程数 = CPU核心数 * 2 (适合I/O密集型任务)
        executor.setMaxPoolSize(cpuCores * 2);
        // 队列容量 = 核心线程数 * 10 (避免频繁创建销毁线程)
        executor.setQueueCapacity(cpuCores * 10);
        // 线程空闲时间 = 60秒
        executor.setKeepAliveSeconds(60);
        // 线程名称前缀
        executor.setThreadNamePrefix("audit-log-");
        // 拒绝策略：调用者运行策略，保证任务不丢失
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        // 等待所有任务结束后再关闭线程池
        executor.setWaitForTasksToCompleteOnShutdown(true);
        // 等待时间 = 60秒
        executor.setAwaitTerminationSeconds(60);
        // 初始化线程池
        executor.initialize();
        log.info("审计日志线程池配置完成 - 核心线程数: {}, 最大线程数: {}, 队列容量: {}", cpuCores, cpuCores * 2, cpuCores * 10);
        return executor;
    }

    /**
     * 通用异步任务线程池
     * 用于其他异步任务
     */
    @Bean("businessAsyncExecutor")
    public Executor businessAsyncExecutor() {
        int cpuCores = Runtime.getRuntime().availableProcessors();
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        // 核心线程数 = CPU核心数 / 2
        executor.setCorePoolSize(Math.max(1, cpuCores / 2));
        // 最大线程数 = CPU核心数
        executor.setMaxPoolSize(cpuCores);
        // 队列容量 = 核心线程数 * 20
        executor.setQueueCapacity(Math.max(1, cpuCores / 2) * 20);
        // 线程空闲时间 = 300秒
        executor.setKeepAliveSeconds(300);
        // 线程名称前缀
        executor.setThreadNamePrefix("business-async-");
        // 拒绝策略：丢弃最老的任务
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.DiscardOldestPolicy());
        // 等待所有任务结束后再关闭线程池
        executor.setWaitForTasksToCompleteOnShutdown(true);
        // 等待时间 = 120秒
        executor.setAwaitTerminationSeconds(120);
        // 初始化线程池
        executor.initialize();
        log.info("通用异步线程池配置完成 - 核心线程数: {}, 最大线程数: {}, 队列容量: {}", Math.max(1, cpuCores / 2), cpuCores, Math.max(1, cpuCores / 2) * 20);
        return executor;
    }


}