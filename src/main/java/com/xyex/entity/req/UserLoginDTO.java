package com.xyex.entity.req;

import com.xyex.entity.model.UserInfo;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 用户登录/注册请求和响应
 *
 * @author xujun
 */
@Data
@Schema(name = "UserLoginDTO", description = "用户登录/注册请求")
public class UserLoginDTO {

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
     * 确认密码（注册时使用）
     */
    @Schema(description = "确认密码")
    private String confirmPassword;

    /**
     * 登录响应数据内部类
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(name = "LoginData", description = "用户信息和token")
    public static class LoginData {

        /**
         * 用户信息
         */
        @Schema(description = "用户信息")
        private UserInfo userInfo;

        /**
         * 认证令牌
         */
        @Schema(description = "认证令牌")
        private String token;
    }
}
