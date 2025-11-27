package com.xyex.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xyex.entity.model.Commodity;
import com.xyex.entity.model.CommodityOrder;
import com.xyex.entity.model.CommodityScore;
import com.xyex.entity.model.CommodityType;
import com.xyex.entity.req.CommodityOrderDTO;
import com.xyex.entity.req.CommodityQueryDTO;
import com.xyex.entity.req.CommodityScoreDTO;
import com.xyex.entity.req.CommodityTypeDTO;
import com.xyex.infrastructure.exception.BusinessException;
import com.xyex.infrastructure.exception.ErrorCode;
import com.xyex.infrastructure.model.BasicServiceImpl;
import com.xyex.mapper.CommodityMapper;
import com.xyex.mapper.CommodityOrderMapper;
import com.xyex.mapper.CommodityScoreMapper;
import com.xyex.mapper.CommodityTypeMapper;
import com.xyex.service.CommodityService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 商品服务实现
 */
@Service
@RequiredArgsConstructor
public class CommodityServiceImpl extends BasicServiceImpl<CommodityMapper, Commodity> implements CommodityService {

    private final CommodityMapper commodityMapper;
    private final CommodityOrderMapper commodityOrderMapper;
    private final CommodityScoreMapper commodityScoreMapper;
    private final CommodityTypeMapper commodityTypeMapper;

  

    @Override
    public Page<Commodity> listCommodity(CommodityQueryDTO queryDTO) {
        return commodityMapper.selectPage(queryDTO.createPage(), queryDTO.createQuery());
    }

    @Override
    public Commodity getCommodityDetail(Long id) {
        if (id == null || id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "商品ID不能为空");
        }

