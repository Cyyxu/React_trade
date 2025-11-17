package com.xyes.springboot.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xyes.springboot.model.dto.privateMessage.PrivateMessageAddRequest;
import com.xyes.springboot.model.entity.PrivateMessage;
import com.xyes.springboot.model.entity.User;
import com.xyes.springboot.service.ChatService;
import com.xyes.springboot.service.PrivateMessageService;
import com.xyes.springboot.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 聊天服务实现
 *
 * @author xujun
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class ChatServiceImpl implements ChatService {

    private final PrivateMessageService privateMessageService;
    private final UserService userService;

    @Override
    public List<Map<String, Object>> getChatSessions(HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);
        Long userId = loginUser.getId();

        // 获取所有与当前用户相关的消息
        List<PrivateMessage> allMessages = privateMessageService.lambdaQuery()
                .and(wrapper -> wrapper
                        .eq(PrivateMessage::getSenderId, userId)
                        .or()
                        .eq(PrivateMessage::getRecipientId, userId)
                )
                .orderByDesc(PrivateMessage::getCreateTime)
                .list();

        // 按对话用户分组
        Map<Long, List<PrivateMessage>> groupedMessages = new HashMap<>();
        for (PrivateMessage msg : allMessages) {
            Long otherUserId = msg.getSenderId().equals(userId) ? msg.getRecipientId() : msg.getSenderId();
            groupedMessages.computeIfAbsent(otherUserId, k -> new ArrayList<>()).add(msg);
        }

        // 构建会话列表
        List<Map<String, Object>> sessions = new ArrayList<>();
        for (Map.Entry<Long, List<PrivateMessage>> entry : groupedMessages.entrySet()) {
            Long otherUserId = entry.getKey();
            List<PrivateMessage> messages = entry.getValue();

            // 获取最新消息
            PrivateMessage latestMsg = messages.get(0);

            // 计算未读数
            long unreadCount = messages.stream()
                    .filter(m -> m.getRecipientId().equals(userId) && m.getAlreadyRead() == 0)
                    .count();

            // 获取对方用户信息
            User otherUser = userService.getById(otherUserId);

            Map<String, Object> session = new HashMap<>();
            session.put("userId", otherUserId);
            session.put("username", otherUser != null ? otherUser.getUserName() : "未知用户");
            session.put("avatar", otherUser != null ? otherUser.getUserAvatar() : null);
            session.put("lastMessage", latestMsg.getContent());
            session.put("lastMessageTime", latestMsg.getCreateTime());
            session.put("unreadCount", unreadCount);

            sessions.add(session);
        }

        // 按最后消息时间排序
        sessions.sort((a, b) -> {
            Date timeA = (Date) a.get("lastMessageTime");
            Date timeB = (Date) b.get("lastMessageTime");
            return timeB.compareTo(timeA);
        });

        return sessions;
    }

    @Override
    public Map<String, Object> getChatHistory(Long userId, int page, int pageSize, HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);
        Long currentUserId = loginUser.getId();

        // 分页查询聊天记录
        Page<PrivateMessage> messagePage = privateMessageService.lambdaQuery()
                .and(wrapper -> wrapper
                        .and(w -> w.eq(PrivateMessage::getSenderId, currentUserId)
                                .eq(PrivateMessage::getRecipientId, userId))
                        .or(w -> w.eq(PrivateMessage::getSenderId, userId)
                                .eq(PrivateMessage::getRecipientId, currentUserId))
                )
                .orderByDesc(PrivateMessage::getCreateTime)
                .page(new Page<>(page, pageSize));

        // 转换为前端需要的格式
        List<Map<String, Object>> messages = messagePage.getRecords().stream()
                .map(msg -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("id", msg.getId());
                    map.put("fromUserId", msg.getSenderId());
                    map.put("toUserId", msg.getRecipientId());
                    map.put("content", msg.getContent());
                    map.put("messageType", "text"); // 默认文本类型
                    map.put("isRead", msg.getAlreadyRead() == 1);
                    map.put("createdAt", msg.getCreateTime());
                    return map;
                })
                .collect(Collectors.toList());

        // 反转顺序，让最早的消息在前面
        Collections.reverse(messages);

        Map<String, Object> result = new HashMap<>();
        result.put("messages", messages);
        result.put("total", messagePage.getTotal());

        return result;
    }

    @Override
    public Map<String, Object> sendMessage(PrivateMessageAddRequest addRequest, HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);

        if (addRequest.getRecipientId() == null) {
            throw new RuntimeException("接收者ID不能为空");
        }

        PrivateMessage message = new PrivateMessage();
        message.setSenderId(loginUser.getId());
        message.setRecipientId(addRequest.getRecipientId());
        message.setContent(addRequest.getContent());
        message.setAlreadyRead(0);
        message.setType("user");
        message.setIsRecalled(0);

        boolean result = privateMessageService.save(message);
        if (!result) {
            throw new RuntimeException("发送消息失败");
        }

        // 返回消息信息
        Map<String, Object> messageData = new HashMap<>();
        messageData.put("id", message.getId());
        messageData.put("fromUserId", message.getSenderId());
        messageData.put("toUserId", message.getRecipientId());
        messageData.put("content", message.getContent());
        messageData.put("messageType", "text");
        messageData.put("isRead", false);
        messageData.put("createdAt", message.getCreateTime());

        return messageData;
    }

    @Override
    public Boolean markAsRead(Long userId, HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);
        Long currentUserId = loginUser.getId();

        // 将该用户发给我的所有未读消息标记为已读
        return privateMessageService.lambdaUpdate()
                .eq(PrivateMessage::getSenderId, userId)
                .eq(PrivateMessage::getRecipientId, currentUserId)
                .eq(PrivateMessage::getAlreadyRead, 0)
                .set(PrivateMessage::getAlreadyRead, 1)
                .update();
    }

    @Override
    public Boolean deleteChatSession(Long userId, HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);
        Long currentUserId = loginUser.getId();

        // 删除与该用户的所有消息
        return privateMessageService.lambdaUpdate()
                .and(wrapper -> wrapper
                        .and(w -> w.eq(PrivateMessage::getSenderId, currentUserId)
                                .eq(PrivateMessage::getRecipientId, userId))
                        .or(w -> w.eq(PrivateMessage::getSenderId, userId)
                                .eq(PrivateMessage::getRecipientId, currentUserId))
                )
                .remove();
    }
}
