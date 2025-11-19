package com.xyes.springboot.model.dto.barrage;

import lombok.Data;

import java.io.Serializable;

/**
 * 编辑弹幕请求
 *

 */
@Data
public class BarrageEditRequest implements Serializable {

    /**
     * id
     */
    private Long id;

    /**
     * 弹幕文本
     */
    private String message;

    /**
     * 用户头像
     */
    private String userAvatar;

    /**
     * 用户id
     */
    private Long userId;

    /**
     * 是否精选（默认0，精选为1）
     */
    private Integer isSelected;



    private static final long serialVersionUID = 1L;
}