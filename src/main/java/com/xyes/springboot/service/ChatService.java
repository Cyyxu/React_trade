package com.xyes.springboot.service;

import com.xyes.springboot.model.dto.privateMessage.PrivateMessageAddRequest;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

/**
 * 聊天服务
 *
 * @author xujun
 */
public interface ChatService {

    /**
     * 获取聊天会话列表
     *
     * @param request HTTP请求
     * @return 会话列表
     */
    List<Map<String, Object>> getChatSessions(HttpServletRequest request);

    /**
     * 获取与某用户的聊天历史
     *
     * @param userId 对方用户ID
     * @param page 页码
     * @param pageSize 每页大小
     * @param request HTTP请求
     * @return 聊天历史
     */
    Map<String, Object> getChatHistory(Long userId, int page, int pageSize, HttpServletRequest request);

    /**
     * 发送消息
     *
     * @param addRequest 消息添加请求
     * @param request HTTP请求
     * @return 消息信息
     */
    Map<String, Object> sendMessage(PrivateMessageAddRequest addRequest, HttpServletRequest request);

    /**
     * 标记消息为已读
     *
     * @param userId 对方用户ID
     * @param request HTTP请求
     * @return 是否成功
     */
    Boolean markAsRead(Long userId, HttpServletRequest request);

    /**
     * 删除聊天会话
     *
     * @param userId 对方用户ID
     * @param request HTTP请求
     * @return 是否成功
     */
    Boolean deleteChatSession(Long userId, HttpServletRequest request);
}
