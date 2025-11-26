package com.xyex.entity.req;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.xyex.entity.model.UserCommodityFavorite;
import com.xyex.infrastructure.model.PageParam;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 用户商品收藏请求
 */
@Data
@Schema(name = "CommodityFavoriteDTO", description = "用户收藏商品请求")
public class CommodityFavoriteDTO extends PageParam {

    /**
     * ID
     */
    @Schema(description = "ID")
    private Long id;
    /**
     * 用户ID
     */
    @Schema(description = "用户ID")
    private Long userId;

    /**
     * 商品ID
     */
    @Schema(description = "商品ID")
    private Long commodityId;
    
    /**
     * 1-正常收藏 0-取消收藏
     */
    private Integer status;

    /**
     * 备注
     */
    @Schema(description = "备注")
    private String remark;

    @Override
    public QueryWrapper<UserCommodityFavorite> createQuery() {
        QueryWrapper<UserCommodityFavorite> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", this.getUserId());
        return wrapper;
    }

}
