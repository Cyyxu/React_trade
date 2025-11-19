package com.xyes.springboot.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xyes.springboot.model.dto.post.*;
import com.xyes.springboot.model.entity.Post;
import com.xyes.springboot.model.vo.PostVO;

import javax.servlet.http.HttpServletRequest;

/**
 * 帖子服务
 *
 */
public interface PostService extends IService<Post> {

    /**
     * 校验
     *
     * @param post
     * @param add
     */
    void validPost(Post post, boolean add);

    /**
     * 获取查询条件
     *
     * @param postQueryRequest
     * @return
     */
    QueryWrapper<Post> getQueryWrapper(PostQueryRequest postQueryRequest);

    /**
     * 从 ES 查询
     *
     * @param postQueryRequest
     * @return
     */
    Page<Post> searchFromEs(PostQueryRequest postQueryRequest);

    /**
     * 获取帖子封装
     *
     * @param post
     * @param request
     * @return
     */
    PostVO getPostVO(Post post, HttpServletRequest request);

    /**
     * 分页获取帖子封装
     *
     * @param postPage
     * @param request
     * @return
     */
    Page<PostVO> getPostVOPage(Page<Post> postPage, HttpServletRequest request);

    /**
     * 创建帖子（业务逻辑）
     *
     * @param postAddRequest
     * @param request
     * @return 新帖子ID
     */
    Long addPost(PostAddRequest postAddRequest, HttpServletRequest request);

    /**
     * 删除帖子（包含权限校验）
     *
     * @param id
     * @param request
     * @return
     */
    Boolean deletePostById(Long id, HttpServletRequest request);

    /**
     * 更新帖子（仅管理员）
     *
     * @param postUpdateRequest
     * @return
     */
    Boolean updatePostById(PostUpdateRequest postUpdateRequest);

    /**
     * 编辑帖子（用户自己可用）
     *
     * @param postEditRequest
     * @param request
     * @return
     */
    Boolean editPostById(PostEditRequest postEditRequest, HttpServletRequest request);

    /**
     * 分页获取帖子列表（实体类）
     *
     * @param postQueryRequest
     * @return
     */
    Page<Post> listPostByPage(PostQueryRequest postQueryRequest);

    /**
     * 分页获取帖子列表（封装类）
     *
     * @param postQueryRequest
     * @param request
     * @return
     */
    Page<PostVO> listPostVOByPage(PostQueryRequest postQueryRequest, HttpServletRequest request);

    /**
     * 分页获取当前用户的帖子列表
     *
     * @param postQueryRequest
     * @param request
     * @return
     */
    Page<PostVO> getMyPostVOPage(PostQueryRequest postQueryRequest, HttpServletRequest request);

    /**
     * 从 ES 搜索帖子（封装类）
     *
     * @param postQueryRequest
     * @param request
     * @return
     */
    Page<PostVO> searchPostVOByPage(PostQueryRequest postQueryRequest, HttpServletRequest request);
}
