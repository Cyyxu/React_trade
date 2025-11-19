package com.xyes.springboot.model.dto.userCommodityFavorites;

import lombok.Data;

import java.io.Serializable;

/**
 * 创建用户商品收藏表请求
 */
@Data
public class UserCommodityFavoritesAddRequest implements Serializable {


    /**
     * 用户 ID
     */
    private Long userId;

    /**
     * 商品 ID
     */
    private Long commodityId;

    /**
     * 1-正常收藏 0-取消收藏
     */
    private Integer status;

    /**
     * 用户备注
     */
    private String remark;



    private static final long serialVersionUID = 1L;
}