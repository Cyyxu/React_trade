package com.xyes.springboot.service;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xyes.springboot.model.dto.commodity.*;
import com.xyes.springboot.model.entity.Commodity;
import com.xyes.springboot.model.vo.CommodityVO;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

public interface CommodityService extends IService<Commodity> {

    void validCommodity(Commodity commodity, boolean add);

    CommodityVO getCommodityVO(Commodity commodity, HttpServletRequest request);

    Wrapper<Commodity> getQueryWrapper(CommodityQueryRequest commodityQueryRequest);

    Page<CommodityVO> getCommodityVOPage(Page<Commodity> commodityPage, HttpServletRequest request);

    Commodity getByIdWithLock(Long id);

    /**
     * 创建商品（业务逻辑）
     *
     * @param commodityAddRequest
     * @param request
     * @return 新商品ID
     */
    Long addCommodity(CommodityAddRequest commodityAddRequest, HttpServletRequest request);

    /**
     * 删除商品（包含权限校验）
     *
     * @param id
     * @param request
     * @return
     */
    Boolean deleteCommodityById(Long id, HttpServletRequest request);

    /**
     * 更新商品（仅管理员）
     *
     * @param commodityUpdateRequest
     * @return
     */
    Boolean updateCommodityById(CommodityUpdateRequest commodityUpdateRequest);

    /**
     * 编辑商品（含特殊权限逻辑）
     *
     * @param commodityEditRequest
     * @param request
     * @return
     */
    Boolean editCommodityById(CommodityEditRequest commodityEditRequest, HttpServletRequest request);

    /**
     * 分页获取当前用户的商品列表
     *
     * @param commodityQueryRequest
     * @param request
     * @return
     */
    Page<CommodityVO> getMyCommodityVOPage(CommodityQueryRequest commodityQueryRequest, HttpServletRequest request);

    /**
     * 分页获取商品列表（带权限控制）
     * 管理员：返回所有商品
     * 普通用户：只返回自己发布的商品
     *
     * @param commodityQueryRequest
     * @param request
     * @return
     */
    Page<CommodityVO> listCommodityVOByPageWithAuth(CommodityQueryRequest commodityQueryRequest, HttpServletRequest request);

    /**
     * 购买商品（业务逻辑）
     *
     * @param buyRequest
     * @param request
     * @return
     */
    Map<String, Object> buyCommodity(BuyCommodityRequest buyRequest, HttpServletRequest request);
}