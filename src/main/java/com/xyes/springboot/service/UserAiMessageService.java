package com.xyes.springboot.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xyes.springboot.model.dto.userAiMessage.*;
import com.xyes.springboot.model.entity.UserAiMessage;
import com.xyes.springboot.model.vo.UserAiMessageVO;

import javax.servlet.http.HttpServletRequest;

/**
 * 用户对话表服务
 *
 */
public interface UserAiMessageService extends IService<UserAiMessage> {

    /**
     * 校验数据
     * @param userAiMessage
     * @param add 对创建的数据进行校验
     */
    void validUserAiMessage(UserAiMessage userAiMessage, boolean add);

    /**
     * 获取查询条件
     *
     * @param userAiMessageQueryRequest
     * @return
     */
    QueryWrapper<UserAiMessage> getQueryWrapper(UserAiMessageQueryRequest userAiMessageQueryRequest);
    
    /**
     * 获取用户对话表封装
     *
     * @param userAiMessage
     * @param request
     * @return
     */
    UserAiMessageVO getUserAiMessageVO(UserAiMessage userAiMessage, HttpServletRequest request);

    /**
     * 分页获取用户对话表封装
     *
     * @param userAiMessagePage
     * @param request
     * @return
     */
    Page<UserAiMessageVO> getUserAiMessageVOPage(Page<UserAiMessage> userAiMessagePage, HttpServletRequest request);

    /**
     * 创建用户对话（包含AI服务调用）
     *
     * @param userAiMessageAddRequest
     * @param request
     * @return
     */
    UserAiMessage addUserAiMessage(UserAiMessageAddRequest userAiMessageAddRequest, HttpServletRequest request);

    /**
     * 删除用户对话（包含权限校验）
     *
     * @param id
     * @param request
     * @return
     */
    Boolean deleteUserAiMessageById(Long id, HttpServletRequest request);

    /**
     * 更新用户对话（仅管理员）
     *
     * @param userAiMessageUpdateRequest
     * @return
     */
    Boolean updateUserAiMessageById(UserAiMessageUpdateRequest userAiMessageUpdateRequest);

    /**
     * 编辑用户对话（用户自己可用）
     *
     * @param userAiMessageEditRequest
     * @param request
     * @return
     */
    Boolean editUserAiMessageById(UserAiMessageEditRequest userAiMessageEditRequest, HttpServletRequest request);

    /**
     * 根据 id 获取用户对话（封装类）
     *
     * @param id
     * @param request
     * @return
     */
    UserAiMessageVO getUserAiMessageVOById(Long id, HttpServletRequest request);

    /**
     * 分页获取用户对话列表（实体类）
     *
     * @param userAiMessageQueryRequest
     * @return
     */
    Page<UserAiMessage> listUserAiMessageByPage(UserAiMessageQueryRequest userAiMessageQueryRequest);

    /**
     * 分页获取用户对话列表（封装类）
     *
     * @param userAiMessageQueryRequest
     * @param request
     * @return
     */
    Page<UserAiMessageVO> listUserAiMessageVOByPage(UserAiMessageQueryRequest userAiMessageQueryRequest, HttpServletRequest request);

    /**
     * 分页获取当前用户的对话列表
     *
     * @param userAiMessageQueryRequest
     * @param request
     * @return
     */
    Page<UserAiMessageVO> getMyUserAiMessageVOPage(UserAiMessageQueryRequest userAiMessageQueryRequest, HttpServletRequest request);
}
