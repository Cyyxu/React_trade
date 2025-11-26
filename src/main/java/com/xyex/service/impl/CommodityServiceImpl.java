package com.xyex.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xyex.entity.model.Commodity;
import com.xyex.entity.req.CommodityQueryDTO;
import com.xyex.infrastructure.exception.BusinessException;
import com.xyex.infrastructure.exception.ErrorCode;
import com.xyex.mapper.CommodityMapper;
import com.xyex.service.CommodityService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 商品服务实现
 */
@Service
@RequiredArgsConstructor
public class CommodityServiceImpl extends ServiceImpl<CommodityMapper, Commodity> implements CommodityService {

    private final CommodityMapper commodityMapper;

    @Override
    public <P extends com.xyex.infrastructure.model.PageParam> Page<Commodity> page(P p) {
        Page<Commodity> page = new Page<>(p.getPageNo(), p.getPageSize());
        return commodityMapper.selectPage(page, p.createQuery());
    }

    @Override
    public Page<Commodity> listCommodity(CommodityQueryDTO queryDTO) {
        if (queryDTO == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "查询参数不能为空");
        }

        Page<Commodity> page = new Page<>(queryDTO.getPageNo(), queryDTO.getPageSize());
        return commodityMapper.selectPage(page, queryDTO.createQuery());
    }

    @Override
    public Commodity getCommodityDetail(Long commodityId) {
        if (commodityId == null || commodityId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "商品ID不能为空");
        }

        Commodity commodity = commodityMapper.selectById(commodityId);
        if (commodity == null || commodity.getIsDelete() == 1) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "商品不存在");
        }

        // 增加浏览量
        incrementViewNum(commodityId);

        return commodity;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void createCommodity(Commodity commodity) {
        if (commodity == null || commodity.getCommodityName() == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "商品信息不完整");
        }

        if (commodity.getPrice() == null || commodity.getPrice().compareTo(java.math.BigDecimal.ZERO) <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "商品价格必须大于0");
        }

        commodity.setIsDelete(0);
        commodity.setViewNum(0);
        commodity.setFavourNum(0);
        commodity.setIsListed(0);
        commodity.setCommodityInventory(commodity.getCommodityInventory() != null ? commodity.getCommodityInventory() : 0);

        commodityMapper.insert(commodity);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateCommodity(Commodity commodity) {
        if (commodity == null || commodity.getId() == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "商品ID不能为空");
        }

        Commodity existing = commodityMapper.selectById(commodity.getId());
        if (existing == null || existing.getIsDelete() == 1) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "商品不存在");
        }

        commodityMapper.updateById(commodity);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteCommodity(Long commodityId) {
        if (commodityId == null || commodityId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "商品ID不能为空");
        }

        Commodity commodity = commodityMapper.selectById(commodityId);
        if (commodity == null || commodity.getIsDelete() == 1) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "商品不存在");
        }

        // 软删除
        Commodity updateCommodity = new Commodity();
        updateCommodity.setId(commodityId);
        updateCommodity.setIsDelete(1);
        commodityMapper.updateById(updateCommodity);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void incrementViewNum(Long commodityId) {
        if (commodityId == null || commodityId <= 0) {
            return;
        }

        Commodity commodity = new Commodity();
        commodity.setId(commodityId);
        commodity.setViewNum(commodityMapper.selectById(commodityId).getViewNum() + 1);
        commodityMapper.updateById(commodity);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void incrementFavourNum(Long commodityId) {
        if (commodityId == null || commodityId <= 0) {
            return;
        }

        Commodity commodity = new Commodity();
        commodity.setId(commodityId);
        commodity.setFavourNum(commodityMapper.selectById(commodityId).getFavourNum() + 1);
        commodityMapper.updateById(commodity);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void decrementFavourNum(Long commodityId) {
        if (commodityId == null || commodityId <= 0) {
            return;
        }

        Commodity existing = commodityMapper.selectById(commodityId);
        if (existing != null && existing.getFavourNum() > 0) {
            Commodity commodity = new Commodity();
            commodity.setId(commodityId);
            commodity.setFavourNum(existing.getFavourNum() - 1);
            commodityMapper.updateById(commodity);
        }
    }
}
