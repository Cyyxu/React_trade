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
 * 公告表
 *
 * @author xujun
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("notice")
@Schema(name = "Notice", description = "公告实体类")
public class Notice extends BasicField implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    @TableId(type = IdType.ASSIGN_ID)
    @Schema(description = "公告ID")
    private Long id;

    /**
     * 公告标题
     */
    @Schema(description = "公告标题")
    private String noticeTitle;

    /**
     * 公告内容
     */
    @Schema(description = "公告内容")
    private String noticeContent;

    /**
     * 创建人id（管理员）
     */
    @Schema(description = "创建人ID（管理员）")
    private Long noticeAdminId;

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
