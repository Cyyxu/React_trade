package com.xyes.springboot.model.dto.comment;

import lombok.Data;

import java.io.Serializable;

/**
 * 更新请求
 */
@Data
public class CommentUpdateRequest implements Serializable {

    /**
     * 评论 ID
     */
    private Long id;


    /**
     * 面经帖子 ID
     */
    private Long postId;

    /**
     * 用户 ID
     */
    private Long userId;

    /**
     * 评论内容
     */
    private String content;

    /**
     * 父评论 ID，支持多级嵌套回复
     */
    private Long parentId;


    private static final long serialVersionUID = 1L;
}