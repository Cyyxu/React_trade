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
 * 帖子点赞表
 *
 * @author xujun
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("post_thumb")
@Schema(name = "PostThumb", description = "帖子点赞实体类")
public class PostThumb extends BasicField implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    @TableId(type = IdType.ASSIGN_ID)
    @Schema(description = "点赞ID")
    private Long id;

    /**
     * 帖子 id
     */
    @Schema(description = "帖子ID")
    private Long postId;

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
}
