package com.xyex.infrastructure.model;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

public abstract class BasicServiceImpl<M extends BaseMapper<T>, T extends BasicField> extends ServiceImpl<M, T> implements BasicService<T> {

    @SuppressWarnings("unchecked")
    @Override
    public <P extends PageParam> Page<T> page(P p) {
        return getBaseMapper().selectPage(p.createPage(), p.createQuery());
    }
}
