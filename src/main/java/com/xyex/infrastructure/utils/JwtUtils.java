package com.xyex.infrastructure.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * JWT工具类
 *
 * @author xujun
 */
@Component
public class JwtUtils {

    @Value("${jwt.tokenHeader}")
    private String tokenHeader;

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private Long expiration;

    @Value("${jwt.tokenHead}")
    private String tokenHead;

    /**
     * 生成JWT token
     *
     * @param userId 用户ID
     * @param userAccount 用户账号
     * @return JWT token
     */
    public String generateToken(Long userId, String userAccount) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId);
        claims.put("userAccount", userAccount);
        return createToken(claims, String.valueOf(userId));
    }

    /**
     * 创建token
     *
     * @param claims 声明
     * @param subject 主题
     * @return JWT token
     */
    private String createToken(Map<String, Object> claims, String subject) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expiration);

        SecretKey key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));

        return Jwts.builder()
                .claims(claims)
                .subject(subject)
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(key)
                .compact();
    }

    /**
     * 解析JWT token
     *
     * @param token JWT token
     * @return Claims
     */
    public Claims parseToken(String token) {
        SecretKey key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    /**
     * 验证token是否有效
     *
     * @param token JWT token
     * @return 是否有效
     */
    public boolean validateToken(String token) {
        try {
            parseToken(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 从token中获取用户ID
     *
     * @param token JWT token
     * @return 用户ID
     */
    public Long getUserIdFromToken(String token) {
        Claims claims = parseToken(token);
        return ((Number) claims.get("userId")).longValue();
    }

    /**
     * 从token中获取用户账号
     *
     * @param token JWT token
     * @return 用户账号
     */
    public String getUserAccountFromToken(String token) {
        Claims claims = parseToken(token);
        return (String) claims.get("userAccount");
    }

    /**
     * 获取token请求头
     *
     * @return token请求头
     */
    public String getTokenHeader() {
        return tokenHeader;
    }

    /**
     * 获取token前缀
     *
     * @return token前缀
     */
    public String getTokenHead() {
        return tokenHead;
    }

    /**
     * 获取token过期时间戳
     *
     * @param token JWT token
     * @return 过期时间戳（毫秒）
     */
    public Long getTokenExpirationTime(String token) {
        Claims claims = parseToken(token);
        Date expirationDate = claims.getExpiration();
        return expirationDate != null ? expirationDate.getTime() : null;
    }
}
