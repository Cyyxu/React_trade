package com.xyes.springboot.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xyes.springboot.exception.BusinessException;
import com.xyes.springboot.exception.ErrorCode;
import com.xyes.springboot.constant.CommonConstant;
import com.xyes.springboot.exception.ThrowUtils;
import com.xyes.springboot.mapper.CommodityScoreMapper;
import com.xyes.springboot.model.dto.commodityScore.*;
import com.xyes.springboot.model.entity.CommodityScore;
import com.xyes.springboot.model.entity.User;
import com.xyes.springboot.model.vo.CommodityScoreVO;
import com.xyes.springboot.service.CommodityScoreService;
import com.xyes.springboot.service.UserService;
import com.xyes.springboot.util.SqlUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 商品评分表服务实现
 *
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class CommodityScoreServiceImpl extends ServiceImpl<CommodityScoreMapper, CommodityScore> implements CommodityScoreService {

    private final UserService userService;
    /**
     * 校验数据
     *
     * @param commodityScore
     * @param add      对创建的数据进行校验
     */
    @Override
    public void validCommodityScore(CommodityScore commodityScore, boolean add) {
        ThrowUtils.throwIf(commodityScore == null, ErrorCode.PARAMS_ERROR);
        Long commodityId = commodityScore.getCommodityId();
        Integer score = commodityScore.getScore();
        // 创建数据时，参数不能为空
        if (add) {
            ThrowUtils.throwIf(commodityId == null || commodityId <=0 , ErrorCode.PARAMS_ERROR);
            ThrowUtils.throwIf(score == null || score <0 , ErrorCode.PARAMS_ERROR);
        }

    }

    /**
     * 获取查询条件
     *
     * @param commodityScoreQueryRequest
     * @return
     */
    @Override
    public QueryWrapper<CommodityScore> getQueryWrapper(CommodityScoreQueryRequest commodityScoreQueryRequest) {
        QueryWrapper<CommodityScore> queryWrapper = new QueryWrapper<>();
        if (commodityScoreQueryRequest == null) {
            return queryWrapper;
        }
        Long id = commodityScoreQueryRequest.getId();
        Long commodityId = commodityScoreQueryRequest.getCommodityId();
        Long userId = commodityScoreQueryRequest.getUserId();
        Integer score = commodityScoreQueryRequest.getScore();
        String sortField = commodityScoreQueryRequest.getSortField();
        String sortOrder = commodityScoreQueryRequest.getSortOrder();
        // 精确查询
        queryWrapper.eq(ObjectUtils.isNotEmpty(commodityId), "commodityId", commodityId);
        queryWrapper.eq(ObjectUtils.isNotEmpty(score), "score", score);
        queryWrapper.eq(ObjectUtils.isNotEmpty(id), "id", id);
        queryWrapper.eq(ObjectUtils.isNotEmpty(userId), "userId", userId);
        // 排序规则
        queryWrapper.orderBy(SqlUtils.validSortField(sortField),
                sortOrder.equals(CommonConstant.SORT_ORDER_ASC),
                sortField);
        return queryWrapper;
    }

    /**
     * 获取商品评分表封装
     *
     * @param commodityScore
     * @param request
     * @return
     */
    @Override
    public CommodityScoreVO getCommodityScoreVO(CommodityScore commodityScore, HttpServletRequest request) {
        // 对象转封装类
        return CommodityScoreVO.objToVo(commodityScore);
    }

    /**
     * 分页获取商品评分表封装
     *
     * @param commodityScorePage
     * @param request
     * @return
     */
    @Override
    public Page<CommodityScoreVO> getCommodityScoreVOPage(Page<CommodityScore> commodityScorePage, HttpServletRequest request) {
        List<CommodityScore> commodityScoreList = commodityScorePage.getRecords();
        Page<CommodityScoreVO> commodityScoreVOPage = new Page<>(commodityScorePage.getCurrent(), commodityScorePage.getSize(), commodityScorePage.getTotal());
        if (CollUtil.isEmpty(commodityScoreList)) {
            return commodityScoreVOPage;
        }
        // 对象列表 => 封装对象列表
        List<CommodityScoreVO> commodityScoreVOList = commodityScoreList.stream().map(CommodityScoreVO::objToVo).collect(Collectors.toList());
        // region 可选
        // 1. 关联查询用户信息
        Set<Long> userIdSet = commodityScoreVOList.stream().map(CommodityScoreVO::getUserId).collect(Collectors.toSet());
        Map<Long, List<User>> userIdUserListMap = userService.listByIds(userIdSet).stream()
                .collect(Collectors.groupingBy(User::getId));
        // 填充信息
        commodityScoreVOList.forEach(commodityScoreVO -> {
            Long userId = commodityScoreVO.getUserId();
            User user = null;
            if (userIdUserListMap.containsKey(userId)) {
                user = userIdUserListMap.get(userId).get(0);
            }
            commodityScoreVO.setUserVO(userService.getUserVO(user));
        });
        // endregion
        commodityScoreVOPage.setRecords(commodityScoreVOList);
        return commodityScoreVOPage;
    }

    @Override
    public Double getAverageScoreBySpotId(Long commodityId) {
        return this.baseMapper.getAverageScoreByCommodityId(commodityId);
    }

    /**
     * 创建评分（业务逻辑）
     *
     * @param commodityScoreAddRequest
     * @param request
     * @return 新评分ID
     */
    @Override
    public Long addCommodityScore(CommodityScoreAddRequest commodityScoreAddRequest, HttpServletRequest request) {
        ThrowUtils.throwIf(commodityScoreAddRequest == null, ErrorCode.PARAMS_ERROR);
        
        // DTO转实体
        CommodityScore commodityScore = new CommodityScore();
        BeanUtils.copyProperties(commodityScoreAddRequest, commodityScore);
        
        // 数据校验
        validCommodityScore(commodityScore, true);
        
        // 获取当前登录用户并设置userId
        User loginUser = userService.getLoginUser(request);
        commodityScore.setUserId(loginUser.getId());
        
        // 写入数据库
        boolean result = this.save(commodityScore);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        
        return commodityScore.getId();
    }

    /**
     * 删除评分（包含权限校验）
     *
     * @param id
     * @param request
     * @return
     */
    @Override
    public Boolean deleteCommodityScoreById(Long id, HttpServletRequest request) {
        ThrowUtils.throwIf(id == null || id <= 0, ErrorCode.PARAMS_ERROR);
        
        // 获取当前登录用户
        User user = userService.getLoginUser(request);
        
        // 判断评分是否存在
        CommodityScore oldCommodityScore = this.getById(id);
        ThrowUtils.throwIf(oldCommodityScore == null, ErrorCode.NOT_FOUND_ERROR);
        
        // 仅本人或管理员可删除
        if (!oldCommodityScore.getUserId().equals(user.getId()) && !userService.isAdmin(request)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        
        // 删除数据库记录
        boolean result = this.removeById(id);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        
        return true;
    }

    /**
     * 更新评分（仅管理员）
     *
     * @param commodityScoreUpdateRequest
     * @return
     */
    @Override
    public Boolean updateCommodityScoreById(CommodityScoreUpdateRequest commodityScoreUpdateRequest) {
        ThrowUtils.throwIf(commodityScoreUpdateRequest == null || commodityScoreUpdateRequest.getId() <= 0, ErrorCode.PARAMS_ERROR);
        
        // DTO转实体
        CommodityScore commodityScore = new CommodityScore();
        BeanUtils.copyProperties(commodityScoreUpdateRequest, commodityScore);
        
        // 数据校验
        validCommodityScore(commodityScore, false);
        
        // 判断是否存在
        long id = commodityScoreUpdateRequest.getId();
        CommodityScore oldCommodityScore = this.getById(id);
        ThrowUtils.throwIf(oldCommodityScore == null, ErrorCode.NOT_FOUND_ERROR);
        
        // 更新数据库
        boolean result = this.updateById(commodityScore);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        
        return true;
    }

    /**
     * 编辑评分（用户自己可用）
     *
     * @param commodityScoreEditRequest
     * @param request
     * @return
     */
    @Override
    public Boolean editCommodityScoreById(CommodityScoreEditRequest commodityScoreEditRequest, HttpServletRequest request) {
        ThrowUtils.throwIf(commodityScoreEditRequest == null || commodityScoreEditRequest.getId() <= 0, ErrorCode.PARAMS_ERROR);
        
        // DTO转实体
        CommodityScore commodityScore = new CommodityScore();
        BeanUtils.copyProperties(commodityScoreEditRequest, commodityScore);
        
        // 数据校验
        validCommodityScore(commodityScore, false);
        
        // 获取当前登录用户
        User loginUser = userService.getLoginUser(request);
        
        // 判断是否存在
        long id = commodityScoreEditRequest.getId();
        CommodityScore oldCommodityScore = this.getById(id);
        ThrowUtils.throwIf(oldCommodityScore == null, ErrorCode.NOT_FOUND_ERROR);
        
        // 仅本人或管理员可编辑
        if (!oldCommodityScore.getUserId().equals(loginUser.getId()) && !userService.isAdmin(loginUser)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        
        // 更新数据库
        boolean result = this.updateById(commodityScore);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        
        return true;
    }

    /**
     * 分页获取当前用户的评分列表
     *
     * @param commodityScoreQueryRequest
     * @param request
     * @return
     */
    @Override
    public Page<CommodityScoreVO> getMyCommodityScoreVOPage(CommodityScoreQueryRequest commodityScoreQueryRequest, HttpServletRequest request) {
        ThrowUtils.throwIf(commodityScoreQueryRequest == null, ErrorCode.PARAMS_ERROR);
        
        // 获取当前登录用户并设置查询条件
        User loginUser = userService.getLoginUser(request);
        commodityScoreQueryRequest.setUserId(loginUser.getId());
        
        long current = commodityScoreQueryRequest.getCurrent();
        long size = commodityScoreQueryRequest.getPageSize();
        
        // 限制爬虫
        ThrowUtils.throwIf(size > 2000, ErrorCode.PARAMS_ERROR);
        
        // 查询数据库
        Page<CommodityScore> commodityScorePage = this.page(
                new Page<>(current, size),
                this.getQueryWrapper(commodityScoreQueryRequest)
        );
        
        // 获取封装类
        return this.getCommodityScoreVOPage(commodityScorePage, request);
    }

    /**
     * 获取商品的平均评分（格式化）
     *
     * @param commodityId
     * @return
     */
    @Override
    public Double getAverageScore(Long commodityId) {
        try {
            log.info("开始获取商品平均分，commodityId: {}", commodityId);
            
            if (commodityId <= 0) {
                log.warn("商品ID无效: {}", commodityId);
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "商品ID无效");
            }
            
            Double averageScore = getAverageScoreBySpotId(commodityId);
            log.info("查询到的平均分: {}", averageScore);
            
            if (averageScore != null) {
                double result = Math.round(averageScore * 100.0) / 100.0;
                log.info("返回的平均分: {}", result);
                return result;
            }
            
            log.info("商品无评分记录");
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "商品评分为空，快来成为第一个评分的人吧");
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("获取商品平均分时发生异常，commodityId: {}", commodityId, e);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "系统错误：" + e.getMessage());
        }
    }

}
