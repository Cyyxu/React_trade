package com.xyes.springboot.model.dto.comment;

import com.xyes.springboot.common.PageParam;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 查询请求
 *
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class CommentQueryRequest extends PageParam {

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
    /**
     * 顶级父 ID，支持多级嵌套回复
     */
    private Long ancestorId;


    private static final long serialVersionUID = 1L;
}