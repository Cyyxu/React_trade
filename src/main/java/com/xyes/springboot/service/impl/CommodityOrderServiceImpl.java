package com.xyes.springboot.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xyes.springboot.exception.BusinessException;
import com.xyes.springboot.exception.ErrorCode;
import com.xyes.springboot.constant.CommonConstant;
import com.xyes.springboot.exception.ThrowUtils;
import com.xyes.springboot.mapper.CommodityOrderMapper;
import com.xyes.springboot.model.dto.commodityOrder.*;
import com.xyes.springboot.model.entity.Commodity;
import com.xyes.springboot.model.entity.CommodityOrder;
import com.xyes.springboot.model.entity.User;
import com.xyes.springboot.model.vo.CommodityOrderVO;
import com.xyes.springboot.service.CommodityOrderService;
import com.xyes.springboot.service.CommodityService;
import com.xyes.springboot.service.UserService;
import com.xyes.springboot.util.SqlUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class CommodityOrderServiceImpl extends ServiceImpl<CommodityOrderMapper, CommodityOrder> implements CommodityOrderService {
    private final UserService userService;
    private final CommodityService commodityService;
    /**
     * @param commodityOrder
     * @param
     */
    @Override
    public void validCommodityOrder(CommodityOrder commodityOrder, boolean add) {
        ThrowUtils.throwIf(commodityOrder == null, ErrorCode.PARAMS_ERROR);
        Long userId = commodityOrder.getUserId();
        Long commodityId = commodityOrder.getCommodityId();
        Integer buyNumber = commodityOrder.getBuyNumber();
        // 创建数据时，参数不能为空
        if (add) {
            ThrowUtils.throwIf(userId == null || userId <= 0, ErrorCode.PARAMS_ERROR);
            ThrowUtils.throwIf(commodityId == null || commodityId <= 0, ErrorCode.PARAMS_ERROR);
            ThrowUtils.throwIf(buyNumber == null || buyNumber <= 0, ErrorCode.PARAMS_ERROR);
        }
    }
    /**
     * @param commodityOrder
     * @param request
     * @return
     */
    @Override
    public CommodityOrderVO getCommodityOrderVO(CommodityOrder commodityOrder, HttpServletRequest request) {
        return null;
    }

    /**
     * @param commodityOrderQueryRequest
     * @return
     */
    @Override
    public QueryWrapper<CommodityOrder> getQueryWrapper(CommodityOrderQueryRequest commodityOrderQueryRequest) {
        QueryWrapper<CommodityOrder> queryWrapper = new QueryWrapper<>();
        if (commodityOrderQueryRequest == null) {
            return queryWrapper;
        }
        Long id = commodityOrderQueryRequest.getId();
        Long userId = commodityOrderQueryRequest.getUserId();
        Long commodityId = commodityOrderQueryRequest.getCommodityId();
        String remark = commodityOrderQueryRequest.getRemark();
        Integer buyNumber = commodityOrderQueryRequest.getBuyNumber();
        Integer payStatus = commodityOrderQueryRequest.getPayStatus();
        String sortField = commodityOrderQueryRequest.getSortField();
        String sortOrder = commodityOrderQueryRequest.getSortOrder();
        // 模糊查询
        queryWrapper.like(StringUtils.isNotBlank(remark), "remark", remark);
        // 精确查询
        queryWrapper.eq(ObjectUtils.isNotEmpty(payStatus), "payStatus", payStatus);
        queryWrapper.eq(ObjectUtils.isNotEmpty(buyNumber), "buyNumber", buyNumber);
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
     * @param commodityOrderPage
     * @param request
     * @return
     */
    @Override
    public Page<CommodityOrderVO> getCommodityOrderVOPage(Page<CommodityOrder> commodityOrderPage, HttpServletRequest request) {
        List<CommodityOrder> commodityOrderList = commodityOrderPage.getRecords();
        Page<CommodityOrderVO> commodityOrderVOPage = new Page<>(commodityOrderPage.getCurrent(), commodityOrderPage.getSize(), commodityOrderPage.getTotal());
        if (CollUtil.isEmpty(commodityOrderList)) {
            return commodityOrderVOPage;
        }
        // 对象列表 => 封装对象列表
        List<CommodityOrderVO> commodityOrderVOList = commodityOrderList.stream()
                .map(CommodityOrderVO::objToVo)
                .collect(Collectors.toList());
        // 关联查询用户信息
        Set<Long> userIdSet = commodityOrderList.stream()
                .map(CommodityOrder::getUserId)
                .collect(Collectors.toSet());
        // 关联查询商品信息
        Set<Long> commodityIdSet = commodityOrderList.stream()
                .map(CommodityOrder::getCommodityId)
                .collect(Collectors.toSet());
        // 批量查询用户信息
        Map<Long, User> userIdUserMap = userService.listByIds(userIdSet).stream()
                .collect(Collectors.toMap(User::getId, user -> user));
        // 批量查询商品信息
        Map<Long, Commodity> commodityIdMap = commodityService.listByIds(commodityIdSet).stream()
                .collect(Collectors.toMap(Commodity::getId, commodity -> commodity));
        // 填充用户信息到 VO 对象
        commodityOrderVOList.forEach(commodityOrderVO -> {
            User user = userIdUserMap.get(commodityOrderVO.getUserId());
            if (user != null) {
                commodityOrderVO.setUserName(user.getUserName());
                commodityOrderVO.setUserPhone(user.getUserPhone());
            }
        });
        // 填充商品信息到 VO 对象
        commodityOrderVOList.forEach(commodityOrderVO -> {
            Commodity commodity = commodityIdMap.get(commodityOrderVO.getCommodityId());
            if (commodity != null) {
                commodityOrderVO.setCommodityName(commodity.getCommodityName());
            }
        });
        commodityOrderVOPage.setRecords(commodityOrderVOList);
        return commodityOrderVOPage;
    }
    /**
     * @param queryRequest
     * @return
     */
    @Override
    public List<CommodityOrder> listByQuery(CommodityOrderQueryRequest queryRequest) {
        QueryWrapper<CommodityOrder> queryWrapper = new QueryWrapper<>();
        if (queryRequest.getUserId() != null) {
            queryWrapper.eq("userId", queryRequest.getUserId());
        }
        if (queryRequest.getPayStatus() != null) {
            queryWrapper.eq("payStatus", queryRequest.getPayStatus());
        }
        return this.list(queryWrapper);
    }
    @Override
    public CommodityOrder getByIdWithLock(Long id) {
        return baseMapper.selectOne(new LambdaQueryWrapper<CommodityOrder>()
                .eq(CommodityOrder::getId, id)
                .last("FOR UPDATE"));
    }

    /**
     * 创建订单（业务逻辑）
     *
     * @param commodityOrderAddRequest
     * @param request
     * @return 新订单ID
     */
    @Override
    public Long addCommodityOrder(CommodityOrderAddRequest commodityOrderAddRequest, HttpServletRequest request) {
        ThrowUtils.throwIf(commodityOrderAddRequest == null, ErrorCode.PARAMS_ERROR);
        
        // DTO转实体
        CommodityOrder commodityOrder = new CommodityOrder();
        BeanUtils.copyProperties(commodityOrderAddRequest, commodityOrder);
        
        // 数据校验
        validCommodityOrder(commodityOrder, true);
        
        // 获取当前登录用户并设置userId
        User loginUser = userService.getLoginUser(request);
        commodityOrder.setUserId(loginUser.getId());
        
        // 写入数据库
        boolean result = this.save(commodityOrder);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        
        return commodityOrder.getId();
    }

    /**
     * 删除订单（包含权限校验）
     *
     * @param id
     * @param request
     * @return
     */
    @Override
    public Boolean deleteCommodityOrderById(Long id, HttpServletRequest request) {
        ThrowUtils.throwIf(id == null || id <= 0, ErrorCode.PARAMS_ERROR);
        
        // 获取当前登录用户
        User user = userService.getLoginUser(request);
        
        // 判断订单是否存在
        CommodityOrder oldCommodityOrder = this.getById(id);
        ThrowUtils.throwIf(oldCommodityOrder == null, ErrorCode.NOT_FOUND_ERROR);
        
        // 仅本人或管理员可删除
        if (!oldCommodityOrder.getUserId().equals(user.getId()) && !userService.isAdmin(request)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        
        // 删除数据库记录
        boolean result = this.removeById(id);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        
        return true;
    }

    /**
     * 更新订单（仅管理员）
     *
     * @param commodityOrderUpdateRequest
     * @return
     */
    @Override
    public Boolean updateCommodityOrderById(CommodityOrderUpdateRequest commodityOrderUpdateRequest) {
        ThrowUtils.throwIf(commodityOrderUpdateRequest == null || commodityOrderUpdateRequest.getId() <= 0, ErrorCode.PARAMS_ERROR);
        
        // DTO转实体
        CommodityOrder commodityOrder = new CommodityOrder();
        BeanUtils.copyProperties(commodityOrderUpdateRequest, commodityOrder);
        
        // 数据校验
        validCommodityOrder(commodityOrder, false);
        
        // 判断是否存在
        long id = commodityOrderUpdateRequest.getId();
        CommodityOrder oldCommodityOrder = this.getById(id);
        ThrowUtils.throwIf(oldCommodityOrder == null, ErrorCode.NOT_FOUND_ERROR);
        
        // 更新数据库
        boolean result = this.updateById(commodityOrder);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        
        return true;
    }

    /**
     * 编辑订单（用户自己可用）
     *
     * @param commodityOrderEditRequest
     * @param request
     * @return
     */
    @Override
    public Boolean editCommodityOrderById(CommodityOrderEditRequest commodityOrderEditRequest, HttpServletRequest request) {
        ThrowUtils.throwIf(commodityOrderEditRequest == null || commodityOrderEditRequest.getId() <= 0, ErrorCode.PARAMS_ERROR);
        
        // DTO转实体
        CommodityOrder commodityOrder = new CommodityOrder();
        BeanUtils.copyProperties(commodityOrderEditRequest, commodityOrder);
        
        // 数据校验
        validCommodityOrder(commodityOrder, false);
        
        // 获取当前登录用户
        User loginUser = userService.getLoginUser(request);
        
        // 判断是否存在
        long id = commodityOrderEditRequest.getId();
        CommodityOrder oldCommodityOrder = this.getById(id);
        ThrowUtils.throwIf(oldCommodityOrder == null, ErrorCode.NOT_FOUND_ERROR);
        
        // 仅本人或管理员可编辑
        if (!oldCommodityOrder.getUserId().equals(loginUser.getId()) && !userService.isAdmin(loginUser)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        
        // 更新数据库
        boolean result = this.updateById(commodityOrder);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        
        return true;
    }

    /**
     * 分页获取当前用户的订单列表
     *
     * @param commodityOrderQueryRequest
     * @param request
     * @return
     */
    @Override
    public Page<CommodityOrderVO> getMyCommodityOrderVOPage(CommodityOrderQueryRequest commodityOrderQueryRequest, HttpServletRequest request) {
        ThrowUtils.throwIf(commodityOrderQueryRequest == null, ErrorCode.PARAMS_ERROR);
        
        // 获取当前登录用户并设置查询条件
        User loginUser = userService.getLoginUser(request);
        commodityOrderQueryRequest.setUserId(loginUser.getId());
        
        long current = commodityOrderQueryRequest.getCurrent();
        long size = commodityOrderQueryRequest.getPageSize();
        
        // 限制爬虫
        ThrowUtils.throwIf(size > 2000, ErrorCode.PARAMS_ERROR);
        
        // 查询数据库
        Page<CommodityOrder> commodityOrderPage = this.page(
                new Page<>(current, size),
                this.getQueryWrapper(commodityOrderQueryRequest)
        );
        
        // 获取封装类
        return this.getCommodityOrderVOPage(commodityOrderPage, request);
    }

    /**
     * 获取订单热力图数据
     *
     * @param userId
     * @param payStatus
     * @return
     */
    @Override
    public List<Map<String, Object>> getCommodityOrderHeatmapData(Long userId, Integer payStatus) {
        // 构建查询条件
        CommodityOrderQueryRequest queryRequest = new CommodityOrderQueryRequest();
        queryRequest.setUserId(userId);
        queryRequest.setPayStatus(payStatus);
        
        // 查询符合条件的订单
        List<CommodityOrder> orderList = this.listByQuery(queryRequest);
        
        // 统计每个日期的订单数量
        Map<String, Integer> dateCountMap = new HashMap<>();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        
        for (CommodityOrder order : orderList) {
            String dateStr = dateFormat.format(order.getCreateTime());
            dateCountMap.put(dateStr, dateCountMap.getOrDefault(dateStr, 0) + 1);
        }
        
        // 将统计结果转换为前端需要的格式
        List<Map<String, Object>> result = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : dateCountMap.entrySet()) {
            Map<String, Object> item = new HashMap<>();
            item.put("date", entry.getKey());
            item.put("value", entry.getValue());
            result.add(item);
        }
        
        return result;
    }
}