package com.xyex.entity.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.xyex.infrastructure.model.BasicField;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 商品表
 *
 * @author xujun
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("commodity")
@Schema(name = "Commodity", description = "商品实体类")
public class Commodity extends BasicField implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 商品 ID
     */
    @TableId(type = IdType.ASSIGN_ID)
    @Schema(description = "商品ID")
    private Long id;

    /**
     * 商品名称
     */
    @Schema(description = "商品名称")
    private String commodityName;

    /**
     * 商品简介
     */
    @Schema(description = "商品简介")
    private String commodityDescription;

    /**
     * 商品封面图
     */
    @Schema(description = "商品封面图")
    private String commodityAvatar;

    /**
     * 商品新旧程度（例如 9成新）
     */
    @Schema(description = "商品新旧程度")
    private String degree;

    /**
     * 商品分类 ID
     */
    @Schema(description = "商品分类ID")
    private Long commodityTypeId;

    /**
     * 管理员 ID （某人创建该商品）
     */
    @Schema(description = "管理员ID")
    private Long adminId;

    /**
     * 是否上架（默认0未上架，1已上架）
     */
    @Schema(description = "是否上架：0-未上架 1-已上架")
    private Integer isListed;

    /**
     * 商品数量（默认0）
     */
    @Schema(description = "商品库存数量")
    private Integer commodityInventory;

    /**
     * 商品价格
     */
    @Schema(description = "商品价格")
    private BigDecimal price;

    /**
     * 商品浏览量
     */
    @Schema(description = "商品浏览量")
    private Integer viewNum;

    /**
     * 商品收藏量
     */
    @Schema(description = "商品收藏量")
    private Integer favourNum;

    /**
     * 创建时间
     */
    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @Schema(description = "更新时间")
    private LocalDateTime updateTime;

    /**
     * 是否删除
     */
    @Schema(description = "是否删除")
    private Integer isDelete;
}
