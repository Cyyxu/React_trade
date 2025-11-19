package com.xyes.springboot.common;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
@Component
@Data
@ConfigurationProperties(prefix = "jwt")
public class JwtProperties {
    /**
     * JWT存储的请求头
     */
    private String tokenHeader;
    /**
     * jwt加解密使用的密钥
     */
    private String secret;
    /**
     * JWT的超时时间
     */
    private long expiration;
    /**
     * JWT负载中拿到的开头
     */
    private String tokenHead;
}
