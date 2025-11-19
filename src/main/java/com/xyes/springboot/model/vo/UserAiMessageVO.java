package com.xyes.springboot.model.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.xyes.springboot.model.entity.UserAiMessage;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.io.Serializable;
import java.util.Date;

/**
 * 用户对话表视图
 *
 */
@Data
public class UserAiMessageVO implements Serializable {

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
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private Date createTime;

    /**
     * 更新时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private Date updateTime;


    private static final long serialVersionUID = 1L;
    /**
     * 封装类转对象
     *
     * @param userAiMessageVO
     * @return
     */
    public static UserAiMessage voToObj(UserAiMessageVO userAiMessageVO) {
        if (userAiMessageVO == null) {
            return null;
        }
        UserAiMessage userAiMessage = new UserAiMessage();
        BeanUtils.copyProperties(userAiMessageVO, userAiMessage);
        return userAiMessage;
    }

    /**
     * 对象转封装类
     *
     * @param userAiMessage
     * @return
     */
    public static UserAiMessageVO objToVo(UserAiMessage userAiMessage) {
        if (userAiMessage == null) {
            return null;
        }
        UserAiMessageVO userAiMessageVO = new UserAiMessageVO();
        BeanUtils.copyProperties(userAiMessage, userAiMessageVO);
        return userAiMessageVO;
    }
}
