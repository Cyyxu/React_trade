package com.xyes.springboot.service;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xyes.springboot.model.dto.commodityOrder.*;
import com.xyes.springboot.model.entity.CommodityOrder;
import com.xyes.springboot.model.vo.CommodityOrderVO;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;


public interface CommodityOrderService extends IService<CommodityOrder> {

    void validCommodityOrder(CommodityOrder commodityOrder, boolean add);

    CommodityOrderVO getCommodityOrderVO(CommodityOrder commodityOrder, HttpServletRequest request);

    Wrapper<CommodityOrder> getQueryWrapper(CommodityOrderQueryRequest commodityOrderQueryRequest);

    Page<CommodityOrderVO> getCommodityOrderVOPage(Page<CommodityOrder> commodityOrderPage, HttpServletRequest request);

    List<CommodityOrder> listByQuery(CommodityOrderQueryRequest queryRequest);

    CommodityOrder getByIdWithLock(Long id);

    /**
     * 创建订单（业务逻辑）
     *
     * @param commodityOrderAddRequest
     * @param request
     * @return 新订单ID
     */
    Long addCommodityOrder(CommodityOrderAddRequest commodityOrderAddRequest, HttpServletRequest request);

    /**
     * 删除订单（包含权限校验）
     *
     * @param id
     * @param request
     * @return
     */
    Boolean deleteCommodityOrderById(Long id, HttpServletRequest request);

    /**
     * 更新订单（仅管理员）
     *
     * @param commodityOrderUpdateRequest
     * @return
     */
    Boolean updateCommodityOrderById(CommodityOrderUpdateRequest commodityOrderUpdateRequest);

    /**
     * 编辑订单（用户自己可用）
     *
     * @param commodityOrderEditRequest
     * @param request
     * @return
     */
    Boolean editCommodityOrderById(CommodityOrderEditRequest commodityOrderEditRequest, HttpServletRequest request);

    /**
     * 分页获取当前用户的订单列表
     *
     * @param commodityOrderQueryRequest
     * @param request
     * @return
     */
    Page<CommodityOrderVO> getMyCommodityOrderVOPage(CommodityOrderQueryRequest commodityOrderQueryRequest, HttpServletRequest request);

    /**
     * 获取订单热力图数据
     *
     * @param userId
     * @param payStatus
     * @return
     */
    List<Map<String, Object>> getCommodityOrderHeatmapData(Long userId, Integer payStatus);

}