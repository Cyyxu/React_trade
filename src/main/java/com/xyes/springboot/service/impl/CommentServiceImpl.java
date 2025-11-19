package com.xyes.springboot.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xyes.springboot.exception.ErrorCode;
import com.xyes.springboot.constant.CommonConstant;
import com.xyes.springboot.exception.BusinessException;
import com.xyes.springboot.exception.ThrowUtils;
import com.xyes.springboot.mapper.CommentMapper;
import com.xyes.springboot.model.dto.comment.CommentAddRequest;
import com.xyes.springboot.model.dto.comment.CommentEditRequest;
import com.xyes.springboot.model.dto.comment.CommentQueryRequest;
import com.xyes.springboot.model.dto.comment.CommentUpdateRequest;
import com.xyes.springboot.model.entity.Comment;
import com.xyes.springboot.model.entity.Post;
import com.xyes.springboot.model.entity.User;
import com.xyes.springboot.model.vo.CommentVO;
import com.xyes.springboot.model.vo.MyCommentVO;
import com.xyes.springboot.model.vo.UserVO;
import com.xyes.springboot.service.CommentService;
import com.xyes.springboot.service.PostService;
import com.xyes.springboot.service.UserService;
import com.xyes.springboot.util.SqlUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 帖子评论服务实现
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class CommentServiceImpl extends ServiceImpl<CommentMapper, Comment> implements CommentService {

    private final UserService userService;
    private final PostService postService;

    /**
     * 校验数据
     *
     * @param comment
     * @param add     对创建的数据进行校验
     */
    @Override
    public void validComment(Comment comment, boolean add) {
        ThrowUtils.throwIf(comment == null, ErrorCode.PARAMS_ERROR);
        String content = comment.getContent();
        Long parentId = comment.getParentId();
        // 创建数据时，参数不能为空
        if (add) {
            ThrowUtils.throwIf(StringUtils.isBlank(content), ErrorCode.PARAMS_ERROR);
            //校验父id
            if (parentId != null && parentId > 0) {
                Comment commentParent = this.getById(parentId);
                ThrowUtils.throwIf(commentParent == null, ErrorCode.NOT_FOUND_ERROR);
            }
        }
        // 修改数据时，有参数则校验
        if (StringUtils.isNotBlank(content)) {
            ThrowUtils.throwIf(content.length() > 1024, ErrorCode.PARAMS_ERROR, "评论过长");
        }
    }

    /**
     * 获取查询条件
     * @param commentQueryRequest
     * @return
     */
    @Override
    public QueryWrapper<Comment> getQueryWrapper(CommentQueryRequest commentQueryRequest) {
        QueryWrapper<Comment> queryWrapper = new QueryWrapper<>();
        if (commentQueryRequest == null) {
            return queryWrapper;
        }
        Long id = commentQueryRequest.getId();
        String content = commentQueryRequest.getContent();
        String sortField = commentQueryRequest.getSortField();
        String sortOrder = commentQueryRequest.getSortOrder();
        Long userId = commentQueryRequest.getUserId();
        // 模糊查询
        queryWrapper.like(StringUtils.isNotBlank(content), "content", content);
        // 精确查询
        queryWrapper.eq(ObjectUtils.isNotEmpty(id), "id", id);
        queryWrapper.eq(ObjectUtils.isNotEmpty(userId), "userId", userId);
        // 排序规则
        queryWrapper.orderBy(SqlUtils.validSortField(sortField),
                sortOrder.equals(CommonConstant.SORT_ORDER_ASC),
                sortField);
        return queryWrapper;
    }

    /**
     * 获取帖子评论封装
     *
     * @param comment
     * @param request
     * @return
     */
    @Override
    public CommentVO getCommentVO(Comment comment, HttpServletRequest request) {
        // 对象转封装类
        CommentVO commentVO = CommentVO.objToVo(comment);
        // 1. 关联查询用户信息
        Long userId = comment.getUserId();
        User user = null;
        if (userId != null && userId > 0) {
            user = userService.getById(userId);
        }
        UserVO userVO = userService.getUserVO(user);
        commentVO.setUser(userVO);
        return commentVO;
    }

    /**
     * 分页获取帖子评论封装
     * @param commentPage
     * @param request
     * @return
     */
    @Override
    public Page<CommentVO> getCommentVOPage(Page<Comment> commentPage, HttpServletRequest request) {
        List<Comment> commentList = commentPage.getRecords();
        Page<CommentVO> commentVOPage = new Page<>(commentPage.getCurrent(), commentPage.getSize(), commentPage.getTotal());
        if (CollUtil.isEmpty(commentList)) {
            return commentVOPage;
        }
        // 对象列表 => 封装对象列表
        List<CommentVO> commentVOList = commentList.stream().map(comment -> CommentVO.objToVo(comment)).collect(Collectors.toList());
        // region 可选
        // 1. 关联查询用户信息
        Set<Long> userIdSet = commentList.stream().map(Comment::getUserId).collect(Collectors.toSet());
        Map<Long, List<User>> userIdUserListMap = userService.listByIds(userIdSet).stream()
                .collect(Collectors.groupingBy(User::getId));
        // 填充信息
        commentVOList.forEach(commentVO -> {
            Long userId = commentVO.getUserId();
            User user = null;
            if (userIdUserListMap.containsKey(userId)) {
                user = userIdUserListMap.get(userId).get(0);
            }
            commentVO.setUser(userService.getUserVO(user));
        });
        // endregion

        commentVOPage.setRecords(commentVOList);
        return commentVOPage;
    }
    /**
     * 根据帖子Id获取评论
     * @param postId
     * @param request
     * @return
     */
    @Override
    public List<CommentVO> getCommentsByPostId(long postId, HttpServletRequest request) {
//        1.参数校验
        if (postId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Post post = postService.getById(postId);
        ThrowUtils.throwIf(post == null, ErrorCode.NOT_FOUND_ERROR);
//        2. 获取该帖子下所有的评论
        QueryWrapper<Comment> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("postId", postId);
        queryWrapper.orderByDesc("createTime");
        List<Comment> commentList = this.list(queryWrapper);
        if (commentList.isEmpty()){
            return  null;
        }
        //纪录
        HashMap<Long, CommentVO> map = new HashMap<>();
//        3.找出一级评论（顶级父Id为空）
        Iterator<Comment> iterator = commentList.iterator();
        while (iterator.hasNext()) {
            Comment comment = iterator.next();
            if (comment.getAncestorId() == null) {
                CommentVO commentVO = CommentVO.objToVo(comment);
                // 初始化 replies，防止为 null
                commentVO.setReplies(new ArrayList<>());
                // 补充信息
                User user = userService.getById(commentVO.getUserId());
                commentVO.setUser(userService.getUserVO(user));
                map.put(comment.getId(), commentVO);
                iterator.remove(); // 使用 Iterator 安全删除元素
            }
        }
        System.out.println("map="+map);
        System.out.println("mapSize="+map.size());
//        4.遍历剩下的列表
        for (Comment comment : commentList) {
            Long ancestorId = comment.getAncestorId();
            if (map.containsKey(ancestorId)) {
                CommentVO questoncomment = map.get(ancestorId);
                //添加进去
                List<CommentVO> replies = questoncomment.getReplies();
                CommentVO commentVO = CommentVO.objToVo(comment);
                //补充信息
                User user = userService.getById( commentVO.getUserId());
                commentVO.setUser(userService.getUserVO(user));
                //添加回复人
                Long parentId = comment.getParentId();
                Comment parentComment = this.getById(parentId);
                if (parentComment==null){
                    continue;
                }
                User repliedUser = userService.getById(parentComment.getUserId());
                commentVO.setRepliedUser(userService.getUserVO(repliedUser));
                replies.add(commentVO);
            }
        }
        List<CommentVO> res = new ArrayList<>();
        for (CommentVO commentVO : map.values()) {
            res.add(commentVO);
        }
        // 使用 Lambda 表达式按 createTime 降序排序
        res.sort((c1, c2) -> c2.getCreateTime().compareTo(c1.getCreateTime())); // 降序
        res.stream().forEach(commentVo->{
            if (commentVo.getReplies()!=null&&commentVo.getReplies().size()>=2){
                commentVo.getReplies().sort(Comparator.comparing(CommentVO::getCreateTime)); // 升序
            }
        });
        return res;
    }

    /**
     * 创建评论（业务逻辑）
     *
     * @param commentAddRequest
     * @param request
     * @return 新评论ID
     */
    @Override
    public Long addComment(CommentAddRequest commentAddRequest, HttpServletRequest request) {
        ThrowUtils.throwIf(commentAddRequest == null, ErrorCode.PARAMS_ERROR);
        
        // DTO转实体
        Comment comment = new Comment();
        BeanUtils.copyProperties(commentAddRequest, comment);
        
        // 数据校验
        validComment(comment, true);
        
        // 校验父ID并设置祖先ID
        Long parentId = commentAddRequest.getParentId();
        if (parentId != null && parentId > 0) {
            Comment commentParent = this.getById(parentId);
            if (commentParent.getAncestorId() == null) {
                // 二级评论
                comment.setAncestorId(commentParent.getId());
            } else {
                comment.setAncestorId(commentParent.getAncestorId());
            }
        }
        
        // 获取当前登录用户并设置userId
        User loginUser = userService.getLoginUser(request);
        comment.setUserId(loginUser.getId());
        
        // 写入数据库
        boolean result = this.save(comment);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        
        return comment.getId();
    }

    /**
     * 删除评论（包含权限校验）
     *
     * @param id
     * @param request
     * @return
     */
    @Override
    public Boolean deleteCommentById(Long id, HttpServletRequest request) {
        ThrowUtils.throwIf(id == null || id <= 0, ErrorCode.PARAMS_ERROR);
        
        // 获取当前登录用户
        User user = userService.getLoginUser(request);
        
        // 判断评论是否存在
        Comment oldComment = this.getById(id);
        ThrowUtils.throwIf(oldComment == null, ErrorCode.NOT_FOUND_ERROR);
        
        // 仅本人或管理员可删除
        if (!oldComment.getUserId().equals(user.getId()) && !userService.isAdmin(request)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        
        // 删除数据库记录
        boolean result = this.removeById(id);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        
        return true;
    }

    /**
     * 更新评论（仅管理员）
     *
     * @param commentUpdateRequest
     * @return
     */
    @Override
    public Boolean updateCommentById(CommentUpdateRequest commentUpdateRequest) {
        ThrowUtils.throwIf(commentUpdateRequest == null || commentUpdateRequest.getId() <= 0, ErrorCode.PARAMS_ERROR);
        
        // DTO转实体
        Comment comment = new Comment();
        BeanUtils.copyProperties(commentUpdateRequest, comment);
        
        // 数据校验
        validComment(comment, false);
        
        // 判断是否存在
        long id = commentUpdateRequest.getId();
        Comment oldComment = this.getById(id);
        ThrowUtils.throwIf(oldComment == null, ErrorCode.NOT_FOUND_ERROR);
        
        // 不允许修改帖子ID
        comment.setPostId(null);
        
        // 更新数据库
        boolean result = this.updateById(comment);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        
        return true;
    }

    /**
     * 编辑评论（用户自己可用）
     *
     * @param commentEditRequest
     * @param request
     * @return
     */
    @Override
    public Boolean editCommentById(CommentEditRequest commentEditRequest, HttpServletRequest request) {
        ThrowUtils.throwIf(commentEditRequest == null || commentEditRequest.getId() <= 0, ErrorCode.PARAMS_ERROR);
        
        // DTO转实体
        Comment comment = new Comment();
        BeanUtils.copyProperties(commentEditRequest, comment);
        
        // 数据校验
        validComment(comment, false);
        
        // 获取当前登录用户
        User loginUser = userService.getLoginUser(request);
        
        // 判断是否存在
        long id = commentEditRequest.getId();
        Comment oldComment = this.getById(id);
        ThrowUtils.throwIf(oldComment == null, ErrorCode.NOT_FOUND_ERROR);
        
        // 仅本人或管理员可编辑
        if (!oldComment.getUserId().equals(loginUser.getId()) && !userService.isAdmin(loginUser)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        
        // 不允许修改帖子ID
        comment.setPostId(null);
        
        // 更新数据库
        boolean result = this.updateById(comment);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        
        return true;
    }

    /**
     * 分页获取当前用户的评论列表
     *
     * @param commentQueryRequest
     * @param request
     * @return
     */
    @Override
    public Page<CommentVO> getMyCommentVOPage(CommentQueryRequest commentQueryRequest, HttpServletRequest request) {
        ThrowUtils.throwIf(commentQueryRequest == null, ErrorCode.PARAMS_ERROR);
        
        // 获取当前登录用户并设置查询条件
        User loginUser = userService.getLoginUser(request);
        commentQueryRequest.setUserId(loginUser.getId());
        
        long current = commentQueryRequest.getCurrent();
        long size = commentQueryRequest.getPageSize();
        
        // 限制爬虫
        ThrowUtils.throwIf(size > 2000, ErrorCode.PARAMS_ERROR);
        
        // 查询数据库
        Page<Comment> commentPage = this.page(
                new Page<>(current, size),
                this.getQueryWrapper(commentQueryRequest)
        );
        
        // 获取封装类
        return this.getCommentVOPage(commentPage, request);
    }

    /**
     * 获取当前用户的评论列表（含帖子信息）
     *
     * @param request
     * @return
     */
    @Override
    public List<MyCommentVO> getMyCommentsWithPost(HttpServletRequest request) {
        // 获取当前登录用户
        User loginUser = userService.getLoginUser(request);
        
        // 查询用户的所有评论
        QueryWrapper<Comment> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userId", loginUser.getId());
        queryWrapper.orderByDesc("updateTime");
        List<Comment> commentList = this.list(queryWrapper);
        
        // 为空直接返回
        if (commentList.isEmpty()) {
            return null;
        }
        
        // 获取所有关联的帖子ID
        Set<Long> postIdSet = commentList.stream()
                .map(Comment::getPostId)
                .collect(Collectors.toSet());
        
        // 批量查询帖子信息
        QueryWrapper<Post> wrapper = new QueryWrapper<>();
        wrapper.select("id", "title", "content");
        wrapper.in("id", postIdSet);
        List<Post> postList = postService.list(wrapper);
        
        // 构建帖子ID到帖子对象的映射
        Map<Long, Post> postMap = postList.stream()
                .collect(Collectors.toMap(Post::getId, post -> post));
        
        // 构建结果列表
        List<MyCommentVO> res = new ArrayList<>();
        commentList.forEach(comment -> {
            MyCommentVO myCommentVO = new MyCommentVO();
            BeanUtils.copyProperties(comment, myCommentVO);
            
            // 添加空值校验
            Post post = postMap.get(comment.getPostId());
            if (post != null) {
                myCommentVO.setPostTitle(post.getTitle());
                myCommentVO.setPostId(post.getId());
                myCommentVO.setPostContent(post.getContent());
            } else {
                // 处理关联帖子不存在的情况
                myCommentVO.setPostTitle("未知帖子");
                myCommentVO.setPostId(null);
            }
            
            res.add(myCommentVO);
        });
        
        return res;
    }

}
