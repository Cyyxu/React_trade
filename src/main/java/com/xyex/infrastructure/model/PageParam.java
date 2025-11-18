package com.xyex.infrastructure.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 分页参数
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PageParam {

    private Integer pageNum = 1;
    private Integer pageSize = 10;
}
