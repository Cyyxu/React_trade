package com.xyes.springboot.model.dto.barrage;

import lombok.Data;

import java.io.Serializable;

/**
 * 创建弹幕请求
 *
 */
@Data
public class BarrageAddRequest implements Serializable {


    /**
     * 弹幕文本
     */
    private String message;

    /**
     * 用户头像
     */
    private String userAvatar;




    private static final long serialVersionUID = 1L;
}