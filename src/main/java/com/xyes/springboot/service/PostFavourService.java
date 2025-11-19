package com.xyes.springboot.service;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xyes.springboot.model.dto.post.PostQueryRequest;
import com.xyes.springboot.model.dto.postfavour.PostFavourAddRequest;
import com.xyes.springboot.model.dto.postfavour.PostFavourQueryRequest;
import com.xyes.springboot.model.entity.Post;
import com.xyes.springboot.model.entity.PostFavour;
import com.xyes.springboot.model.entity.User;
import com.xyes.springboot.model.vo.PostVO;

import javax.servlet.http.HttpServletRequest;

/**
 * 帖子收藏服务
 *
 */
public interface PostFavourService extends IService<PostFavour> {

    /**
     * 帖子收藏
     *
     * @param postId
     * @param loginUser
     * @return
     */
    int doPostFavour(long postId, User loginUser);

    /**
     * 分页获取用户收藏的帖子列表
     *
     * @param page
     * @param queryWrapper
     * @param favourUserId
     * @return
     */
    Page<Post> listFavourPostByPage(IPage<Post> page, Wrapper<Post> queryWrapper,
            long favourUserId);

    /**
     * 帖子收藏（内部服务）
     *
     * @param userId
     * @param postId
     * @return
     */
    int doPostFavourInner(long userId, long postId);

    /**
     * 收藏/取消收藏（业务逻辑）
     *
     * @param postFavourAddRequest
     * @param request
     * @return resultNum 收藏变化数
     */
    Integer doPostFavourWithRequest(PostFavourAddRequest postFavourAddRequest, HttpServletRequest request);

    /**
     * 获取我收藏的帖子列表
     *
     * @param postQueryRequest
     * @param request
     * @return
     */
    Page<PostVO> getMyFavourPostVOPage(PostQueryRequest postQueryRequest, HttpServletRequest request);

    /**
     * 获取用户收藏的帖子列表
     *
     * @param postFavourQueryRequest
     * @param request
     * @return
     */
    Page<PostVO> getFavourPostVOPage(PostFavourQueryRequest postFavourQueryRequest, HttpServletRequest request);
}
