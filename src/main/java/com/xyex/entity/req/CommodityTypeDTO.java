package com.xyex.entity.req;

import com.xyex.infrastructure.model.PageParam;

import lombok.Data;

import com.xyex.entity.model.CommodityType;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;

@Data
public class CommodityTypeDTO extends PageParam {
    /**
     * 商品分类 ID
     */
    private Long id;

    /**
     * 商品类别名称
     */
    private String typeName;

    @Override
    public QueryWrapper<CommodityType> createQuery() {
        QueryWrapper<CommodityType> wrapper = new QueryWrapper<>();
        wrapper.eq("id", this.getId());
        return wrapper;
    }
}
