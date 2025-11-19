package com.xyes.springboot.common;


import com.xyes.springboot.model.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * JWT工具类
 * 提供JWT Token的生成、解析和验证功能
 *
 * @author xujun
 */
@Component
@RequiredArgsConstructor
public class JwtKit {
    private final JwtProperties jwtProperties;
    /**
     * 生成Token
     * @param  user 自定义要存储的用户对象信息
     * @return string(Token)
     */
    public String generateToken(User user) {
        Map<String, Object> claims = new HashMap<String, Object>(10);
        claims.put("username", user.toString());
        claims.put("createdate", new Date());
        claims.put("id", System.currentTimeMillis());

        // 获取有效的密钥字节数组
        byte[] keyBytes = getValidKeyBytes();

        // 使用字节数组而不是字符串
        return Jwts.builder().addClaims(claims)
                .setExpiration(new Date(System.currentTimeMillis() + jwtProperties.getExpiration()))
                .signWith(SignatureAlgorithm.HS256, keyBytes)  // 修改这里
                .compact();
    }

    // 添加获取有效密钥字节数组的方法
    private byte[] getValidKeyBytes() {
        String secret = jwtProperties.getSecret();

        // 如果密钥为空，使用默认值
        if (secret == null || secret.trim().isEmpty()) {
            secret = "default_secret_key_with_sufficient_length_at_least_32_characters_123456";
        }

        // 确保密钥长度足够（HS256推荐至少32字节）
        if (secret.length() < 32) {
            // 扩展密钥到足够长度
            StringBuilder sb = new StringBuilder(secret);
            while (sb.length() < 32) {
                sb.append("0");
            }
            secret = sb.toString();
        }

        return secret.getBytes(java.nio.charset.StandardCharsets.UTF_8);
    }

    /**
     * 校验Token是否合法
     *
     * @param token 要校验的Token
     * @return Claims (过期时间，用户信息，创建时间)
     */
    public  Claims parseJwtToken(String token) {
        Claims claims = null;
        // 根据哪个密钥解密
        claims = Jwts.parser().setSigningKey(jwtProperties.getSecret())
                // 设置要解析的Token
                .parseClaimsJws(token)
                .getBody();
        return claims;
    }
}
