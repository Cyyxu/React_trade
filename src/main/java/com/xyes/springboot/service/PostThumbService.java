package com.xyes.springboot.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xyes.springboot.model.dto.postthumb.PostThumbAddRequest;
import com.xyes.springboot.model.entity.PostThumb;
import com.xyes.springboot.model.entity.User;

import javax.servlet.http.HttpServletRequest;

/**
 * 帖子点赞服务
 *
 */
public interface PostThumbService extends IService<PostThumb> {

    /**
     * 点赞
     * @param postId
     * @param loginUser
     * @return
     */
    int doPostThumb(long postId, User loginUser);

    /**
     * 帖子点赞（内部服务）
     *
     * @param userId
     * @param postId
     * @return
     */
    int doPostThumbInner(long userId, long postId);

    /**
     * 点赞/取消点赞（业务逻辑）
     *
     * @param postThumbAddRequest
     * @param request
     * @return resultNum 本次点赞变化数
     */
    Integer doThumbWithRequest(PostThumbAddRequest postThumbAddRequest, HttpServletRequest request);
}
