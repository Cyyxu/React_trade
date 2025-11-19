package com.xyes.springboot.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xyes.springboot.exception.BusinessException;
import com.xyes.springboot.exception.ErrorCode;
import com.xyes.springboot.constant.CommonConstant;
import com.xyes.springboot.exception.ThrowUtils;
import com.xyes.springboot.mapper.CommodityTypeMapper;
import com.xyes.springboot.mapper.Commoditymapper;
import com.xyes.springboot.model.dto.commodityType.*;
import com.xyes.springboot.model.entity.Commodity;
import com.xyes.springboot.model.entity.CommodityType;
import com.xyes.springboot.model.entity.User;
import com.xyes.springboot.model.vo.CommodityTypeVO;
import com.xyes.springboot.service.CommodityTypeService;
import com.xyes.springboot.service.UserService;
import com.xyes.springboot.util.SqlUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class CommodityTypeServiceImpl extends ServiceImpl<CommodityTypeMapper, CommodityType> implements CommodityTypeService {

    private final UserService userService;
    private final Commoditymapper commodityMapper;

    /**
     * 校验数据
     *
     * @param commodityType
     * @param add      对创建的数据进行校验
     */
    @Override
    public void validCommodityType(CommodityType commodityType, boolean add) {
        ThrowUtils.throwIf(commodityType == null, ErrorCode.PARAMS_ERROR);
        String typeName = commodityType.getTypeName();
        // 创建数据时，参数不能为空
        if (add) {
            ThrowUtils.throwIf(StringUtils.isBlank(typeName), ErrorCode.PARAMS_ERROR);
        }
    }
    /**
     * 获取查询条件
     * @param commodityTypeQueryRequest
     * @return
     */
    @Override
    public QueryWrapper<CommodityType> getQueryWrapper(CommodityTypeQueryRequest commodityTypeQueryRequest) {
        QueryWrapper<CommodityType> queryWrapper = new QueryWrapper<>();
        if (commodityTypeQueryRequest == null) {
            return queryWrapper;
        }
        Long id = commodityTypeQueryRequest.getId();
        String typeName = commodityTypeQueryRequest.getTypeName();
        String sortField = commodityTypeQueryRequest.getSortField();
        String sortOrder = commodityTypeQueryRequest.getSortOrder();
        // 模糊查询
        queryWrapper.like(StringUtils.isNotBlank(typeName), "typeName", typeName);
        // 精确查询
        queryWrapper.eq(ObjectUtils.isNotEmpty(id), "id", id);
        // 排序规则
        queryWrapper.orderBy(SqlUtils.validSortField(sortField),
                sortOrder.equals(CommonConstant.SORT_ORDER_ASC),
                sortField);
        return queryWrapper;
    }

    /**
     * 获取商品类别表封装
     * @param commodityType
     * @param request
     * @return
     */
    @Override
    public CommodityTypeVO getCommodityTypeVO(CommodityType commodityType, HttpServletRequest request) {
        // 对象转封装类
        return CommodityTypeVO.objToVo(commodityType);
    }

    /**
     * 分页获取商品类别表封装
     * @param commodityTypePage
     * @param request
     * @return
     */
    @Override
    public Page<CommodityTypeVO> getCommodityTypeVOPage(Page<CommodityType> commodityTypePage, HttpServletRequest request) {
        List<CommodityType> commodityTypeList = commodityTypePage.getRecords();
        Page<CommodityTypeVO> commodityTypeVOPage = new Page<>(commodityTypePage.getCurrent(), commodityTypePage.getSize(), commodityTypePage.getTotal());
        if (CollUtil.isEmpty(commodityTypeList)) {
            return commodityTypeVOPage;
        }
        // 对象列表 => 封装对象列表
        List<CommodityTypeVO> commodityTypeVOList = commodityTypeList.stream()
                .map(CommodityTypeVO::objToVo)
                .collect(Collectors.toList());
        commodityTypeVOPage.setRecords(commodityTypeVOList);
        return commodityTypeVOPage;
    }

    /**
     * 创建商品类别（业务逻辑）
     *
     * @param commodityTypeAddRequest
     * @param request
     * @return 新商品类别ID
     */
    @Override
    public Long addCommodityType(CommodityTypeAddRequest commodityTypeAddRequest, HttpServletRequest request) {
        ThrowUtils.throwIf(commodityTypeAddRequest == null, ErrorCode.PARAMS_ERROR);
        
        // DTO转实体
        CommodityType commodityType = new CommodityType();
        BeanUtils.copyProperties(commodityTypeAddRequest, commodityType);
        
        // 数据校验
        validCommodityType(commodityType, true);
        
        // 写入数据库
        boolean result = this.save(commodityType);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        
        return commodityType.getId();
    }

    /**
     * 删除商品类别（包含权限校验和商品检查）
     *
     * @param id
     * @param request
     * @return
     */
    @Override
    public Boolean deleteCommodityTypeById(Long id, HttpServletRequest request) {
        ThrowUtils.throwIf(id == null || id <= 0, ErrorCode.PARAMS_ERROR);
        
        // 判断商品分类是否存在
        CommodityType oldCommodityType = this.getById(id);
        ThrowUtils.throwIf(oldCommodityType == null, ErrorCode.NOT_FOUND_ERROR);
        
        // 仅管理员可删除
        if (!userService.isAdmin(request)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        
        // 检查该分类下是否有挂载的商品
        QueryWrapper<Commodity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("commodityTypeId", id);
        long count = commodityMapper.selectCount(queryWrapper);
        if (count > 0) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "该分类下存在商品，无法删除");
        }
        
        // 执行删除操作
        boolean result = this.removeById(id);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        
        return true;
    }

    /**
     * 更新商品类别（仅管理员）
     *
     * @param commodityTypeUpdateRequest
     * @return
     */
    @Override
    public Boolean updateCommodityTypeById(CommodityTypeUpdateRequest commodityTypeUpdateRequest) {
        ThrowUtils.throwIf(commodityTypeUpdateRequest == null || commodityTypeUpdateRequest.getId() <= 0, ErrorCode.PARAMS_ERROR);
        
        // DTO转实体
        CommodityType commodityType = new CommodityType();
        BeanUtils.copyProperties(commodityTypeUpdateRequest, commodityType);
        
        // 数据校验
        validCommodityType(commodityType, false);
        
        // 判断是否存在
        long id = commodityTypeUpdateRequest.getId();
        CommodityType oldCommodityType = this.getById(id);
        ThrowUtils.throwIf(oldCommodityType == null, ErrorCode.NOT_FOUND_ERROR);
        
        // 更新数据库
        boolean result = this.updateById(commodityType);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        
        return true;
    }

    /**
     * 编辑商品类别（仅管理员）
     *
     * @param commodityTypeEditRequest
     * @param request
     * @return
     */
    @Override
    public Boolean editCommodityTypeById(CommodityTypeEditRequest commodityTypeEditRequest, HttpServletRequest request) {
        ThrowUtils.throwIf(commodityTypeEditRequest == null || commodityTypeEditRequest.getId() <= 0, ErrorCode.PARAMS_ERROR);
        
        // DTO转实体
        CommodityType commodityType = new CommodityType();
        BeanUtils.copyProperties(commodityTypeEditRequest, commodityType);
        
        // 数据校验
        validCommodityType(commodityType, false);
        
        // 获取当前登录用户
        User loginUser = userService.getLoginUser(request);
        
        // 判断是否存在
        long id = commodityTypeEditRequest.getId();
        CommodityType oldCommodityType = this.getById(id);
        ThrowUtils.throwIf(oldCommodityType == null, ErrorCode.NOT_FOUND_ERROR);
        
        // 仅管理员可编辑
        if (!userService.isAdmin(loginUser)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        
        // 更新数据库
        boolean result = this.updateById(commodityType);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        
        return true;
    }

}