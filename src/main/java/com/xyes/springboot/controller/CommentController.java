package com.xyes.springboot.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xyes.springboot.annotation.RequireRole;
import com.xyes.springboot.common.DeleteRequest;
import com.xyes.springboot.exception.ErrorCode;
import com.xyes.springboot.constant.UserConstant;
import com.xyes.springboot.exception.ThrowUtils;
import com.xyes.springboot.model.dto.comment.CommentAddRequest;
import com.xyes.springboot.model.dto.comment.CommentEditRequest;
import com.xyes.springboot.model.dto.comment.CommentQueryRequest;
import com.xyes.springboot.model.dto.comment.CommentUpdateRequest;
import com.xyes.springboot.model.entity.Comment;
import com.xyes.springboot.model.vo.CommentVO;
import com.xyes.springboot.model.vo.MyCommentVO;
import com.xyes.springboot.service.CommentService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 帖子评论管理接口
 * 提供帖子评论的创建、删除、更新、查询等功能
 *
 * * @author xujun
 */
@RestController
@RequestMapping("/comment")
@Slf4j
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    /**
     * 创建帖子评论
     *
     * @param commentAddRequest 评论添加请求
     * @param request HTTP请求
     * @return 新创建的评论ID
     */
    @PostMapping("/add")
    public Long addComment(@RequestBody CommentAddRequest commentAddRequest, HttpServletRequest request) {
        return commentService.addComment(commentAddRequest, request);
    }

    /**
     * 删除帖子评论
     *
     * @param deleteRequest 删除请求
     * @param request HTTP请求
     * @return 是否删除成功
     */
    @PostMapping("/delete")
    public Boolean deleteComment(@RequestBody DeleteRequest deleteRequest, HttpServletRequest request) {
        ThrowUtils.throwIf(deleteRequest == null || deleteRequest.getId() <= 0, ErrorCode.PARAMS_ERROR);
        return commentService.deleteCommentById(deleteRequest.getId(), request);
    }

    /**
     * 更新帖子评论（仅管理员可用）
     *
     * @param commentUpdateRequest 评论更新请求
     * @return 是否更新成功
     */
    @PostMapping("/update")
    @RequireRole(UserConstant.ADMIN_ROLE)
    public Boolean updateComment(@RequestBody CommentUpdateRequest commentUpdateRequest) {
        return commentService.updateCommentById(commentUpdateRequest);
    }

    /**
     * 根据ID获取帖子评论（封装类）
     *
     * @param id 评论ID
     * @param request HTTP请求
     * @return 评论VO
     */
    @GetMapping("/get/vo")
    public CommentVO getCommentVOById(long id, HttpServletRequest request) {
        ThrowUtils.throwIf(id <= 0, ErrorCode.PARAMS_ERROR);
        Comment comment = commentService.getById(id);
        ThrowUtils.throwIf(comment == null, ErrorCode.NOT_FOUND_ERROR);
        return commentService.getCommentVO(comment, request);
    }

    /**
     * 分页获取帖子评论列表（仅管理员可用）
     *
     * @param commentQueryRequest 评论查询请求
     * @return 评论分页列表
     */
    @PostMapping("/list/page")
    @RequireRole(UserConstant.ADMIN_ROLE)
    public Page<Comment> listCommentByPage(@RequestBody CommentQueryRequest commentQueryRequest) {
        long current = commentQueryRequest.getCurrent();
        long size = commentQueryRequest.getPageSize();
        Page<Comment> commentPage = commentService.page(new Page<>(current, size),
                commentService.getQueryWrapper(commentQueryRequest));
        return commentPage;
    }

    /**
     * 分页获取帖子评论列表（封装类）
     *
     * @param commentQueryRequest 评论查询请求
     * @param request HTTP请求
     * @return 评论VO分页列表
     */
    @PostMapping("/list/page/vo")
    public Page<CommentVO> listCommentVOByPage(@RequestBody CommentQueryRequest commentQueryRequest,
                                                             HttpServletRequest request) {
        long current = commentQueryRequest.getCurrent();
        long size = commentQueryRequest.getPageSize();
        ThrowUtils.throwIf(size > 2000, ErrorCode.PARAMS_ERROR);
        Page<Comment> commentPage = commentService.page(new Page<>(current, size),
                commentService.getQueryWrapper(commentQueryRequest));
        return commentService.getCommentVOPage(commentPage, request);
    }

    /**
     * 分页获取当前登录用户创建的帖子评论列表
     *
     * @param commentQueryRequest 评论查询请求
     * @param request HTTP请求
     * @return 评论VO分页列表
     */
    @PostMapping("/my/list/page/vo")
    public Page<CommentVO> listMyCommentVOByPage(@RequestBody CommentQueryRequest commentQueryRequest,
                                                               HttpServletRequest request) {
        return commentService.getMyCommentVOPage(commentQueryRequest, request);
    }

    /**
     * 编辑帖子评论（给用户使用）
     *
     * @param commentEditRequest 评论编辑请求
     * @param request HTTP请求
     * @return 是否编辑成功
     */
    @PostMapping("/edit")
    public Boolean editComment(@RequestBody CommentEditRequest commentEditRequest, HttpServletRequest request) {
        return commentService.editCommentById(commentEditRequest, request);
    }

    /**
     * 根据帖子ID获取帖子评论列表（封装类）
     *
     * @param postId 帖子ID
     * @param request HTTP请求
     * @return 评论VO列表
     */
    @GetMapping("/get/questonCommnet")
    public List<CommentVO> getCommentByPostId(long postId, HttpServletRequest request) {
        return commentService.getCommentsByPostId(postId, request);
    }

    /**
     * 获取当前登录用户创建的帖子评论列表（含帖子信息）
     *
     * @param request HTTP请求
     * @return 我的评论VO列表（含帖子信息）
     */
    @PostMapping("/myComments")
    public List<MyCommentVO> listMyComments(HttpServletRequest request) {
        return commentService.getMyCommentsWithPost(request);
    }
}
