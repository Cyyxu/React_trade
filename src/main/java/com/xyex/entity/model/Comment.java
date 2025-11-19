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
 * 评论表
 *
 * @author xujun
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("comment")
@Schema(name = "Comment", description = "评论实体类")
public class Comment extends BasicField implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 评论 ID
     */
    @TableId(type = IdType.ASSIGN_ID)
    @Schema(description = "评论ID")
    private Long id;

    /**
     * 面经帖子 ID
     */
    @Schema(description = "帖子ID")
    private Long postId;

    /**
     * 用户 ID
     */
    @Schema(description = "用户ID")
    private Long userId;

    /**
     * 评论内容
     */
    @Schema(description = "评论内容")
    private String content;

    /**
     * 父评论 ID，支持多级嵌套回复
     */
    @Schema(description = "父评论ID")
    private Long parentId;

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

    /**
     * 祖先评论ID
     */
    @Schema(description = "祖先评论ID")
    private Long ancestorId;
}
