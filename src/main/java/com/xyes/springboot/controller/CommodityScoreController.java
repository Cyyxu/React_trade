package com.xyes.springboot.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xyes.springboot.annotation.RequireRole;
import com.xyes.springboot.common.DeleteRequest;
import com.xyes.springboot.exception.ErrorCode;
import com.xyes.springboot.constant.UserConstant;
import com.xyes.springboot.exception.ThrowUtils;
import com.xyes.springboot.model.dto.commodityScore.CommodityScoreAddRequest;
import com.xyes.springboot.model.dto.commodityScore.CommodityScoreEditRequest;
import com.xyes.springboot.model.dto.commodityScore.CommodityScoreQueryRequest;
import com.xyes.springboot.model.dto.commodityScore.CommodityScoreUpdateRequest;
import com.xyes.springboot.model.entity.CommodityScore;
import com.xyes.springboot.model.vo.CommodityScoreVO;
import com.xyes.springboot.service.CommodityScoreService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * 商品评分管理接口
 * 提供商品评分的创建、删除、更新、查询以及平均评分计算等功能
 *
 * * @author xujun
 */
@RestController
@RequestMapping("/commodityScore")
@Slf4j
@RequiredArgsConstructor
public class CommodityScoreController {

    private final CommodityScoreService commodityScoreService;

    /**
     * 创建商品评分
     *
     * @param commodityScoreAddRequest 商品评分添加请求
     * @param request HTTP请求
     * @return 新创建的商品评分ID
     */
    @PostMapping("/add")
    public Long addCommodityScore(@RequestBody CommodityScoreAddRequest commodityScoreAddRequest, HttpServletRequest request) {
        return commodityScoreService.addCommodityScore(commodityScoreAddRequest, request);
    }

    /**
     * 删除商品评分
     *
     * @param deleteRequest 删除请求
     * @param request HTTP请求
     * @return 是否删除成功
     */
    @PostMapping("/delete")
    public Boolean deleteCommodityScore(@RequestBody DeleteRequest deleteRequest, HttpServletRequest request) {
        ThrowUtils.throwIf(deleteRequest == null || deleteRequest.getId() <= 0, ErrorCode.PARAMS_ERROR);
        return commodityScoreService.deleteCommodityScoreById(deleteRequest.getId(), request);
    }

    /**
     * 更新商品评分（仅管理员可用）
     *
     * @param commodityScoreUpdateRequest 商品评分更新请求
     * @return 是否更新成功
     */
    @PostMapping("/update")
    @RequireRole(UserConstant.ADMIN_ROLE)
    public Boolean updateCommodityScore(@RequestBody CommodityScoreUpdateRequest commodityScoreUpdateRequest) {
        return commodityScoreService.updateCommodityScoreById(commodityScoreUpdateRequest);
    }

    /**
     * 根据ID获取商品评分（封装类）
     *
     * @param id 商品评分ID
     * @param request HTTP请求
     * @return 商品评分VO
     */
    @GetMapping("/get/vo")
    public CommodityScoreVO getCommodityScoreVOById(long id, HttpServletRequest request) {
        ThrowUtils.throwIf(id <= 0, ErrorCode.PARAMS_ERROR);
        CommodityScore commodityScore = commodityScoreService.getById(id);
        ThrowUtils.throwIf(commodityScore == null, ErrorCode.NOT_FOUND_ERROR);
        return commodityScoreService.getCommodityScoreVO(commodityScore, request);
    }

    /**
     * 分页获取商品评分列表（仅管理员可用）
     *
     * @param commodityScoreQueryRequest 商品评分查询请求
     * @return 商品评分分页列表
     */
    @PostMapping("/list/page")
    @RequireRole(UserConstant.ADMIN_ROLE)
    public Page<CommodityScore> listCommodityScoreByPage(@RequestBody CommodityScoreQueryRequest commodityScoreQueryRequest) {
        long current = commodityScoreQueryRequest.getCurrent();
        long size = commodityScoreQueryRequest.getPageSize();
        Page<CommodityScore> commodityScorePage = commodityScoreService.page(new Page<>(current, size),
                commodityScoreService.getQueryWrapper(commodityScoreQueryRequest));
        return commodityScorePage;
    }

    /**
     * 分页获取商品评分列表（封装类）
     *
     * @param commodityScoreQueryRequest 商品评分查询请求
     * @param request HTTP请求
     * @return 商品评分VO分页列表
     */
    @PostMapping("/list/page/vo")
    public Page<CommodityScoreVO> listCommodityScoreVOByPage(@RequestBody CommodityScoreQueryRequest commodityScoreQueryRequest,
                                                               HttpServletRequest request) {
        long current = commodityScoreQueryRequest.getCurrent();
        long size = commodityScoreQueryRequest.getPageSize();
        ThrowUtils.throwIf(size > 2000, ErrorCode.PARAMS_ERROR);
        Page<CommodityScore> commodityScorePage = commodityScoreService.page(new Page<>(current, size),
                commodityScoreService.getQueryWrapper(commodityScoreQueryRequest));
        return commodityScoreService.getCommodityScoreVOPage(commodityScorePage, request);
    }

    /**
     * 分页获取当前登录用户创建的商品评分列表
     *
     * @param commodityScoreQueryRequest 商品评分查询请求
     * @param request HTTP请求
     * @return 商品评分VO分页列表
     */
    @PostMapping("/my/list/page/vo")
    public Page<CommodityScoreVO> listMyCommodityScoreVOByPage(@RequestBody CommodityScoreQueryRequest commodityScoreQueryRequest,
                                                                 HttpServletRequest request) {
        return commodityScoreService.getMyCommodityScoreVOPage(commodityScoreQueryRequest, request);
    }

    /**
     * 编辑商品评分（给用户使用）
     *
     * @param commodityScoreEditRequest 商品评分编辑请求
     * @param request HTTP请求
     * @return 是否编辑成功
     */
    @PostMapping("/edit")
    public Boolean editCommodityScore(@RequestBody CommodityScoreEditRequest commodityScoreEditRequest, HttpServletRequest request) {
        return commodityScoreService.editCommodityScoreById(commodityScoreEditRequest, request);
    }

    /**
     * 获取商品的平均评分
     *
     * @param commodityId 商品ID
     * @return 平均评分
     */
    @GetMapping("/averageScore")
    public Double getAverageScore(@RequestParam("commodityId") long commodityId) {
        return commodityScoreService.getAverageScore(commodityId);
    }
}
