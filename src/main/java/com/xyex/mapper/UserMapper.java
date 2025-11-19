package com.xyex.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xyex.entity.model.UserInfo;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户数据访问层
 *
 * @author xujun
 */
@Mapper
public interface UserMapper extends BaseMapper<UserInfo> {

}
