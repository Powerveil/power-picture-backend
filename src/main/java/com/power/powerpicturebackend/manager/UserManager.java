package com.power.powerpicturebackend.manager;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.power.powerpicturebackend.common.BaseResponse;
import com.power.powerpicturebackend.common.DeleteRequest;
import com.power.powerpicturebackend.common.ResultUtils;
import com.power.powerpicturebackend.model.convertor.UserConvertor;
import com.power.powerpicturebackend.model.dto.req.user.*;
import com.power.powerpicturebackend.model.entity.User;
import com.power.powerpicturebackend.model.vo.LoginUserVO;
import com.power.powerpicturebackend.model.vo.UserVO;
import com.power.powerpicturebackend.service.UserService;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

@Component
public class UserManager {
    @Resource
    private UserService userService;

    public BaseResponse<Long> register(UserRegisterRequest userRegisterRequest) {
        String userAccount = userRegisterRequest.getUserAccount();
        String userPassword = userRegisterRequest.getUserPassword();
        String checkPassword = userRegisterRequest.getCheckPassword();
        return ResultUtils.success(userService.userRegister(userAccount, userPassword, checkPassword));
    }

    public BaseResponse<LoginUserVO> login(UserLoginRequest userLoginRequest, HttpServletRequest request) {
        String userAccount = userLoginRequest.getUserAccount();
        String userPassword = userLoginRequest.getUserPassword();
        return ResultUtils.success(userService.userLogin(userAccount, userPassword, request));
    }

    public BaseResponse<LoginUserVO> getLoginUser(HttpServletRequest request) {
        User currentUser = userService.getLoginUser(request);
        return ResultUtils.success(UserConvertor.INSTANCE.mapToLoginUserVo(currentUser));
    }

    public BaseResponse<Boolean> userLogout(HttpServletRequest request) {
        return ResultUtils.success(userService.userLogout(request));
    }

    public BaseResponse<Long> addUser(UserAddRequest userAddRequest) {
        return ResultUtils.success(userService.addUser(userAddRequest));
    }

    public BaseResponse<User> getUserById(Long id) {
        return ResultUtils.success(userService.getUserById(id));
    }

    public BaseResponse<UserVO> getUserVOById(Long id) {
        return ResultUtils.success(userService.getUserVOById(id));
    }

    public BaseResponse<Boolean> deleteUser(DeleteRequest deleteRequest) {
        return ResultUtils.success(userService.deleteUser(deleteRequest));
    }

    public BaseResponse<Boolean> updateUser(UserUpdateRequest userUpdateRequest) {
        return ResultUtils.success(userService.updateUser(userUpdateRequest));
    }

    public BaseResponse<Page<UserVO>> listUserVOByPage(UserQueryRequest userQueryRequest) {
        return ResultUtils.success(userService.listUserVOByPage(userQueryRequest));
    }
}
