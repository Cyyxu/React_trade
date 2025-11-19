package com.xyex.service;

import com.xyex.entity.model.UserInfo;
import com.xyex.entity.req.UserLoginDTO;
import com.xyex.infrastructure.model.BasicService;
import jakarta.servlet.http.HttpServletRequest;

/**
 * 用户服务接口
 *
 * @author xujun
 */
public interface UserService extends BasicService<UserInfo> {
    /**
     * 用户注册
     *
     * @param userRegisterDTO 用户注册请求
     * @return 新注册用户的ID
     */
    Long register(UserLoginDTO userRegisterDTO);

    UserLoginDTO.LoginData login(UserLoginDTO userLoginDTO);

    /**
     * 用户注销
     *
     * @param request HTTP请求
     * @return 是否注销成功
     */
    Boolean logout(HttpServletRequest request);
}
