package com.xyes.springboot.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xyes.springboot.annotation.RequireRole;
import com.xyes.springboot.common.DeleteRequest;
import com.xyes.springboot.exception.ErrorCode;
import com.xyes.springboot.constant.UserConstant;
import com.xyes.springboot.exception.ThrowUtils;
import com.xyes.springboot.model.dto.commodityType.CommodityTypeAddRequest;
import com.xyes.springboot.model.dto.commodityType.CommodityTypeEditRequest;
import com.xyes.springboot.model.dto.commodityType.CommodityTypeQueryRequest;
import com.xyes.springboot.model.dto.commodityType.CommodityTypeUpdateRequest;
import com.xyes.springboot.model.entity.CommodityType;
import com.xyes.springboot.model.vo.CommodityTypeVO;
import com.xyes.springboot.service.CommodityTypeService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * 商品类别管理接口
 * 提供商品类别的创建、删除、更新、查询等功能
 *
 * * @author xujun
 */
@RestController
@RequestMapping("/commodityType")
@Slf4j
@RequiredArgsConstructor
public class CommodityTypeController {

    private final CommodityTypeService commodityTypeService;

    /**
     * 创建商品类别
     *
     * @param commodityTypeAddRequest 商品类别添加请求
     * @param request HTTP请求
     * @return 新创建的商品类别ID
     */
    @PostMapping("/add")
    public Long addCommodityType(@RequestBody CommodityTypeAddRequest commodityTypeAddRequest, HttpServletRequest request) {
        return commodityTypeService.addCommodityType(commodityTypeAddRequest, request);
    }

    /**
     * 删除商品类别
     *
     * @param deleteRequest 删除请求
     * @param request HTTP请求
     * @return 是否删除成功
     */
    @PostMapping("/delete")
    public Boolean deleteCommodityType(@RequestBody DeleteRequest deleteRequest, HttpServletRequest request) {
        ThrowUtils.throwIf(deleteRequest == null || deleteRequest.getId() <= 0, ErrorCode.PARAMS_ERROR);
        return commodityTypeService.deleteCommodityTypeById(deleteRequest.getId(), request);
    }

    /**
     * 更新商品类别（仅管理员可用）
     *
     * @param commodityTypeUpdateRequest 商品类别更新请求
     * @return 是否更新成功
     */
    @PostMapping("/update")
    @RequireRole(UserConstant.ADMIN_ROLE)
    public Boolean updateCommodityType(@RequestBody CommodityTypeUpdateRequest commodityTypeUpdateRequest) {
        return commodityTypeService.updateCommodityTypeById(commodityTypeUpdateRequest);
    }

    /**
     * 根据ID获取商品类别（封装类）
     *
     * @param id 商品类别ID
     * @param request HTTP请求
     * @return 商品类别VO
     */
    @GetMapping("/get/vo")
    public CommodityTypeVO getCommodityTypeVOById(long id, HttpServletRequest request) {
        ThrowUtils.throwIf(id <= 0, ErrorCode.PARAMS_ERROR);
        CommodityType commodityType = commodityTypeService.getById(id);
        ThrowUtils.throwIf(commodityType == null, ErrorCode.NOT_FOUND_ERROR);
        return commodityTypeService.getCommodityTypeVO(commodityType, request);
    }

    /**
     * 分页获取商品类别列表（仅管理员可用）
     *
     * @param commodityTypeQueryRequest 商品类别查询请求
     * @return 商品类别分页列表
     */
    @PostMapping("/list/page")
    @RequireRole(UserConstant.ADMIN_ROLE)
    public Page<CommodityType> listCommodityTypeByPage(@RequestBody CommodityTypeQueryRequest commodityTypeQueryRequest) {
        long current = commodityTypeQueryRequest.getCurrent();
        long size = commodityTypeQueryRequest.getPageSize();
        Page<CommodityType> commodityTypePage = commodityTypeService.page(new Page<>(current, size),
                commodityTypeService.getQueryWrapper(commodityTypeQueryRequest));
        return commodityTypePage;
    }

    /**
     * 分页获取商品类别列表（封装类）
     *
     * @param commodityTypeQueryRequest 商品类别查询请求
     * @param request HTTP请求
     * @return 商品类别VO分页列表
     */
    @PostMapping("/list/page/vo")
    public Page<CommodityTypeVO> listCommodityTypeVOByPage(@RequestBody CommodityTypeQueryRequest commodityTypeQueryRequest,
                                                               HttpServletRequest request) {
        long current = commodityTypeQueryRequest.getCurrent();
        long size = commodityTypeQueryRequest.getPageSize();
        ThrowUtils.throwIf(size > 2000, ErrorCode.PARAMS_ERROR);
        Page<CommodityType> commodityTypePage = commodityTypeService.page(new Page<>(current, size),
                commodityTypeService.getQueryWrapper(commodityTypeQueryRequest));
        return commodityTypeService.getCommodityTypeVOPage(commodityTypePage, request);
    }

    /**
     * 分页获取当前登录用户创建的商品类别列表
     *
     * @param commodityTypeQueryRequest 商品类别查询请求
     * @param request HTTP请求
     * @return 商品类别VO分页列表
     */
    @PostMapping("/my/list/page/vo")
    public Page<CommodityTypeVO> listMyCommodityTypeVOByPage(@RequestBody CommodityTypeQueryRequest commodityTypeQueryRequest,
                                                                 HttpServletRequest request) {
        ThrowUtils.throwIf(commodityTypeQueryRequest == null, ErrorCode.PARAMS_ERROR);
        long current = commodityTypeQueryRequest.getCurrent();
        long size = commodityTypeQueryRequest.getPageSize();
        ThrowUtils.throwIf(size > 2000, ErrorCode.PARAMS_ERROR);
        Page<CommodityType> commodityTypePage = commodityTypeService.page(new Page<>(current, size),
                commodityTypeService.getQueryWrapper(commodityTypeQueryRequest));
        return commodityTypeService.getCommodityTypeVOPage(commodityTypePage, request);
    }

    /**
     * 编辑商品类别（给用户使用）
     *
     * @param commodityTypeEditRequest 商品类别编辑请求
     * @param request HTTP请求
     * @return 是否编辑成功
     */
    @PostMapping("/edit")
    public Boolean editCommodityType(@RequestBody CommodityTypeEditRequest commodityTypeEditRequest, HttpServletRequest request) {
        return commodityTypeService.editCommodityTypeById(commodityTypeEditRequest, request);
    }
}
