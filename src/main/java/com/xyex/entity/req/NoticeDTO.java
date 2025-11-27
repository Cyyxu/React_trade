package com.xyex.entity.req;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.xyex.entity.model.Notice;
import com.xyex.infrastructure.model.PageParam;

import lombok.Data;

@Data
public class NoticeDTO extends PageParam {
    /**
     * id
     */
    private Long id;
    /**
     * 公告标题
     */
    private String noticeTitle;
    /**
     * 公告内容
     */
    private String noticeContent;
    /**
     * 创建人id
     */
    private Long noticeAdminId;
    @Override
    public QueryWrapper<Notice> createQuery() {
        return null;
    }
}
