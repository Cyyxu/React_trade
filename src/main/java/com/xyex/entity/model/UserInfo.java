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
 * 用户信息表
 *
 * @author xujun
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("user_info")
@Schema(name = "UserInfo", description = "用户信息实体类")
public class UserInfo extends BasicField implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    @TableId(type = IdType.ASSIGN_ID)
    @Schema(description = "用户ID")
    private Long id;

    /**
     * 账号
     */
    @Schema(description = "账号")
    private String userAccount;

    /**
     * 密码
     */
    @Schema(description = "密码")
    private String userPassword;

    /**
     * 旧密码（仅用于修改密码时使用）
     */
    @Schema(description = "旧密码（修改密码时使用）")
    private transient String oldPassword;

    /**
     * 微信开放平台id
     */
    @Schema(description = "微信开放平台id")
    private String unionId;

    /**
     * 公众号openId
     */
    @Schema(description = "公众号openId")
    private String mpOpenId;

    /**
     * 用户昵称
     */
    @Schema(description = "用户昵称")
    private String userName;

    /**
     * 用户头像
     */
    @Schema(description = "用户头像")
    private String userAvatar;

    /**
     * 用户简介
     */
    @Schema(description = "用户简介")
    private String userProfile;

    /**
     * 用户角色：user/admin/ban
     */
    @Schema(description = "用户角色：user/admin/ban")
    private String userRole;

    /**
     * 联系电话
     */
    @Schema(description = "联系电话")
    private String userPhone;

    /**
     * 邮箱
     */
    @Schema(description = "邮箱")
    private String userEmail;

    /**
     * 学校
     */
    @Schema(description = "学校")
    private String userSchool;

    /**
     * 专业
     */
    @Schema(description = "专业")
    private String userMajor;

    /**
     * 地址
     */
    @Schema(description = "地址")
    private String userAddress;

    /**
     * 编辑时间
     */
    @Schema(description = "编辑时间")
    private LocalDateTime editTime;

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
