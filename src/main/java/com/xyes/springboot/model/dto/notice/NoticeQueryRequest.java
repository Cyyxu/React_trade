package com.xyes.springboot.model.dto.notice;

import com.xyes.springboot.common.PageParam;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 查询公告请求
 *
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class NoticeQueryRequest extends PageParam {

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
     * 发布公告的管理员 id
     */
    private Long noticeAdminId;

    private static final long serialVersionUID = 1L;
}