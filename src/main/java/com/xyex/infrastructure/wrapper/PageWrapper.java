package com.xyex.infrastructure.wrapper;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 分页包装器
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PageWrapper<T> {

    private Integer pageNum;
    private Integer pageSize;
    private Long total;
    private List<T> list;
}
