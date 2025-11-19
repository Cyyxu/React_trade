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
 * 商品订单表
 *
 * @author xujun
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("commodity_order")
@Schema(name = "CommodityOrder", description = "商品订单实体类")
public class CommodityOrder extends BasicField implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 订单 ID
     */
    @TableId(type = IdType.ASSIGN_ID)
    @Schema(description = "订单ID")
    private Long id;

    /**
     * 用户 ID
     */
    @Schema(description = "用户ID")
    private Long userId;

    /**
     * 商品 ID
     */
    @Schema(description = "商品ID")
    private Long commodityId;

    /**
     * 订单备注
     */
    @Schema(description = "订单备注")
    private String remark;

    /**
     * 购买数量
     */
    @Schema(description = "购买数量")
    private Integer buyNumber;

    /**
     * 订单总支付金额
     */
    @Schema(description = "订单总支付金额")
    private BigDecimal paymentAmount;

    /**
     * 0-未支付 1-已支付
     */
    @Schema(description = "支付状态：0-未支付 1-已支付")
    private Integer payStatus;

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
