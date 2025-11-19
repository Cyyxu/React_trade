package com.xyes.springboot.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xyes.springboot.model.entity.CommodityScore;
import org.apache.ibatis.annotations.Select;

/**
* @description 针对表【commodity_score】的数据库操作Mapper
*/
public interface CommodityScoreMapper extends BaseMapper<CommodityScore> {
    /**
     * 查询商品的平均评分
     * @param commodityId 商品 ID
     * @return 平均评分
     */
    @Select("SELECT COALESCE(AVG(score), 0.0) AS averageScore FROM commodity_score WHERE commodityId = #{commodityId} AND isDelete = 0")
    Double getAverageScoreByCommodityId(Long commodityId);

}




