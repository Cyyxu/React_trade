package com.xyex.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xyex.entity.model.Commodity;
import org.apache.ibatis.annotations.Mapper;

/**
 * 商品 Mapper
 */
@Mapper
public interface CommodityMapper extends BaseMapper<Commodity> {
}
