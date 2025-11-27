package com.xyex.entity.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.xyex.infrastructure.model.BasicField;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 商品评分表
 *
 * @author xujun
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("commodity_score")
@Schema(name = "CommodityScore", description = "商品评分实体类")
public class CommodityScore extends BasicField implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 商品评分 ID
     */
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    @Schema(description = "商品评分ID")
    private Long id;

    /**
     * 商品 ID
     */
    @Schema(description = "商品ID")
    private Long commodityId;

    /**
     * 用户 ID
     */
    @Schema(description = "用户ID")
    private Long userId;

    /**
     * 评分（0-5，星级评分）
     */
    @Schema(description = "评分（0-5，星级评分）")
    private Integer score;

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
