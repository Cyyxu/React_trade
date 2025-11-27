package com.xyex.service;

import java.util.List;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xyex.entity.model.Notice;
import com.xyex.entity.req.NoticeDTO;
import com.xyex.infrastructure.model.BasicService;

public interface NoticeService extends BasicService<Notice> {
    /**
     * 添加公告
     */
    void addNotice(Notice notice);
    /**
     * 获取公告列表
     */
    Page<Notice> listNotice(NoticeDTO noticeDTO);
    /**
     * 获取公告详情
     */
    Notice getNoticeDetail(Long id);
    /**
     * 更新公告
     */
    void updateNotice(Notice notice);
    /**
     * 删除公告
     */
    void deleteNoticeBatch(List<Long> ids);

}