        Commodity commodity = commodityMapper.selectById(id);
        if (commodity == null || commodity.getIsDelete() == 1) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "商品不存在");
        }

        // 增加浏览量
        incrementViewNum(id);

        return commodity;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void createCommodity(Commodity commodity) {
        if (commodity == null || commodity.getCommodityName() == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "商品信息不完整");
        }

        if (commodity.getPrice() == null || commodity.getPrice().compareTo(BigDecimal.ZERO) <= 0) {
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
    public void deleteCommodity(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "商品ID不能为空");
        }

        for (Long id : ids) {
            Commodity commodity = commodityMapper.selectById(id);
        if (commodity == null || commodity.getIsDelete() == 1) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "商品不存在");
        }

        // 软删除
        Commodity updateCommodity = new Commodity();
        updateCommodity.setId(id);
        updateCommodity.setIsDelete(1);
        commodityMapper.updateById(updateCommodity);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void incrementViewNum(Long id) {
        if (id == null || id <= 0) {
            return;
        }

        Commodity commodity = new Commodity();
        commodity.setId(id);
        commodity.setViewNum(commodityMapper.selectById(id).getViewNum() + 1);
        commodityMapper.updateById(commodity);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void incrementFavourNum(Long id) {
        if (id == null || id <= 0) {
            return;
        }

        Commodity commodity = new Commodity();
        commodity.setId(id);
        commodity.setFavourNum(commodityMapper.selectById(id).getFavourNum() + 1);
        commodityMapper.updateById(commodity);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void decrementFavourNum(Long id) {
        if (id == null || id <= 0) {
            return;
        }

        Commodity existing = commodityMapper.selectById(id);
        if (existing != null && existing.getFavourNum() > 0) {
            Commodity commodity = new Commodity();
            commodity.setId(id);
            commodity.setFavourNum(existing.getFavourNum() - 1);
            commodityMapper.updateById(commodity);
        }
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void buyCommodity(Commodity commodity) {
        if (commodity == null || commodity.getId() == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "商品ID不能为空");
        }

        Commodity existing = commodityMapper.selectById(commodity.getId());
        if (existing == null || existing.getIsDelete() == 1) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "商品不存在");
        }

        // 购买商品
        Commodity updateCommodity = new Commodity();
        updateCommodity.setId(commodity.getId());
        updateCommodity.setIsListed(1);
        updateCommodity.setCommodityInventory(existing.getCommodityInventory() - 1);
        commodityMapper.updateById(updateCommodity);
    }
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addOrder(CommodityOrderDTO commodityOrderDTO) {
        if (commodityOrderDTO == null || commodityOrderDTO.getCommodityId() == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "商品ID不能为空");
        }

        Commodity existing = commodityMapper.selectById(commodityOrderDTO.getCommodityId());
        if (existing == null || existing.getIsDelete() == 1) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "商品不存在");
        }
        CommodityOrder order = new CommodityOrder();
        order.setCommodityId(commodityOrderDTO.getCommodityId());
        order.setUserId(commodityOrderDTO.getUserId());
        order.setPayStatus(0);
        order.setPaymentAmount(commodityOrderDTO.getPaymentAmount());
        order.setBuyNumber(commodityOrderDTO.getBuyNumber());
        order.setCreateTime(LocalDateTime.now());
        order.setUpdateTime(LocalDateTime.now());
        order.setIsDelete(0);
        commodityOrderMapper.insert(order);
    }
    @Override
    public Page<CommodityOrder> listOrder(CommodityOrderDTO queryDTO) {
        return commodityOrderMapper.selectPage(queryDTO.createPage(), queryDTO.createQuery());
    }
    @Override
    public CommodityOrder getOrderDetail(Long id) {
        return commodityOrderMapper.selectById(id);
    }
    @Override
    public void updateOrder(CommodityOrder commodityOrder) {
        commodityOrderMapper.updateById(commodityOrder);
    }
    @Override
    public void deleteOrder(List<Long> ids) {
        commodityOrderMapper.deleteBatchIds(ids);
    }
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void payOrder(CommodityOrder commodityOrder) {
        commodityOrderMapper.updateById(commodityOrder);
    }
    @Override
    public void score(CommodityScoreDTO commodityScoreDTO) {
        CommodityScore score = new CommodityScore();
        score.setCommodityId(commodityScoreDTO.getCommodityId());
        score.setUserId(commodityScoreDTO.getUserId());
        score.setScore(commodityScoreDTO.getScore());
        score.setCreateTime(LocalDateTime.now());
        score.setUpdateTime(LocalDateTime.now());
        score.setIsDelete(0);
        commodityScoreMapper.insert(score);
    }
    @Override
    public Page<CommodityScore> listScore(CommodityScoreDTO queryDTO) {
        return commodityScoreMapper.selectPage(queryDTO.createPage(), queryDTO.createQuery());
    }
    @Override
    public CommodityScore getScoreDetail(Long id) {
        return commodityScoreMapper.selectById(id);
    }
    @Override
    public void updateScore(CommodityScore commodityScore) {
        commodityScoreMapper.updateById(commodityScore);
    }

    @Override
    public void deleteScore(List<Long> ids) {
        commodityScoreMapper.deleteBatchIds(ids);
    }
    @Override
    public void addType(CommodityTypeDTO commodityTypeDTO) {
        CommodityType type = new CommodityType();
        type.setTypeName(commodityTypeDTO.getTypeName());
        type.setCreateTime(LocalDateTime.now());
        type.setUpdateTime(LocalDateTime.now());
        type.setIsDelete(0);
        commodityTypeMapper.insert(type);
    }
    @Override
    public Page<CommodityType> listType(CommodityTypeDTO queryDTO) {
        return commodityTypeMapper.selectPage(queryDTO.createPage(), queryDTO.createQuery());
    }
    @Override
    public CommodityType getTypeDetail(Long id) {
        return commodityTypeMapper.selectById(id);
    }
    @Override
    public void updateType(CommodityType commodityType) {
        commodityTypeMapper.updateById(commodityType);
    }
    @Override
    public void deleteType(List<Long> ids) {
        commodityTypeMapper.deleteBatchIds(ids);
    }
}
