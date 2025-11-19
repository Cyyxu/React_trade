package com.xyes.springboot.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xyes.springboot.model.dto.comment.CommentAddRequest;
import com.xyes.springboot.model.dto.comment.CommentEditRequest;
import com.xyes.springboot.model.dto.comment.CommentQueryRequest;
import com.xyes.springboot.model.dto.comment.CommentUpdateRequest;
import com.xyes.springboot.model.entity.Comment;
import com.xyes.springboot.model.vo.CommentVO;
import com.xyes.springboot.model.vo.MyCommentVO;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 帖子评论服务
 *
 */
public interface CommentService extends IService<Comment> {

    /**
     * 校验数据
     *
     * @param comment
     * @param add 对创建的数据进行校验
     */
    void validComment(Comment comment, boolean add);

    /**
     * 获取查询条件
     *
     * @param commentQueryRequest
     * @return
     */
    QueryWrapper<Comment> getQueryWrapper(CommentQueryRequest commentQueryRequest);
    
    /**
     * 获取帖子评论封装
     *
     * @param comment
     * @param request
     * @return
     */
    CommentVO getCommentVO(Comment comment, HttpServletRequest request);

    /**
     * 分页获取帖子评论封装
     *
     * @param commentPage
     * @param request
     * @return
     */
    Page<CommentVO> getCommentVOPage(Page<Comment> commentPage, HttpServletRequest request);

    /**
     * 根据帖子Id获取评论
     *
     * @param postId
     * @param request
     * @return
     */
    List<CommentVO> getCommentsByPostId(long postId, HttpServletRequest request);

    /**
     * 创建评论（业务逻辑）
     *
     * @param commentAddRequest
     * @param request
     * @return 新评论ID
     */
    Long addComment(CommentAddRequest commentAddRequest, HttpServletRequest request);

    /**
     * 删除评论（包含权限校验）
     *
     * @param id
     * @param request
     * @return
     */
    Boolean deleteCommentById(Long id, HttpServletRequest request);

    /**
     * 更新评论（仅管理员）
     *
     * @param commentUpdateRequest
     * @return
     */
    Boolean updateCommentById(CommentUpdateRequest commentUpdateRequest);

    /**
     * 编辑评论（用户自己可用）
     *
     * @param commentEditRequest
     * @param request
     * @return
     */
    Boolean editCommentById(CommentEditRequest commentEditRequest, HttpServletRequest request);

    /**
     * 分页获取当前用户的评论列表
     *
     * @param commentQueryRequest
     * @param request
     * @return
     */
    Page<CommentVO> getMyCommentVOPage(CommentQueryRequest commentQueryRequest, HttpServletRequest request);

    /**
     * 获取当前用户的评论列表（含帖子信息）
     *
     * @param request
     * @return
     */
    List<MyCommentVO> getMyCommentsWithPost(HttpServletRequest request);
}
