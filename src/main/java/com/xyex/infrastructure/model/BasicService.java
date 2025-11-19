package com.xyex.infrastructure.model;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

public interface BasicService<T extends BasicField> extends IService<T> {

    /**
     * 列表查询(内置)
     *
     * @param p 查询参数
     * @return 结果列表
     */
    <P extends PageParam> Page<T> page(P p);

}
