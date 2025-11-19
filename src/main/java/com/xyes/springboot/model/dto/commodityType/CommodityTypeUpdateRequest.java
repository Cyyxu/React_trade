package com.xyes.springboot.model.dto.commodityType;

import lombok.Data;

import java.io.Serializable;


@Data
public class CommodityTypeUpdateRequest implements Serializable {
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