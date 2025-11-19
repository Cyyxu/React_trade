package com.xyes.springboot.service;

import com.xyes.springboot.model.entity.Commodity;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.scripting.support.ResourceScriptSource;
import com.xyes.springboot.util.SpringContextUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Collections;

/**
 * 库存服务类 - 使用Redis+Lua脚本处理库存扣减，防止超卖
 */
@Service
@RequiredArgsConstructor
public class InventoryService {

    private final StringRedisTemplate stringRedisTemplate;

    private DefaultRedisScript<Long> deductInventoryScript;

    /**
     * 延迟获取 CommodityService，避免循环依赖
     */
    private CommodityService getCommodityService() {
        return SpringContextUtil.getBean(CommodityService.class);
    }

    @PostConstruct
    public void init() {
        deductInventoryScript = new DefaultRedisScript<>();
        deductInventoryScript.setScriptSource(
                new ResourceScriptSource(new ClassPathResource("lua/deduct_inventory.lua"))
        );
        deductInventoryScript.setResultType(Long.class);
    }

    public Long deductInventory(Long commodityId, Long quantity) {
        if (commodityId == null || quantity == null || quantity <= 0) {
            return -1L;
        }
        String key = "inventory:" + commodityId;
        return stringRedisTemplate.execute(deductInventoryScript,
                Collections.singletonList(key),
                quantity.toString());
    }

    public Long deductInventoryWithInit(Long commodityId, Long buyNumber) {
        if (commodityId == null || buyNumber == null || buyNumber <= 0) {
            return -1L;
        }
        String key = "inventory:" + commodityId;

        Long result = tryDeduct(key, buyNumber);
        if (result == -1) {
            Commodity commodity = getCommodityService().getById(commodityId);
            if (commodity == null || commodity.getCommodityInventory() == null) {
                return -1L;
            }
            Integer dbStock = commodity.getCommodityInventory();
            stringRedisTemplate.opsForValue().set(key, dbStock.toString());
            result = tryDeduct(key, buyNumber);
        }
        return result;
    }

    private Long tryDeduct(String key, Long buyNumber) {
        return stringRedisTemplate.execute(deductInventoryScript,
                Collections.singletonList(key),
                buyNumber.toString());
    }

    public void initInventory(Long commodityId, Long quantity) {
        String key = "inventory:" + commodityId;
        stringRedisTemplate.opsForValue().set(key, quantity.toString());
    }

    public Long getInventory(Long commodityId) {
        String key = "inventory:" + commodityId;
        String stock = stringRedisTemplate.opsForValue().get(key);
        return stock == null ? null : Long.valueOf(stock);
    }
}
