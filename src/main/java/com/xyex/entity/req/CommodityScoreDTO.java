package com.xyex.entity.req;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.xyex.infrastructure.model.PageParam;

import lombok.Data;

@Data
public class CommodityScoreDTO extends PageParam{

    /**
     * 商品 ID
     */
    private Long commodityId;

    /**
     * 用户 ID
     */
    private Long userId;

    /**
     * 评分（0-5，星级评分）
     */
    private Integer score;

    public QueryWrapper createQuery() {
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("commodity_id", this.getCommodityId());
        return queryWrapper;
    }
}