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
 * 商品分类表
 *
 * @author xujun
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("commodity_type")
@Schema(name = "CommodityType", description = "商品分类实体类")
public class CommodityType extends BasicField implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 商品分类 ID
     */
    @TableId(type = IdType.ASSIGN_ID)
    @Schema(description = "商品分类ID")
    private Long id;

    /**
     * 商品类别名称
     */
    @Schema(description = "商品类别名称")
    private String typeName;

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
