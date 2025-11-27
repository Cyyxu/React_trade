package com.xyex.controller;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xyex.entity.model.Notice;
import com.xyex.entity.req.NoticeDTO;
import com.xyex.service.NoticeService;

import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/notice")
@Slf4j
@RequiredArgsConstructor
@Tag(name = "公告管理")
public class NoticeController {
    private final NoticeService noticeService;

    @PostMapping("/add")
    public void addNotice(@RequestBody Notice notice) {
        noticeService.addNotice(notice);
    }
    /**
     * 获取公告列表
     */
    @GetMapping("/list")
    public Page<Notice> listNotice(NoticeDTO noticeDTO) {
        return noticeService.listNotice(noticeDTO);
    }
    /**
     * 获取公告详情
     */
    @GetMapping("/{id}")
    public Notice getNoticeDetail(@PathVariable Long id) {
        return noticeService.getNoticeDetail(id);
    }
    /**
     * 更新公告
     */
    @PutMapping("/update")
    public void updateNotice(@RequestBody Notice notice) {
        noticeService.updateNotice(notice);
    }

    /**
     * 批量删除公告
     */
    @DeleteMapping("/delete")
    public void deleteNoticeBatch(@RequestBody List<Long> ids) {
        noticeService.deleteNoticeBatch(ids);
    }

}
