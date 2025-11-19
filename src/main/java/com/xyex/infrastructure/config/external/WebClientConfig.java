package com.xyex.infrastructure.config.external;

import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;
import reactor.netty.resources.ConnectionProvider;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

/**
 * WebClient配置（响应式，非阻塞）
 * 优点：高性能、支持响应式编程、内存占用小
 * 缺点：学习曲线较陡、需要理解响应式编程
 */
@Slf4j
@Configuration
public class WebClientConfig {

    @Bean
    public WebClient webClient() {
        // 配置连接池
        ConnectionProvider provider = ConnectionProvider.builder("custom")
                .maxConnections(500)
                .maxIdleTime(Duration.ofSeconds(20))
                .maxLifeTime(Duration.ofSeconds(60))
                .pendingAcquireTimeout(Duration.ofSeconds(60))
                .evictInBackground(Duration.ofSeconds(120))
                .build();

        // 配置HttpClient：调整超时设置以适配流式响应
        HttpClient httpClient = HttpClient.create(provider)
                // 连接超时：5秒（建立TCP连接的超时，保持不变）
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 60000)
                // 全局响应超时：3分钟（整个请求的最大耗时，根据业务调整）
                .responseTimeout(Duration.ofMinutes(3))
                .doOnConnected(conn ->
                        conn.addHandlerLast(new ReadTimeoutHandler(240, TimeUnit.SECONDS))  // 读超时：30秒（允许分片间隔更长）
                                .addHandlerLast(new WriteTimeoutHandler(60, TimeUnit.SECONDS)));  // 写超时：保持10秒

        return WebClient.builder()
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .codecs(configurer -> configurer
                        .defaultCodecs()
                        .maxInMemorySize(10 * 1024 * 1024) // 核心配置：扩大缓冲区到10MB
                )
                .filter(logRequest())
                .filter(logResponse())
                .build();
    }

    // 请求日志
    private ExchangeFilterFunction logRequest() {
        return ExchangeFilterFunction.ofRequestProcessor(Mono::just);
    }

    // 响应日志
    private ExchangeFilterFunction logResponse() {
        return ExchangeFilterFunction.ofResponseProcessor(clientResponse -> {
            if (clientResponse.statusCode().value() / 100 != 2) {
                log.info("Response status: {}", clientResponse.statusCode());
            }
            return Mono.just(clientResponse);
        });
    }
}