# AI服务响应时间优化方案

## 当前性能瓶颈分析

从日志分析，AI服务响应时间约105秒，主要瓶颈如下：

### 1. **数据库查询效率低** ⚠️ 可优化
- **问题**：`commodityService.list()` 查询所有商品，然后在内存中过滤和排序
- **影响**：如果商品数量多（如1000+），会查询大量无用数据
- **当前**：查询所有商品 → 内存过滤 → 排序 → 限制50个

### 2. **提示词过长** ⚠️ 可优化
- **问题**：50个商品的详细信息导致提示词过长（831字符）
- **影响**：AI处理长文本需要更多时间
- **当前**：每个商品包含：名称、新旧程度、库存、价格

### 3. **AI服务本身慢** ⚠️ 部分可优化
- **问题**：讯飞星火API处理长文本需要时间
- **影响**：这是主要瓶颈（约100秒）
- **优化**：减少提示词长度、使用流式响应

### 4. **同步阻塞** ⚠️ 可优化
- **问题**：同步等待AI响应，阻塞请求线程
- **影响**：用户体验差，服务器资源占用
- **优化**：改为异步处理

### 5. **重复查询** ⚠️ 可优化
- **问题**：每次请求都查询数据库
- **影响**：商品数据变化不频繁，可以缓存
- **优化**：添加缓存机制

---

## 优化方案（按优先级排序）

### 🚀 方案1：优化数据库查询（预计提升：5-10秒）

**当前代码问题**：
```java
List<Commodity> commodities = commodityService.list().stream()
    .filter(commodity -> commodity.getIsListed() == 1)
    .sorted(Comparator.comparing(Commodity::getId).reversed())
    .limit(50)
    .collect(Collectors.toList());
```

**优化后**：
```java
// 使用数据库查询，直接在SQL层面过滤和排序
QueryWrapper<Commodity> queryWrapper = new QueryWrapper<>();
queryWrapper.eq("isListed", 1)
    .orderByDesc("id")
    .last("LIMIT 30"); // 减少到30个商品
List<Commodity> commodities = commodityService.list(queryWrapper);
```

**收益**：
- 减少数据传输量
- 利用数据库索引
- 减少内存占用

---

### 🚀 方案2：减少商品数量和简化提示词（预计提升：20-40秒）

**优化点**：
1. 商品数量：50 → 20-30个
2. 简化字段：只保留关键信息
3. 优化提示词结构

**优化后代码**：
```java
// 只查询20个最新上架商品
QueryWrapper<Commodity> queryWrapper = new QueryWrapper<>();
queryWrapper.eq("isListed", 1)
    .orderByDesc("id")
    .last("LIMIT 20");
List<Commodity> commodities = commodityService.list(queryWrapper);

// 简化商品信息格式
String commodityList = commodities.stream()
    .map(c -> String.format("%s|%s|%d|%.0f", 
        c.getCommodityName(), 
        c.getDegree(), 
        c.getCommodityInventory(), 
        c.getPrice()))
    .collect(Collectors.joining("\n"));
```

**收益**：
- 提示词长度减少约40%
- AI处理时间减少约30-40%

---

### 🚀 方案3：添加缓存机制（预计提升：2-5秒）

**实现**：
```java
@Cacheable(value = "commodities", key = "'listed:top20'", unless = "#result == null || #result.isEmpty()")
public List<Commodity> getTopListedCommodities() {
    QueryWrapper<Commodity> queryWrapper = new QueryWrapper<>();
    queryWrapper.eq("isListed", 1)
        .orderByDesc("id")
        .last("LIMIT 20");
    return this.list(queryWrapper);
}
```

**收益**：
- 避免重复查询数据库
- 响应时间减少2-5秒

---

### 🚀 方案4：异步处理（提升用户体验）

**实现**：
```java
@Async("businessAsyncExecutor")
public CompletableFuture<String> callAIServiceAsync(String prompt) {
    // 异步调用AI服务
    return CompletableFuture.supplyAsync(() -> {
        try {
            return sparkAIManager.sendMessageAndGetResponse(prompt, 120);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }, threadPoolExecutor);
}
```

**前端改进**：
- 立即返回消息ID
- 使用WebSocket或轮询获取结果
- 显示"AI正在思考中..."

**收益**：
- 用户体验大幅提升
- 服务器资源利用更合理

---

### 🚀 方案5：流式响应（可选，需要前端配合）

**实现**：
- 使用SSE（Server-Sent Events）或WebSocket
- AI响应边生成边返回
- 前端实时显示生成内容

**收益**：
- 用户感知延迟降低
- 体验更流畅

---

## 优化效果预估

| 优化方案 | 预计提升 | 实施难度 | 优先级 |
|---------|---------|---------|--------|
| 优化数据库查询 | 5-10秒 | ⭐ 简单 | 🔥 高 |
| 减少商品数量 | 20-40秒 | ⭐ 简单 | 🔥 高 |
| 添加缓存 | 2-5秒 | ⭐⭐ 中等 | 🔥 高 |
| 异步处理 | 用户体验 | ⭐⭐⭐ 较难 | ⭐ 中 |
| 流式响应 | 用户体验 | ⭐⭐⭐⭐ 困难 | ⭐ 低 |

**综合优化后预计响应时间**：从105秒降低到 **60-80秒**

---

## 实施建议

### 第一阶段（立即实施）：
1. ✅ 优化数据库查询
2. ✅ 减少商品数量到20-30个
3. ✅ 简化提示词格式

### 第二阶段（短期）：
4. ✅ 添加缓存机制
5. ✅ 优化提示词内容

### 第三阶段（长期）：
6. ⚠️ 异步处理（需要前端配合）
7. ⚠️ 流式响应（需要前端配合）

