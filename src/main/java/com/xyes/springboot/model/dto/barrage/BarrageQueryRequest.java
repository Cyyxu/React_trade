package com.xyes.springboot.model.dto.barrage;

import com.xyes.springboot.common.PageParam;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.util.Date;

/**
 * 查询弹幕请求
 *

 */
@EqualsAndHashCode(callSuper = true)
@Data
public class BarrageQueryRequest extends PageParam {

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