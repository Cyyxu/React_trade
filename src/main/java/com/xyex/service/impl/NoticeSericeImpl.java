package com.xyex.service.impl;

import java.util.List;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xyex.entity.model.Notice;
import com.xyex.entity.req.NoticeDTO;
import com.xyex.infrastructure.model.BasicServiceImpl;
import com.xyex.mapper.NoticeMapper;
import com.xyex.service.NoticeService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class NoticeSericeImpl extends BasicServiceImpl<NoticeMapper,Notice> implements NoticeService{
    
    @Override
    public void addNotice(Notice notice) {
        this.save(notice);
    }

    @Override
    public Page<Notice> listNotice(NoticeDTO noticeDTO) {
        return this.getBaseMapper().selectPage(noticeDTO.createPage(), noticeDTO.createQuery());
    }

    @Override
    public void updateNotice(Notice notice) {
        this.updateById(notice);
    }

    @Override
    public Notice getNoticeDetail(Long id) {
        return this.getById(id);
    }

    @Override
    public void deleteNoticeBatch(List<Long> ids) {
        this.removeByIds(ids);
    }

}
