package com.xyes.springboot.controller;

import com.xyes.springboot.model.dto.postthumb.PostThumbAddRequest;
import com.xyes.springboot.service.PostThumbService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * 帖子点赞管理接口
 * 提供帖子点赞、取消点赞等功能
 *
 * * @author xujun
 */
@RestController
@RequestMapping("/post_thumb")
@Slf4j
@RequiredArgsConstructor
public class PostThumbController {

    private final PostThumbService postThumbService;

    /**
     * 点赞/取消点赞帖子
     *
     * @param postThumbAddRequest 帖子点赞请求
     * @param request HTTP请求
     * @return 本次点赞变化数（1表示点赞，-1表示取消点赞，0表示无变化）
     */
    @PostMapping("/")
    public Integer doThumb(@RequestBody PostThumbAddRequest postThumbAddRequest,
            HttpServletRequest request) {
        return postThumbService.doThumbWithRequest(postThumbAddRequest, request);
    }

}
