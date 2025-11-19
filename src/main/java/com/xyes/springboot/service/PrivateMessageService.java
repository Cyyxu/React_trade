package com.xyes.springboot.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xyes.springboot.model.dto.privateMessage.*;
import com.xyes.springboot.model.entity.PrivateMessage;
import com.xyes.springboot.model.vo.PrivateMessageVO;

import javax.servlet.http.HttpServletRequest;

/**
 * 私信表服务
 */
public interface PrivateMessageService extends IService<PrivateMessage> {

    /**
     * 校验数据
     * @param privateMessage
     * @param add 对创建的数据进行校验
     */
    void validPrivateMessage(PrivateMessage privateMessage, boolean add);

    /**
     * 获取查询条件
     * @param privateMessageQueryRequest
     * @return
     */
    QueryWrapper<PrivateMessage> getQueryWrapper(PrivateMessageQueryRequest privateMessageQueryRequest);
    
    /**
     * 获取私信表封装
     * @param privateMessage
     * @param request
     * @return
     */
    PrivateMessageVO getPrivateMessageVO(PrivateMessage privateMessage, HttpServletRequest request);

    /**
     * 分页获取私信表封装
     * @param privateMessagePage
     * @param request
     * @return
     */
    Page<PrivateMessageVO> getPrivateMessageVOPage(Page<PrivateMessage> privateMessagePage, HttpServletRequest request);

    /**
     * 创建私信（业务逻辑）
     *
     * @param privateMessageAddRequest
     * @param request
     * @return 新私信ID
     */
    Long addPrivateMessage(PrivateMessageAddRequest privateMessageAddRequest, HttpServletRequest request);

    /**
     * 删除私信（包含权限校验）
     *
     * @param id
     * @param request
     * @return
     */
    Boolean deletePrivateMessageById(Long id, HttpServletRequest request);

    /**
     * 更新私信（仅管理员）
     *
     * @param privateMessageUpdateRequest
     * @return
     */
    Boolean updatePrivateMessageById(PrivateMessageUpdateRequest privateMessageUpdateRequest);

    /**
     * 编辑私信（用户自己可用）
     *
     * @param privateMessageEditRequest
     * @param request
     * @return
     */
    Boolean editPrivateMessageById(PrivateMessageEditRequest privateMessageEditRequest, HttpServletRequest request);

    /**
     * 分页获取私信列表（实体类）
     *
     * @param privateMessageQueryRequest
     * @return
     */
    Page<PrivateMessage> listPrivateMessageByPage(PrivateMessageQueryRequest privateMessageQueryRequest);

    /**
     * 分页获取私信列表（封装类）
     *
     * @param privateMessageQueryRequest
     * @param request
     * @return
     */
    Page<PrivateMessageVO> listPrivateMessageVOByPage(PrivateMessageQueryRequest privateMessageQueryRequest, HttpServletRequest request);

    /**
     * 分页获取当前用户的私信列表
     *
     * @param privateMessageQueryRequest
     * @param request
     * @return
     */
    Page<PrivateMessageVO> getMyPrivateMessageVOPage(PrivateMessageQueryRequest privateMessageQueryRequest, HttpServletRequest request);

    /**
     * 根据 id 获取私信表（封装类）
     *
     * @param id
     * @param request
     * @return
     */
    PrivateMessageVO getPrivateMessageVOById(Long id, HttpServletRequest request);
}
