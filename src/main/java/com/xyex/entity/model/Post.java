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
 * 帖子表
 *
 * @author xujun
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("post")
@Schema(name = "Post", description = "帖子实体类")
public class Post extends BasicField implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    @TableId(type = IdType.ASSIGN_ID)
    @Schema(description = "帖子ID")
    private Long id;

    /**
     * 标题
     */
    @Schema(description = "标题")
    private String title;

    /**
     * 内容
     */
    @Schema(description = "内容")
    private String content;

    /**
     * 标签列表（json 数组）
     */
    @Schema(description = "标签列表（json数组）")
    private String tags;

    /**
     * 点赞数
     */
    @Schema(description = "点赞数")
    private Integer thumbNum;

    /**
     * 收藏数
     */
    @Schema(description = "收藏数")
    private Integer favourNum;

    /**
     * 创建用户 id
     */
    @Schema(description = "创建用户ID")
    private Long userId;

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
