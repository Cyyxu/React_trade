package com.xyes.springboot.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xyes.springboot.annotation.RequireRole;
import com.xyes.springboot.common.DeleteRequest;
import com.xyes.springboot.constant.UserConstant;
import com.xyes.springboot.model.dto.userCommodityFavorites.UserCommodityFavoritesAddRequest;
import com.xyes.springboot.model.dto.userCommodityFavorites.UserCommodityFavoritesEditRequest;
import com.xyes.springboot.model.dto.userCommodityFavorites.UserCommodityFavoritesQueryRequest;
import com.xyes.springboot.model.dto.userCommodityFavorites.UserCommodityFavoritesUpdateRequest;
import com.xyes.springboot.model.entity.UserCommodityFavorites;
import com.xyes.springboot.model.vo.UserCommodityFavoritesVO;
import com.xyes.springboot.service.UserCommodityFavoritesService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;


/**
 * 用户商品收藏管理接口
 * 提供用户商品收藏的创建、删除、更新、查询等功能
 *
 * * @author xujun
 */
@RestController
@RequestMapping("/userCommodityFavorites")
@Slf4j
@RequiredArgsConstructor
public class UserCommodityFavoritesController {

    private final UserCommodityFavoritesService userCommodityFavoritesService;

    /**
     * 创建用户商品收藏
     *
     * @param userCommodityFavoritesAddRequest 用户商品收藏添加请求
     * @param request HTTP请求
     * @return 新创建的用户商品收藏ID
     */
    @PostMapping("/add")
    public Long addUserCommodityFavorites(@RequestBody UserCommodityFavoritesAddRequest userCommodityFavoritesAddRequest, HttpServletRequest request) {
        return userCommodityFavoritesService.addUserCommodityFavorites(userCommodityFavoritesAddRequest, request);
    }

    /**
     * 删除用户商品收藏
     *
     * @param deleteRequest 删除请求
     * @param request HTTP请求
     * @return 是否删除成功
     */
    @PostMapping("/delete")
    public Boolean deleteUserCommodityFavorites(@RequestBody DeleteRequest deleteRequest, HttpServletRequest request) {
        return userCommodityFavoritesService.deleteUserCommodityFavoritesById(deleteRequest.getId(), request);
    }

    /**
     * 更新用户商品收藏（仅管理员可用）
     *
     * @param userCommodityFavoritesUpdateRequest 用户商品收藏更新请求
     * @return 是否更新成功
     */
    @PostMapping("/update")
    @RequireRole(UserConstant.ADMIN_ROLE)
    public Boolean updateUserCommodityFavorites(@RequestBody UserCommodityFavoritesUpdateRequest userCommodityFavoritesUpdateRequest) {
        return userCommodityFavoritesService.updateUserCommodityFavoritesById(userCommodityFavoritesUpdateRequest);
    }

    /**
     * 根据ID获取用户商品收藏（封装类）
     *
     * @param id 用户商品收藏ID
     * @param request HTTP请求
     * @return 用户商品收藏VO
     */
    @GetMapping("/get/vo")
    public UserCommodityFavoritesVO getUserCommodityFavoritesVOById(long id, HttpServletRequest request) {
        return userCommodityFavoritesService.getUserCommodityFavoritesVOById(id, request);
    }

    /**
     * 分页获取用户商品收藏列表（仅管理员可用）
     *
     * @param userCommodityFavoritesQueryRequest 用户商品收藏查询请求
     * @return 用户商品收藏分页列表
     */
    @PostMapping("/list/page")
    @RequireRole(UserConstant.ADMIN_ROLE)
    public Page<UserCommodityFavorites> listUserCommodityFavoritesByPage(@RequestBody UserCommodityFavoritesQueryRequest userCommodityFavoritesQueryRequest) {
        return userCommodityFavoritesService.listUserCommodityFavoritesByPage(userCommodityFavoritesQueryRequest);
    }

    /**
     * 分页获取用户商品收藏列表（封装类）
     *
     * @param userCommodityFavoritesQueryRequest 用户商品收藏查询请求
     * @param request HTTP请求
     * @return 用户商品收藏VO分页列表
     */
    @PostMapping("/list/page/vo")
    public Page<UserCommodityFavoritesVO> listUserCommodityFavoritesVOByPage(@RequestBody UserCommodityFavoritesQueryRequest userCommodityFavoritesQueryRequest,
                                                               HttpServletRequest request) {
        return userCommodityFavoritesService.listUserCommodityFavoritesVOByPage(userCommodityFavoritesQueryRequest, request);
    }

    /**
     * 分页获取当前登录用户的商品收藏列表
     *
     * @param userCommodityFavoritesQueryRequest 用户商品收藏查询请求
     * @param request HTTP请求
     * @return 用户商品收藏VO分页列表
     */
    @PostMapping("/my/list/page/vo")
    public Page<UserCommodityFavoritesVO> listMyUserCommodityFavoritesVOByPage(@RequestBody UserCommodityFavoritesQueryRequest userCommodityFavoritesQueryRequest,
                                                                 HttpServletRequest request) {
        return userCommodityFavoritesService.getMyUserCommodityFavoritesVOPage(userCommodityFavoritesQueryRequest, request);
    }

    /**
     * 编辑用户商品收藏（给用户使用）
     *
     * @param userCommodityFavoritesEditRequest 用户商品收藏编辑请求
     * @param request HTTP请求
     * @return 是否编辑成功
     */
    @PostMapping("/edit")
    public Boolean editUserCommodityFavorites(@RequestBody UserCommodityFavoritesEditRequest userCommodityFavoritesEditRequest, HttpServletRequest request) {
        return userCommodityFavoritesService.editUserCommodityFavoritesById(userCommodityFavoritesEditRequest, request);
    }
}
