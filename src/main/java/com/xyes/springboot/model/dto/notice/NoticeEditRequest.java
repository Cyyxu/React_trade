package com.xyes.springboot.model.dto.notice;

import lombok.Data;

import java.io.Serializable;


@Data
public class NoticeEditRequest implements Serializable {

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
}