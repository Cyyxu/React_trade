package com.xyes.springboot.controller;

import com.xyes.springboot.annotation.RequireRole;
import com.xyes.springboot.constant.UserConstant;
import com.xyes.springboot.model.dto.privateMessage.PrivateMessageAddRequest;
import com.xyes.springboot.service.ChatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

/**
 * 聊天接口
 * 提供聊天会话、消息历史、发送消息等功能
 *
 * @author xujun
 */
@RestController
@RequestMapping("/chat")
@Slf4j
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;

    /**
     * 获取聊天会话列表
     *
     * @param request HTTP请求
     * @return 会话列表
     */
    @GetMapping("/sessions")
    @RequireRole(UserConstant.DEFAULT_ROLE)
    public List<Map<String, Object>> getChatSessions(HttpServletRequest request) {
        return chatService.getChatSessions(request);
    }

    /**
     * 获取与某用户的聊天历史
     *
     * @param userId 对方用户ID
     * @param page 页码
     * @param pageSize 每页大小
     * @param request HTTP请求
     * @return 聊天历史
     */
    @GetMapping("/history/{userId}")
    @RequireRole(UserConstant.DEFAULT_ROLE)
    public Map<String, Object> getChatHistory(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "50") int pageSize,
            HttpServletRequest request) {
        return chatService.getChatHistory(userId, page, pageSize, request);
    }

    /**
     * 发送消息（HTTP 方式，作为 WebSocket 的备用方案）
     *
     * @param addRequest 消息添加请求
     * @param request HTTP请求
     * @return 消息信息
     */
    @PostMapping("/send")
    @RequireRole(UserConstant.DEFAULT_ROLE)
    public Map<String, Object> sendMessage(
            @RequestBody PrivateMessageAddRequest addRequest,
            HttpServletRequest request) {
        return chatService.sendMessage(addRequest, request);
    }

    /**
     * 标记消息为已读
     *
     * @param userId 对方用户ID
     * @param request HTTP请求
     * @return 是否成功
     */
    @PostMapping("/read/{userId}")
    @RequireRole(UserConstant.DEFAULT_ROLE)
    public Boolean markAsRead(
            @PathVariable Long userId,
            HttpServletRequest request) {
        return chatService.markAsRead(userId, request);
    }

    /**
     * 删除聊天会话
     *
     * @param userId 对方用户ID
     * @param request HTTP请求
     * @return 是否成功
     */
    @DeleteMapping("/session/{userId}")
    @RequireRole(UserConstant.DEFAULT_ROLE)
    public Boolean deleteChatSession(
            @PathVariable Long userId,
            HttpServletRequest request) {
        return chatService.deleteChatSession(userId, request);
    }
}
