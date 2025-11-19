package com.xyes.springboot.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xyes.springboot.model.dto.barrage.BarrageAddRequest;
import com.xyes.springboot.model.dto.barrage.BarrageQueryRequest;
import com.xyes.springboot.model.dto.barrage.BarrageUpdateRequest;
import com.xyes.springboot.model.entity.Barrage;
import com.xyes.springboot.model.vo.BarrageVO;

import javax.servlet.http.HttpServletRequest;

/**
 * 弹幕服务
 *
 */
public interface BarrageService extends IService<Barrage> {

    /**
     * 校验数据
     *
     * @param barrage
     * @param add 对创建的数据进行校验
     */
    void validBarrage(Barrage barrage, boolean add);

    /**
     * 获取查询条件
     *
     * @param barrageQueryRequest
     * @return
     */
    QueryWrapper<Barrage> getQueryWrapper(BarrageQueryRequest barrageQueryRequest);
    
    /**
     * 获取弹幕封装
     *
     * @param barrage
     * @param request
     * @return
     */
    BarrageVO getBarrageVO(Barrage barrage, HttpServletRequest request);

    /**
     * 分页获取弹幕封装
     *
     * @param barragePage
     * @param request
     * @return
     */
    Page<BarrageVO> getBarrageVOPage(Page<Barrage> barragePage, HttpServletRequest request);

    /**
     * 创建弹幕（业务逻辑）
     *
     * @param barrageAddRequest
     * @param request
     * @return 新弹幕ID
     */
    Long addBarrage(BarrageAddRequest barrageAddRequest, HttpServletRequest request);

    /**
     * 删除弹幕（包含权限校验）
     *
     * @param id
     * @param request
     * @return
     */
    Boolean deleteBarrageById(Long id, HttpServletRequest request);

    /**
     * 更新弹幕（仅管理员）
     *
     * @param barrageUpdateRequest
     * @return
     */
    Boolean updateBarrageById(BarrageUpdateRequest barrageUpdateRequest);

    /**
     * 分页获取当前用户的弹幕列表
     *
     * @param barrageQueryRequest
     * @param request
     * @return
     */
    Page<BarrageVO> getMyBarrageVOPage(BarrageQueryRequest barrageQueryRequest, HttpServletRequest request);
}
