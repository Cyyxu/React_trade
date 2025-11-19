package com.xyex.infrastructure.model;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableLogic;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Setter
@Schema(name = "BaseField", description = "基础字段")
public class BasicField implements Serializable {

    @Schema(description = "创建人ID标识")
    @TableField(value = "created_user_id", fill = FieldFill.INSERT)
    private String createdUserId;

    @Schema(description = "修改ID标识")
    @TableField(value = "created_user_name", fill = FieldFill.INSERT)
    private String createdUserName;

    @Schema(description = "创建人名称")
    @TableField(value = "update_user_id", fill = FieldFill.INSERT_UPDATE)
    private String updateUserId;

    @Schema(description = "创建人名称")
    @TableField(value = "update_user_name", fill = FieldFill.INSERT_UPDATE)
    private String updateUserName;

    @Schema(description = "创建时间")
    @TableField(value = "created_at", fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @Schema(description = "更新时间")
    @TableField(value = "updated_at", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;

    @Schema(description = "创建人名称")
    @TableLogic(value = "active", delval = "delete")
    private String status;

}
