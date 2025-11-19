package com.xyes.springboot.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xyes.springboot.annotation.RequireRole;
import com.xyes.springboot.common.DeleteRequest;
import com.xyes.springboot.constant.UserConstant;
import com.xyes.springboot.model.dto.userAiMessage.UserAiMessageAddRequest;
import com.xyes.springboot.model.dto.userAiMessage.UserAiMessageEditRequest;
import com.xyes.springboot.model.dto.userAiMessage.UserAiMessageQueryRequest;
import com.xyes.springboot.model.dto.userAiMessage.UserAiMessageUpdateRequest;
import com.xyes.springboot.model.entity.UserAiMessage;
import com.xyes.springboot.model.vo.UserAiMessageVO;
import com.xyes.springboot.service.UserAiMessageService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * 用户AI对话管理接口
 * 提供用户与AI的对话功能，包括创建对话、查询历史记录等
 *
 * * @author xujun
 */
@RestController
@RequestMapping("/userAiMessage")
@Slf4j
@RequiredArgsConstructor
public class UserAiMessageController {

    private final UserAiMessageService userAiMessageService;

    /**
     * 创建用户AI对话（包含AI服务调用）
     *
     * @param userAiMessageAddRequest 用户AI对话添加请求
     * @param request HTTP请求
     * @return 用户AI对话实体
     */
    @PostMapping("/add")
    public UserAiMessage addUserAiMessage(@RequestBody UserAiMessageAddRequest userAiMessageAddRequest, HttpServletRequest request) {
        return userAiMessageService.addUserAiMessage(userAiMessageAddRequest, request);
    }

    /**
     * 删除用户AI对话
     *
     * @param deleteRequest 删除请求
     * @param request HTTP请求
     * @return 是否删除成功
     */
    @PostMapping("/delete")
    public Boolean deleteUserAiMessage(@RequestBody DeleteRequest deleteRequest, HttpServletRequest request) {
        return userAiMessageService.deleteUserAiMessageById(deleteRequest.getId(), request);
    }

    /**
     * 更新用户AI对话（仅管理员可用）
     *
     * @param userAiMessageUpdateRequest 用户AI对话更新请求
     * @return 是否更新成功
     */
    @PostMapping("/update")
    @RequireRole(UserConstant.ADMIN_ROLE)
    public Boolean updateUserAiMessage(@RequestBody UserAiMessageUpdateRequest userAiMessageUpdateRequest) {
        return userAiMessageService.updateUserAiMessageById(userAiMessageUpdateRequest);
    }

    /**
     * 根据ID获取用户AI对话（封装类）
     *
     * @param id 对话ID
     * @param request HTTP请求
     * @return 用户AI对话VO
     */
    @GetMapping("/get/vo")
    public UserAiMessageVO getUserAiMessageVOById(long id, HttpServletRequest request) {
        return userAiMessageService.getUserAiMessageVOById(id, request);
    }

    /**
     * 分页获取用户AI对话列表（仅管理员可用）
     *
     * @param userAiMessageQueryRequest 用户AI对话查询请求
     * @return 用户AI对话分页列表
     */
    @PostMapping("/list/page")
    @RequireRole(UserConstant.ADMIN_ROLE)
    public Page<UserAiMessage> listUserAiMessageByPage(@RequestBody UserAiMessageQueryRequest userAiMessageQueryRequest) {
        return userAiMessageService.listUserAiMessageByPage(userAiMessageQueryRequest);
    }

    /**
     * 分页获取用户AI对话列表（封装类）
     *
     * @param userAiMessageQueryRequest 用户AI对话查询请求
     * @param request HTTP请求
     * @return 用户AI对话VO分页列表
     */
    @PostMapping("/list/page/vo")
    public Page<UserAiMessageVO> listUserAiMessageVOByPage(@RequestBody UserAiMessageQueryRequest userAiMessageQueryRequest,
                                                                         HttpServletRequest request) {
        return userAiMessageService.listUserAiMessageVOByPage(userAiMessageQueryRequest, request);
    }

    /**
     * 分页获取当前登录用户创建的AI对话列表
     *
     * @param userAiMessageQueryRequest 用户AI对话查询请求
     * @param request HTTP请求
     * @return 用户AI对话VO分页列表
     */
    @PostMapping("/my/list/page/vo")
    public Page<UserAiMessageVO> listMyUserAiMessageVOByPage(@RequestBody UserAiMessageQueryRequest userAiMessageQueryRequest,
                                                                           HttpServletRequest request) {
        return userAiMessageService.getMyUserAiMessageVOPage(userAiMessageQueryRequest, request);
    }

    /**
     * 编辑用户AI对话（给用户使用）
     *
     * @param userAiMessageEditRequest 用户AI对话编辑请求
     * @param request HTTP请求
     * @return 是否编辑成功
     */
    @PostMapping("/edit")
    public Boolean editUserAiMessage(@RequestBody UserAiMessageEditRequest userAiMessageEditRequest, HttpServletRequest request) {
        return userAiMessageService.editUserAiMessageById(userAiMessageEditRequest, request);
    }

}