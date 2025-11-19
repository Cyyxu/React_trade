package com.xyes.springboot.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xyes.springboot.exception.BusinessException;
import com.xyes.springboot.exception.ErrorCode;
import com.xyes.springboot.constant.CommonConstant;
import com.xyes.springboot.exception.ThrowUtils;
import com.xyes.springboot.mapper.BarrageMapper;
import com.xyes.springboot.model.dto.barrage.BarrageAddRequest;
import com.xyes.springboot.model.dto.barrage.BarrageQueryRequest;
import com.xyes.springboot.model.dto.barrage.BarrageUpdateRequest;
import com.xyes.springboot.model.entity.Barrage;
import com.xyes.springboot.model.entity.User;
import com.xyes.springboot.model.vo.BarrageVO;
import com.xyes.springboot.service.BarrageService;
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

/**
 * 弹幕服务实现
 *
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class BarrageServiceImpl extends ServiceImpl<BarrageMapper, Barrage> implements BarrageService {

    private final UserService userService;

    /**
     * 校验数据
     *
     * @param barrage
     * @param add     对创建的数据进行校验
     */
    @Override
    public void validBarrage(Barrage barrage, boolean add) {
        ThrowUtils.throwIf(barrage == null, ErrorCode.PARAMS_ERROR);
        // todo 从对象中取值
        String message = barrage.getMessage();
        String userAvatar = barrage.getUserAvatar();
        Long userId = barrage.getUserId();
        // 创建数据时，参数不能为空
        if (add) {
            // todo 补充校验规则
            ThrowUtils.throwIf(StringUtils.isBlank(message), ErrorCode.PARAMS_ERROR);
            ThrowUtils.throwIf(StringUtils.isBlank(userAvatar), ErrorCode.PARAMS_ERROR);
            ThrowUtils.throwIf(userId < 0, ErrorCode.PARAMS_ERROR);
        }
        // 修改数据时，有参数则校验
        // todo 补充校验规则
        if (StringUtils.isNotBlank(message)) {
            ThrowUtils.throwIf(message.length() > 50, ErrorCode.PARAMS_ERROR, "弹幕消息过长");
        }
    }

    /**
     * 获取查询条件
     *
     * @param barrageQueryRequest
     * @return
     */
    @Override
    public QueryWrapper<Barrage> getQueryWrapper(BarrageQueryRequest barrageQueryRequest) {
        QueryWrapper<Barrage> queryWrapper = new QueryWrapper<>();
        if (barrageQueryRequest == null) {
            return queryWrapper;
        }
        String message = barrageQueryRequest.getMessage();
        Long userId = barrageQueryRequest.getUserId();
        Integer isSelected = barrageQueryRequest.getIsSelected();
        String sortField = barrageQueryRequest.getSortField();
        String sortOrder = barrageQueryRequest.getSortOrder();
        // 模糊查询
        queryWrapper.like(StringUtils.isNotBlank(message), "message", message);
        // 精确查询
        queryWrapper.ne(ObjectUtils.isNotEmpty(isSelected), "isSelected", isSelected);
        queryWrapper.eq(ObjectUtils.isNotEmpty(userId), "userId", userId);
        // 排序规则
        queryWrapper.orderBy(SqlUtils.validSortField(sortField),
                sortOrder.equals(CommonConstant.SORT_ORDER_ASC),
                sortField);
        return queryWrapper;
    }

    /**
     * 获取弹幕封装
     *
     * @param barrage
     * @param request
     * @return
     */
    @Override
    public BarrageVO getBarrageVO(Barrage barrage, HttpServletRequest request) {
        // 对象转封装类
        return BarrageVO.objToVo(barrage);
    }

    /**
     * 分页获取弹幕封装
     *
     * @param barragePage
     * @param request
     * @return
     */
    @Override
    public Page<BarrageVO> getBarrageVOPage(Page<Barrage> barragePage, HttpServletRequest request) {
        List<Barrage> barrageList = barragePage.getRecords();
        Page<BarrageVO> barrageVOPage = new Page<>(barragePage.getCurrent(), barragePage.getSize(), barragePage.getTotal());
        if (CollUtil.isEmpty(barrageList)) {
            return barrageVOPage;
        }
        // 对象列表 => 封装对象列表
        List<BarrageVO> barrageVOList = barrageList.stream().map(BarrageVO::objToVo).collect(Collectors.toList());
        barrageVOPage.setRecords(barrageVOList);
        return barrageVOPage;
    }

    /**
     * 创建弹幕（业务逻辑）
     *
     * @param barrageAddRequest
     * @param request
     * @return 新弹幕ID
     */
    @Override
    public Long addBarrage(BarrageAddRequest barrageAddRequest, HttpServletRequest request) {
        ThrowUtils.throwIf(barrageAddRequest == null, ErrorCode.PARAMS_ERROR);
        
        // DTO转实体
        Barrage barrage = new Barrage();
        BeanUtils.copyProperties(barrageAddRequest, barrage);
        
        // 获取当前登录用户并设置userId
        User user = userService.getLoginUser(request);
        barrage.setUserId(user.getId());
        
        // 数据校验
        validBarrage(barrage, true);
        
        // 写入数据库
        boolean result = this.save(barrage);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        
        return barrage.getId();
    }

    /**
     * 删除弹幕（包含权限校验）
     *
     * @param id
     * @param request
     * @return
     */
    @Override
    public Boolean deleteBarrageById(Long id, HttpServletRequest request) {
        ThrowUtils.throwIf(id == null || id <= 0, ErrorCode.PARAMS_ERROR);
        
        // 获取当前登录用户
        User user = userService.getLoginUser(request);
        
        // 判断弹幕是否存在
        Barrage oldBarrage = this.getById(id);
        ThrowUtils.throwIf(oldBarrage == null, ErrorCode.NOT_FOUND_ERROR);
        
        // 仅本人或管理员可删除
        if (!oldBarrage.getUserId().equals(user.getId()) && !userService.isAdmin(request)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        
        // 删除数据库记录
        boolean result = this.removeById(id);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        
        return true;
    }

    /**
     * 更新弹幕（仅管理员）
     *
     * @param barrageUpdateRequest
     * @return
     */
    @Override
    public Boolean updateBarrageById(BarrageUpdateRequest barrageUpdateRequest) {
        ThrowUtils.throwIf(barrageUpdateRequest == null || barrageUpdateRequest.getId() <= 0, ErrorCode.PARAMS_ERROR);
        
        // DTO转实体
        Barrage barrage = new Barrage();
        BeanUtils.copyProperties(barrageUpdateRequest, barrage);
        
        // 数据校验
        validBarrage(barrage, false);
        
        // 判断是否存在
        long id = barrageUpdateRequest.getId();
        Barrage oldBarrage = this.getById(id);
        ThrowUtils.throwIf(oldBarrage == null, ErrorCode.NOT_FOUND_ERROR);
        
        // 更新数据库
        boolean result = this.updateById(barrage);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        
        return true;
    }

    /**
     * 分页获取当前用户的弹幕列表
     *
     * @param barrageQueryRequest
     * @param request
     * @return
     */
    @Override
    public Page<BarrageVO> getMyBarrageVOPage(BarrageQueryRequest barrageQueryRequest, HttpServletRequest request) {
        ThrowUtils.throwIf(barrageQueryRequest == null, ErrorCode.PARAMS_ERROR);
        
        // 获取当前登录用户并设置查询条件
        User loginUser = userService.getLoginUser(request);
        barrageQueryRequest.setUserId(loginUser.getId());
        
        long current = barrageQueryRequest.getCurrent();
        long size = barrageQueryRequest.getPageSize();
        
        // 限制爬虫
        ThrowUtils.throwIf(size > 2000, ErrorCode.PARAMS_ERROR);
        
        // 查询数据库
        Page<Barrage> barragePage = this.page(
                new Page<>(current, size),
                this.getQueryWrapper(barrageQueryRequest)
        );
        
        // 获取封装类
        return this.getBarrageVOPage(barragePage, request);
    }

}
