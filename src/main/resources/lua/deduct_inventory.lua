-- Lua脚本用于扣减商品库存，防止超卖
-- KEYS[1] 商品库存的key
-- ARGV[1] 需要扣减的数量
-- 返回值: 扣减后的库存数量，如果库存不足返回-1

local stock = redis.call('GET', KEYS[1])
if not stock then
    return -1
end

local current_stock = tonumber(stock)
local deduct_amount = tonumber(ARGV[1])

if current_stock >= deduct_amount then
    local new_stock = current_stock - deduct_amount
    redis.call('SET', KEYS[1], new_stock)
    return new_stock
else
    return -1
end