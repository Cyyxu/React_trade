package com.xyes.springboot.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xyes.springboot.model.dto.userCommodityFavorites.*;
import com.xyes.springboot.model.entity.UserCommodityFavorites;
import com.xyes.springboot.model.vo.UserCommodityFavoritesVO;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 用户商品收藏表服务
 *
 */
public interface UserCommodityFavoritesService extends IService<UserCommodityFavorites> {

    /**
     * 校验数据
     *
     * @param userCommodityFavorites
     * @param add 对创建的数据进行校验
     */
    void validUserCommodityFavorites(UserCommodityFavorites userCommodityFavorites, boolean add);

    /**
     * 获取查询条件
     *
     * @param userCommodityFavoritesQueryRequest
     * @return
     */
    QueryWrapper<UserCommodityFavorites> getQueryWrapper(UserCommodityFavoritesQueryRequest userCommodityFavoritesQueryRequest);
    
    /**
     * 获取用户商品收藏表封装
     *
     * @param userCommodityFavorites
     * @param request
     * @return
     */
    UserCommodityFavoritesVO getUserCommodityFavoritesVO(UserCommodityFavorites userCommodityFavorites, HttpServletRequest request);

    /**
     * 分页获取用户商品收藏表封装
     *
     * @param userCommodityFavoritesPage
     * @param request
     * @return
     */
    Page<UserCommodityFavoritesVO> getUserCommodityFavoritesVOPage(Page<UserCommodityFavorites> userCommodityFavoritesPage, HttpServletRequest request);

    /**
     * 批量保存用户商品收藏记录
     *
     * @param favoritesList 收藏记录列表
     * @return 是否成功
     */
    boolean saveBatchFavorites(List<UserCommodityFavorites> favoritesList);

    /**
     * 批量更新用户商品收藏记录
     *
     * @param favoritesList 收藏记录列表
     * @return 是否成功
     */
    boolean updateBatchFavorites(List<UserCommodityFavorites> favoritesList);

    /**
     * 创建用户商品收藏（业务逻辑）
     *
     * @param userCommodityFavoritesAddRequest
     * @param request
     * @return 新收藏ID
     */
    Long addUserCommodityFavorites(UserCommodityFavoritesAddRequest userCommodityFavoritesAddRequest, HttpServletRequest request);

    /**
     * 删除用户商品收藏（包含权限校验）
     *
     * @param id
     * @param request
     * @return
     */
    Boolean deleteUserCommodityFavoritesById(Long id, HttpServletRequest request);

    /**
     * 更新用户商品收藏（仅管理员）
     *
     * @param userCommodityFavoritesUpdateRequest
     * @return
     */
    Boolean updateUserCommodityFavoritesById(UserCommodityFavoritesUpdateRequest userCommodityFavoritesUpdateRequest);

    /**
     * 编辑用户商品收藏（用户自己可用）
     *
     * @param userCommodityFavoritesEditRequest
     * @param request
     * @return
     */
    Boolean editUserCommodityFavoritesById(UserCommodityFavoritesEditRequest userCommodityFavoritesEditRequest, HttpServletRequest request);

    /**
     * 根据 id 获取用户商品收藏（封装类）
     *
     * @param id
     * @param request
     * @return
     */
    UserCommodityFavoritesVO getUserCommodityFavoritesVOById(Long id, HttpServletRequest request);

    /**
     * 分页获取用户商品收藏列表（实体类）
     *
     * @param userCommodityFavoritesQueryRequest
     * @return
     */
    Page<UserCommodityFavorites> listUserCommodityFavoritesByPage(UserCommodityFavoritesQueryRequest userCommodityFavoritesQueryRequest);

    /**
     * 分页获取用户商品收藏列表（封装类）
     *
     * @param userCommodityFavoritesQueryRequest
     * @param request
     * @return
     */
    Page<UserCommodityFavoritesVO> listUserCommodityFavoritesVOByPage(UserCommodityFavoritesQueryRequest userCommodityFavoritesQueryRequest, HttpServletRequest request);

    /**
     * 分页获取当前用户的商品收藏列表
     *
     * @param userCommodityFavoritesQueryRequest
     * @param request
     * @return
     */
    Page<UserCommodityFavoritesVO> getMyUserCommodityFavoritesVOPage(UserCommodityFavoritesQueryRequest userCommodityFavoritesQueryRequest, HttpServletRequest request);
}