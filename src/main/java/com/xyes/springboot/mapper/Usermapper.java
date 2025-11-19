package com.xyes.springboot.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xyes.springboot.model.entity.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface Usermapper extends BaseMapper<User> {
    User selectByIdWithLock(Long id);
}
