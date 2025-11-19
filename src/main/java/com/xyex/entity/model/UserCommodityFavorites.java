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
 * 用户商品收藏表
 *
 * @author xujun
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("user_commodity_favorites")
@Schema(name = "UserCommodityFavorites", description = "用户商品收藏实体类")
public class UserCommodityFavorites extends BasicField implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    @TableId(type = IdType.ASSIGN_ID)
    @Schema(description = "收藏ID")
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
     * 用户备注
     */
    @Schema(description = "用户备注")
    private String remark;

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
     * 1-正常收藏 0-取消收藏
     */
    @Schema(description = "状态：1-正常收藏 0-取消收藏")
    private String status;

    /**
     * 是否删除
     */
    @Schema(description = "是否删除")
    private Integer isDelete;
}
