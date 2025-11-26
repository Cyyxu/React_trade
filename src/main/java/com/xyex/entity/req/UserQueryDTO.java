package com.xyex.entity.req;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xyex.entity.model.UserInfo;
import com.xyex.infrastructure.model.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 用户查询DTO
 * 支持关键词查询（账号、昵称、简介）和按介绍ID查询
 *
 * @author xujun
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(name = "UserQueryDTO", description = "用户查询DTO")
public class UserQueryDTO extends PageParam {

    /**
     * 介绍ID
     */
    @Schema(description = "ID")
    private Long Id;

    @Override
    public Wrapper<UserInfo> createQuery() {
        LambdaQueryWrapper<UserInfo> wrapper = new LambdaQueryWrapper<>();
        
        // 关键词查询（账号、昵称、简介）
        String keyword = this.getKeyword();
        if (keyword != null && !keyword.isEmpty()) {
            wrapper.and(w -> w
                    .like(UserInfo::getUserAccount, keyword)
                    .or().like(UserInfo::getUserName, keyword)
                    .or().like(UserInfo::getUserProfile, keyword)
            );
        }
        
        // 按介绍ID查询
        if (Id != null) {
            wrapper.eq(UserInfo::getId, Id);
        }
        
        // 排除已删除的用户
        wrapper.eq(UserInfo::getIsDelete, 0);
        
        // 排序
        wrapper.orderByDesc(UserInfo::getCreateTime);
        
        return wrapper;
    }
}
