package com.xyes.springboot.model.dto.userAiMessage;

import com.xyes.springboot.common.PageParam;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * 查询用户对话表请求
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class UserAiMessageQueryRequest extends PageParam {

    /**
     *
     */
    private Long id;

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

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;


    private static final long serialVersionUID = 1L;
}