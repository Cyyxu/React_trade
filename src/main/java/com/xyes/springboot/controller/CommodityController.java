package com.xyes.springboot.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xyes.springboot.annotation.RequireRole;
import com.xyes.springboot.common.DeleteRequest;
import com.xyes.springboot.exception.ErrorCode;
import com.xyes.springboot.constant.UserConstant;
import com.xyes.springboot.exception.ThrowUtils;
import com.xyes.springboot.model.dto.commodity.*;
import com.xyes.springboot.model.entity.Commodity;
import com.xyes.springboot.model.vo.CommodityVO;
import com.xyes.springboot.service.CommodityService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * 商品管理接口
 * 提供商品的创建、删除、更新、查询以及购买等功能
 *
 * * @author xujun
 */
@RestController
@RequestMapping("/commodity")
@Slf4j
@RequiredArgsConstructor
public class CommodityController {

    private final CommodityService commodityService;

    /**
     * 创建商品
     *
     * @param commodityAddRequest 商品添加请求
     * @param request HTTP请求
     * @return 新创建的商品ID
     */
    @PostMapping("/add")
    public Long addCommodity(@RequestBody CommodityAddRequest commodityAddRequest, HttpServletRequest request) {
        return commodityService.addCommodity(commodityAddRequest, request);
    }

    /**
     * 删除商品
     *
     * @param deleteRequest 删除请求
     * @param request HTTP请求
     * @return 是否删除成功
     */
    @PostMapping("/delete")
    public Boolean deleteCommodity(@RequestBody DeleteRequest deleteRequest, HttpServletRequest request) {
        ThrowUtils.throwIf(deleteRequest == null || deleteRequest.getId() <= 0, ErrorCode.PARAMS_ERROR);
        return commodityService.deleteCommodityById(deleteRequest.getId(), request);
    }

    /**
     * 更新商品（仅管理员可用）
     *
     * @param commodityUpdateRequest 商品更新请求
     * @return 是否更新成功
     */
    @PostMapping("/update")
    @RequireRole(UserConstant.ADMIN_ROLE)
    public Boolean updateCommodity(@RequestBody CommodityUpdateRequest commodityUpdateRequest) {
        return commodityService.updateCommodityById(commodityUpdateRequest);
    }

    /**
     * 根据ID获取商品（封装类）
     *
     * @param id 商品ID
     * @param request HTTP请求
     * @return 商品VO
     */
    @GetMapping("/get/vo")
    public CommodityVO getCommodityVOById(long id, HttpServletRequest request) {
        ThrowUtils.throwIf(id <= 0, ErrorCode.PARAMS_ERROR);
        Commodity commodity = commodityService.getById(id);
        ThrowUtils.throwIf(commodity == null, ErrorCode.NOT_FOUND_ERROR);
        return commodityService.getCommodityVO(commodity, request);
    }

    /**
     * 分页获取商品列表（仅管理员可用）
     *
     * @param commodityQueryRequest 商品查询请求
     * @return 商品分页列表
     */
    @PostMapping("/list/page")
    @RequireRole(UserConstant.ADMIN_ROLE)
    public Page<Commodity> listCommodityByPage(@RequestBody CommodityQueryRequest commodityQueryRequest) {
        long current = commodityQueryRequest.getCurrent();
        long size = commodityQueryRequest.getPageSize();
        Page<Commodity> commodityPage = commodityService.page(new Page<>(current, size),
                commodityService.getQueryWrapper(commodityQueryRequest));
        return commodityPage;
    }

    /**
     * 分页获取商品列表（封装类）
     * 普通用户：只能看到自己发布的商品
     * 管理员：可以看到所有商品
     *
     * @param commodityQueryRequest 商品查询请求
     * @param request HTTP请求
     * @return 商品VO分页列表
     */
    @PostMapping("/list/page/vo")
    public Page<CommodityVO> listCommodityVOByPage(@RequestBody CommodityQueryRequest commodityQueryRequest,
                                                                 HttpServletRequest request) {
        // 管理员可以看到所有商品，普通用户只能看到自己的商品
        return commodityService.listCommodityVOByPageWithAuth(commodityQueryRequest, request);
    }

    /**
     * 分页获取当前登录用户创建的商品列表
     *
     * @param commodityQueryRequest 商品查询请求
     * @param request HTTP请求
     * @return 商品VO分页列表
     */
    @PostMapping("/my/list/page/vo")
    public Page<CommodityVO> listMyCommodityVOByPage(@RequestBody CommodityQueryRequest commodityQueryRequest,
                                                                   HttpServletRequest request) {
        return commodityService.getMyCommodityVOPage(commodityQueryRequest, request);
    }

    /**
     * 编辑商品（给用户使用）
     *
     * @param commodityEditRequest 商品编辑请求
     * @param request HTTP请求
     * @return 是否编辑成功
     */
    @PostMapping("/edit")
    public Boolean editCommodity(@RequestBody CommodityEditRequest commodityEditRequest, HttpServletRequest request) {
        return commodityService.editCommodityById(commodityEditRequest, request);
    }

    /**
     * 购买商品（根据余额情况自动支付或创建未支付订单）
     *
     * @param buyRequest 购买请求
     * @param request HTTP请求
     * @return 购买结果，包含订单信息或支付状态
     */
    @PostMapping("/buy")
    public Map<String, Object> buyCommodity(@RequestBody BuyCommodityRequest buyRequest, HttpServletRequest request) {
        return commodityService.buyCommodity(buyRequest, request);
    }
}
