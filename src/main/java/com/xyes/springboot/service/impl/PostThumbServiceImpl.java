package com.xyes.springboot.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xyes.springboot.exception.ErrorCode;
import com.xyes.springboot.exception.BusinessException;
import com.xyes.springboot.exception.ThrowUtils;
import com.xyes.springboot.mapper.PostThumbMapper;
import com.xyes.springboot.model.dto.postthumb.PostThumbAddRequest;
import com.xyes.springboot.model.entity.Post;
import com.xyes.springboot.model.entity.PostThumb;
import com.xyes.springboot.model.entity.User;
import com.xyes.springboot.service.PostService;
import com.xyes.springboot.service.PostThumbService;
import com.xyes.springboot.service.UserService;
import org.springframework.aop.framework.AopContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;

/**
 * 帖子点赞服务实现
 *
 */
@Service
@RequiredArgsConstructor
public class PostThumbServiceImpl extends ServiceImpl<PostThumbMapper, PostThumb>
        implements PostThumbService {

    private final PostService postService;
    private final UserService userService;

    /**
     * 点赞
     *
     * @param postId
     * @param loginUser
     * @return
     */
    @Override
    public int doPostThumb(long postId, User loginUser) {
        // 判断实体是否存在，根据类别获取实体
        Post post = postService.getById(postId);
        if (post == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        // 是否已点赞
        long userId = loginUser.getId();
        // 每个用户串行点赞
        // 锁必须要包裹住事务方法
        PostThumbService postThumbService = (PostThumbService) AopContext.currentProxy();
        synchronized (String.valueOf(userId).intern()) {
            return postThumbService.doPostThumbInner(userId, postId);
        }
    }

    /**
     * 封装了事务的方法
     *
     * @param userId
     * @param postId
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int doPostThumbInner(long userId, long postId) {
        PostThumb postThumb = new PostThumb();
        postThumb.setUserId(userId);
        postThumb.setPostId(postId);
        QueryWrapper<PostThumb> thumbQueryWrapper = new QueryWrapper<>(postThumb);
        PostThumb oldPostThumb = this.getOne(thumbQueryWrapper);
        boolean result;
        // 已点赞
        if (oldPostThumb != null) {
            result = this.remove(thumbQueryWrapper);
            if (result) {
                // 点赞数 - 1
                result = postService.update()
                        .eq("id", postId)
                        .gt("thumbNum", 0)
                        .setSql("thumbNum = thumbNum - 1")
                        .update();
                return result ? -1 : 0;
            } else {
                throw new BusinessException(ErrorCode.SYSTEM_ERROR);
            }
        } else {
            // 未点赞
            result = this.save(postThumb);
            if (result) {
                // 点赞数 + 1
                result = postService.update()
                        .eq("id", postId)
                        .setSql("thumbNum = thumbNum + 1")
                        .update();
                return result ? 1 : 0;
            } else {
                throw new BusinessException(ErrorCode.SYSTEM_ERROR);
            }
        }
    }

    /**
     * 点赞/取消点赞（业务逻辑）
     *
     * @param postThumbAddRequest
     * @param request
     * @return resultNum 本次点赞变化数
     */
    @Override
    public Integer doThumbWithRequest(PostThumbAddRequest postThumbAddRequest, HttpServletRequest request) {
        ThrowUtils.throwIf(postThumbAddRequest == null || postThumbAddRequest.getPostId() <= 0, ErrorCode.PARAMS_ERROR);
        
        // 登录才能点赞
        User loginUser = userService.getLoginUser(request);
        long postId = postThumbAddRequest.getPostId();
        
        return doPostThumb(postId, loginUser);
    }

}
