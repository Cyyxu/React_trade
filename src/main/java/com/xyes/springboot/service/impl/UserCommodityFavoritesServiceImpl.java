package com.xyes.springboot.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xyes.springboot.exception.BusinessException;
import com.xyes.springboot.exception.ErrorCode;
import com.xyes.springboot.constant.CommonConstant;
import com.xyes.springboot.exception.ThrowUtils;
import com.xyes.springboot.mapper.UserCommodityFavoritesMapper;
import com.xyes.springboot.model.dto.userCommodityFavorites.*;
import com.xyes.springboot.model.entity.UserCommodityFavorites;
import com.xyes.springboot.model.entity.User;
import com.xyes.springboot.model.vo.UserCommodityFavoritesVO;
import com.xyes.springboot.service.CommodityService;
import com.xyes.springboot.service.UserCommodityFavoritesService;
import com.xyes.springboot.service.UserService;
import com.xyes.springboot.util.SqlUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 用户商品收藏表服务实现
 *
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class UserCommodityFavoritesServiceImpl extends ServiceImpl<UserCommodityFavoritesMapper, UserCommodityFavorites> implements UserCommodityFavoritesService {

    private final UserService userService;
    private final CommodityService commodityService;

    /**
     * 校验数据
     *
     * @param userCommodityFavorites
     * @param add                    对创建的数据进行校验
     */
    @Override
    public void validUserCommodityFavorites(UserCommodityFavorites userCommodityFavorites, boolean add) {
        ThrowUtils.throwIf(userCommodityFavorites == null, ErrorCode.PARAMS_ERROR);
        Long commodityId = userCommodityFavorites.getCommodityId();
        // 创建数据时，参数不能为空
        if (add) {
            ThrowUtils.throwIf(commodityId == null || commodityId <= 0, ErrorCode.PARAMS_ERROR);
        }
    }

    /**
     * 获取查询条件
     *
     * @param userCommodityFavoritesQueryRequest
     * @return
     */
    @Override
    public QueryWrapper<UserCommodityFavorites> getQueryWrapper(UserCommodityFavoritesQueryRequest userCommodityFavoritesQueryRequest) {
        QueryWrapper<UserCommodityFavorites> queryWrapper = new QueryWrapper<>();
        if (userCommodityFavoritesQueryRequest == null) {
            return queryWrapper;
        }
        Long id = userCommodityFavoritesQueryRequest.getId();
        Long userId = userCommodityFavoritesQueryRequest.getUserId();
        Long commodityId = userCommodityFavoritesQueryRequest.getCommodityId();
        Integer status = userCommodityFavoritesQueryRequest.getStatus();
        String remark = userCommodityFavoritesQueryRequest.getRemark();
        String sortField = userCommodityFavoritesQueryRequest.getSortField();
        String sortOrder = userCommodityFavoritesQueryRequest.getSortOrder();

        // 模糊查询
        queryWrapper.like(StringUtils.isNotBlank(remark), "remark", remark);
        // 精确查询
        queryWrapper.eq(ObjectUtils.isNotEmpty(status), "status", status);
        queryWrapper.eq(ObjectUtils.isNotEmpty(commodityId), "commodityId", commodityId);
        queryWrapper.eq(ObjectUtils.isNotEmpty(id), "id", id);
        queryWrapper.eq(ObjectUtils.isNotEmpty(userId), "userId", userId);
        // 排序规则
        queryWrapper.orderBy(SqlUtils.validSortField(sortField),
                sortOrder.equals(CommonConstant.SORT_ORDER_ASC),
                sortField);
        return queryWrapper;
    }


    /**
     * 获取用户商品收藏表封装
     *
     * @param userCommodityFavorites
     * @param request
     * @return
     */
    @Override
    public UserCommodityFavoritesVO getUserCommodityFavoritesVO(UserCommodityFavorites userCommodityFavorites, HttpServletRequest request) {
        // 对象转封装类
        return UserCommodityFavoritesVO.objToVo(userCommodityFavorites, commodityService);
    }

    /**
     * 分页获取用户商品收藏表封装
     * @param userCommodityFavoritesPage
     * @param request
     * @return
     */
    @Override
    public Page<UserCommodityFavoritesVO> getUserCommodityFavoritesVOPage(Page<UserCommodityFavorites> userCommodityFavoritesPage, HttpServletRequest request) {
        List<UserCommodityFavorites> userCommodityFavoritesList = userCommodityFavoritesPage.getRecords();
        Page<UserCommodityFavoritesVO> userCommodityFavoritesVOPage = new Page<>(userCommodityFavoritesPage.getCurrent(), userCommodityFavoritesPage.getSize(), userCommodityFavoritesPage.getTotal());
        if (CollUtil.isEmpty(userCommodityFavoritesList)) {
            return userCommodityFavoritesVOPage;
        }
        // 对象列表 => 封装对象列表
        List<UserCommodityFavoritesVO> userCommodityFavoritesVOList = userCommodityFavoritesList.stream()
                .map(favorites -> UserCommodityFavoritesVO.objToVo(favorites, commodityService))
                .collect(Collectors.toList());

        userCommodityFavoritesVOPage.setRecords(userCommodityFavoritesVOList);
        return userCommodityFavoritesVOPage;
    }

    /**
     * 批量保存用户商品收藏记录
     * @param favoritesList 收藏记录列表
     * @return 是否成功
     */
    @Override
    public boolean saveBatchFavorites(List<UserCommodityFavorites> favoritesList) {
        // 使用MyBatis Plus的批量保存功能，每批处理1000条记录
        return this.saveBatch(favoritesList, 1000);
    }

    /**
     * 批量更新用户商品收藏记录
     * @param favoritesList 收藏记录列表
     * @return 是否成功
     */
    @Override
    public boolean updateBatchFavorites(List<UserCommodityFavorites> favoritesList) {
        // 使用MyBatis Plus的批量更新功能，每批处理1000条记录
        return this.updateBatchById(favoritesList, 100);
    }

    /**
     * 创建用户商品收藏（业务逻辑）
     *
     * @param userCommodityFavoritesAddRequest
     * @param request
     * @return 新收藏ID
     */
    @Override
    public Long addUserCommodityFavorites(UserCommodityFavoritesAddRequest userCommodityFavoritesAddRequest, HttpServletRequest request) {
        ThrowUtils.throwIf(userCommodityFavoritesAddRequest == null, ErrorCode.PARAMS_ERROR);
        
        // 获取当前登录用户
        User loginUser = userService.getLoginUser(request);
        Long userId = loginUser.getId();
        Long commodityId = userCommodityFavoritesAddRequest.getCommodityId();
        
        // 检查是否已经收藏过
        UserCommodityFavorites existingFavorite = this.lambdaQuery()
                .eq(UserCommodityFavorites::getUserId, userId)
                .eq(UserCommodityFavorites::getCommodityId, commodityId)
                .one();
        
        // 如果已经收藏过，直接返回已有的收藏ID
        if (existingFavorite != null) {
            // 如果之前取消了收藏（status=0），则更新为正常收藏
            if (existingFavorite.getStatus() == 0) {
                existingFavorite.setStatus(1);
                this.updateById(existingFavorite);
            }
            return existingFavorite.getId();
        }
        
        // DTO转实体
        UserCommodityFavorites userCommodityFavorites = new UserCommodityFavorites();
        BeanUtils.copyProperties(userCommodityFavoritesAddRequest, userCommodityFavorites);
        userCommodityFavorites.setUserId(userId);
        
        // 数据校验
        validUserCommodityFavorites(userCommodityFavorites, true);
        
        // 写入数据库
        boolean result = this.save(userCommodityFavorites);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        
        return userCommodityFavorites.getId();
    }

    /**
     * 删除用户商品收藏（包含权限校验）
     *
     * @param id
     * @param request
     * @return
     */
    @Override
    public Boolean deleteUserCommodityFavoritesById(Long id, HttpServletRequest request) {
        ThrowUtils.throwIf(id == null || id <= 0, ErrorCode.PARAMS_ERROR);
        
        // 获取当前登录用户
        User user = userService.getLoginUser(request);
        
        // 判断收藏是否存在
        UserCommodityFavorites oldUserCommodityFavorites = this.getById(id);
        ThrowUtils.throwIf(oldUserCommodityFavorites == null, ErrorCode.NOT_FOUND_ERROR);
        
        // 仅本人或管理员可删除
        if (!oldUserCommodityFavorites.getUserId().equals(user.getId()) && !userService.isAdmin(request)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        
        // 删除数据库记录
        boolean result = this.removeById(id);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        
        return true;
    }

    /**
     * 更新用户商品收藏（仅管理员）
     *
     * @param userCommodityFavoritesUpdateRequest
     * @return
     */
    @Override
    public Boolean updateUserCommodityFavoritesById(UserCommodityFavoritesUpdateRequest userCommodityFavoritesUpdateRequest) {
        ThrowUtils.throwIf(userCommodityFavoritesUpdateRequest == null || userCommodityFavoritesUpdateRequest.getId() <= 0, ErrorCode.PARAMS_ERROR);
        
        // DTO转实体
        UserCommodityFavorites userCommodityFavorites = new UserCommodityFavorites();
        BeanUtils.copyProperties(userCommodityFavoritesUpdateRequest, userCommodityFavorites);
        
        // 数据校验
        validUserCommodityFavorites(userCommodityFavorites, false);
        
        // 判断是否存在
        long id = userCommodityFavoritesUpdateRequest.getId();
        UserCommodityFavorites oldUserCommodityFavorites = this.getById(id);
        ThrowUtils.throwIf(oldUserCommodityFavorites == null, ErrorCode.NOT_FOUND_ERROR);
        
        // 更新数据库
        boolean result = this.updateById(userCommodityFavorites);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        
        return true;
    }

    /**
     * 编辑用户商品收藏（用户自己可用）
     *
     * @param userCommodityFavoritesEditRequest
     * @param request
     * @return
     */
    @Override
    public Boolean editUserCommodityFavoritesById(UserCommodityFavoritesEditRequest userCommodityFavoritesEditRequest, HttpServletRequest request) {
        ThrowUtils.throwIf(userCommodityFavoritesEditRequest == null || userCommodityFavoritesEditRequest.getId() <= 0, ErrorCode.PARAMS_ERROR);
        
        // DTO转实体
        UserCommodityFavorites userCommodityFavorites = new UserCommodityFavorites();
        BeanUtils.copyProperties(userCommodityFavoritesEditRequest, userCommodityFavorites);
        
        // 数据校验
        validUserCommodityFavorites(userCommodityFavorites, false);
        
        // 获取当前登录用户
        User loginUser = userService.getLoginUser(request);
        
        // 判断是否存在
        long id = userCommodityFavoritesEditRequest.getId();
        UserCommodityFavorites oldUserCommodityFavorites = this.getById(id);
        ThrowUtils.throwIf(oldUserCommodityFavorites == null, ErrorCode.NOT_FOUND_ERROR);
        
        // 仅本人或管理员可编辑
        if (!oldUserCommodityFavorites.getUserId().equals(loginUser.getId()) && !userService.isAdmin(loginUser)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        
        // 更新数据库
        boolean result = this.updateById(userCommodityFavorites);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        
        return true;
    }

    /**
     * 根据 id 获取用户商品收藏（封装类）
     *
     * @param id
     * @param request
     * @return
     */
    @Override
    public UserCommodityFavoritesVO getUserCommodityFavoritesVOById(Long id, HttpServletRequest request) {
        ThrowUtils.throwIf(id == null || id <= 0, ErrorCode.PARAMS_ERROR);
        // 查询数据库
        UserCommodityFavorites userCommodityFavorites = this.getById(id);
        ThrowUtils.throwIf(userCommodityFavorites == null, ErrorCode.NOT_FOUND_ERROR);
        // 获取封装类
        return this.getUserCommodityFavoritesVO(userCommodityFavorites, request);
    }

    /**
     * 分页获取用户商品收藏列表（实体类）
     *
     * @param userCommodityFavoritesQueryRequest
     * @return
     */
    @Override
    public Page<UserCommodityFavorites> listUserCommodityFavoritesByPage(UserCommodityFavoritesQueryRequest userCommodityFavoritesQueryRequest) {
        long current = userCommodityFavoritesQueryRequest.getCurrent();
        long size = userCommodityFavoritesQueryRequest.getPageSize();
        return this.page(new Page<>(current, size),
                this.getQueryWrapper(userCommodityFavoritesQueryRequest));
    }

    /**
     * 分页获取用户商品收藏列表（封装类）
     *
     * @param userCommodityFavoritesQueryRequest
     * @param request
     * @return
     */
    @Override
    public Page<UserCommodityFavoritesVO> listUserCommodityFavoritesVOByPage(UserCommodityFavoritesQueryRequest userCommodityFavoritesQueryRequest, HttpServletRequest request) {
        long current = userCommodityFavoritesQueryRequest.getCurrent();
        long size = userCommodityFavoritesQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 2000, ErrorCode.PARAMS_ERROR);
        // 查询数据库
        Page<UserCommodityFavorites> userCommodityFavoritesPage = this.page(new Page<>(current, size),
                this.getQueryWrapper(userCommodityFavoritesQueryRequest));
        // 获取封装类
        return this.getUserCommodityFavoritesVOPage(userCommodityFavoritesPage, request);
    }

    /**
     * 分页获取当前用户的商品收藏列表
     *
     * @param userCommodityFavoritesQueryRequest
     * @param request
     * @return
     */
    @Override
    public Page<UserCommodityFavoritesVO> getMyUserCommodityFavoritesVOPage(UserCommodityFavoritesQueryRequest userCommodityFavoritesQueryRequest, HttpServletRequest request) {
        ThrowUtils.throwIf(userCommodityFavoritesQueryRequest == null, ErrorCode.PARAMS_ERROR);
        
        // 获取当前登录用户并设置查询条件
        User loginUser = userService.getLoginUser(request);
        userCommodityFavoritesQueryRequest.setUserId(loginUser.getId());
        
        long current = userCommodityFavoritesQueryRequest.getCurrent();
        long size = userCommodityFavoritesQueryRequest.getPageSize();
        
        // 限制爬虫
        ThrowUtils.throwIf(size > 2000, ErrorCode.PARAMS_ERROR);
        
        // 查询数据库
        Page<UserCommodityFavorites> userCommodityFavoritesPage = this.page(
                new Page<>(current, size),
                this.getQueryWrapper(userCommodityFavoritesQueryRequest)
        );
        
        // 获取封装类
        return this.getUserCommodityFavoritesVOPage(userCommodityFavoritesPage, request);
    }

}