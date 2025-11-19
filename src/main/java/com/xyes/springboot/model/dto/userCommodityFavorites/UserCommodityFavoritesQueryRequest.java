package com.xyes.springboot.model.dto.userCommodityFavorites;

import com.xyes.springboot.common.PageParam;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 查询用户商品收藏表请求
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class UserCommodityFavoritesQueryRequest extends PageParam {


    /**
     *
     */
    private Long id;

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