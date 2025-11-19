package com.xyes.springboot.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xyes.springboot.annotation.RequireRole;
import com.xyes.springboot.common.DeleteRequest;
import com.xyes.springboot.exception.ErrorCode;
import com.xyes.springboot.constant.UserConstant;
import com.xyes.springboot.exception.ThrowUtils;
import com.xyes.springboot.model.dto.barrage.BarrageAddRequest;
import com.xyes.springboot.model.dto.barrage.BarrageQueryRequest;
import com.xyes.springboot.model.dto.barrage.BarrageUpdateRequest;
import com.xyes.springboot.model.entity.Barrage;
import com.xyes.springboot.model.vo.BarrageVO;
import com.xyes.springboot.service.BarrageService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;


/**
 * 弹幕管理接口
 * 提供弹幕的创建、删除、更新、查询等功能
 *
 * * * @author xujun
 */
@RestController
@RequestMapping("/barrage")
@Slf4j
@RequiredArgsConstructor
public class BarrageController {

    private final BarrageService barrageService;

    /**
     * 创建弹幕
     *
     * @param barrageAddRequest 弹幕添加请求
     * @param request HTTP请求
     * @return 新创建的弹幕ID
     */
    @PostMapping("/add")
    @RequireRole
    public Long addBarrage(@RequestBody BarrageAddRequest barrageAddRequest, HttpServletRequest request) {
        return barrageService.addBarrage(barrageAddRequest, request);
    }

    /**
     * 删除弹幕
     *
     * @param deleteRequest 删除请求
     * @param request HTTP请求
     * @return 是否删除成功
     */
    @PostMapping("/delete")
    public Boolean deleteBarrage(@RequestBody DeleteRequest deleteRequest, HttpServletRequest request) {
        ThrowUtils.throwIf(deleteRequest == null || deleteRequest.getId() <= 0, ErrorCode.PARAMS_ERROR);
        return barrageService.deleteBarrageById(deleteRequest.getId(), request);
    }

    /**
     * 更新弹幕（仅管理员可用）
     *
     * @param barrageUpdateRequest 弹幕更新请求
     * @return 是否更新成功
     */
    @PostMapping("/update")
    @RequireRole(UserConstant.ADMIN_ROLE)
    public Boolean updateBarrage(@RequestBody BarrageUpdateRequest barrageUpdateRequest) {
        return barrageService.updateBarrageById(barrageUpdateRequest);
    }

    /**
     * 根据ID获取弹幕（封装类）
     *
     * @param id 弹幕ID
     * @param request HTTP请求
     * @return 弹幕VO
     */
    @GetMapping("/get/vo")
    public BarrageVO getBarrageVOById(long id, HttpServletRequest request) {
        ThrowUtils.throwIf(id <= 0, ErrorCode.PARAMS_ERROR);
        Barrage barrage = barrageService.getById(id);
        ThrowUtils.throwIf(barrage == null, ErrorCode.NOT_FOUND_ERROR);
        return barrageService.getBarrageVO(barrage, request);
    }

    /**
     * 分页获取弹幕列表（仅管理员可用）
     *
     * @param barrageQueryRequest 弹幕查询请求
     * @return 弹幕分页列表
     */
    @PostMapping("/list/page")
    @RequireRole(UserConstant.ADMIN_ROLE)
    public Page<Barrage> listBarrageByPage(@RequestBody BarrageQueryRequest barrageQueryRequest) {
        long current = barrageQueryRequest.getCurrent();
        long size = barrageQueryRequest.getPageSize();
        Page<Barrage> barragePage = barrageService.page(new Page<>(current, size),
                barrageService.getQueryWrapper(barrageQueryRequest));
        return barragePage;
    }

    /**
     * 分页获取弹幕列表（封装类）
     *
     * @param barrageQueryRequest 弹幕查询请求
     * @param request HTTP请求
     * @return 弹幕VO分页列表
     */
    @PostMapping("/list/page/vo")
    public Page<BarrageVO> listBarrageVOByPage(@RequestBody BarrageQueryRequest barrageQueryRequest,
                                                               HttpServletRequest request) {
        long current = barrageQueryRequest.getCurrent();
        long size = barrageQueryRequest.getPageSize();
        ThrowUtils.throwIf(size > 2000, ErrorCode.PARAMS_ERROR);
        Page<Barrage> barragePage = barrageService.page(new Page<>(current, size),
                barrageService.getQueryWrapper(barrageQueryRequest));
        return barrageService.getBarrageVOPage(barragePage, request);
    }

    /**
     * 分页获取当前登录用户创建的弹幕列表
     *
     * @param barrageQueryRequest 弹幕查询请求
     * @param request HTTP请求
     * @return 弹幕VO分页列表
     */
    @PostMapping("/my/list/page/vo")
    public Page<BarrageVO> listMyBarrageVOByPage(@RequestBody BarrageQueryRequest barrageQueryRequest,
                                                                 HttpServletRequest request) {
        return barrageService.getMyBarrageVOPage(barrageQueryRequest, request);
    }

}
