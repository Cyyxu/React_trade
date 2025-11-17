package com.xyes.springboot.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xyes.springboot.annotation.RequireRole;
import com.xyes.springboot.common.DeleteRequest;
import com.xyes.springboot.constant.UserConstant;
import com.xyes.springboot.model.dto.user.*;
import com.xyes.springboot.model.entity.User;
import com.xyes.springboot.model.vo.LoginResponse;
import com.xyes.springboot.model.vo.LoginUserVO;
import com.xyes.springboot.model.vo.UserVO;
import com.xyes.springboot.service.UserService;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * 用户管理接口
 * 提供用户注册、登录、注销以及用户信息管理等功能
 *
 * @author xujun
 */
@RequestMapping("/user")
@RestController
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    /**
     * 用户注册
     *
     * @param userRegisterRequest 用户注册请求
     * @return 新注册用户的ID
     */
    @PostMapping("/register")
    public Long userRegister(@RequestBody UserRegisterRequest userRegisterRequest) {
        return userService.userRegister(userRegisterRequest);
    }

    /**
     * 用户登录
     *
     * @param userLoginRequest 用户登录请求
     * @param request HTTP请求
     * @return 登录响应，包含用户信息和JWT token
     */
    @PostMapping("/login")
    public LoginResponse login(@RequestBody UserLoginRequest userLoginRequest, HttpServletRequest request) {
        return userService.login(userLoginRequest, request);
    }

    /**
     * 用户注销
     *
     * @param request HTTP请求
     * @return 是否注销成功
     */
    @PostMapping("/logout")
    public Boolean userLogout(HttpServletRequest request) {
        return userService.userLogout(request);
    }

    /**
     * 获取当前登录用户信息
     *
     * @param request HTTP请求
     * @return 当前登录用户信息
     */
    @GetMapping("/get/login")
    public LoginUserVO getLoginUser(HttpServletRequest request) {
        User user = userService.getLoginUser(request);
        return userService.getLoginUserVO(user);
    }

    /**
     * 添加用户（仅管理员）
     *
     * @param userAddRequest 用户添加请求
     * @param request HTTP请求
     * @return 新添加用户的ID
     */
    @PostMapping("/add")
    @RequireRole(UserConstant.ADMIN_ROLE)
    public Long addUser(@RequestBody UserAddRequest userAddRequest, HttpServletRequest request) {
        return userService.addUser(userAddRequest, request);
    }

    /**
     * 删除用户（仅管理员）
     *
     * @param deleteRequest 删除请求
     * @param request HTTP请求
     * @return 是否删除成功
     */
    @PostMapping("/delete")
    @RequireRole(UserConstant.ADMIN_ROLE)
    public Boolean deleteUser(@RequestBody DeleteRequest deleteRequest, HttpServletRequest request) {
        return userService.deleteUserById(deleteRequest.getId(), request);
    }

    /**
     * 更新用户信息（仅管理员）
     *
     * @param userUpdateRequest 用户更新请求
     * @param request HTTP请求
     * @return 是否更新成功
     */
    @PostMapping("/update")
    @RequireRole(UserConstant.ADMIN_ROLE)
    public Boolean updateUser(@RequestBody UserUpdateRequest userUpdateRequest, HttpServletRequest request) {
        return userService.updateUserById(userUpdateRequest, request);
    }

    /**
     * 根据ID获取用户信息（仅管理员）
     *
     * @param id 用户ID
     * @param request HTTP请求
     * @return 用户实体
     */
    @GetMapping("/get")
    @RequireRole(UserConstant.ADMIN_ROLE)
    public User getUserById(long id, HttpServletRequest request) {
        return userService.getUserById(id, request);
    }

    /**
     * 根据ID获取用户信息（封装类）
     *
     * @param id 用户ID
     * @param request HTTP请求
     * @return 用户VO
     */
    @GetMapping("/get/vo")
    public UserVO getUserVOById(long id, HttpServletRequest request) {
        return userService.getUserVOById(id, request);
    }

    /**
     * 分页获取用户列表（仅管理员）
     *
     * @param userQueryRequest 用户查询请求
     * @param request HTTP请求
     * @return 用户分页列表
     */
    @PostMapping("/list/page")
    @RequireRole(UserConstant.ADMIN_ROLE)
    public Page<User> listUserByPage(@RequestBody UserQueryRequest userQueryRequest, HttpServletRequest request) {
        return userService.listUserByPage(userQueryRequest, request);
    }

    /**
     * 分页获取用户列表（封装类）
     *
     * @param userQueryRequest 用户查询请求
     * @param request HTTP请求
     * @return 用户VO分页列表
     */
    @PostMapping("/list/page/vo")
    public Page<UserVO> listUserVOByPage(@RequestBody UserQueryRequest userQueryRequest,HttpServletRequest request) {
        return userService.listUserVOByPage(userQueryRequest, request);
    }

    /**
     * 更新当前登录用户信息
     *
     * @param userUpdateMyRequest 用户更新请求
     * @param request HTTP请求
     * @return 是否更新成功
     */
    @PostMapping("/update/my")
    public Boolean updateMyUser(@RequestBody UserUpdateMyRequest userUpdateMyRequest, HttpServletRequest request) {
        return userService.updateMyUser(userUpdateMyRequest, request);
    }
}
