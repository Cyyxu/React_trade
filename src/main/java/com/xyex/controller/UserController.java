package com.xyex.controller;

import com.xyex.entity.req.UserLoginDTO;
import com.xyex.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;

/**
 * 用户管理接口
 * 提供用户注册、登录、注销以及用户信息管理等功能
 *
 * @author xujun
 */
@RequestMapping("/user")
@RestController
@RequiredArgsConstructor
@Tag(name = "用户管理")
public class UserController {
    private final UserService userService;

    /**
     * 用户注册
     *
     * @param userRegisterDTO 用户注册请求
     * @return 新注册用户的ID
     */
    @PostMapping("/register")
    @Operation(summary = "用户注册")
    public Long register(@RequestBody UserLoginDTO userRegisterDTO) {
        return userService.register(userRegisterDTO);
    }

    /**
     * 用户登录
     *
     * @return 登录响应
     */
    @PostMapping("/login")
    @Operation(summary = "用户登录")
    public UserLoginDTO.LoginData login(@RequestBody UserLoginDTO userLoginDTO) {
        return userService.login(userLoginDTO);
    }

    /**
     * 用户注销
     *
     * @return 是否注销成功
     */
    @PostMapping("/logout")
    @Operation(summary = "用户注销")
    public Boolean logout(HttpServletRequest request) {
        return userService.logout(request);
    }
}
