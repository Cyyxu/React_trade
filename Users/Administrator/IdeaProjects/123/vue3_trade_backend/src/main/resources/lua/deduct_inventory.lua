-- Lua脚本用于扣减商品库存，防止超卖
-- KEYS[1] 商品库存的key
-- ARGV[1] 需要扣减的数量
-- 返回值: 扣减后的库存数量，如果库存不足返回-1

local stock = redis.call('GET', KEYS[1])

-- 详细检查stock值
if stock == false then
    return -1
end

if stock == nil then
    return -1
end

-- 直接将stock作为数字处理，因为它是字符串形式的数字
local current_stock = tonumber(stock)

local deduct_amount = tonumber(ARGV[1])

-- 检查数值转换是否成功
if current_stock == nil then
    return -1
end

if deduct_amount == nil then
    return -1
end

-- 检查数值是否有效
if current_stock < 0 then
    return -1
end

if deduct_amount <= 0 then
    return -1
end

-- 执行库存比较和扣减
if current_stock >= deduct_amount then
    local new_stock = current_stock - deduct_amount
    -- 确保结果不为负数
    if new_stock < 0 then
        return -1
    end
    redis.call('SET', KEYS[1], tostring(new_stock))
    return new_stock
else
    return -1
end