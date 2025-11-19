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
 * 私聊消息表
 *
 * @author xujun
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("private_message")
@Schema(name = "PrivateMessage", description = "私聊消息实体类")
public class PrivateMessage extends BasicField implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 消息 ID
     */
    @TableId(type = IdType.ASSIGN_ID)
    @Schema(description = "消息ID")
    private Long id;

    /**
     * 发送者 ID
     */
    @Schema(description = "发送者ID")
    private Long senderId;

    /**
     * 接收者 ID
     */
    @Schema(description = "接收者ID")
    private Long recipientId;

    /**
     * 消息内容(UTF8MB4 支持Emoji表情)
     */
    @Schema(description = "消息内容")
    private String content;

    /**
     * 0-未阅读 1-已阅读
     */
    @Schema(description = "是否已阅读：0-未阅读 1-已阅读")
    private Integer alreadyRead;

    /**
     * 消息发送类型（用户发送还是管理员发送,user Or admin)枚举
     */
    @Schema(description = "消息发送类型：user或admin")
    private String type;

    /**
     * 是否撤回  0-未撤回 1-已撤回
     */
    @Schema(description = "是否撤回：0-未撤回 1-已撤回")
    private Integer isRecalled;

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
