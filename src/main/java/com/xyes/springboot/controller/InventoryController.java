package com.xyes.springboot.controller;

import com.xyes.springboot.service.InventoryService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * 库存管理接口
 * 提供商品库存的初始化、查询、扣减等功能
 *
 * * @author xujun
 */
@RestController
@RequestMapping("/inventory")
@Slf4j
@RequiredArgsConstructor
public class InventoryController {

    private final InventoryService inventoryService;

    /**
     * 初始化商品库存
     *
     * @param commodityId 商品ID
     * @param quantity 初始库存数量
     * @return 是否成功
     */
    @PostMapping("/init")
    public Boolean initInventory(@RequestParam Long commodityId, @RequestParam Long quantity) {
        inventoryService.initInventory(commodityId, quantity);
        return true;
    }

    /**
     * 查询商品库存
     *
     * @param commodityId 商品ID
     * @return 库存数量
     */
    @GetMapping("/get")
    public Long getInventory(@RequestParam Long commodityId) {
        Long inventory = inventoryService.getInventory(commodityId);
        return inventory;
    }

    /**
     * 扣减商品库存
     *
     * @param commodityId 商品ID
     * @param quantity 扣减数量
     * @return 扣减后库存数量，-1表示库存不足
     */
    @PostMapping("/deduct")
    public Long deductInventory(@RequestParam Long commodityId, @RequestParam Long quantity) {
        Long result = inventoryService.deductInventory(commodityId, quantity);
        return result;
    }
}