package com.xyes.springboot.service;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xyes.springboot.model.dto.commodityType.*;
import com.xyes.springboot.model.entity.CommodityType;
import com.xyes.springboot.model.vo.CommodityTypeVO;

import javax.servlet.http.HttpServletRequest;

public interface CommodityTypeService extends IService<CommodityType> {

    void validCommodityType(CommodityType commodityType, boolean add);

    Wrapper<CommodityType> getQueryWrapper(CommodityTypeQueryRequest commodityTypeQueryRequest);

    Page<CommodityTypeVO> getCommodityTypeVOPage(Page<CommodityType> commodityTypePage, HttpServletRequest request);

    CommodityTypeVO getCommodityTypeVO(CommodityType commodityType, HttpServletRequest request);

    /**
     * 创建商品类别（业务逻辑）
     *
     * @param commodityTypeAddRequest
     * @param request
     * @return 新商品类别ID
     */
    Long addCommodityType(CommodityTypeAddRequest commodityTypeAddRequest, HttpServletRequest request);

    /**
     * 删除商品类别（包含权限校验和商品检查）
     *
     * @param id
     * @param request
     * @return
     */
    Boolean deleteCommodityTypeById(Long id, HttpServletRequest request);

    /**
     * 更新商品类别（仅管理员）
     *
     * @param commodityTypeUpdateRequest
     * @return
     */
    Boolean updateCommodityTypeById(CommodityTypeUpdateRequest commodityTypeUpdateRequest);

    /**
     * 编辑商品类别（仅管理员）
     *
     * @param commodityTypeEditRequest
     * @param request
     * @return
     */
    Boolean editCommodityTypeById(CommodityTypeEditRequest commodityTypeEditRequest, HttpServletRequest request);
}