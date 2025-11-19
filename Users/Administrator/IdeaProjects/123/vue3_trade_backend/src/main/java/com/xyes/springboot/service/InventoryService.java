public void initInventory(Long commodityId, Long quantity) {
    if (commodityId == null || quantity == null) {
        System.err.println("初始化库存参数验证失败: commodityId=" + commodityId + ", quantity=" + quantity);
        return;
    }
    String key = "inventory:" + commodityId;
    System.out.println("初始化库存到Redis: key=" + key + ", quantity=" + quantity);
    // 将库存值以字符串形式存储，而不是Java序列化对象
    redisTemplate.opsForValue().set(key, quantity.toString());
}