package com.xyes.springboot.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xyes.springboot.model.dto.post.PostQueryRequest;
import com.xyes.springboot.model.dto.postfavour.PostFavourAddRequest;
import com.xyes.springboot.model.dto.postfavour.PostFavourQueryRequest;
import com.xyes.springboot.model.vo.PostVO;
import com.xyes.springboot.service.PostFavourService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * 帖子收藏管理接口
 * 提供帖子收藏、取消收藏以及查询收藏列表等功能
 *
 * * @author xujun
 */
@RestController
@RequestMapping("/post_favour")
@Slf4j
@RequiredArgsConstructor
public class PostFavourController {

    private final PostFavourService postFavourService;

    /**
     * 收藏/取消收藏帖子
     *
     * @param postFavourAddRequest 帖子收藏请求
     * @param request HTTP请求
     * @return 收藏变化数（1表示收藏，-1表示取消收藏，0表示无变化）
     */
    @PostMapping("/")
    public Integer doPostFavour(@RequestBody PostFavourAddRequest postFavourAddRequest,
            HttpServletRequest request) {
        return postFavourService.doPostFavourWithRequest(postFavourAddRequest, request);
    }

    /**
     * 获取我收藏的帖子列表
     *
     * @param postQueryRequest 帖子查询请求
     * @param request HTTP请求
     * @return 帖子VO分页列表
     */
    @PostMapping("/my/list/page")
    public Page<PostVO> listMyFavourPostByPage(@RequestBody PostQueryRequest postQueryRequest,
            HttpServletRequest request) {
        return postFavourService.getMyFavourPostVOPage(postQueryRequest, request);
    }

    /**
     * 获取用户收藏的帖子列表
     *
     * @param postFavourQueryRequest 帖子收藏查询请求
     * @param request HTTP请求
     * @return 帖子VO分页列表
     */
    @PostMapping("/list/page")
    public Page<PostVO> listFavourPostByPage(@RequestBody PostFavourQueryRequest postFavourQueryRequest,
            HttpServletRequest request) {
        return postFavourService.getFavourPostVOPage(postFavourQueryRequest, request);
    }
}
