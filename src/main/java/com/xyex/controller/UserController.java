package com.xyex.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xyex.annotation.RequireRole;
import com.xyex.entity.model.UserCommodityFavorite;
import com.xyex.entity.model.UserInfo;
import com.xyex.entity.req.CommodityFavoriteDTO;
import com.xyex.entity.req.UserLoginDTO;
import com.xyex.entity.req.UserQueryDTO;
import com.xyex.entity.res.UserProfileVO;
import com.xyex.service.UserService;
import com.xyex.shared.enums.UserConstant;
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

    /**
     * 更新当前登录用户信息
     * 用户本人可以更新自己的信息
     *
     * @param userInfo 用户信息
     * @param request HTTP请求
     * @return 是否更新成功
     */
    @PutMapping("/update")
    @Operation(summary = "更新用户信息")
    public Boolean updateMyUser(@RequestBody UserInfo userInfo, HttpServletRequest request) {
        return userService.updateMyUser(userInfo, request);
    }

    /**
     * 分页获取用户列表（管理员）
     * 支持关键词查询（账号、昵称、简介）和按介绍ID查询
     *
     * @param userQueryDTO 查询参数
     * @return 用户列表
     */
    @PostMapping("/list")
    @Operation(summary = "分页获取用户列表")
    @RequireRole(UserConstant.ADMIN_ROLE)
    public Page<UserInfo> listUsers(@RequestBody UserQueryDTO userQueryDTO) {
        return userService.listUsers(userQueryDTO);
    }

    /**
     * 获取用户公开信息展示页面
     * 展示用户的非敏感信息
     *
     * @param userId 用户ID
     * @return 用户公开信息
     */
    @GetMapping("/{userId}/profile")
    @Operation(summary = "获取用户公开信息")
    public UserProfileVO getUserProfile(@PathVariable Long userId) {
        return userService.getUserProfile(userId);
    }
    /**
     * 用户收藏商品
     *
     * @param commodityFavoriteDTO 收藏参数
     */
    @PostMapping("/CommodityFavorite/add")
    @Operation(summary = "用户收藏商品")
    public void addCommodityFavorite(@RequestBody CommodityFavoriteDTO commodityFavoriteDTO) {
        userService.addCommodityFavorite(commodityFavoriteDTO);
    }
    /**
     * 用户取消收藏商品
     *
     * @param commodityFavoriteDTO 收藏参数
     */
    @PostMapping("/CommodityFavorite/delete")
    @Operation(summary = "用户取消收藏商品")
    public void deleteCommodityFavorite(@RequestBody CommodityFavoriteDTO commodityFavoriteDTO) {
        userService.deleteCommodityFavorite(commodityFavoriteDTO);
    }
    /**
     * 用户收藏商品列表
     *
     * @param commodityFavoriteDTO 收藏参数
     * @return 收藏商品列表
     */
    @PostMapping("/CommodityFavorite/list")
    @Operation(summary = "用户收藏商品列表")
    public Page<UserCommodityFavorite> listCommodityFavorite(@RequestBody CommodityFavoriteDTO commodityFavoriteDTO) {
        return userService.listCommodityFavorite(commodityFavoriteDTO);
    }

}
