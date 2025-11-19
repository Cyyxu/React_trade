package com.xyes.springboot.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xyes.springboot.annotation.RequireRole;
import com.xyes.springboot.common.DeleteRequest;
import com.xyes.springboot.exception.ErrorCode;
import com.xyes.springboot.constant.UserConstant;
import com.xyes.springboot.exception.ThrowUtils;
import com.xyes.springboot.model.dto.commodityOrder.CommodityOrderEditRequest;
import com.xyes.springboot.model.dto.commodityOrder.CommodityOrderQueryRequest;
import com.xyes.springboot.model.dto.commodityOrder.CommodityOrderUpdateRequest;
import com.xyes.springboot.model.dto.commodityOrder.CommodityOrderAddRequest;
import com.xyes.springboot.model.entity.CommodityOrder;
import com.xyes.springboot.model.vo.CommodityOrderVO;
import com.xyes.springboot.service.CommodityOrderService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

/**
 * 商品订单管理接口
 * 提供商品订单的创建、删除、更新、查询等功能
 *
 * * @author xujun
 */
@RestController
@RequestMapping("/commodityOrder")
@Slf4j
@RequiredArgsConstructor
public class CommodityOrderController {

    private final CommodityOrderService commodityOrderService;

    /**
     * 创建商品订单
     *
     * @param commodityOrderAddRequest 商品订单添加请求
     * @param request HTTP请求
     * @return 新创建的商品订单ID
     */
    @PostMapping("/add")
    public Long addCommodityOrder(@RequestBody CommodityOrderAddRequest commodityOrderAddRequest, HttpServletRequest request) {
        return commodityOrderService.addCommodityOrder(commodityOrderAddRequest, request);
    }

    /**
     * 删除商品订单
     *
     * @param deleteRequest 删除请求
     * @param request HTTP请求
     * @return 是否删除成功
     */
    @PostMapping("/delete")
    public Boolean deleteCommodityOrder(@RequestBody DeleteRequest deleteRequest, HttpServletRequest request) {
        ThrowUtils.throwIf(deleteRequest == null || deleteRequest.getId() <= 0, ErrorCode.PARAMS_ERROR);
        return commodityOrderService.deleteCommodityOrderById(deleteRequest.getId(), request);
    }

    /**
     * 更新商品订单（仅管理员可用）
     *
     * @param commodityOrderUpdateRequest 商品订单更新请求
     * @return 是否更新成功
     */
    @PostMapping("/update")
    @RequireRole(UserConstant.ADMIN_ROLE)
    public Boolean updateCommodityOrder(@RequestBody CommodityOrderUpdateRequest commodityOrderUpdateRequest) {
        return commodityOrderService.updateCommodityOrderById(commodityOrderUpdateRequest);
    }

    /**
     * 根据ID获取商品订单（封装类）
     *
     * @param id 商品订单ID
     * @param request HTTP请求
     * @return 商品订单VO
     */
    @GetMapping("/get/vo")
    public CommodityOrderVO getCommodityOrderVOById(long id, HttpServletRequest request) {
        ThrowUtils.throwIf(id <= 0, ErrorCode.PARAMS_ERROR);
        CommodityOrder commodityOrder = commodityOrderService.getById(id);
        ThrowUtils.throwIf(commodityOrder == null, ErrorCode.NOT_FOUND_ERROR);
        return commodityOrderService.getCommodityOrderVO(commodityOrder, request);
    }

    /**
     * 分页获取商品订单列表（仅管理员可用）
     *
     * @param commodityOrderQueryRequest 商品订单查询请求
     * @return 商品订单分页列表
     */
    @PostMapping("/list/page")
    @RequireRole(UserConstant.ADMIN_ROLE)
    public Page<CommodityOrder> listCommodityOrderByPage(@RequestBody CommodityOrderQueryRequest commodityOrderQueryRequest) {
        long current = commodityOrderQueryRequest.getCurrent();
        long size = commodityOrderQueryRequest.getPageSize();
        Page<CommodityOrder> commodityOrderPage = commodityOrderService.page(new Page<>(current, size),
                commodityOrderService.getQueryWrapper(commodityOrderQueryRequest));
        return commodityOrderPage;
    }

    /**
     * 分页获取商品订单列表（封装类）
     *
     * @param commodityOrderQueryRequest 商品订单查询请求
     * @param request HTTP请求
     * @return 商品订单VO分页列表
     */
    @PostMapping("/list/page/vo")
    public Page<CommodityOrderVO> listCommodityOrderVOByPage(@RequestBody CommodityOrderQueryRequest commodityOrderQueryRequest,
                                                               HttpServletRequest request) {
        long current = commodityOrderQueryRequest.getCurrent();
        long size = commodityOrderQueryRequest.getPageSize();
        ThrowUtils.throwIf(size > 2000, ErrorCode.PARAMS_ERROR);
        Page<CommodityOrder> commodityOrderPage = commodityOrderService.page(new Page<>(current, size),
                commodityOrderService.getQueryWrapper(commodityOrderQueryRequest));
        return commodityOrderService.getCommodityOrderVOPage(commodityOrderPage, request);
    }

    /**
     * 分页获取当前登录用户创建的商品订单列表
     *
     * @param commodityOrderQueryRequest 商品订单查询请求
     * @param request HTTP请求
     * @return 商品订单VO分页列表
     */
    @PostMapping("/my/list/page/vo")
    public Page<CommodityOrderVO> listMyCommodityOrderVOByPage(@RequestBody CommodityOrderQueryRequest commodityOrderQueryRequest,
                                                                 HttpServletRequest request) {
        return commodityOrderService.getMyCommodityOrderVOPage(commodityOrderQueryRequest, request);
    }

    /**
     * 编辑商品订单（给用户使用）
     *
     * @param commodityOrderEditRequest 商品订单编辑请求
     * @param request HTTP请求
     * @return 是否编辑成功
     */
    @PostMapping("/edit")
    public Boolean editCommodityOrder(@RequestBody CommodityOrderEditRequest commodityOrderEditRequest, HttpServletRequest request) {
        return commodityOrderService.editCommodityOrderById(commodityOrderEditRequest, request);
    }

    /**
     * 根据用户ID和支付状态查询商品订单，返回日期和订单数量的列表（用于ECharts热力图）
     *
     * @param userId 用户ID
     * @param payStatus 支付状态
     * @return 日期和订单数量的列表
     */
    @GetMapping("/getCommodityOrderHeatmapData")
    public List<Map<String, Object>> getCommodityOrderHeatmapData(
            @RequestParam Long userId,
            @RequestParam Integer payStatus) {
        return commodityOrderService.getCommodityOrderHeatmapData(userId, payStatus);
    }

}
