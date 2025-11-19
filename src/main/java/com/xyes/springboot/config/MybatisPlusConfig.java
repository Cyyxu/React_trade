package com.xyes.springboot.config;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.baomidou.mybatisplus.core.incrementer.IdentifierGenerator;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MybatisPlusConfig implements IdentifierGenerator, MetaObjectHandler {

    // 方法2：雪花算法简化版
    private static final long START_TIMESTAMP = 1640995200000L; // 2022-01-01 00:00:00
    private static final long MACHINE_ID_BITS = 5L;
    private static final long SEQUENCE_BITS = 12L;
    private static final long MAX_SEQUENCE = ~(-1L << SEQUENCE_BITS);
    private static final long MACHINE_ID_SHIFT = SEQUENCE_BITS;
    private static final long TIMESTAMP_SHIFT = SEQUENCE_BITS + MACHINE_ID_BITS;

    private static long sequence = 0L;
    private static long lastTimestamp = -1L;

    /**
     * 雪花算法生成ID（适合分布式环境）
     * 41位时间戳 + 5位机器ID + 12位序列号 = 58位（会超过16位，需要截取）
     */
    private synchronized Long generateSnowflakeId() {
        long timestamp = System.currentTimeMillis();

        if (timestamp < lastTimestamp) {
            throw new RuntimeException("时钟回拨异常");
        }

        if (timestamp == lastTimestamp) {
            sequence = (sequence + 1) & MAX_SEQUENCE;
            if (sequence == 0) {
                timestamp = getNextMillis(lastTimestamp);
            }
        } else {
            sequence = 0L;
        }

        lastTimestamp = timestamp;

        // 机器ID，可配置
        long machineId = 1L;
        long id = ((timestamp - START_TIMESTAMP) << TIMESTAMP_SHIFT) |
                (machineId << MACHINE_ID_SHIFT) |
                sequence;

        // 截取到16位以内（取绝对值并限制在16位内）
        return Math.abs(id) % 10000000000000000L;
    }

    private static long getNextMillis(long lastTimestamp) {
        long timestamp = System.currentTimeMillis();
        while (timestamp <= lastTimestamp) {
            timestamp = System.currentTimeMillis();
        }
        return timestamp;
    }

    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        // 添加分页插件
        interceptor.addInnerInterceptor(new PaginationInnerInterceptor(DbType.MYSQL));
        return interceptor;
    }

    @Override
    public Long nextId(Object entity) {
        return this.generateSnowflakeId();
    }

    @Override
    public void insertFill(MetaObject metaObject) {
        // no-op: 项目未统一定义自动填充字段
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        // no-op: 项目未统一定义自动填充字段
    }
}