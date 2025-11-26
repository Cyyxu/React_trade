package com.xyex.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xyex.entity.model.UserCommodityFavorite;
import com.xyex.entity.model.UserInfo;
import com.xyex.entity.req.CommodityFavoriteDTO;
import com.xyex.entity.req.UserLoginDTO;
import com.xyex.entity.req.UserQueryDTO;
import com.xyex.entity.res.UserProfileVO;
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

    /**
     * 更新当前登录用户信息
     *
     * @param userInfo 用户信息
     * @param request HTTP请求
     * @return 是否更新成功
     */
    Boolean updateMyUser(UserInfo userInfo, HttpServletRequest request);

    /**
     * 分页获取用户列表（管理员）
     * 支持关键词查询和按介绍ID查询
     *
     * @param userQueryDTO 查询参数
     * @return 用户列表
     */
    Page<UserInfo> listUsers(UserQueryDTO userQueryDTO);

    /**
     * 获取用户公开信息展示页面
     * 展示用户的非敏感信息
     *
     * @param userId 用户ID
     * @return 用户公开信息
     */
    UserProfileVO getUserProfile(Long userId);

    /**
     * 用户收藏商品
     *
     * @param commodityFavoriteDTO 收藏商品请求
     */
    void addCommodityFavorite(CommodityFavoriteDTO commodityFavoriteDTO);

    /**
     * 用户取消收藏商品
     *
     * @param commodityFavoriteDTO 收藏商品请求
     */
    void deleteCommodityFavorite(CommodityFavoriteDTO commodityFavoriteDTO);

        /**
     * 用户收藏商品列表
     *
     * @param commodityFavoriteDTO 收藏商品请求
     * @return 收藏商品列表
     */
    Page<UserCommodityFavorite> listCommodityFavorite(CommodityFavoriteDTO commodityFavoriteDTO);

}


