package com.xyes.springboot.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xyes.springboot.annotation.RequireRole;
import com.xyes.springboot.common.DeleteRequest;
import com.xyes.springboot.exception.ErrorCode;
import com.xyes.springboot.constant.UserConstant;
import com.xyes.springboot.exception.ThrowUtils;
import com.xyes.springboot.model.dto.notice.NoticeAddRequest;
import com.xyes.springboot.model.dto.notice.NoticeQueryRequest;
import com.xyes.springboot.model.dto.notice.NoticeUpdateRequest;
import com.xyes.springboot.model.entity.Notice;
import com.xyes.springboot.model.vo.NoticeVO;
import com.xyes.springboot.service.NoticeService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * 公告管理接口
 * 提供公告的创建、删除、更新、查询等功能
 *
 * * @author xujun
 */
@RestController
@RequestMapping("/notice")
@Slf4j
@RequiredArgsConstructor
public class NoticeController {
    private final NoticeService noticeService;

    /**
     * 创建公告
     *
     * @param noticeAddRequest 公告添加请求
     * @param request HTTP请求
     * @return 新创建的公告ID
     */
    @PostMapping("/add")
    public Long addNotice(@RequestBody NoticeAddRequest noticeAddRequest, HttpServletRequest request) {
        return noticeService.addNotice(noticeAddRequest, request);
    }

    /**
     * 删除公告
     *
     * @param deleteRequest 删除请求
     * @param request HTTP请求
     * @return 是否删除成功
     */
    @PostMapping("/delete")
    public Boolean deleteNotice(@RequestBody DeleteRequest deleteRequest, HttpServletRequest request) {
        ThrowUtils.throwIf(deleteRequest == null || deleteRequest.getId() <= 0, ErrorCode.PARAMS_ERROR);
        return noticeService.deleteNoticeById(deleteRequest.getId(), request);
    }

    /**
     * 更新公告（仅管理员可用）
     *
     * @param noticeUpdateRequest 公告更新请求
     * @return 是否更新成功
     */
    @PostMapping("/update")
    @RequireRole(UserConstant.ADMIN_ROLE)
    public Boolean updateNotice(@RequestBody NoticeUpdateRequest noticeUpdateRequest) {
        return noticeService.updateNoticeById(noticeUpdateRequest);
    }

    /**
     * 根据ID获取公告（封装类）
     *
     * @param id 公告ID
     * @param request HTTP请求
     * @return 公告VO
     */
    @GetMapping("/get/vo")
    public NoticeVO getNoticeVOById(long id, HttpServletRequest request) {
        ThrowUtils.throwIf(id <= 0, ErrorCode.PARAMS_ERROR);
        Notice notice = noticeService.getById(id);
        ThrowUtils.throwIf(notice == null, ErrorCode.NOT_FOUND_ERROR);
        return noticeService.getNoticeVO(notice, request);
    }

    /**
     * 分页获取公告列表（仅管理员可用）
     *
     * @param noticeQueryRequest 公告查询请求
     * @return 公告分页列表
     */
    @PostMapping("/list/page")
    @RequireRole(UserConstant.ADMIN_ROLE)
    public Page<Notice> listNoticeByPage(@RequestBody NoticeQueryRequest noticeQueryRequest) {
        return noticeService.listNoticeByPage(noticeQueryRequest);
    }

    /**
     * 分页获取公告列表（封装类）
     *
     * @param noticeQueryRequest 公告查询请求
     * @param request HTTP请求
     * @return 公告VO分页列表
     */
    @PostMapping("/list/page/vo")
    public Page<NoticeVO> listNoticeVOByPage(@RequestBody NoticeQueryRequest noticeQueryRequest,
                                                           HttpServletRequest request) {
        return noticeService.listNoticeVOByPage(noticeQueryRequest, request);
    }

    /**
     * 分页获取当前登录用户创建的公告列表
     *
     * @param noticeQueryRequest 公告查询请求
     * @param request HTTP请求
     * @return 公告VO分页列表
     */
    @PostMapping("/my/list/page/vo")
    public Page<NoticeVO> listMyNoticeVOByPage(@RequestBody NoticeQueryRequest noticeQueryRequest,
                                                             HttpServletRequest request) {
        return noticeService.getMyNoticeVOPage(noticeQueryRequest, request);
    }
}
