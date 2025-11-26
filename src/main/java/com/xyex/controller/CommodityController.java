package com.xyex.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xyex.entity.model.Commodity;
import com.xyex.entity.req.CommodityQueryDTO;
import com.xyex.service.CommodityService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
    @GetMapping("/{commodityId}")
    @Operation(summary = "获取商品详情")
    public Commodity getCommodityDetail(@PathVariable Long commodityId) {
        return commodityService.getCommodityDetail(commodityId);
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
    @DeleteMapping("/{commodityId}")
    @Operation(summary = "删除商品")
    public void deleteCommodity(@PathVariable Long commodityId) {
        commodityService.deleteCommodity(commodityId);
    }
}
