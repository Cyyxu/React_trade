package com.xyex.entity.res;

import io.swagger.v3.oas.annotations.media.Schema;
import com.xyex.entity.model.UserInfo;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 用户公开信息展示VO
 * 只展示非敏感信息
 *
 * @author xujun
 */
@Data
@Schema(name = "UserProfileVO", description = "用户公开信息")
public class UserProfileVO {

    /**
     * 用户ID
     */
    @Schema(description = "用户ID")
    private Long id;

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
     * 创建时间
     */
    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    public static UserProfileVO convert(UserInfo user) {
        // 构建用户公开信息VO
        UserProfileVO profileVO = new UserProfileVO();
        profileVO.setId(user.getId());
        profileVO.setUserName(user.getUserName());
        profileVO.setUserAvatar(user.getUserAvatar());
        profileVO.setUserProfile(user.getUserProfile());
        profileVO.setUserSchool(user.getUserSchool());
        profileVO.setUserMajor(user.getUserMajor());
        profileVO.setUserPhone(user.getUserPhone());
        profileVO.setUserEmail(user.getUserEmail());
        profileVO.setCreateTime(user.getCreateTime());
        return profileVO;
    }
}
