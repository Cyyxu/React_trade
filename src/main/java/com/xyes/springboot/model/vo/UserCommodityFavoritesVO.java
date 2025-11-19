package com.xyes.springboot.model.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.xyes.springboot.model.entity.Commodity;
import com.xyes.springboot.model.entity.UserCommodityFavorites;
import com.xyes.springboot.service.CommodityService;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 用户商品收藏表视图
 *
 * @author xujun
 */
@Data
public class UserCommodityFavoritesVO implements Serializable {

    /**
     * 主键 ID
     */
    private Long id;

    /**
     * 用户 ID
     */
    private Long userId;

    /**
     * 商品 ID
     */
    private Long commodityId;

    /**
     * 商品名称
     */
    private String commodityName;

    /**
     * 商品简介
     */
    private String commodityDescription;

    /**
     * 商品封面图
     */
    private String commodityAvatar;

    /**
     * 商品新旧程度（例如 9成新）
     */
    private String degree;

    /**
     * 商品分类 ID
     */
    private Long commodityTypeId;

    /**
     * 管理员 ID （某人创建该商品）
     */
    private Long adminId;

    /**
     * 是否上架（默认0未上架，1已上架）
     */
    private Integer isListed;

    /**
     * 商品数量（默认0）
     */
    private Integer commodityInventory;

    /**
     * 商品价格
     */
    private BigDecimal price;

    /**
     * 商品浏览量
     */
    private Integer viewNum;

    /**
     * 商品收藏量
     */
    private Integer favourNum;

    /**
     * 1-正常收藏 0-取消收藏
     */
    private Integer status;

    /**
     * 用户备注
     */
    private String remark;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;

    /**
     * 更新时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date updateTime;

    private static final long serialVersionUID = 1L;

    /**
     * 封装类转对象
     *
     * @param userCommodityFavoritesVO
     * @return
     */
    public static UserCommodityFavorites voToObj(UserCommodityFavoritesVO userCommodityFavoritesVO) {
        if (userCommodityFavoritesVO == null) {
            return null;
        }
        UserCommodityFavorites userCommodityFavorites = new UserCommodityFavorites();
        BeanUtils.copyProperties(userCommodityFavoritesVO, userCommodityFavorites);
        return userCommodityFavorites;
    }

    /**
     * 对象转封装类
     *
     * @param userCommodityFavorites 用户商品收藏实体
     * @param commodityService 商品服务，用于查询商品信息
     * @return 用户商品收藏VO
     */
    public static UserCommodityFavoritesVO objToVo(UserCommodityFavorites userCommodityFavorites, CommodityService commodityService) {
        if (userCommodityFavorites == null) {
            return null;
        }
        UserCommodityFavoritesVO userCommodityFavoritesVO = new UserCommodityFavoritesVO();
        // 复制 UserCommodityFavorites 的基本字段
        BeanUtils.copyProperties(userCommodityFavorites, userCommodityFavoritesVO);

        // 根据 commodityId 查询商品信息
        if (userCommodityFavorites.getCommodityId() != null && commodityService != null) {
            Commodity commodity = commodityService.getById(userCommodityFavorites.getCommodityId());
            if (commodity != null) {
                // 将 Commodity 的字段赋值给 UserCommodityFavoritesVO
                userCommodityFavoritesVO.setCommodityName(commodity.getCommodityName());
                userCommodityFavoritesVO.setCommodityDescription(commodity.getCommodityDescription());
                userCommodityFavoritesVO.setCommodityAvatar(commodity.getCommodityAvatar());
                userCommodityFavoritesVO.setDegree(commodity.getDegree());
                userCommodityFavoritesVO.setCommodityTypeId(commodity.getCommodityTypeId());
                userCommodityFavoritesVO.setAdminId(commodity.getAdminId());
                userCommodityFavoritesVO.setIsListed(commodity.getIsListed());
                userCommodityFavoritesVO.setCommodityInventory(commodity.getCommodityInventory());
                userCommodityFavoritesVO.setPrice(commodity.getPrice());
                userCommodityFavoritesVO.setViewNum(commodity.getViewNum());
                userCommodityFavoritesVO.setFavourNum(commodity.getFavourNum());
            }
        }

        return userCommodityFavoritesVO;
    }
}