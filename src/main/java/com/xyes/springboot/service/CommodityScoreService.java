package com.xyes.springboot.service;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xyes.springboot.model.dto.commodityScore.*;
import com.xyes.springboot.model.entity.CommodityScore;
import com.xyes.springboot.model.vo.CommodityScoreVO;

import javax.servlet.http.HttpServletRequest;

public interface CommodityScoreService extends IService<CommodityScore> {

    void validCommodityScore(CommodityScore commodityScore, boolean add);

    CommodityScoreVO getCommodityScoreVO(CommodityScore commodityScore, HttpServletRequest request);

    Wrapper<CommodityScore> getQueryWrapper(CommodityScoreQueryRequest commodityScoreQueryRequest);

    Page<CommodityScoreVO> getCommodityScoreVOPage(Page<CommodityScore> commodityScorePage, HttpServletRequest request);

    Double getAverageScoreBySpotId(Long commodityId);

    /**
     * 创建评分（业务逻辑）
     *
     * @param commodityScoreAddRequest
     * @param request
     * @return 新评分ID
     */
    Long addCommodityScore(CommodityScoreAddRequest commodityScoreAddRequest, HttpServletRequest request);

    /**
     * 删除评分（包含权限校验）
     *
     * @param id
     * @param request
     * @return
     */
    Boolean deleteCommodityScoreById(Long id, HttpServletRequest request);

    /**
     * 更新评分（仅管理员）
     *
     * @param commodityScoreUpdateRequest
     * @return
     */
    Boolean updateCommodityScoreById(CommodityScoreUpdateRequest commodityScoreUpdateRequest);

    /**
     * 编辑评分（用户自己可用）
     *
     * @param commodityScoreEditRequest
     * @param request
     * @return
     */
    Boolean editCommodityScoreById(CommodityScoreEditRequest commodityScoreEditRequest, HttpServletRequest request);

    /**
     * 分页获取当前用户的评分列表
     *
     * @param commodityScoreQueryRequest
     * @param request
     * @return
     */
    Page<CommodityScoreVO> getMyCommodityScoreVOPage(CommodityScoreQueryRequest commodityScoreQueryRequest, HttpServletRequest request);

    /**
     * 获取商品的平均评分（格式化）
     *
     * @param commodityId
     * @return
     */
    Double getAverageScore(Long commodityId);
}