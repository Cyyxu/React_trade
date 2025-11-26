package com.xyex.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xyex.entity.model.UserCommodityFavorite;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户商品收藏 Mapper
 */
@Mapper
public interface UserCommodityFavoriteMapper extends BaseMapper<UserCommodityFavorite> {
}
