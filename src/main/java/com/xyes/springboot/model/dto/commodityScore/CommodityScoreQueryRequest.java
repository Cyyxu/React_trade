package com.xyes.springboot.model.dto.commodityScore;

import com.xyes.springboot.common.PageParam;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 查询商品评分表请求
 *
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class CommodityScoreQueryRequest extends PageParam {

    /**
     * 商品评分 ID
     */
    private Long id;

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

    private static final long serialVersionUID = 1L;
}