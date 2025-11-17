package com.xyes.springboot.websocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.xyes.springboot.model.entity.PrivateMessage;
import com.xyes.springboot.service.PrivateMessageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * WebSocket 聊天处理器
 */
@Slf4j
@Component
public class ChatWebSocketHandler extends TextWebSocketHandler {

    // 存储用户 ID 和 WebSocket 会话的映射
    private static final Map<Long, WebSocketSession> USER_SESSIONS = new ConcurrentHashMap<>();
    
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    @Resource
    private PrivateMessageService privateMessageService;

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        // 从 session 中获取用户 ID（实际应该从 token 中解析）
        Long userId = getUserIdFromSession(session);
        if (userId != null) {
            USER_SESSIONS.put(userId, session);
            log.info("用户 {} 建立 WebSocket 连接", userId);
            
            // 发送连接成功消息
            sendMessage(session, Map.of("type", "connected", "message", "连接成功"));
        }
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String payload = message.getPayload();
        log.info("收到消息: {}", payload);
        
        try {
            Map<String, Object> data = objectMapper.readValue(payload, Map.class);
            String type = (String) data.get("type");
            
            if ("ping".equals(type)) {
                // 心跳响应
                sendMessage(session, Map.of("type", "pong"));
            } else if ("message".equals(type)) {
                // 处理聊天消息
                handleChatMessage(session, data);
            }
        } catch (Exception e) {
            log.error("处理消息失败", e);
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        Long userId = getUserIdFromSession(session);
        if (userId != null) {
            USER_SESSIONS.remove(userId);
            log.info("用户 {} 断开 WebSocket 连接", userId);
        }
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        log.error("WebSocket 传输错误", exception);
        session.close();
    }

    /**
     * 处理聊天消息
     */
    private void handleChatMessage(WebSocketSession session, Map<String, Object> data) {
        try {
            Map<String, Object> messageData = (Map<String, Object>) data.get("data");
            Integer toUserIdInt = (Integer) messageData.get("toUserId");
            Long toUserId = toUserIdInt.longValue();
            String content = (String) messageData.get("content");
            String messageType = (String) messageData.get("messageType");
            
            Long fromUserId = getUserIdFromSession(session);
            
            // 保存消息到数据库
            PrivateMessage privateMessage = new PrivateMessage();
            privateMessage.setSenderId(fromUserId);
            privateMessage.setRecipientId(toUserId);
            privateMessage.setContent(content);
            privateMessage.setAlreadyRead(0);
            privateMessage.setType("user");
            privateMessage.setIsRecalled(0);
            privateMessageService.save(privateMessage);
            
            // 构造消息对象
            Map<String, Object> messageToSend = Map.of(
                "type", "message",
                "data", Map.of(
                    "id", System.currentTimeMillis(), // 实际应该是数据库生成的 ID
                    "fromUserId", fromUserId,
                    "toUserId", toUserId,
                    "content", content,
                    "messageType", messageType,
                    "createdAt", new java.util.Date().toString(),
                    "isRead", false
                )
            );
            
            // 发送给接收方
            WebSocketSession toSession = USER_SESSIONS.get(toUserId);
            if (toSession != null && toSession.isOpen()) {
                sendMessage(toSession, messageToSend);
            }
            
            // 回显给发送方
            sendMessage(session, messageToSend);
            
        } catch (Exception e) {
            log.error("处理聊天消息失败", e);
        }
    }

    /**
     * 发送消息
     */
    private void sendMessage(WebSocketSession session, Object message) {
        try {
            String json = objectMapper.writeValueAsString(message);
            session.sendMessage(new TextMessage(json));
        } catch (IOException e) {
            log.error("发送消息失败", e);
        }
    }

    /**
     * 从 session 中获取用户 ID
     * 实际应该从 token 中解析
     */
    private Long getUserIdFromSession(WebSocketSession session) {
        // 简化处理：从 URL 参数中获取
        String query = session.getUri().getQuery();
        if (query != null && query.contains("token=")) {
            // 实际应该解析 JWT token 获取用户 ID
            // 这里简化为直接返回一个测试 ID
            return 1L;
        }
        return 1L; // 默认用户 ID
    }

    /**
     * 向指定用户发送消息
     */
    public static void sendToUser(Long userId, Object message) {
        WebSocketSession session = USER_SESSIONS.get(userId);
        if (session != null && session.isOpen()) {
            try {
                ObjectMapper mapper = new ObjectMapper();
                String json = mapper.writeValueAsString(message);
                session.sendMessage(new TextMessage(json));
            } catch (IOException e) {
                log.error("发送消息给用户 {} 失败", userId, e);
            }
        }
    }
}
