package com.xyes.springboot.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xyes.springboot.annotation.RequireRole;
import com.xyes.springboot.common.DeleteRequest;
import com.xyes.springboot.exception.ErrorCode;
import com.xyes.springboot.constant.UserConstant;
import com.xyes.springboot.exception.ThrowUtils;
import com.xyes.springboot.model.dto.post.PostAddRequest;
import com.xyes.springboot.model.dto.post.PostEditRequest;
import com.xyes.springboot.model.dto.post.PostQueryRequest;
import com.xyes.springboot.model.dto.post.PostUpdateRequest;
import com.xyes.springboot.model.entity.Post;
import com.xyes.springboot.model.vo.PostVO;
import com.xyes.springboot.service.PostService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * 帖子管理接口
 * 提供帖子的创建、删除、更新、查询以及搜索等功能
 *
 * * @author xujun
 */
@RestController
@RequestMapping("/post")
@Slf4j
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    /**
     * 创建帖子
     *
     * @param postAddRequest 帖子添加请求
     * @param request HTTP请求
     * @return 新创建的帖子ID
     */
    @PostMapping("/add")
    public Long addPost(@RequestBody PostAddRequest postAddRequest, HttpServletRequest request) {
        return postService.addPost(postAddRequest, request);
    }

    /**
     * 删除帖子
     *
     * @param deleteRequest 删除请求
     * @param request HTTP请求
     * @return 是否删除成功
     */
    @PostMapping("/delete")
    public Boolean deletePost(@RequestBody DeleteRequest deleteRequest, HttpServletRequest request) {
        ThrowUtils.throwIf(deleteRequest == null || deleteRequest.getId() <= 0, ErrorCode.PARAMS_ERROR);
        return postService.deletePostById(deleteRequest.getId(), request);
    }

    /**
     * 更新帖子（仅管理员）
     *
     * @param postUpdateRequest 帖子更新请求
     * @return 是否更新成功
     */
    @PostMapping("/update")
    @RequireRole(UserConstant.ADMIN_ROLE)
    public Boolean updatePost(@RequestBody PostUpdateRequest postUpdateRequest) {
        return postService.updatePostById(postUpdateRequest);
    }

    /**
     * 根据ID获取帖子（封装类）
     *
     * @param id 帖子ID
     * @param request HTTP请求
     * @return 帖子VO
     */
    @GetMapping("/get/vo")
    public PostVO getPostVOById(long id, HttpServletRequest request) {
        ThrowUtils.throwIf(id <= 0, ErrorCode.PARAMS_ERROR);
        Post post = postService.getById(id);
        ThrowUtils.throwIf(post == null, ErrorCode.NOT_FOUND_ERROR);
        return postService.getPostVO(post, request);
    }

    /**
     * 分页获取帖子列表（仅管理员）
     *
     * @param postQueryRequest 帖子查询请求
     * @return 帖子分页列表
     */
    @PostMapping("/list/page")
    @RequireRole(UserConstant.ADMIN_ROLE)
    public Page<Post> listPostByPage(@RequestBody PostQueryRequest postQueryRequest) {
        return postService.listPostByPage(postQueryRequest);
    }

    /**
     * 分页获取帖子列表（封装类）
     *
     * @param postQueryRequest 帖子查询请求
     * @param request HTTP请求
     * @return 帖子VO分页列表
     */
    @PostMapping("/list/page/vo")
    public Page<PostVO> listPostVOByPage(@RequestBody PostQueryRequest postQueryRequest,
                                   HttpServletRequest request) {
        return postService.listPostVOByPage(postQueryRequest, request);
    }

    /**
     * 分页获取当前用户创建的帖子列表
     *
     * @param postQueryRequest 帖子查询请求
     * @param request HTTP请求
     * @return 帖子VO分页列表
     */
    @PostMapping("/my/list/page/vo")
    public Page<PostVO> listMyPostVOByPage(@RequestBody PostQueryRequest postQueryRequest,
            HttpServletRequest request) {
        return postService.getMyPostVOPage(postQueryRequest, request);
    }

    /**
     * 分页搜索帖子（从ES查询，封装类）
     *
     * @param postQueryRequest 帖子查询请求
     * @param request HTTP请求
     * @return 帖子VO分页列表
     */
    @PostMapping("/search/page/vo")
    public Page<PostVO> searchPostVOByPage(@RequestBody PostQueryRequest postQueryRequest,
            HttpServletRequest request) {
        return postService.searchPostVOByPage(postQueryRequest, request);
    }

    /**
     * 编辑帖子（用户）
     *
     * @param postEditRequest 帖子编辑请求
     * @param request HTTP请求
     * @return 是否编辑成功
     */
    @PostMapping("/edit")
    public Boolean editPost(@RequestBody PostEditRequest postEditRequest, HttpServletRequest request) {
        return postService.editPostById(postEditRequest, request);
    }

}
