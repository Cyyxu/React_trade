package com.xyes.springboot.service;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xyes.springboot.model.dto.user.*;
import com.xyes.springboot.model.entity.User;
import com.xyes.springboot.model.vo.LoginResponse;
import com.xyes.springboot.model.vo.LoginUserVO;
import com.xyes.springboot.model.vo.UserVO;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

public interface UserService extends IService<User>  {

    LoginUserVO userLogin(String userAccount, String userPassword, HttpServletRequest request);

    User getLoginUser(HttpServletRequest request);

    long userRegister(String userAccount, String userPassword, String checkPassword);

    boolean userLogout(HttpServletRequest request);

    LoginUserVO getLoginUserVO(User user);

    UserVO getUserVO(User user);

    List<UserVO> getUserVO(List<User> userList);

    Wrapper<User> getQueryWrapper(UserQueryRequest userQueryRequest);

    boolean isAdmin(HttpServletRequest request);

    boolean isAdmin(User user);

    User getByIdWithLock(Long id);

    User getLoginUserPermitNull(HttpServletRequest request);

    /**
     * 用户注册（业务逻辑）
     *
     * @param userRegisterRequest
     * @return 新用户ID
     */
    Long userRegister(UserRegisterRequest userRegisterRequest);

    /**
     * 用户登录（包含JWT token生成）
     *
     * @param userLoginRequest
     * @param request
     * @return
     */
    LoginResponse login(UserLoginRequest userLoginRequest, HttpServletRequest request);

    /**
     * 添加用户（仅管理员）
     *
     * @param userAddRequest
     * @param request
     * @return 新用户ID
     */
    Long addUser(UserAddRequest userAddRequest, HttpServletRequest request);

    /**
     * 删除用户（仅管理员）
     *
     * @param id
     * @param request
     * @return
     */
    Boolean deleteUserById(Long id, HttpServletRequest request);

    /**
     * 更新用户（仅管理员）
     *
     * @param userUpdateRequest
     * @param request
     * @return
     */
    Boolean updateUserById(UserUpdateRequest userUpdateRequest, HttpServletRequest request);

    /**
     * 根据ID获取用户（仅管理员）
     *
     * @param id
     * @param request
     * @return
     */
    User getUserById(Long id, HttpServletRequest request);

    /**
     * 根据ID获取用户VO
     *
     * @param id
     * @param request
     * @return
     */
    UserVO getUserVOById(Long id, HttpServletRequest request);

    /**
     * 分页获取用户列表（实体类，仅管理员）
     *
     * @param userQueryRequest
     * @param request
     * @return
     */
    Page<User> listUserByPage(UserQueryRequest userQueryRequest, HttpServletRequest request);

    /**
     * 分页获取用户列表（封装类）
     *
     * @param userQueryRequest
     * @param request
     * @return
     */
    Page<UserVO> listUserVOByPage(UserQueryRequest userQueryRequest, HttpServletRequest request);

    /**
     * 更新当前登录用户信息
     *
     * @param userUpdateMyRequest
     * @param request
     * @return
     */
    Boolean updateMyUser(UserUpdateMyRequest userUpdateMyRequest, HttpServletRequest request);
}
