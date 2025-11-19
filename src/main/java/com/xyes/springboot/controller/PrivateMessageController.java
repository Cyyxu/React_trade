package com.xyes.springboot.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xyes.springboot.annotation.RequireRole;
import com.xyes.springboot.common.DeleteRequest;
import com.xyes.springboot.constant.UserConstant;
import com.xyes.springboot.model.dto.privateMessage.PrivateMessageAddRequest;
import com.xyes.springboot.model.dto.privateMessage.PrivateMessageEditRequest;
import com.xyes.springboot.model.dto.privateMessage.PrivateMessageQueryRequest;
import com.xyes.springboot.model.dto.privateMessage.PrivateMessageUpdateRequest;
import com.xyes.springboot.model.entity.PrivateMessage;
import com.xyes.springboot.model.vo.PrivateMessageVO;
import com.xyes.springboot.service.PrivateMessageService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * 私信管理接口
 * 提供私信的创建、删除、更新、查询等功能
 *
 * * @author xujun
 */
@RestController
@RequestMapping("/privateMessage")
@Slf4j
@RequiredArgsConstructor
public class PrivateMessageController {

    private final PrivateMessageService privateMessageService;

    /**
     * 创建私信
     *
     * @param privateMessageAddRequest 私信添加请求
     * @param request HTTP请求
     * @return 新创建的私信ID
     */
    @PostMapping("/add")
    public Long addPrivateMessage(@RequestBody PrivateMessageAddRequest privateMessageAddRequest, HttpServletRequest request) {
        return privateMessageService.addPrivateMessage(privateMessageAddRequest, request);
    }

    /**
     * 删除私信
     *
     * @param deleteRequest 删除请求
     * @param request HTTP请求
     * @return 是否删除成功
     */
    @PostMapping("/delete")
    public Boolean deletePrivateMessage(@RequestBody DeleteRequest deleteRequest, HttpServletRequest request) {
        return privateMessageService.deletePrivateMessageById(deleteRequest.getId(), request);
    }

    /**
     * 更新私信（仅管理员可用）
     *
     * @param privateMessageUpdateRequest 私信更新请求
     * @return 是否更新成功
     */
    @PostMapping("/update")
    @RequireRole(UserConstant.ADMIN_ROLE)
    public Boolean updatePrivateMessage(@RequestBody PrivateMessageUpdateRequest privateMessageUpdateRequest) {
        return privateMessageService.updatePrivateMessageById(privateMessageUpdateRequest);
    }

    /**
     * 根据ID获取私信（封装类）
     *
     * @param id 私信ID
     * @param request HTTP请求
     * @return 私信VO
     */
    @GetMapping("/get/vo")
    public PrivateMessageVO getPrivateMessageVOById(long id, HttpServletRequest request) {
        return privateMessageService.getPrivateMessageVOById(id, request);
    }

    /**
     * 分页获取私信列表（仅管理员可用）
     *
     * @param privateMessageQueryRequest 私信查询请求
     * @return 私信分页列表
     */
    @PostMapping("/list/page")
    @RequireRole(UserConstant.ADMIN_ROLE)
    public Page<PrivateMessage> listPrivateMessageByPage(@RequestBody PrivateMessageQueryRequest privateMessageQueryRequest) {
        return privateMessageService.listPrivateMessageByPage(privateMessageQueryRequest);
    }

    /**
     * 分页获取私信列表（封装类）
     *
     * @param privateMessageQueryRequest 私信查询请求
     * @param request HTTP请求
     * @return 私信VO分页列表
     */
    @PostMapping("/list/page/vo")
    public Page<PrivateMessageVO> listPrivateMessageVOByPage(@RequestBody PrivateMessageQueryRequest privateMessageQueryRequest,
                                                               HttpServletRequest request) {
        return privateMessageService.listPrivateMessageVOByPage(privateMessageQueryRequest, request);
    }

    /**
     * 分页获取当前登录用户创建的私信列表
     *
     * @param privateMessageQueryRequest 私信查询请求
     * @param request HTTP请求
     * @return 私信VO分页列表
     */
    @PostMapping("/my/list/page/vo")
    public Page<PrivateMessageVO> listMyPrivateMessageVOByPage(@RequestBody PrivateMessageQueryRequest privateMessageQueryRequest,
                                                                 HttpServletRequest request) {
        return privateMessageService.getMyPrivateMessageVOPage(privateMessageQueryRequest, request);
    }

    /**
     * 编辑私信（给用户使用）
     *
     * @param privateMessageEditRequest 私信编辑请求
     * @param request HTTP请求
     * @return 是否编辑成功
     */
    @PostMapping("/edit")
    public Boolean editPrivateMessage(@RequestBody PrivateMessageEditRequest privateMessageEditRequest, HttpServletRequest request) {
        return privateMessageService.editPrivateMessageById(privateMessageEditRequest, request);
    }
}
