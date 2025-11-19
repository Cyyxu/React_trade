package com.xyes.springboot.model.dto.comment;

import lombok.Data;

import java.io.Serializable;

/**
 * 创建请求
 */
@Data
public class CommentAddRequest implements Serializable {


    /**
     * 面经帖子 ID
     */
    private Long postId;

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