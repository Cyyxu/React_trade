package com.xyes.springboot.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xyes.springboot.model.entity.Commodity;

/**
* @description 针对表【commodity】的数据库操作Mapper
*/
public interface Commoditymapper extends BaseMapper<Commodity> {
    Commodity selectByIdWithLock(Long id);
}




