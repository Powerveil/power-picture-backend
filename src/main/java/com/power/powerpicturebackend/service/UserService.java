package com.power.powerpicturebackend.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.power.powerpicturebackend.common.DeleteRequest;
import com.power.powerpicturebackend.model.dto.req.user.UserAddRequest;
import com.power.powerpicturebackend.model.dto.req.user.UserQueryRequest;
import com.power.powerpicturebackend.model.dto.req.user.UserUpdateRequest;
import com.power.powerpicturebackend.model.entity.User;
import com.power.powerpicturebackend.model.vo.LoginUserVO;
import com.power.powerpicturebackend.model.vo.UserVO;

import javax.servlet.http.HttpServletRequest;

/**
* @author power
* @description 针对表【user(用户)】的数据库操作Service
* @createDate 2025-01-19 22:33:48
*/
public interface UserService extends IService<User> {
    /**
     * 用户注册
     *
     * @param userAccount   用户账户
     * @param userPassword  用户密码
     * @param checkPassword 校验密码
     * @return 新用户 id
     */
    long userRegister(String userAccount, String userPassword, String checkPassword);

    /**
     * 用户登录
     *
     * @param userAccount  用户账户
     * @param userPassword 用户密码
     * @param request
     * @return 脱敏后的用户信息
     */
    LoginUserVO userLogin(String userAccount, String userPassword, HttpServletRequest request);


    /**
     * 获取当前登录用户
     *
     * @param request
     * @return
     */
    User getLoginUser(HttpServletRequest request);


    /**
     * 用户注销
     *
     * @param request
     * @return
     */
    boolean userLogout(HttpServletRequest request);


    String getEncryptPassWord(String userPassWord);

    LambdaQueryWrapper<User> getQueryWrapper(UserQueryRequest userQueryRequest);

    Long addUser(UserAddRequest userAddRequest);

    User getUserById(Long id);

    UserVO getUserVOById(Long id);

    Boolean deleteUser(DeleteRequest deleteRequest);

    Boolean updateUser(UserUpdateRequest userUpdateRequest);

    Page<UserVO> listUserVOByPage(UserQueryRequest userQueryRequest);
}
