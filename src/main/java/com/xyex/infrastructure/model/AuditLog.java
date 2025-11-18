package com.xyex.infrastructure.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 审计日志
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuditLog {

    private Long id;
    private String operator;
    private String operation;
    private String details;
    private LocalDateTime createTime;
}
