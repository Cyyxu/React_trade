package com.xyes.springboot.model.dto.userAiMessage;

import lombok.Data;

import java.io.Serializable;

/**
 * 创建用户对话表请求
 */
@Data
public class UserAiMessageAddRequest implements Serializable {


    /**
     * 用户输入
     */
    private String userInputText;

    /**
     * AI 生成结果
     */
    private String aiGenerateText;

    /**
     * 用户ID
     */
    private Long userId;




    private static final long serialVersionUID = 1L;
}