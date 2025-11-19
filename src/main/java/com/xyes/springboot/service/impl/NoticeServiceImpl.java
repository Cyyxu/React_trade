package com.xyes.springboot.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xyes.springboot.exception.BusinessException;
import com.xyes.springboot.exception.ErrorCode;
import com.xyes.springboot.constant.CommonConstant;
import com.xyes.springboot.exception.ThrowUtils;
import com.xyes.springboot.mapper.NoticeMapper;
import com.xyes.springboot.model.dto.notice.*;
import com.xyes.springboot.model.entity.Notice;
import com.xyes.springboot.model.entity.User;
import com.xyes.springboot.model.vo.NoticeVO;
import com.xyes.springboot.model.vo.UserVO;
import com.xyes.springboot.service.NoticeService;
import com.xyes.springboot.service.UserService;
import com.xyes.springboot.util.SqlUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 公告服务实现
 *
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class NoticeServiceImpl extends ServiceImpl<NoticeMapper, Notice> implements NoticeService {
    private final UserService userService;

    /**
     * 校验数据
     *
     * @param notice
     * @param add    对创建的数据进行校验
     */
    @Override
    public void validNotice(Notice notice, boolean add) {
        ThrowUtils.throwIf(notice == null, ErrorCode.PARAMS_ERROR);
        // todo 从对象中取值
        String noticeTitle = notice.getNoticeTitle();
        String noticeContent = notice.getNoticeContent();
        Long noticeAdminId = notice.getNoticeAdminId();
        // 创建数据时，参数不能为空
        if (add) {
            ThrowUtils.throwIf(StringUtils.isBlank(noticeTitle), ErrorCode.PARAMS_ERROR);
            ThrowUtils.throwIf(StringUtils.isBlank(noticeContent), ErrorCode.PARAMS_ERROR);
            ThrowUtils.throwIf(noticeAdminId < 0, ErrorCode.PARAMS_ERROR);
        }
        // 修改数据时，有参数则校验
        ThrowUtils.throwIf(StringUtils.isBlank(noticeTitle), ErrorCode.PARAMS_ERROR);
        ThrowUtils.throwIf(StringUtils.isBlank(noticeContent), ErrorCode.PARAMS_ERROR);
        ThrowUtils.throwIf(noticeAdminId < 0, ErrorCode.PARAMS_ERROR);
    }

    /**
     * 获取查询条件
     *
     * @param noticeQueryRequest
     * @return
     */
    @Override
    public QueryWrapper<Notice> getQueryWrapper(NoticeQueryRequest noticeQueryRequest) {
        QueryWrapper<Notice> queryWrapper = new QueryWrapper<>();
        if (noticeQueryRequest == null) {
            return queryWrapper;
        }
        String noticeTitle = noticeQueryRequest.getNoticeTitle();
        String noticeContent = noticeQueryRequest.getNoticeContent();
        String sortField = noticeQueryRequest.getSortField();
        String sortOrder = noticeQueryRequest.getSortOrder();
        // 模糊查询
        queryWrapper.like(StringUtils.isNotBlank(noticeTitle), "noticeTitle", noticeTitle);
        queryWrapper.like(StringUtils.isNotBlank(noticeContent), "noticeContent", noticeContent);
        // 排序规则
        queryWrapper.orderBy(SqlUtils.validSortField(sortField),
                sortOrder.equals(CommonConstant.SORT_ORDER_ASC),
                sortField);
        return queryWrapper;
    }

    /**
     * 获取公告封装
     *
     * @param notice
     * @param request
     * @return
     */
    @Override
    public NoticeVO getNoticeVO(Notice notice, HttpServletRequest request) {
        // 对象转封装类
        NoticeVO noticeVO = NoticeVO.objToVo(notice);
        // region 可选
        // 1. 关联查询用户信息
        Long userId = notice.getNoticeAdminId();
        User user = null;
        if (userId != null && userId > 0) {
            user = userService.getById(userId);
        }
        UserVO userVO = userService.getUserVO(user);
        noticeVO.setUser(userVO);
        return noticeVO;
    }

    /**
     * 分页获取公告封装
     *
     * @param noticePage
     * @param request
     * @return
     */
    @Override
    public Page<NoticeVO> getNoticeVOPage(Page<Notice> noticePage, HttpServletRequest request) {
        List<Notice> noticeList = noticePage.getRecords();
        Page<NoticeVO> noticeVOPage = new Page<>(noticePage.getCurrent(), noticePage.getSize(), noticePage.getTotal());
        if (CollUtil.isEmpty(noticeList)) {
            return noticeVOPage;
        }
        // 对象列表 => 封装对象列表
        List<NoticeVO> noticeVOList = noticeList.stream().map(notice -> {
            return NoticeVO.objToVo(notice);
        }).collect(Collectors.toList());
        // region 可选
        // 1. 关联查询用户信息
        Set<Long> userIdSet = noticeList.stream().map(Notice::getNoticeAdminId).collect(Collectors.toSet());
        Map<Long, List<User>> userIdUserListMap = userService.listByIds(userIdSet).stream()
                .collect(Collectors.groupingBy(User::getId));

        // 填充信息
        noticeVOList.forEach(noticeVO -> {
            Long userId = noticeVO.getNoticeAdminId();
            User user = null;
            if (userIdUserListMap.containsKey(userId)) {
                user = userIdUserListMap.get(userId).get(0);
            }
            noticeVO.setUser(userService.getUserVO(user));
        });
        // endregion
        noticeVOPage.setRecords(noticeVOList);
        return noticeVOPage;
    }

    /**
     * 创建公告（业务逻辑）
     *
     * @param noticeAddRequest
     * @param request
     * @return 新公告ID
     */
    @Override
    public Long addNotice(NoticeAddRequest noticeAddRequest, HttpServletRequest request) {
        ThrowUtils.throwIf(noticeAddRequest == null, ErrorCode.PARAMS_ERROR);
        
        // DTO转实体
        Notice notice = new Notice();
        BeanUtils.copyProperties(noticeAddRequest, notice);
        
        // 数据校验
        validNotice(notice, true);
        
        // 写入数据库
        boolean result = this.save(notice);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        
        return notice.getId();
    }

    /**
     * 删除公告（包含权限校验）
     *
     * @param id
     * @param request
     * @return
     */
    @Override
    public Boolean deleteNoticeById(Long id, HttpServletRequest request) {
        ThrowUtils.throwIf(id == null || id <= 0, ErrorCode.PARAMS_ERROR);
        
        // 判断是否存在
        Notice oldNotice = this.getById(id);
        ThrowUtils.throwIf(oldNotice == null, ErrorCode.NOT_FOUND_ERROR);
        
        // 仅管理员可删除
        if (!userService.isAdmin(request)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        
        // 删除数据库记录
        boolean result = this.removeById(id);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        
        return true;
    }

    /**
     * 更新公告（仅管理员）
     *
     * @param noticeUpdateRequest
     * @return
     */
    @Override
    public Boolean updateNoticeById(NoticeUpdateRequest noticeUpdateRequest) {
        ThrowUtils.throwIf(noticeUpdateRequest == null || noticeUpdateRequest.getId() <= 0, ErrorCode.PARAMS_ERROR);
        
        // DTO转实体
        Notice notice = new Notice();
        BeanUtils.copyProperties(noticeUpdateRequest, notice);
        
        // 数据校验
        validNotice(notice, false);
        
        // 判断是否存在
        long id = noticeUpdateRequest.getId();
        Notice oldNotice = this.getById(id);
        ThrowUtils.throwIf(oldNotice == null, ErrorCode.NOT_FOUND_ERROR);
        
        // 更新数据库
        boolean result = this.updateById(notice);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        
        return true;
    }

    /**
     * 分页获取当前用户的公告列表
     *
     * @param noticeQueryRequest
     * @param request
     * @return
     */
    @Override
    public Page<NoticeVO> getMyNoticeVOPage(NoticeQueryRequest noticeQueryRequest, HttpServletRequest request) {
        ThrowUtils.throwIf(noticeQueryRequest == null, ErrorCode.PARAMS_ERROR);
        
        // 获取当前登录用户并设置查询条件
        User loginUser = userService.getLoginUser(request);
        noticeQueryRequest.setNoticeAdminId(loginUser.getId());
        
        long current = noticeQueryRequest.getCurrent();
        long size = noticeQueryRequest.getPageSize();
        
        // 限制爬虫
        ThrowUtils.throwIf(size > 2000, ErrorCode.PARAMS_ERROR);
        
        // 查询数据库
        Page<Notice> noticePage = this.page(
                new Page<>(current, size),
                this.getQueryWrapper(noticeQueryRequest)
        );
        
        // 获取封装类
        return this.getNoticeVOPage(noticePage, request);
    }

    /**
     * 分页获取公告列表（实体类）
     *
     * @param noticeQueryRequest
     * @return
     */
    @Override
    public Page<Notice> listNoticeByPage(NoticeQueryRequest noticeQueryRequest) {
        long current = noticeQueryRequest.getCurrent();
        long size = noticeQueryRequest.getPageSize();
        // 查询数据库
        return this.page(new Page<>(current, size),
                this.getQueryWrapper(noticeQueryRequest));
    }

    /**
     * 分页获取公告列表（封装类）
     *
     * @param noticeQueryRequest
     * @param request
     * @return
     */
    @Override
    public Page<NoticeVO> listNoticeVOByPage(NoticeQueryRequest noticeQueryRequest, HttpServletRequest request) {
        long current = noticeQueryRequest.getCurrent();
        long size = noticeQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 2000, ErrorCode.PARAMS_ERROR);
        // 查询数据库
        Page<Notice> noticePage = this.page(new Page<>(current, size),
                this.getQueryWrapper(noticeQueryRequest));
        // 获取封装类
        return this.getNoticeVOPage(noticePage, request);
    }

}
