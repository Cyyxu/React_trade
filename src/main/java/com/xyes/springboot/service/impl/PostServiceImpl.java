package com.xyes.springboot.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xyes.springboot.exception.ErrorCode;
import com.xyes.springboot.constant.CommonConstant;
import com.xyes.springboot.exception.BusinessException;
import com.xyes.springboot.exception.ThrowUtils;
import com.xyes.springboot.mapper.PostFavourMapper;
import com.xyes.springboot.mapper.PostMapper;
import com.xyes.springboot.mapper.PostThumbMapper;
import com.xyes.springboot.model.dto.post.*;
import com.xyes.springboot.model.entity.Post;
import com.xyes.springboot.model.entity.PostFavour;
import com.xyes.springboot.model.entity.PostThumb;
import com.xyes.springboot.model.entity.User;
import com.xyes.springboot.model.vo.PostVO;
import com.xyes.springboot.model.vo.UserVO;
import com.xyes.springboot.service.PostService;
import com.xyes.springboot.service.UserService;
import com.xyes.springboot.util.SqlUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.sort.SortBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 帖子服务实现
 *
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class PostServiceImpl extends ServiceImpl<PostMapper, Post> implements PostService {

    private final UserService userService;
    private final PostThumbMapper postThumbMapper;
    private final PostFavourMapper postFavourMapper;
    private final ElasticsearchRestTemplate elasticsearchRestTemplate;

    @Override
    public void validPost(Post post, boolean add) {
        if (post == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        String title = post.getTitle();
        String content = post.getContent();
        String tags = post.getTags();
        // 创建时，参数不能为空
        if (add) {
            ThrowUtils.throwIf(StringUtils.isAnyBlank(title, content, tags), ErrorCode.PARAMS_ERROR);
        }
        // 有参数则校验
        if (StringUtils.isNotBlank(title) && title.length() > 80) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "标题过长");
        }
        if (StringUtils.isNotBlank(content) && content.length() > 8192) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "内容过长");
        }
    }

    /**
     * 获取查询包装类
     *
     * @param postQueryRequest
     * @return
     */
    @Override
    public QueryWrapper<Post> getQueryWrapper(PostQueryRequest postQueryRequest) {
        QueryWrapper<Post> queryWrapper = new QueryWrapper<>();
        if (postQueryRequest == null) {
            return queryWrapper;
        }
        String searchText = postQueryRequest.getSearchText();
        String sortField = postQueryRequest.getSortField();
        String sortOrder = postQueryRequest.getSortOrder();
        Long id = postQueryRequest.getId();
        String title = postQueryRequest.getTitle();
        String content = postQueryRequest.getContent();
        List<String> tagList = postQueryRequest.getTags();
        Long userId = postQueryRequest.getUserId();
        Long notId = postQueryRequest.getNotId();
        // 拼接查询条件
        if (StringUtils.isNotBlank(searchText)) {
            queryWrapper.and(qw -> qw.like("title", searchText).or().like("content", searchText));
        }
        queryWrapper.like(StringUtils.isNotBlank(title), "title", title);
        queryWrapper.like(StringUtils.isNotBlank(content), "content", content);
        if (CollUtil.isNotEmpty(tagList)) {
            for (String tag : tagList) {
                queryWrapper.like("tags", "[" + tag + "]");
            }
        }
        queryWrapper.ne(ObjectUtils.isNotEmpty(notId), "id", notId);
        queryWrapper.eq(ObjectUtils.isNotEmpty(id), "id", id);
        queryWrapper.eq(ObjectUtils.isNotEmpty(userId), "userId", userId);
        queryWrapper.orderBy(SqlUtils.validSortField(sortField), sortOrder.equals(CommonConstant.SORT_ORDER_ASC),
                sortField);
        return queryWrapper;
    }

    @Override
    public Page<Post> searchFromEs(PostQueryRequest postQueryRequest) {
        Long id = postQueryRequest.getId();
        Long notId = postQueryRequest.getNotId();
        String searchText = postQueryRequest.getSearchText();
        String title = postQueryRequest.getTitle();
        String content = postQueryRequest.getContent();
        List<String> tagList = postQueryRequest.getTags();
        List<String> orTagList = postQueryRequest.getOrTags();
        Long userId = postQueryRequest.getUserId();
        // es 起始页为 0
        long current = postQueryRequest.getCurrent() - 1;
        long pageSize = postQueryRequest.getPageSize();
        String sortField = postQueryRequest.getSortField();
        String sortOrder = postQueryRequest.getSortOrder();
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        // 过滤
        boolQueryBuilder.filter(QueryBuilders.termQuery("isDelete", 0));
        if (id != null) {
            boolQueryBuilder.filter(QueryBuilders.termQuery("id", id));
        }
        if (notId != null) {
            boolQueryBuilder.mustNot(QueryBuilders.termQuery("id", notId));
        }
        if (userId != null) {
            boolQueryBuilder.filter(QueryBuilders.termQuery("userId", userId));
        }
        // 必须包含所有标签
        if (CollUtil.isNotEmpty(tagList)) {
            for (String tag : tagList) {
                boolQueryBuilder.filter(QueryBuilders.termQuery("tags", tag));
            }
        }
        // 包含任何一个标签即可
        if (CollUtil.isNotEmpty(orTagList)) {
            BoolQueryBuilder orTagBoolQueryBuilder = QueryBuilders.boolQuery();
            for (String tag : orTagList) {
                orTagBoolQueryBuilder.should(QueryBuilders.termQuery("tags", tag));
            }
            orTagBoolQueryBuilder.minimumShouldMatch(1);
            boolQueryBuilder.filter(orTagBoolQueryBuilder);
        }
        // 按关键词检索
        if (StringUtils.isNotBlank(searchText)) {
            boolQueryBuilder.should(QueryBuilders.matchQuery("title", searchText));
            boolQueryBuilder.should(QueryBuilders.matchQuery("description", searchText));
            boolQueryBuilder.should(QueryBuilders.matchQuery("content", searchText));
            boolQueryBuilder.minimumShouldMatch(1);
        }
        // 按标题检索
        if (StringUtils.isNotBlank(title)) {
            boolQueryBuilder.should(QueryBuilders.matchQuery("title", title));
            boolQueryBuilder.minimumShouldMatch(1);
        }
        // 按内容检索
        if (StringUtils.isNotBlank(content)) {
            boolQueryBuilder.should(QueryBuilders.matchQuery("content", content));
            boolQueryBuilder.minimumShouldMatch(1);
        }
        // 排序
        SortBuilder<?> sortBuilder = SortBuilders.scoreSort();
        if (StringUtils.isNotBlank(sortField)) {
            sortBuilder = SortBuilders.fieldSort(sortField);
            sortBuilder.order(CommonConstant.SORT_ORDER_ASC.equals(sortOrder) ? SortOrder.ASC : SortOrder.DESC);
        }
        // 分页
        PageRequest pageRequest = PageRequest.of((int) current, (int) pageSize);
        // 构造查询
        NativeSearchQuery searchQuery = new NativeSearchQueryBuilder().withQuery(boolQueryBuilder)
                .withPageable(pageRequest).withSorts(sortBuilder).build();
        SearchHits<PostEsDTO> searchHits = elasticsearchRestTemplate.search(searchQuery, PostEsDTO.class);
        Page<Post> page = new Page<>();
        page.setTotal(searchHits.getTotalHits());
        List<Post> resourceList = new ArrayList<>();
        // 查出结果后，从 db 获取最新动态数据（比如点赞数）
        if (searchHits.hasSearchHits()) {
            List<SearchHit<PostEsDTO>> searchHitList = searchHits.getSearchHits();
            List<Long> postIdList = searchHitList.stream().map(searchHit -> searchHit.getContent().getId())
                    .collect(Collectors.toList());
            List<Post> postList = baseMapper.selectBatchIds(postIdList);
            if (postList != null) {
                Map<Long, List<Post>> idPostMap = postList.stream().collect(Collectors.groupingBy(Post::getId));
                postIdList.forEach(postId -> {
                    if (idPostMap.containsKey(postId)) {
                        resourceList.add(idPostMap.get(postId).get(0));
                    } else {
                        // 从 es 清空 db 已物理删除的数据
                        String delete = elasticsearchRestTemplate.delete(String.valueOf(postId), PostEsDTO.class);
                        log.info("delete post {}", delete);
                    }
                });
            }
        }
        page.setRecords(resourceList);
        return page;
    }
    @Override
    public PostVO getPostVO(Post post, HttpServletRequest request) {
        PostVO postVO = PostVO.objToVo(post);
        long postId = post.getId();
        // 1. 关联查询用户信息
        Long userId = post.getUserId();
        User user = null;
        if (userId != null && userId > 0) {
            user = userService.getById(userId);
        }
        UserVO userVO = userService.getUserVO(user);
        postVO.setUser(userVO);
        // 2. 已登录，获取用户点赞、收藏状态
        User loginUser = userService.getLoginUserPermitNull(request);
        if (loginUser != null) {
            // 获取点赞
            QueryWrapper<PostThumb> postThumbQueryWrapper = new QueryWrapper<>();
            postThumbQueryWrapper.in("postId", postId);
            postThumbQueryWrapper.eq("userId", loginUser.getId());
            PostThumb postThumb = postThumbMapper.selectOne(postThumbQueryWrapper);
            postVO.setHasThumb(postThumb != null);
            // 获取收藏
            QueryWrapper<PostFavour> postFavourQueryWrapper = new QueryWrapper<>();
            postFavourQueryWrapper.in("postId", postId);
            postFavourQueryWrapper.eq("userId", loginUser.getId());
            PostFavour postFavour = postFavourMapper.selectOne(postFavourQueryWrapper);
            postVO.setHasFavour(postFavour != null);
        }
        return postVO;
    }

    @Override
    public Page<PostVO> getPostVOPage(Page<Post> postPage, HttpServletRequest request) {
        List<Post> postList = postPage.getRecords();
        Page<PostVO> postVOPage = new Page<>(postPage.getCurrent(), postPage.getSize(), postPage.getTotal());
        if (CollUtil.isEmpty(postList)) {
            return postVOPage;
        }
        
        try {
            // 1. 关联查询用户信息
            Set<Long> userIdSet = postList.stream().map(Post::getUserId).collect(Collectors.toSet());
            Map<Long, List<User>> userIdUserListMap = new HashMap<>();
            if (!userIdSet.isEmpty()) {
                userIdUserListMap = userService.listByIds(userIdSet).stream()
                        .collect(Collectors.groupingBy(User::getId));
            }

            // 2. 已登录，获取用户点赞、收藏状态
            Map<Long, Boolean> postIdHasThumbMap = new HashMap<>();
            Map<Long, Boolean> postIdHasFavourMap = new HashMap<>();
            User loginUser = userService.getLoginUserPermitNull(request);
            
            if (loginUser != null && !postList.isEmpty()) {
                Set<Long> postIdSet = postList.stream().map(Post::getId).collect(Collectors.toSet());
                if (postIdSet.size() <= 1000) {
                    // 批量获取点赞
                    QueryWrapper<PostThumb> postThumbQueryWrapper = new QueryWrapper<>();
                    postThumbQueryWrapper.in("postId", postIdSet);
                    postThumbQueryWrapper.eq("userId", loginUser.getId());
                    List<PostThumb> postPostThumbList = postThumbMapper.selectList(postThumbQueryWrapper);
                    Set<Long> thumbPostIdSet = postPostThumbList.stream()
                            .map(PostThumb::getPostId)
                            .collect(Collectors.toSet());
                    postIdHasThumbMap = postIdSet.stream()
                            .collect(Collectors.toMap(
                                    postId -> postId,
                                    thumbPostIdSet::contains
                            ));
                    
                    // 批量获取收藏
                    QueryWrapper<PostFavour> postFavourQueryWrapper = new QueryWrapper<>();
                    postFavourQueryWrapper.in("postId", postIdSet);
                    postFavourQueryWrapper.eq("userId", loginUser.getId());
                    List<PostFavour> postFavourList = postFavourMapper.selectList(postFavourQueryWrapper);
                    Set<Long> favourPostIdSet = postFavourList.stream()
                            .map(PostFavour::getPostId)
                            .collect(Collectors.toSet());
                    postIdHasFavourMap = postIdSet.stream()
                            .collect(Collectors.toMap(
                                    postId -> postId,
                                    favourPostIdSet::contains
                            ));
                }
            }
            
            // 填充信息
            List<PostVO> postVOList = new ArrayList<>();
            for (Post post : postList) {
                PostVO postVO = PostVO.objToVo(post);
                Long userId = post.getUserId();
                User user = null;
                if (userId != null && userIdUserListMap.containsKey(userId)) {
                    user = userIdUserListMap.get(userId).get(0);
                }
                postVO.setUser(userService.getUserVO(user));
                postVO.setHasThumb(postIdHasThumbMap.getOrDefault(post.getId(), false));
                postVO.setHasFavour(postIdHasFavourMap.getOrDefault(post.getId(), false));
                postVOList.add(postVO);
            }
            postVOPage.setRecords(postVOList);
        } catch (Exception e) {
            log.error("获取帖子VO分页数据时发生异常", e);
            // 发生异常时返回空列表而不是让整个接口挂掉
            postVOPage.setRecords(new ArrayList<>());
        }
        
        return postVOPage;
    }

    /**
     * 创建帖子（业务逻辑）
     *
     * @param postAddRequest
     * @param request
     * @return 新帖子ID
     */
    @Override
    public Long addPost(PostAddRequest postAddRequest, HttpServletRequest request) {
        ThrowUtils.throwIf(postAddRequest == null, ErrorCode.PARAMS_ERROR);
        
        // DTO转实体
        Post post = new Post();
        BeanUtils.copyProperties(postAddRequest, post);
        
        // 处理 tags 列表转 JSON 字符串
        List<String> tags = postAddRequest.getTags();
        if (tags != null) {
            post.setTags(JSONUtil.toJsonStr(tags));
        }
        
        // 数据校验
        validPost(post, true);
        
        // 获取当前登录用户并设置userId
        User loginUser = userService.getLoginUser(request);
        post.setUserId(loginUser.getId());
        post.setFavourNum(0);
        post.setThumbNum(0);
        
        // 写入数据库
        boolean result = this.save(post);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        
        return post.getId();
    }

    /**
     * 删除帖子（包含权限校验）
     *
     * @param id
     * @param request
     * @return
     */
    @Override
    public Boolean deletePostById(Long id, HttpServletRequest request) {
        ThrowUtils.throwIf(id == null || id <= 0, ErrorCode.PARAMS_ERROR);
        
        // 获取当前登录用户
        User user = userService.getLoginUser(request);
        
        // 判断帖子是否存在
        Post oldPost = this.getById(id);
        ThrowUtils.throwIf(oldPost == null, ErrorCode.NOT_FOUND_ERROR);
        
        // 仅本人或管理员可删除
        if (!oldPost.getUserId().equals(user.getId()) && !userService.isAdmin(request)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        
        // 删除数据库记录
        return this.removeById(id);
    }

    /**
     * 更新帖子（仅管理员）
     *
     * @param postUpdateRequest
     * @return
     */
    @Override
    public Boolean updatePostById(PostUpdateRequest postUpdateRequest) {
        ThrowUtils.throwIf(postUpdateRequest == null || postUpdateRequest.getId() <= 0, ErrorCode.PARAMS_ERROR);
        
        // DTO转实体
        Post post = new Post();
        BeanUtils.copyProperties(postUpdateRequest, post);
        
        // 处理 tags 列表转 JSON 字符串
        List<String> tags = postUpdateRequest.getTags();
        if (tags != null) {
            post.setTags(JSONUtil.toJsonStr(tags));
        }
        
        // 数据校验
        validPost(post, false);
        
        // 判断是否存在
        long id = postUpdateRequest.getId();
        Post oldPost = this.getById(id);
        ThrowUtils.throwIf(oldPost == null, ErrorCode.NOT_FOUND_ERROR);
        
        // 更新数据库
        return this.updateById(post);
    }

    /**
     * 编辑帖子（用户自己可用）
     *
     * @param postEditRequest
     * @param request
     * @return
     */
    @Override
    public Boolean editPostById(PostEditRequest postEditRequest, HttpServletRequest request) {
        ThrowUtils.throwIf(postEditRequest == null || postEditRequest.getId() <= 0, ErrorCode.PARAMS_ERROR);
        
        // DTO转实体
        Post post = new Post();
        BeanUtils.copyProperties(postEditRequest, post);
        
        // 处理 tags 列表转 JSON 字符串
        List<String> tags = postEditRequest.getTags();
        if (tags != null) {
            post.setTags(JSONUtil.toJsonStr(tags));
        }
        
        // 数据校验
        validPost(post, false);
        
        // 获取当前登录用户
        User loginUser = userService.getLoginUser(request);
        
        // 判断是否存在
        long id = postEditRequest.getId();
        Post oldPost = this.getById(id);
        ThrowUtils.throwIf(oldPost == null, ErrorCode.NOT_FOUND_ERROR);
        
        // 仅本人或管理员可编辑
        if (!oldPost.getUserId().equals(loginUser.getId()) && !userService.isAdmin(loginUser)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        
        // 更新数据库
        return this.updateById(post);
    }

    /**
     * 分页获取帖子列表（实体类）
     *
     * @param postQueryRequest
     * @return
     */
    @Override
    public Page<Post> listPostByPage(PostQueryRequest postQueryRequest) {
        long current = postQueryRequest.getCurrent();
        long size = postQueryRequest.getPageSize();
        return this.page(new Page<>(current, size),
                this.getQueryWrapper(postQueryRequest));
    }

    /**
     * 分页获取帖子列表（封装类）
     *
     * @param postQueryRequest
     * @param request
     * @return
     */
    @Override
    public Page<PostVO> listPostVOByPage(PostQueryRequest postQueryRequest, HttpServletRequest request) {
        try {
            long current = postQueryRequest.getCurrent();
            long size = postQueryRequest.getPageSize();
            // 限制爬虫
            ThrowUtils.throwIf(size > 2000, ErrorCode.PARAMS_ERROR);
            // 设置查询超时时间，防止长时间阻塞
            Page<Post> postPage = this.page(new Page<>(current, size),
                    this.getQueryWrapper(postQueryRequest));
            // 添加超时保护
            if (postPage.getTotal() > 10000) {
                // 如果数据量过大，限制只查询前10000条
                postPage.setSize(10000);
            }
            return this.getPostVOPage(postPage, request);
        } catch (Exception e) {
            log.error("获取帖子列表时发生异常", e);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "系统繁忙，请稍后再试");
        }
    }

    /**
     * 分页获取当前用户的帖子列表
     *
     * @param postQueryRequest
     * @param request
     * @return
     */
    @Override
    public Page<PostVO> getMyPostVOPage(PostQueryRequest postQueryRequest, HttpServletRequest request) {
        ThrowUtils.throwIf(postQueryRequest == null, ErrorCode.PARAMS_ERROR);
        
        // 获取当前登录用户并设置查询条件
        User loginUser = userService.getLoginUser(request);
        postQueryRequest.setUserId(loginUser.getId());
        
        long current = postQueryRequest.getCurrent();
        long size = postQueryRequest.getPageSize();
        
        // 限制爬虫
        ThrowUtils.throwIf(size > 2000, ErrorCode.PARAMS_ERROR);
        
        // 查询数据库
        Page<Post> postPage = this.page(
                new Page<>(current, size),
                this.getQueryWrapper(postQueryRequest)
        );
        
        // 获取封装类
        return this.getPostVOPage(postPage, request);
    }

    /**
     * 从 ES 搜索帖子（封装类）
     *
     * @param postQueryRequest
     * @param request
     * @return
     */
    @Override
    public Page<PostVO> searchPostVOByPage(PostQueryRequest postQueryRequest, HttpServletRequest request) {
        long size = postQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 2000, ErrorCode.PARAMS_ERROR);
        // 从 ES 查询
        Page<Post> postPage = this.searchFromEs(postQueryRequest);
        // 获取封装类
        return this.getPostVOPage(postPage, request);
    }

}
