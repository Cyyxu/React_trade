package com.xyex.entity.req;

import java.math.BigDecimal;

import com.alibaba.excel.util.StringUtils;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import com.xyex.infrastructure.model.PageParam;

import lombok.Data;
import io.swagger.v3.oas.annotations.media.Schema;

@Data
@Schema(description = "商品订单DTO")
public class CommodityOrderDTO extends PageParam {
 
    /**
     * 用户 ID
     */
    private Long userId;

    /**
     * 商品 ID
     */
    private Long commodityId;

    /**
     * 订单备注
     */
    private String remark;

    /**
     * 购买数量
     */
    private Integer buyNumber;

    /**
     * 订单总支付金额
     */
    private BigDecimal paymentAmount;

    /**
     * 0-未支付 1-已支付
     */
    private Integer payStatus;

    private static final long serialVersionUID = 1L;

    @Override
    public QueryWrapper createQuery() {
        QueryWrapper queryWrapper = new QueryWrapper();
         // 模糊查询
        queryWrapper.like(StringUtils.isNotBlank(remark), "remark", remark);
        // 精确查询
        queryWrapper.eq(ObjectUtils.isNotEmpty(payStatus), "payStatus", payStatus);
        queryWrapper.eq(ObjectUtils.isNotEmpty(buyNumber), "buyNumber", buyNumber);
        queryWrapper.eq(ObjectUtils.isNotEmpty(commodityId), "commodityId", commodityId);
        queryWrapper.eq(ObjectUtils.isNotEmpty(userId), "userId", userId);
       
        return queryWrapper;
    }

}
