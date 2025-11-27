package com.xyex.service;

import java.util.List;

import org.springframework.web.bind.annotation.ResponseBody;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xyex.entity.model.Commodity;
import com.xyex.entity.req.CommodityOrderDTO;
import com.xyex.entity.req.CommodityQueryDTO;
import com.xyex.entity.req.CommodityScoreDTO;
import com.xyex.entity.req.CommodityTypeDTO;
import com.xyex.infrastructure.model.BasicService;
import com.xyex.entity.model.CommodityOrder;
import com.xyex.entity.model.CommodityScore;
import com.xyex.entity.model.CommodityType;

/**
 * 商品服务接口
 */
public interface CommodityService extends BasicService<Commodity> {

    /**
     * 分页查询商品列表
     *
     * @param queryDTO 查询条件
     * @return 商品分页数据
     */
    Page<Commodity> listCommodity(CommodityQueryDTO queryDTO);

    /**
     * 获取商品详情
     *
     * @param commodityId 商品ID
     * @return 商品信息
     */
    Commodity getCommodityDetail(Long id);

    /**
     * 创建商品
     *
     * @param commodity 商品信息
     */
    void createCommodity(Commodity commodity);

    /**
     * 更新商品
     *
     * @param commodity 商品信息
     */
    void updateCommodity(Commodity commodity);

    /**
     * 删除商品
     *
     * @param commodityIds 商品ID
     */
    void deleteCommodity(List<Long> commodityIds);

    /**
     * 增加商品浏览量
     *
     * @param commodityId 商品ID
     */
    void incrementViewNum(Long commodityId);

    /**
     * 增加商品收藏量
     *
     * @param commodityId 商品ID
     */
    void incrementFavourNum(Long commodityId);

    /**
     * 减少商品收藏量
     *
     * @param commodityId 商品ID
     */
    void decrementFavourNum(Long commodityId);
    
    /**
     * 购买商品
     *
     * @param commodity 商品信息
     */
    void buyCommodity(Commodity commodity);

    /**
     * 创建商品订单
     *
     * @param commodity 商品信息
     */
    void addOrder(CommodityOrderDTO commodityOrderDTO);
    
    /**
     * 获取商品订单列表
     *
     * @param queryDTO 查询条件
     * @return 商品订单列表
     */
    Page<CommodityOrder> listOrder(CommodityOrderDTO queryDTO);
    
    /**
     * 获取商品订单详情
     *
     * @param id 商品订单ID
     * @return 商品订单详情
     */
    CommodityOrder getOrderDetail(Long id);
    
    /**
     * 更新商品订单
     *
     * @param commodityOrder 商品订单信息
     */
    void updateOrder(CommodityOrder commodityOrder);
    
    /**
     * 删除商品订单
     *
     * @param ids 商品订单ID
     */
    void deleteOrder(List<Long> ids);

    /**
     * 商品评分
     *
     * @param commodityScoreDTO 商品评分信息
     */
    void score(CommodityScoreDTO commodityScoreDTO);
    /**
     * 获取商品评分列表
     *
     * @param queryDTO 查询条件
     * @return 商品评分列表
     */
    Page<CommodityScore> listScore(CommodityScoreDTO queryDTO);

    /**
     * 获取商品评分详情
     *
     * @param id 商品评分ID
     * @return 商品评分详情
     */
    CommodityScore getScoreDetail(Long id);
    
    /**
     * 更新商品评分
     *
     * @param commodityScore 商品评分信息
     */
    void updateScore(CommodityScore commodityScore);
    
    /**
     * 删除商品评分
     *
     * @param ids 商品评分ID
     */
    void deleteScore(List<Long> ids);
    /**
     * 支付商品订单
     *
     * @param commodityOrder 商品订单信息
     */
    void payOrder(CommodityOrder commodityOrder);
    /**
     * 添加商品类型
     *
     * @param commodityType 商品类型信息
     */
    void addType(CommodityTypeDTO commodityTypeDTO);
    /**
     * 获取商品类型列表
     *
     * @param queryDTO 查询条件
     * @return 商品类型列表
     */
    Page<CommodityType> listType(CommodityTypeDTO queryDTO);
    /**
     * 获取商品类型详情
     *
     * @param id 商品类型ID
     * @return 商品类型详情
     */
    CommodityType getTypeDetail(Long id);
    /**
     * 更新商品类型
     *
     * @param commodityType 商品类型信息
     */
    void updateType(CommodityType commodityType);
    /**
     * 删除商品类型
     *
     * @param ids 商品类型ID
     */
    void deleteType(List<Long> ids);

    
}
