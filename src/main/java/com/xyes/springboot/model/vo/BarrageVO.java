package com.xyes.springboot.model.vo;

import com.xyes.springboot.model.entity.Barrage;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.io.Serializable;

/**
 * 弹幕视图
 */
@Data
public class BarrageVO implements Serializable {

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

    /**
     * 封装类转对象
     *
     * @param barrageVO
     * @return
     */
    public static Barrage voToObj(BarrageVO barrageVO) {
        if (barrageVO == null) {
            return null;
        }
        Barrage barrage = new Barrage();
        BeanUtils.copyProperties(barrageVO, barrage);
        return barrage;
    }

    /**
     * 对象转封装类
     *
     * @param barrage
     * @return
     */
    public static BarrageVO objToVo(Barrage barrage) {
        if (barrage == null) {
            return null;
        }
        BarrageVO barrageVO = new BarrageVO();
        BeanUtils.copyProperties(barrage, barrageVO);
        return barrageVO;
    }
}
