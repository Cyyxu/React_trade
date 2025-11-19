package com.xyes.springboot.model.dto.commodityType;

import com.xyes.springboot.common.PageParam;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 查询商品类别表请求
 *
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class CommodityTypeQueryRequest extends PageParam {

    /**
     * 商品分类 ID
     */
    private Long id;

    /**
     * 商品类别名称
     */
    private String typeName;

    private static final long serialVersionUID = 1L;
}