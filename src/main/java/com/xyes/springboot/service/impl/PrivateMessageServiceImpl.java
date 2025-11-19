package com.xyes.springboot.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xyes.springboot.exception.BusinessException;
import com.xyes.springboot.exception.ErrorCode;
import com.xyes.springboot.constant.CommonConstant;
import com.xyes.springboot.exception.ThrowUtils;
import com.xyes.springboot.mapper.PrivateMessageMapper;
import com.xyes.springboot.model.dto.privateMessage.*;
import com.xyes.springboot.model.entity.PrivateMessage;
import com.xyes.springboot.model.entity.User;
import com.xyes.springboot.model.vo.PrivateMessageVO;
import com.xyes.springboot.service.PrivateMessageService;
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
 * 私信表服务实现
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class PrivateMessageServiceImpl extends ServiceImpl<PrivateMessageMapper, PrivateMessage> implements PrivateMessageService {

    private final UserService userService;
    /**
     * 校验数据
     * @param privateMessage
     * @param add      对创建的数据进行校验
     */
    @Override
    public void validPrivateMessage(PrivateMessage privateMessage, boolean add) {
        ThrowUtils.throwIf(privateMessage == null, ErrorCode.PARAMS_ERROR);
        Long senderId = privateMessage.getSenderId();
        Long recipientId = privateMessage.getRecipientId();
        String content = privateMessage.getContent();
        // 创建数据时，参数不能为空
        if (add) {
            ThrowUtils.throwIf(StringUtils.isBlank(content), ErrorCode.PARAMS_ERROR);
            ThrowUtils.throwIf(senderId == null || senderId <= 0, ErrorCode.PARAMS_ERROR);
            ThrowUtils.throwIf(recipientId == null || recipientId <= 0, ErrorCode.PARAMS_ERROR);
        }
    }
    /**
     * 获取查询条件
     * @param privateMessageQueryRequest
     * @return
     */
    @Override
    public QueryWrapper<PrivateMessage> getQueryWrapper(PrivateMessageQueryRequest privateMessageQueryRequest) {
        QueryWrapper<PrivateMessage> queryWrapper = new QueryWrapper<>();
        if (privateMessageQueryRequest == null) {
            return queryWrapper;
        }
        Long id = privateMessageQueryRequest.getId();
        Long senderId = privateMessageQueryRequest.getSenderId();
        Long recipientId = privateMessageQueryRequest.getRecipientId();
        String content = privateMessageQueryRequest.getContent();
        Integer alreadyRead = privateMessageQueryRequest.getAlreadyRead();
        String type = privateMessageQueryRequest.getType();
        Integer isRecalled = privateMessageQueryRequest.getIsRecalled();
        String sortField = privateMessageQueryRequest.getSortField();
        String sortOrder = privateMessageQueryRequest.getSortOrder();
        // 模糊查询
        queryWrapper.like(StringUtils.isNotBlank(content), "content", content);
        queryWrapper.like(StringUtils.isNotBlank(type), "type", type);
        // 精确查询
        queryWrapper.eq(ObjectUtils.isNotEmpty(isRecalled), "isRecalled", isRecalled);
        queryWrapper.eq(ObjectUtils.isNotEmpty(alreadyRead), "alreadyRead", alreadyRead);
        queryWrapper.eq(ObjectUtils.isNotEmpty(id), "id", id);
        queryWrapper.eq(ObjectUtils.isNotEmpty(senderId), "senderId", senderId);
        queryWrapper.eq(ObjectUtils.isNotEmpty(recipientId), "recipientId", recipientId);
        // 排序规则
        queryWrapper.orderBy(SqlUtils.validSortField(sortField),
                sortOrder.equals(CommonConstant.SORT_ORDER_ASC),
                sortField);
        return queryWrapper;
    }
    /**
     * 获取私信表封装
     * @param privateMessage
     * @param request
     * @return
     */
    @Override
    public PrivateMessageVO getPrivateMessageVO(PrivateMessage privateMessage, HttpServletRequest request) {
        // 对象转封装类
        return PrivateMessageVO.objToVo(privateMessage);
    }
    /**
     * 分页获取私信表封装
     *
     * @param privateMessagePage
     * @param request
     * @return
     */
    @Override
    public Page<PrivateMessageVO> getPrivateMessageVOPage(Page<PrivateMessage> privateMessagePage, HttpServletRequest request) {
        List<PrivateMessage> privateMessageList = privateMessagePage.getRecords();
        Page<PrivateMessageVO> privateMessageVOPage = new Page<>(privateMessagePage.getCurrent(), privateMessagePage.getSize(), privateMessagePage.getTotal());
        if (CollUtil.isEmpty(privateMessageList)) {
            return privateMessageVOPage;
        }
        // 对象列表 => 封装对象列表
        List<PrivateMessageVO> privateMessageVOList = privateMessageList.stream().map(PrivateMessageVO::objToVo).collect(Collectors.toList());
        privateMessageVOPage.setRecords(privateMessageVOList);
        return privateMessageVOPage;
    }

    /**
     * 创建私信（业务逻辑）
     *
     * @param privateMessageAddRequest
     * @param request
     * @return 新私信ID
     */
    @Override
    public Long addPrivateMessage(PrivateMessageAddRequest privateMessageAddRequest, HttpServletRequest request) {
        ThrowUtils.throwIf(privateMessageAddRequest == null, ErrorCode.PARAMS_ERROR);
        
        // DTO转实体
        PrivateMessage privateMessage = new PrivateMessage();
        BeanUtils.copyProperties(privateMessageAddRequest, privateMessage);
        
        // 获取当前登录用户并设置senderId
        User loginUser = userService.getLoginUser(request);
        ThrowUtils.throwIf(loginUser == null, ErrorCode.NOT_LOGIN_ERROR);
        privateMessage.setSenderId(loginUser.getId());
        
        // 设置默认值
        if (privateMessage.getAlreadyRead() == null) {
            privateMessage.setAlreadyRead(0); // 默认未阅读
        }
        if (privateMessage.getType() == null || privateMessage.getType().isEmpty()) {
            privateMessage.setType("user"); // 默认用户发送
        }
        if (privateMessage.getIsRecalled() == null) {
            privateMessage.setIsRecalled(0); // 默认未撤回
        }
        
        // 数据校验
        validPrivateMessage(privateMessage, true);
        
        // 写入数据库
        boolean result = this.save(privateMessage);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        
        return privateMessage.getId();
    }

    /**
     * 删除私信（包含权限校验）
     *
     * @param id
     * @param request
     * @return
     */
    @Override
    public Boolean deletePrivateMessageById(Long id, HttpServletRequest request) {
        ThrowUtils.throwIf(id == null || id <= 0, ErrorCode.PARAMS_ERROR);
        
        // 获取当前登录用户
        User user = userService.getLoginUser(request);
        
        // 判断私信是否存在
        PrivateMessage oldPrivateMessage = this.getById(id);
        ThrowUtils.throwIf(oldPrivateMessage == null, ErrorCode.NOT_FOUND_ERROR);
        
        // 仅本人或管理员可删除
        boolean isOwner = oldPrivateMessage.getSenderId() != null && oldPrivateMessage.getSenderId().equals(user.getId());
        boolean isAdmin = userService.isAdmin(request);
        if (!isOwner && !isAdmin) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        
        // 删除数据库记录
        boolean result = this.removeById(id);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        
        return true;
    }

    /**
     * 更新私信（仅管理员）
     *
     * @param privateMessageUpdateRequest
     * @return
     */
    @Override
    public Boolean updatePrivateMessageById(PrivateMessageUpdateRequest privateMessageUpdateRequest) {
        ThrowUtils.throwIf(privateMessageUpdateRequest == null || privateMessageUpdateRequest.getId() <= 0, ErrorCode.PARAMS_ERROR);
        
        // DTO转实体
        PrivateMessage privateMessage = new PrivateMessage();
        BeanUtils.copyProperties(privateMessageUpdateRequest, privateMessage);
        
        // 数据校验
        validPrivateMessage(privateMessage, false);
        
        // 判断是否存在
        long id = privateMessageUpdateRequest.getId();
        PrivateMessage oldPrivateMessage = this.getById(id);
        ThrowUtils.throwIf(oldPrivateMessage == null, ErrorCode.NOT_FOUND_ERROR);
        
        // 更新数据库
        boolean result = this.updateById(privateMessage);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        
        return true;
    }

    /**
     * 编辑私信（用户自己可用）
     *
     * @param privateMessageEditRequest
     * @param request
     * @return
     */
    @Override
    public Boolean editPrivateMessageById(PrivateMessageEditRequest privateMessageEditRequest, HttpServletRequest request) {
        ThrowUtils.throwIf(privateMessageEditRequest == null || privateMessageEditRequest.getId() <= 0, ErrorCode.PARAMS_ERROR);
        
        // DTO转实体
        PrivateMessage privateMessage = new PrivateMessage();
        BeanUtils.copyProperties(privateMessageEditRequest, privateMessage);
        
        // 数据校验
        validPrivateMessage(privateMessage, false);
        
        // 获取当前登录用户
        User loginUser = userService.getLoginUser(request);
        
        // 判断是否存在
        long id = privateMessageEditRequest.getId();
        PrivateMessage oldPrivateMessage = this.getById(id);
        ThrowUtils.throwIf(oldPrivateMessage == null, ErrorCode.NOT_FOUND_ERROR);
        
        // 仅本人或管理员可编辑
        boolean isOwner = oldPrivateMessage.getSenderId() != null && oldPrivateMessage.getSenderId().equals(loginUser.getId());
        boolean isAdmin = userService.isAdmin(loginUser);
        if (!isOwner && !isAdmin) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        
        // 更新数据库
        boolean result = this.updateById(privateMessage);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        
        return true;
    }

    /**
     * 分页获取私信列表（实体类）
     *
     * @param privateMessageQueryRequest
     * @return
     */
    @Override
    public Page<PrivateMessage> listPrivateMessageByPage(PrivateMessageQueryRequest privateMessageQueryRequest) {
        long current = privateMessageQueryRequest.getCurrent();
        long size = privateMessageQueryRequest.getPageSize();
        return this.page(new Page<>(current, size),
                this.getQueryWrapper(privateMessageQueryRequest));
    }

    /**
     * 分页获取私信列表（封装类）
     *
     * @param privateMessageQueryRequest
     * @param request
     * @return
     */
    @Override
    public Page<PrivateMessageVO> listPrivateMessageVOByPage(PrivateMessageQueryRequest privateMessageQueryRequest, HttpServletRequest request) {
        long current = privateMessageQueryRequest.getCurrent();
        long size = privateMessageQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 2000, ErrorCode.PARAMS_ERROR);
        // 查询数据库
        Page<PrivateMessage> privateMessagePage = this.page(new Page<>(current, size),
                this.getQueryWrapper(privateMessageQueryRequest));
        // 获取封装类
        return this.getPrivateMessageVOPage(privateMessagePage, request);
    }

    /**
     * 分页获取当前用户的私信列表
     *
     * @param privateMessageQueryRequest
     * @param request
     * @return
     */
    @Override
    public Page<PrivateMessageVO> getMyPrivateMessageVOPage(PrivateMessageQueryRequest privateMessageQueryRequest, HttpServletRequest request) {
        ThrowUtils.throwIf(privateMessageQueryRequest == null, ErrorCode.PARAMS_ERROR);
        
        // 获取当前登录用户并设置查询条件
        User loginUser = userService.getLoginUser(request);
        privateMessageQueryRequest.setSenderId(loginUser.getId());
        
        long current = privateMessageQueryRequest.getCurrent();
        long size = privateMessageQueryRequest.getPageSize();
        
        // 限制爬虫
        ThrowUtils.throwIf(size > 2000, ErrorCode.PARAMS_ERROR);
        
        // 查询数据库
        Page<PrivateMessage> privateMessagePage = this.page(
                new Page<>(current, size),
                this.getQueryWrapper(privateMessageQueryRequest)
        );
        
        // 获取封装类
        return this.getPrivateMessageVOPage(privateMessagePage, request);
    }

    /**
     * 根据 id 获取私信表（封装类）
     *
     * @param id
     * @param request
     * @return
     */
    @Override
    public PrivateMessageVO getPrivateMessageVOById(Long id, HttpServletRequest request) {
        ThrowUtils.throwIf(id == null || id <= 0, ErrorCode.PARAMS_ERROR);
        // 查询数据库
        PrivateMessage privateMessage = this.getById(id);
        ThrowUtils.throwIf(privateMessage == null, ErrorCode.NOT_FOUND_ERROR);
        // 获取封装类
        return this.getPrivateMessageVO(privateMessage, request);
    }

}
