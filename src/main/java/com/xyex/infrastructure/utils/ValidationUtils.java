package com.xyex.infrastructure.utils;

import com.xyex.infrastructure.exception.BusinessException;
import com.xyex.infrastructure.exception.ErrorCode;
import org.apache.commons.lang3.StringUtils;

/**
 * 用户验证工具类
 *
 * @author xujun
 */
public class ValidationUtils {

    /**
     * 验证账号和密码（注册）
     *
     * @param userAccount 账号
     * @param userPassword 密码
     * @param confirmPassword 确认密码
     */
    public static void validateRegister(String userAccount, String userPassword, String confirmPassword) {
        if (StringUtils.isAnyBlank(userAccount, userPassword, confirmPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数为空");
        }
        if (userAccount.length() < 4) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户账号过短");
        }
        if (userPassword.length() < 8 || confirmPassword.length() < 8) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户密码过短");
        }
        if (!userPassword.equals(confirmPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "两次输入的密码不一致");
        }
    }

    /**
     * 验证账号和密码（登录）
     *
     * @param userAccount 账号
     * @param userPassword 密码
     */
    public static void validateLogin(String userAccount, String userPassword) {
        if (StringUtils.isAnyBlank(userAccount, userPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数为空");
        }
        if (userAccount.length() < 4) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户账号过短");
        }
        if (userPassword.length() < 8) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户密码过短");
        }
    }
}
