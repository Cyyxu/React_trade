package com.xyex.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.xyex.entity.req.UserLoginDTO;
import com.xyex.entity.model.UserInfo;
import com.xyex.infrastructure.exception.BusinessException;
import com.xyex.infrastructure.exception.ErrorCode;
import com.xyex.infrastructure.model.BasicServiceImpl;
import com.xyex.infrastructure.utils.ValidationUtils;
import com.xyex.mapper.UserMapper;
import com.xyex.service.UserService;

import lombok.AllArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;
import com.xyex.infrastructure.utils.JwtUtils;
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

        // 4. 生成token
        String token = jwtUtils.generateToken(user.getId(), user.getUserAccount());

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
}
