package com.xyes.springboot.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xyes.springboot.exception.BusinessException;
import com.xyes.springboot.exception.ErrorCode;
import com.xyes.springboot.constant.CommonConstant;
import com.xyes.springboot.exception.ThrowUtils;
import com.xyes.springboot.mapper.Commoditymapper;
import com.xyes.springboot.model.dto.commodity.*;
import com.xyes.springboot.model.entity.Commodity;
import com.xyes.springboot.model.entity.CommodityOrder;
import com.xyes.springboot.model.entity.CommodityType;
import com.xyes.springboot.model.entity.User;
import com.xyes.springboot.model.vo.CommodityVO;
import com.xyes.springboot.service.*;
import com.xyes.springboot.util.SpringContextUtil;
import com.xyes.springboot.util.SqlUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class CommodityServiceImpl extends ServiceImpl<Commoditymapper, Commodity> implements CommodityService {
    private final UserService userService;
    private final InventoryService inventoryService;
    private final TransactionTemplate transactionTemplate;

    /**
     * 延迟获取 CommodityTypeService，避免循环依赖
     */
    private CommodityTypeService getCommodityTypeService() {
        return SpringContextUtil.getBean(CommodityTypeService.class);
    }

    /**
     * 延迟获取 CommodityOrderService，避免循环依赖
     */
    private CommodityOrderService getCommodityOrderService() {
        return SpringContextUtil.getBean(CommodityOrderService.class);
    }
    
    @Override
    public boolean save(Commodity commodity) {
        boolean result = super.save(commodity);
        if (result && commodity.getCommodityInventory() != null) {
            // 商品创建成功后，将实际库存初始化到Redis
            inventoryService.initInventory(commodity.getId(), (long) commodity.getCommodityInventory());
        }
        return result;
    }

    @Override
    public boolean updateById(Commodity commodity) {
        boolean result = super.updateById(commodity);
        if (result && commodity.getCommodityInventory() != null) {
            // 商品更新成功后，同步实际库存到Redis
            inventoryService.initInventory(commodity.getId(), (long) commodity.getCommodityInventory());
        }
        return result;
    }
    /**
     * @param commodity
     * @param
     */
    @Override
    public void validCommodity(Commodity commodity, boolean add) {
        ThrowUtils.throwIf(commodity == null, ErrorCode.PARAMS_ERROR);
        String commodityName = commodity.getCommodityName();
        Integer commodityInventory = commodity.getCommodityInventory();
        // 创建数据时，参数不能为空
        if (add) {
            ThrowUtils.throwIf(StringUtils.isBlank(commodityName), ErrorCode.PARAMS_ERROR);
            ThrowUtils.throwIf(commodityInventory == null || commodityInventory <= 0, ErrorCode.PARAMS_ERROR);
        }
    }
    /**
     * @param commodity
     * @param request
     * @return
     */
    @Override
    public CommodityVO getCommodityVO(Commodity commodity, HttpServletRequest request) {
        CommodityVO commodityVO = CommodityVO.objToVo(commodity);
        if (commodity.getCommodityTypeId() != null) {
            CommodityType commodityType = getCommodityTypeService().getById(commodity.getCommodityTypeId());
            if (commodityType != null) {
                commodityVO.setCommodityTypeName(commodityType.getTypeName());
            }
        }
        // 1. 关联查询用户信息
        Long adminId = commodity.getAdminId();
        User admin = null;
        if (adminId != null && adminId > 0) {
            admin = userService.getById(adminId);
        }
        if (admin != null) {
            commodityVO.setAdminName(admin.getUserName());
        }
        return commodityVO;
    }
    /**
     * @param commodityQueryRequest
     * @return
     */
    @Override
    public QueryWrapper<Commodity> getQueryWrapper(CommodityQueryRequest commodityQueryRequest) {
        QueryWrapper<Commodity> queryWrapper = new QueryWrapper<>();
        if (commodityQueryRequest == null) {
            return queryWrapper;
        }
        Long id = commodityQueryRequest.getId();
        String commodityName = commodityQueryRequest.getCommodityName();
        String commodityDescription = commodityQueryRequest.getCommodityDescription();
        String degree = commodityQueryRequest.getDegree();
        Long commodityTypeId = commodityQueryRequest.getCommodityTypeId();
        Long adminId = commodityQueryRequest.getAdminId();
        Integer isListed = commodityQueryRequest.getIsListed();
        Integer commodityInventory = commodityQueryRequest.getCommodityInventory();
        String sortField = commodityQueryRequest.getSortField();
        String sortOrder = commodityQueryRequest.getSortOrder();
        //条件查询
        queryWrapper.like(StringUtils.isNotBlank(commodityName), "commodityName", commodityName);
        queryWrapper.like(StringUtils.isNotBlank(commodityDescription), "commodityDescription", commodityDescription);
        queryWrapper.like(StringUtils.isNotBlank(degree), "degree", degree);
        queryWrapper.eq(ObjectUtils.isNotEmpty(commodityTypeId), "commodityTypeId", commodityTypeId);
        queryWrapper.eq(ObjectUtils.isNotEmpty(id), "id", id);
        queryWrapper.eq(ObjectUtils.isNotEmpty(adminId), "adminId", adminId);
        queryWrapper.ge(ObjectUtils.isNotEmpty(commodityInventory), "commodityInventory", commodityInventory);
        queryWrapper.eq(ObjectUtils.isNotEmpty(isListed), "isListed", isListed);
        // 排序规则
        queryWrapper.orderBy(SqlUtils.validSortField(sortField),
                sortOrder.equals(CommonConstant.SORT_ORDER_ASC),
                sortField);
        return queryWrapper;
    }
    /**
     * @param commodityPage
     * @param request
     * @return
     */
    @Override
    public Page<CommodityVO> getCommodityVOPage(Page<Commodity> commodityPage, HttpServletRequest request) {
        List<Commodity> commodityList = commodityPage.getRecords();
        Page<CommodityVO> commodityVOPage = new Page<>(commodityPage.getCurrent(), commodityPage.getSize(), commodityPage.getTotal());
        if (CollUtil.isEmpty(commodityList)) {
            return commodityVOPage;
        }
        
        // 批量查询商品类型并转换为Map
        Map<Long, CommodityType> commodityTypeMap = buildCommodityTypeMap(commodityList);
        
        // 转换为VO并设置商品类型名称
        List<CommodityVO> commodityVOList = commodityList.stream()
                .map(commodity -> convertToVO(commodity, commodityTypeMap))
                .collect(Collectors.toList());

        commodityVOPage.setRecords(commodityVOList);
        return commodityVOPage;
    }

    /**
     * 构建商品类型映射
     */
    private Map<Long, CommodityType> buildCommodityTypeMap(List<Commodity> commodityList) {
        Set<Long> commodityTypeIds = commodityList.stream()
                .map(Commodity::getCommodityTypeId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
        
        if (CollUtil.isEmpty(commodityTypeIds)) {
            return new HashMap<>();
        }
        
        List<CommodityType> commodityTypes = getCommodityTypeService().listByIds(commodityTypeIds);
        return commodityTypes.stream()
                .collect(Collectors.toMap(CommodityType::getId, type -> type));
    }

    /**
     * 转换为VO并设置商品类型名称
     */
    private CommodityVO convertToVO(Commodity commodity, Map<Long, CommodityType> commodityTypeMap) {
        CommodityVO commodityVO = CommodityVO.objToVo(commodity);
        if (commodity.getCommodityTypeId() != null) {
            CommodityType commodityType = commodityTypeMap.get(commodity.getCommodityTypeId());
            if (commodityType != null) {
                commodityVO.setCommodityTypeName(commodityType.getTypeName());
            }
        }
        return commodityVO;
    }
    @Override
    public Commodity getByIdWithLock(Long id) {
        return baseMapper.selectByIdWithLock(id);
    }

    /**
     * 创建商品（业务逻辑）
     *
     * @param commodityAddRequest
     * @param request
     * @return 新商品ID
     */
    @Override
    public Long addCommodity(CommodityAddRequest commodityAddRequest, HttpServletRequest request) {
        ThrowUtils.throwIf(commodityAddRequest == null, ErrorCode.PARAMS_ERROR);
        
        // DTO转实体
        Commodity commodity = new Commodity();
        BeanUtils.copyProperties(commodityAddRequest, commodity);
        
        // 数据校验
        validCommodity(commodity, true);
        
        // 获取当前登录用户并设置adminId
        User loginUser = userService.getLoginUser(request);
        commodity.setAdminId(loginUser.getId());
        
        // 写入数据库
        boolean result = this.save(commodity);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        
        return commodity.getId();
    }

    /**
     * 删除商品（包含权限校验）
     * 用户可以删除自己的商品，管理员可以删除所有商品
     *
     * @param id
     * @param request
     * @return
     */
    @Override
    public Boolean deleteCommodityById(Long id, HttpServletRequest request) {
        ThrowUtils.throwIf(id == null || id <= 0, ErrorCode.PARAMS_ERROR);
        
        // 获取当前登录用户
        User loginUser = userService.getLoginUser(request);
        
        // 判断是否存在
        Commodity oldCommodity = this.getById(id);
        ThrowUtils.throwIf(oldCommodity == null, ErrorCode.NOT_FOUND_ERROR);
        
        // 仅本人或管理员可删除
        if (!oldCommodity.getAdminId().equals(loginUser.getId()) && !userService.isAdmin(loginUser)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        
        // 删除数据库记录
        boolean result = this.removeById(id);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        
        return true;
    }

    /**
     * 更新商品（仅管理员）
     *
     * @param commodityUpdateRequest
     * @return
     */
    @Override
    public Boolean updateCommodityById(CommodityUpdateRequest commodityUpdateRequest) {
        ThrowUtils.throwIf(commodityUpdateRequest == null || commodityUpdateRequest.getId() <= 0, ErrorCode.PARAMS_ERROR);
        
        // DTO转实体
        Commodity commodity = new Commodity();
        BeanUtils.copyProperties(commodityUpdateRequest, commodity);
        
        // 数据校验
        validCommodity(commodity, false);
        
        // 判断是否存在
        long id = commodityUpdateRequest.getId();
        Commodity oldCommodity = this.getById(id);
        ThrowUtils.throwIf(oldCommodity == null, ErrorCode.NOT_FOUND_ERROR);
        
        // 更新数据库
        boolean result = this.updateById(commodity);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        
        return true;
    }

    /**
     * 编辑商品（含特殊权限逻辑）
     *
     * @param commodityEditRequest
     * @param request
     * @return
     */
    @Override
    public Boolean editCommodityById(CommodityEditRequest commodityEditRequest, HttpServletRequest request) {
        ThrowUtils.throwIf(commodityEditRequest == null || commodityEditRequest.getId() <= 0, ErrorCode.PARAMS_ERROR);
        
        log.info("开始编辑商品，ID: {}, 请求数据: {}", commodityEditRequest.getId(), commodityEditRequest);
        
        // DTO转实体
        Commodity commodity = new Commodity();
        BeanUtils.copyProperties(commodityEditRequest, commodity);
        
        log.info("转换后的商品实体: {}", commodity);
        
        // 数据校验
        validCommodity(commodity, false);
        
        // 验证商品存在性和权限
        validateEditPermission(commodityEditRequest, request);
        
        // 更新数据库
        boolean result = this.updateById(commodity);
        log.info("商品更新结果: {}, 商品ID: {}", result, commodity.getId());
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        
        return true;
    }

    /**
     * 验证编辑权限
     * 用户可以编辑自己的商品，管理员可以编辑所有商品
     */
    private void validateEditPermission(CommodityEditRequest commodityEditRequest, HttpServletRequest request) {
        long id = commodityEditRequest.getId();
        Commodity oldCommodity = this.getById(id);
        ThrowUtils.throwIf(oldCommodity == null, ErrorCode.NOT_FOUND_ERROR);
        
        // 判断是否需要跳过权限检查（更新浏览量或者收藏量，无需权限检查）
        if (!shouldSkipPermissionCheck(commodityEditRequest)) {
            User loginUser = userService.getLoginUser(request);
            
            // 仅本人或管理员可编辑
            if (!oldCommodity.getAdminId().equals(loginUser.getId()) && !userService.isAdmin(loginUser)) {
                throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
            }
        }
    }

    /**
     * 分页获取当前用户的商品列表
     *
     * @param commodityQueryRequest
     * @param request
     * @return
     */
    @Override
    public Page<CommodityVO> getMyCommodityVOPage(CommodityQueryRequest commodityQueryRequest, HttpServletRequest request) {
        ThrowUtils.throwIf(commodityQueryRequest == null, ErrorCode.PARAMS_ERROR);
        
        // 获取当前登录用户并设置查询条件
        User loginUser = userService.getLoginUser(request);
        commodityQueryRequest.setAdminId(loginUser.getId());
        
        long current = commodityQueryRequest.getCurrent();
        long size = commodityQueryRequest.getPageSize();
        
        // 限制爬虫
        ThrowUtils.throwIf(size > 2000, ErrorCode.PARAMS_ERROR);
        
        // 查询数据库
        Page<Commodity> commodityPage = this.page(
                new Page<>(current, size),
                this.getQueryWrapper(commodityQueryRequest)
        );
        
        // 获取封装类
        return this.getCommodityVOPage(commodityPage, request);
    }

    /**
     * 分页获取商品列表（带权限控制）
     * 管理员：返回所有商品
     * 普通用户：只返回自己发布的商品
     *
     * @param commodityQueryRequest
     * @param request
     * @return
     */
    @Override
    public Page<CommodityVO> listCommodityVOByPageWithAuth(CommodityQueryRequest commodityQueryRequest, HttpServletRequest request) {
        ThrowUtils.throwIf(commodityQueryRequest == null, ErrorCode.PARAMS_ERROR);
        
        // 获取当前登录用户
        User loginUser = userService.getLoginUser(request);
        
        // 如果不是管理员，只能查看自己发布的商品
        if (!userService.isAdmin(loginUser)) {
            commodityQueryRequest.setAdminId(loginUser.getId());
        }
        // 管理员可以查看所有商品，不设置 adminId 过滤
        
        long current = commodityQueryRequest.getCurrent();
        long size = commodityQueryRequest.getPageSize();
        
        // 限制爬虫
        ThrowUtils.throwIf(size > 2000, ErrorCode.PARAMS_ERROR);
        
        // 查询数据库
        Page<Commodity> commodityPage = this.page(
                new Page<>(current, size),
                this.getQueryWrapper(commodityQueryRequest)
        );
        
        // 获取封装类
        return this.getCommodityVOPage(commodityPage, request);
    }

    /**
     * 购买商品（业务逻辑）
     *
     * @param buyRequest
     * @param request
     * @return
     */
    @Override
    public synchronized Map<String, Object> buyCommodity(BuyCommodityRequest buyRequest, HttpServletRequest request) {
        // 参数校验
        validateBuyRequest(buyRequest);
        
        // 获取当前用户
        User loginUser = userService.getLoginUser(request);
        
        // 查询并验证商品
        Commodity commodity = validateAndGetCommodity(buyRequest.getCommodityId());
        
        // 计算总金额并创建订单
        CommodityOrder order = createOrder(buyRequest, loginUser, commodity);
        
        // 执行购买事务
        return executePurchaseTransaction(order, commodity, buyRequest.getBuyNumber());
    }

    /**
     * 校验购买请求参数
     */
    private void validateBuyRequest(BuyCommodityRequest buyRequest) {
        if (buyRequest == null || buyRequest.getCommodityId() == null || buyRequest.getBuyNumber() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数错误");
        }
    }

    /**
     * 验证并获取商品（带锁）
     */
    private Commodity validateAndGetCommodity(Long commodityId) {
        Commodity commodity = this.getByIdWithLock(commodityId);
        if (commodity == null || commodity.getIsDelete() == 1) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "商品不存在");
        }
        if (commodity.getIsListed() != 1) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "商品未上架");
        }
        return commodity;
    }

    /**
     * 创建订单
     */
    private CommodityOrder createOrder(BuyCommodityRequest buyRequest, User loginUser, Commodity commodity) {
        BigDecimal totalAmount = commodity.getPrice().multiply(new BigDecimal(buyRequest.getBuyNumber()));
        CommodityOrder order = new CommodityOrder();
        order.setUserId(loginUser.getId());
        order.setCommodityId(buyRequest.getCommodityId());
        order.setBuyNumber(buyRequest.getBuyNumber());
        order.setPaymentAmount(totalAmount);
        order.setRemark(buyRequest.getRemark());
        order.setPayStatus(0);
        return order;
    }

    /**
     * 执行购买事务
     */
    private Map<String, Object> executePurchaseTransaction(CommodityOrder order, Commodity commodity, Integer buyNumber) {
        return transactionTemplate.execute(status -> {
            try {
                // 创建订单
                order.setPayStatus(1);
                if (!getCommodityOrderService().save(order)) {
                    throw new BusinessException(ErrorCode.OPERATION_ERROR, "订单创建失败");
                }
                
                // 扣减库存
                Long inventoryResult = deductInventory(commodity.getId(), buyNumber);
                
                // 更新商品库存
                commodity.setCommodityInventory(inventoryResult.intValue());
                if (!this.updateById(commodity)) {
                    throw new BusinessException(ErrorCode.OPERATION_ERROR, "库存更新失败");
                }
                
                // 返回结果
                return buildPurchaseResult(order);
            } catch (Exception e) {
                status.setRollbackOnly();
                throw new BusinessException(ErrorCode.OPERATION_ERROR, "交易失败：" + e.getMessage());
            }
        });
    }

    /**
     * 扣减库存
     */
    private Long deductInventory(Long commodityId, Integer buyNumber) {
        log.info("开始处理库存扣减: commodityId={}, buyNumber={}", commodityId, buyNumber);
        
        Long currentInventory = inventoryService.getInventory(commodityId);
        log.info("Redis中的当前库存: {}", currentInventory);
        
        Long inventoryResult = inventoryService.deductInventoryWithInit(commodityId, (long) buyNumber);
        log.info("库存扣减结果: {}", inventoryResult);
        
        if (inventoryResult < 0) {
            log.error("库存不足: commodityId={}, requestBuyNumber={}", commodityId, buyNumber);
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "库存不足");
        }
        
        return inventoryResult;
    }

    /**
     * 构建购买结果
     */
    private Map<String, Object> buildPurchaseResult(CommodityOrder order) {
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("orderId", order.getId());
        resultMap.put("payStatus", order.getPayStatus());
        resultMap.put("needPay", false);
        return resultMap;
    }

    /**
     * 判断是否需要跳过权限检查
     */
    private boolean shouldSkipPermissionCheck(CommodityEditRequest request) {
        return (request.getFavourNum() != null && request.getFavourNum() >= 0)
                || (request.getViewNum() != null && request.getViewNum() >= 0);
    }

}
