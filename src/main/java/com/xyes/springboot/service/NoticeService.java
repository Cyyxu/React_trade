package com.xyes.springboot.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xyes.springboot.model.dto.notice.*;
import com.xyes.springboot.model.entity.Notice;
import com.xyes.springboot.model.vo.NoticeVO;

import javax.servlet.http.HttpServletRequest;

/**
 * 公告服务
 *
 */
public interface NoticeService extends IService<Notice> {

    /**
     * 校验数据
     *
     * @param notice
     * @param add 对创建的数据进行校验
     */
    void validNotice(Notice notice, boolean add);

    /**
     * 获取查询条件
     *
     * @param noticeQueryRequest
     * @return
     */
    QueryWrapper<Notice> getQueryWrapper(NoticeQueryRequest noticeQueryRequest);
    
    /**
     * 获取公告封装
     *
     * @param notice
     * @param request
     * @return
     */
    NoticeVO getNoticeVO(Notice notice, HttpServletRequest request);

    /**
     * 分页获取公告封装
     *
     * @param noticePage
     * @param request
     * @return
     */
    Page<NoticeVO> getNoticeVOPage(Page<Notice> noticePage, HttpServletRequest request);

    /**
     * 创建公告（业务逻辑）
     *
     * @param noticeAddRequest
     * @param request
     * @return 新公告ID
     */
    Long addNotice(NoticeAddRequest noticeAddRequest, HttpServletRequest request);

    /**
     * 删除公告（包含权限校验）
     *
     * @param id
     * @param request
     * @return
     */
    Boolean deleteNoticeById(Long id, HttpServletRequest request);

    /**
     * 更新公告（仅管理员）
     *
     * @param noticeUpdateRequest
     * @return
     */
    Boolean updateNoticeById(NoticeUpdateRequest noticeUpdateRequest);

    /**
     * 分页获取当前用户的公告列表
     *
     * @param noticeQueryRequest
     * @param request
     * @return
     */
    Page<NoticeVO> getMyNoticeVOPage(NoticeQueryRequest noticeQueryRequest, HttpServletRequest request);

    /**
     * 分页获取公告列表（实体类）
     *
     * @param noticeQueryRequest
     * @return
     */
    Page<Notice> listNoticeByPage(NoticeQueryRequest noticeQueryRequest);

    /**
     * 分页获取公告列表（封装类）
     *
     * @param noticeQueryRequest
     * @param request
     * @return
     */
    Page<NoticeVO> listNoticeVOByPage(NoticeQueryRequest noticeQueryRequest, HttpServletRequest request);
}
