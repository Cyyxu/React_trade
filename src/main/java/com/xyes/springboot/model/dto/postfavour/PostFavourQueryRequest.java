package com.xyes.springboot.model.dto.postfavour;

import com.xyes.springboot.common.PageParam;
import com.xyes.springboot.model.dto.post.PostQueryRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;


@Data
@EqualsAndHashCode(callSuper = true)
public class PostFavourQueryRequest extends PageParam {

    /**
     * 帖子查询请求
     */
    private PostQueryRequest postQueryRequest;

    /**
     * 用户 id
     */
    private Long userId;

    private static final long serialVersionUID = 1L;
}