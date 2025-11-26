package com.xyex.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xyex.entity.model.UserCommodityFavorite;
import com.xyex.entity.model.UserInfo;
import com.xyex.entity.req.CommodityFavoriteDTO;
import com.xyex.entity.req.UserLoginDTO;
import com.xyex.entity.req.UserQueryDTO;
import com.xyex.entity.res.UserProfileVO;
import com.xyex.infrastructure.exception.BusinessException;
import com.xyex.infrastructure.exception.ErrorCode;
import com.xyex.infrastructure.model.BasicServiceImpl;
import com.xyex.infrastructure.utils.ValidationUtils;
import com.xyex.infrastructure.utils.JwtUtils;
import com.xyex.mapper.UserMapper;
import com.xyex.mapper.UserCommodityFavoriteMapper;
import com.xyex.service.UserService;

import lombok.AllArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;
import jakarta.servlet.http.HttpServletRequest;

/**
 * 用户服务实现类
 *
 * @author xujun
 */
@Service
@AllArgsConstructor
public class UserServiceImpl extends BasicServiceImpl<UserMapper, UserInfo> implements UserService {
    private final JwtUtils jwtUtils;
    private final UserCommodityFavoriteMapper userCommodityFavoriteMapper;
    

    @Override
    public Long register(UserLoginDTO userRegisterDTO) {
        // 1. 校验
        String userAccount = userRegisterDTO.getUserAccount();
        String userPassword = userRegisterDTO.getUserPassword();
        String confirmPassword = userRegisterDTO.getConfirmPassword();

        ValidationUtils.validateRegister(userAccount, userPassword, confirmPassword);
        synchronized (userAccount.intern()) {
            // 账户不能重复
            QueryWrapper<UserInfo> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("user_account", userAccount);
            long count = this.baseMapper.selectCount(queryWrapper);
            if (count > 0) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号重复");
            }
            // 2. 加密
            String encryptPassword = DigestUtils.md5DigestAsHex((userPassword).getBytes());
            // 3. 插入数据
            UserInfo user = new UserInfo();
            user.setUserAccount(userAccount);
            user.setUserPassword(encryptPassword);
            boolean saveResult = this.save(user);
            if (!saveResult) {
                throw new BusinessException(ErrorCode.SYSTEM_ERROR);
            }
            return user.getId();
        }
    }

    @Override
    public UserLoginDTO.LoginData login(UserLoginDTO userLoginDTO) {
        // 1. 校验
        String userAccount = userLoginDTO.getUserAccount();
        String userPassword = userLoginDTO.getUserPassword();

        ValidationUtils.validateLogin(userAccount, userPassword);

        // 2. 查询用户
        QueryWrapper<UserInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_account", userAccount);
        UserInfo user = this.baseMapper.selectOne(queryWrapper);
        if (user == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户不存在");
        }

        // 3. 验证密码
        String encryptPassword = DigestUtils.md5DigestAsHex(userPassword.getBytes());
        if (!encryptPassword.equals(user.getUserPassword())) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "密码错误");
        }

        // 4. 生成token（包含用户角色）
        String token = jwtUtils.generateToken(user.getId(), user.getUserAccount(), user.getUserRole());

        // 5. 返回登录响应
        UserLoginDTO.LoginData loginData = new UserLoginDTO.LoginData();
        loginData.setUserInfo(user);
        loginData.setToken(token);
        return loginData;
    }

    @Override
    public Boolean logout(HttpServletRequest request) {
        // 从请求头中获取token
        String token = request.getHeader("Authorization");
        if (token == null || token.isEmpty()) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "未登录");
        }
        
        // 验证token是否有效
        if (!jwtUtils.validateToken(token)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "token无效或已过期");
        }
        
        // 移除session中的用户登录状态
        request.getSession().removeAttribute("USER_LOGIN_STATE");
        
        return true;
    }

    @Override
    public Boolean updateMyUser(UserInfo userInfo, HttpServletRequest request) {
        // 验证参数不为空
        if (userInfo == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户信息不能为空");
        }
        
        // 从请求头中获取token
        String token = request.getHeader("Authorization");
        if (token == null || token.isEmpty()) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "未登录");
        }
        
        // 验证token是否有效
        if (!jwtUtils.validateToken(token)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "token无效或已过期");
        }
        
        // 从token中获取登录用户ID
        Long loginUserId = jwtUtils.getUserIdFromToken(token);
        
        // 设置用户ID为登录用户ID（防止用户修改他人信息）
        userInfo.setId(loginUserId);
        
        // 如果提供了新密码，则需要验证旧密码
        if (userInfo.getUserPassword() != null && !userInfo.getUserPassword().isEmpty()) {
            String oldPassword = userInfo.getOldPassword();
            if (oldPassword == null || oldPassword.isEmpty()) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "修改密码需要提供旧密码");
            }
            
            // 查询当前用户信息
            UserInfo currentUser = this.getById(loginUserId);
            if (currentUser == null) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户不存在");
            }
            
            // 验证旧密码
            String encryptOldPassword = DigestUtils.md5DigestAsHex(oldPassword.getBytes());
            if (!encryptOldPassword.equals(currentUser.getUserPassword())) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "旧密码错误");
            }
            
            // 加密新密码
            String encryptNewPassword = DigestUtils.md5DigestAsHex(userInfo.getUserPassword().getBytes());
            userInfo.setUserPassword(encryptNewPassword);
        }
        
        // 更新用户信息
        boolean result = this.updateById(userInfo);
        if (!result) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "用户信息更新失败");
        }
        
        return true;
    }

    @Override
    public Page<UserInfo> listUsers(UserQueryDTO userQueryDTO) {
        if (userQueryDTO == null) {
            userQueryDTO = new UserQueryDTO();
        }
        
        // 创建分页对象
        Page<UserInfo> page = userQueryDTO.createPage();
        
        // 创建查询条件
        QueryWrapper<UserInfo> queryWrapper = (QueryWrapper<UserInfo>) userQueryDTO.createQuery();
        
        // 执行分页查询
        return this.page(page, queryWrapper);
    }

    @Override
    public UserProfileVO getUserProfile(Long userId) {
        // 验证用户ID
        if (userId == null || userId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户ID不能为空");
        }
        
        // 查询用户信息
        UserInfo user = this.getById(userId);
        if (user == null || user.getIsDelete() == 1) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户不存在");
        }

        return UserProfileVO.convert(user);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addCommodityFavorite(CommodityFavoriteDTO commodityFavoriteDTO) {
        if (commodityFavoriteDTO == null
                || commodityFavoriteDTO.getUserId() == null
                || commodityFavoriteDTO.getCommodityId() == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "收藏参数不完整");
        }

        Long userId = commodityFavoriteDTO.getUserId();
        Long commodityId = commodityFavoriteDTO.getCommodityId();

        UserInfo user = this.getById(userId);
        if (user == null || user.getIsDelete() == 1) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户不存在");
        }

        QueryWrapper<UserCommodityFavorite> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", userId)
                .eq("commodity_id", commodityId);

        UserCommodityFavorite existing = userCommodityFavoriteMapper.selectOne(wrapper);
        if (existing != null) {
            if ("1".equals(existing.getStatus())) {
                return;
            }
            existing.setStatus("1");
            existing.setRemark(commodityFavoriteDTO.getRemark());
            userCommodityFavoriteMapper.updateById(existing);
            return;
        }

        UserCommodityFavorite favorite = new UserCommodityFavorite();
        favorite.setUserId(userId);
        favorite.setCommodityId(commodityId);
        favorite.setRemark(commodityFavoriteDTO.getRemark());
        favorite.setStatus("1");
        userCommodityFavoriteMapper.insert(favorite);
    }

    @Override
    public void deleteCommodityFavorite(CommodityFavoriteDTO commodityFavoriteDTO) {
        if (commodityFavoriteDTO == null
                || commodityFavoriteDTO.getUserId() == null
                || commodityFavoriteDTO.getCommodityId() == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "收藏参数不完整");
        }

        Long userId = commodityFavoriteDTO.getUserId();
        Long commodityId = commodityFavoriteDTO.getCommodityId();

        UserInfo user = this.getById(userId);
        if (user == null || user.getIsDelete() == 1) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户不存在");
        }

        QueryWrapper<UserCommodityFavorite> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", userId)
                .eq("commodity_id", commodityId);

        UserCommodityFavorite existing = userCommodityFavoriteMapper.selectOne(wrapper);
        if (existing != null) {
            existing.setStatus("0");
            userCommodityFavoriteMapper.updateById(existing);
            return;
        }
    }

    @Override
    public Page<UserCommodityFavorite> listCommodityFavorite(CommodityFavoriteDTO commodityFavoriteDTO) {
        if (commodityFavoriteDTO == null
                || commodityFavoriteDTO.getUserId() == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "收藏参数不完整");
        }

        Long userId = commodityFavoriteDTO.getUserId();

        UserInfo user = this.getById(userId);
        if (user == null || user.getIsDelete() == 1) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户不存在");
        }

        return userCommodityFavoriteMapper.selectPage(commodityFavoriteDTO.createPage(), commodityFavoriteDTO.createQuery());
    }   

}
