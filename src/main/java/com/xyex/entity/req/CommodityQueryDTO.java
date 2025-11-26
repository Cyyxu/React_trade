package com.xyex.entity.req;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xyex.entity.model.Commodity;
import com.xyex.infrastructure.model.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 商品查询DTO
 * 支持关键词查询（商品名称、简介）、分类查询、价格范围查询
 *
 * @author xujun
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(name = "CommodityQueryDTO", description = "商品查询DTO")
public class CommodityQueryDTO extends PageParam {

    /**
     * 商品ID
     */
    @Schema(description = "商品ID")
    private Long id;

    /**
     * 商品分类ID
     */
    @Schema(description = "商品分类ID")
    private Long commodityTypeId;

    /**
     * 最小价格
     */
    @Schema(description = "最小价格")
    private java.math.BigDecimal minPrice;

    /**
     * 最大价格
     */
    @Schema(description = "最大价格")
    private java.math.BigDecimal maxPrice;

    /**
     * 是否上架（0-未上架 1-已上架）
     */
    @Schema(description = "是否上架：0-未上架 1-已上架")
    private Integer isListed;

    @Override
    public Wrapper<Commodity> createQuery() {
        LambdaQueryWrapper<Commodity> wrapper = new LambdaQueryWrapper<>();

        // 关键词查询（商品名称、简介）
        String keyword = this.getKeyword();
        if (keyword != null && !keyword.isEmpty()) {
            wrapper.and(w -> w
                    .like(Commodity::getCommodityName, keyword)
                    .or().like(Commodity::getCommodityDescription, keyword)
            );
        }

        // 按商品ID查询
        if (id != null) {
            wrapper.eq(Commodity::getId, id);
        }

        // 按分类查询
        if (commodityTypeId != null) {
            wrapper.eq(Commodity::getCommodityTypeId, commodityTypeId);
        }

        // 按价格范围查询
        if (minPrice != null) {
            wrapper.ge(Commodity::getPrice, minPrice);
        }
        if (maxPrice != null) {
            wrapper.le(Commodity::getPrice, maxPrice);
        }

        // 按上架状态查询
        if (isListed != null) {
            wrapper.eq(Commodity::getIsListed, isListed);
        }

        // 排除已删除的商品
        wrapper.eq(Commodity::getIsDelete, 0);

        // 排序：按创建时间倒序
        wrapper.orderByDesc(Commodity::getCreateTime);

        return wrapper;
    }
}
