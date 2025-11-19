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
 * 用户AI消息表
 *
 * @author xujun
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("user_ai_message")
@Schema(name = "UserAiMessage", description = "用户AI消息实体类")
public class UserAiMessage extends BasicField implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    @TableId(type = IdType.ASSIGN_ID)
    @Schema(description = "消息ID")
    private Long id;

    /**
     * 用户输入
     */
    @Schema(description = "用户输入")
    private String userInputText;

    /**
     * AI生成的文本
     */
    @Schema(description = "AI生成的文本")
    private String aiGenerateText;

    /**
     * 用户ID
     */
    @Schema(description = "用户ID")
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
     * 逻辑删除
     */
    @Schema(description = "逻辑删除")
    private Integer isDelete;
}
