package com.xyex.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xyex.entity.model.Commodity;
import com.xyex.entity.model.CommodityOrder;
import com.xyex.entity.model.CommodityScore;
import com.xyex.entity.model.CommodityType;
import com.xyex.entity.req.CommodityOrderDTO;
import com.xyex.entity.req.CommodityQueryDTO;
import com.xyex.entity.req.CommodityScoreDTO;
import com.xyex.entity.req.CommodityTypeDTO;
import com.xyex.service.CommodityService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

import org.springframework.web.bind.annotation.*;

/**
 * 商品管理控制器
 */
@RestController
@RequestMapping("/commodity")
@Slf4j
@Tag(name = "商品管理")
@RequiredArgsConstructor
public class CommodityController {
    
    private final CommodityService commodityService;

    /**
     * 分页查询商品列表
     */
    @GetMapping("/list")
    @Operation(summary = "分页查询商品列表")
    public Page<Commodity> listCommodity(CommodityQueryDTO queryDTO) {
        return commodityService.listCommodity(queryDTO);
    }

    /**
     * 获取商品详情
     */
    @GetMapping("/{id}")
    @Operation(summary = "获取商品详情")
    public Commodity getCommodityDetail(@PathVariable Long id) {
        return commodityService.getCommodityDetail(id);
    }

    /**
     * 创建商品
     */
    @PostMapping("/create")
    @Operation(summary = "创建商品")
    public void createCommodity(@RequestBody Commodity commodity) {
        commodityService.createCommodity(commodity);
    }

    /**
     * 更新商品
     */
    @PutMapping("/update")
    @Operation(summary = "更新商品")
    public void updateCommodity(@RequestBody Commodity commodity) {
        commodityService.updateCommodity(commodity);
    }

    /**
     * 删除商品
     */
    @DeleteMapping("/delete")
    @Operation(summary = "删除商品")
    public void deleteCommodity(@RequestBody List<Long> commodityIds) {
        commodityService.deleteCommodity(commodityIds);
    }

    /**
     * 购买商品
     */
    @PostMapping("/buy")
    @Operation(summary = "购买商品")
    public void buyCommodity(@RequestBody Commodity commodity) {
        commodityService.buyCommodity(commodity);
    }
    /**
     * 增加商品浏览量
     */
    @PostMapping("/order/add")
    @Operation(summary = "创建商品订单")
    public void addOrder(@RequestBody CommodityOrderDTO commodityOrderDTO) {
        commodityService.addOrder(commodityOrderDTO);
    }
    /**
     * 获取商品订单列表
     */
    @GetMapping("/order/list")
    @Operation(summary = "获取商品订单列表")
    public Page<CommodityOrder> listOrder(CommodityOrderDTO queryDTO) {
        return commodityService.listOrder(queryDTO);
    }
    /**
     * 获取商品订单详情
     */
    @GetMapping("/order/{id}")
    @Operation(summary = "获取商品订单详情")
    public CommodityOrder getOrderDetail(@PathVariable Long id) {
        return commodityService.getOrderDetail(id);
    }
    /**
     * 更新商品订单
     */
    @PutMapping("/order/update")
    @Operation(summary = "更新商品订单")
    public void updateOrder(@RequestBody CommodityOrder commodityOrder) {
        commodityService.updateOrder(commodityOrder);
    }
    /**
     * 删除商品订单
     */
    @DeleteMapping("/order/delete")
    @Operation(summary = "删除商品订单")
    public void deleteOrder(@RequestBody List<Long> ids) {
        commodityService.deleteOrder(ids);
    }
    /**
     * 支付商品订单
     */
    @PostMapping("/order/pay")
    @Operation(summary = "支付商品订单")
    public void payOrder(@RequestBody CommodityOrder commodityOrder) {
        commodityService.payOrder(commodityOrder);
    }
    /**
     * 商品评分
     */
    @PostMapping("/score/add")
    @Operation(summary = "商品评分")
    public void score(@RequestBody CommodityScoreDTO commodityScoreDTO) {
        commodityService.score(commodityScoreDTO);
    }
    /**
     * 获取商品评分列表
     */
    @GetMapping("/score/list")
    @Operation(summary = "获取商品评分列表")
    public Page<CommodityScore> listScore(CommodityScoreDTO queryDTO) {
        return commodityService.listScore(queryDTO);
    }
    /**
     * 获取商品评分详情
     */
    @GetMapping("/score/{id}")
    @Operation(summary = "获取商品评分详情")
    public CommodityScore getScoreDetail(@PathVariable Long id) {
        return commodityService.getScoreDetail(id);
    }
    /**
     * 更新商品评分
     */
    @PutMapping("/score/update")
    @Operation(summary = "更新商品评分")
    public void updateScore(@RequestBody CommodityScore commodityScore) {
        commodityService.updateScore(commodityScore);
    }
    /**
     * 删除商品评分
     */
    @DeleteMapping("/score/delete")
    @Operation(summary = "删除商品评分")
    public void deleteScore(@RequestBody List<Long> ids) {
        commodityService.deleteScore(ids);
    }
    @PostMapping("/type/add")
    @Operation(summary = "添加商品类型")
    public void addType(@RequestBody CommodityTypeDTO commodityTypeDTO) {
        commodityService.addType(commodityTypeDTO);
    }
    @GetMapping("/type/list")
    @Operation(summary = "获取商品类型列表")
    public Page<CommodityType> listType(CommodityTypeDTO queryDTO) {
        return commodityService.listType(queryDTO);
    }
    @GetMapping("/type/{id}")
    @Operation(summary = "获取商品类型详情")
    public CommodityType getTypeDetail(@PathVariable Long id) {
        return commodityService.getTypeDetail(id);
    }
    @PutMapping("/type/update")
    @Operation(summary = "更新商品类型")
    public void updateType(@RequestBody CommodityType commodityType) {
        commodityService.updateType(commodityType);
    }
    @DeleteMapping("/type/delete")
    @Operation(summary = "删除商品类型")
    public void deleteType(@RequestBody List<Long> ids) {
        commodityService.deleteType(ids);
    }




}
