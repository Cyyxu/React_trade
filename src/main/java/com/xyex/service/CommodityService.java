package com.xyex.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xyex.entity.model.Commodity;
import com.xyex.entity.req.CommodityQueryDTO;
import com.xyex.infrastructure.model.BasicService;

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
    Commodity getCommodityDetail(Long commodityId);

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
     * @param commodityId 商品ID
     */
    void deleteCommodity(Long commodityId);

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
}
