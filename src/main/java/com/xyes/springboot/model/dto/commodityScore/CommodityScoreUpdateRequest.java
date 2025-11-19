package com.xyes.springboot.model.dto.commodityScore;

import lombok.Data;

import java.io.Serializable;


@Data
public class CommodityScoreUpdateRequest implements Serializable {
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