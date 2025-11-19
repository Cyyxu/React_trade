package com.xyes.springboot.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xyes.springboot.exception.ErrorCode;
import com.xyes.springboot.exception.BusinessException;
import com.xyes.springboot.exception.ThrowUtils;
import com.xyes.springboot.mapper.PostFavourMapper;
import com.xyes.springboot.model.dto.post.PostQueryRequest;
import com.xyes.springboot.model.dto.postfavour.PostFavourAddRequest;
import com.xyes.springboot.model.dto.postfavour.PostFavourQueryRequest;
import com.xyes.springboot.model.entity.Post;
import com.xyes.springboot.model.entity.PostFavour;
import com.xyes.springboot.model.entity.User;
import com.xyes.springboot.model.vo.PostVO;
import com.xyes.springboot.service.PostFavourService;
import com.xyes.springboot.service.PostService;
import com.xyes.springboot.service.UserService;
import org.springframework.aop.framework.AopContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;

/**
 * 帖子收藏服务实现
 */
@Service
@RequiredArgsConstructor
public class PostFavourServiceImpl extends ServiceImpl<PostFavourMapper, PostFavour>
        implements PostFavourService {

    private final PostService postService;
    private final UserService userService;
    /**
     * 帖子收藏
     *
     * @param postId
     * @param loginUser
     * @return
     */
    @Override
    public int doPostFavour(long postId, User loginUser) {
        // 判断是否存在
        Post post = postService.getById(postId);
        if (post == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        // 是否已帖子收藏
        long userId = loginUser.getId();
        // 每个用户串行帖子收藏
        // 锁必须要包裹住事务方法
        PostFavourService postFavourService = (PostFavourService) AopContext.currentProxy();
        synchronized (String.valueOf(userId).intern()) {
            return postFavourService.doPostFavourInner(userId, postId);
        }
    }
    @Override
    public Page<Post> listFavourPostByPage(IPage<Post> page, Wrapper<Post> queryWrapper, long favourUserId) {
        if (favourUserId <= 0) {
            return new Page<>();
        }
        return baseMapper.listFavourPostByPage(page, queryWrapper, favourUserId);
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
    public int doPostFavourInner(long userId, long postId) {
        PostFavour postFavour = new PostFavour();
        postFavour.setUserId(userId);
        postFavour.setPostId(postId);
        QueryWrapper<PostFavour> postFavourQueryWrapper = new QueryWrapper<>(postFavour);
        PostFavour oldPostFavour = this.getOne(postFavourQueryWrapper);
        boolean result;
        // 已收藏
        if (oldPostFavour != null) {
            result = this.remove(postFavourQueryWrapper);
            if (result) {
                // 帖子收藏数 - 1
                result = postService.update()
                        .eq("id", postId)
                        .gt("favourNum", 0)
                        .setSql("favourNum = favourNum - 1")
                        .update();
                return result ? -1 : 0;
            } else {
                throw new BusinessException(ErrorCode.SYSTEM_ERROR);
            }
        } else {
            // 未帖子收藏
            result = this.save(postFavour);
            if (result) {
                // 帖子收藏数 + 1
                result = postService.update()
                        .eq("id", postId)
                        .setSql("favourNum = favourNum + 1")
                        .update();
                return result ? 1 : 0;
            } else {
                throw new BusinessException(ErrorCode.SYSTEM_ERROR);
            }
        }
    }

    /**
     * 收藏/取消收藏（业务逻辑）
     *
     * @param postFavourAddRequest
     * @param request
     * @return resultNum 收藏变化数
     */
    @Override
    public Integer doPostFavourWithRequest(PostFavourAddRequest postFavourAddRequest, HttpServletRequest request) {
        ThrowUtils.throwIf(postFavourAddRequest == null || postFavourAddRequest.getPostId() <= 0, ErrorCode.PARAMS_ERROR);
        
        // 登录才能操作
        User loginUser = userService.getLoginUser(request);
        long postId = postFavourAddRequest.getPostId();
        
        return doPostFavour(postId, loginUser);
    }

    /**
     * 获取我收藏的帖子列表
     *
     * @param postQueryRequest
     * @param request
     * @return
     */
    @Override
    public Page<PostVO> getMyFavourPostVOPage(PostQueryRequest postQueryRequest, HttpServletRequest request) {
        ThrowUtils.throwIf(postQueryRequest == null, ErrorCode.PARAMS_ERROR);
        
        // 获取当前登录用户
        User loginUser = userService.getLoginUser(request);
        
        long current = postQueryRequest.getCurrent();
        long size = postQueryRequest.getPageSize();
        
        // 限制爬虫
        ThrowUtils.throwIf(size > 2000, ErrorCode.PARAMS_ERROR);
        
        // 查询收藏的帖子
        Page<Post> postPage = listFavourPostByPage(
                new Page<>(current, size),
                postService.getQueryWrapper(postQueryRequest),
                loginUser.getId()
        );
        
        // 获取封装类
        return postService.getPostVOPage(postPage, request);
    }

    /**
     * 获取用户收藏的帖子列表
     *
     * @param postFavourQueryRequest
     * @param request
     * @return
     */
    @Override
    public Page<PostVO> getFavourPostVOPage(PostFavourQueryRequest postFavourQueryRequest, HttpServletRequest request) {
        ThrowUtils.throwIf(postFavourQueryRequest == null, ErrorCode.PARAMS_ERROR);
        
        long current = postFavourQueryRequest.getCurrent();
        long size = postFavourQueryRequest.getPageSize();
        Long userId = postFavourQueryRequest.getUserId();
        
        // 限制爬虫
        ThrowUtils.throwIf(size > 2000 || userId == null, ErrorCode.PARAMS_ERROR);
        
        // 查询收藏的帖子
        Page<Post> postPage = listFavourPostByPage(
                new Page<>(current, size),
                postService.getQueryWrapper(postFavourQueryRequest.getPostQueryRequest()),
                userId
        );
        
        // 获取封装类
        return postService.getPostVOPage(postPage, request);
    }

}
