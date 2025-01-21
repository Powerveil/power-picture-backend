package com.power.powerpicturebackend.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.power.powerpicturebackend.annotation.AuthCheck;
import com.power.powerpicturebackend.common.BaseResponse;
import com.power.powerpicturebackend.common.DeleteRequest;
import com.power.powerpicturebackend.constants.UserConstant;
import com.power.powerpicturebackend.manager.UserManager;
import com.power.powerpicturebackend.model.dto.req.user.*;
import com.power.powerpicturebackend.model.entity.User;
import com.power.powerpicturebackend.model.vo.LoginUserVO;
import com.power.powerpicturebackend.model.vo.UserVO;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/user")
public class UserController {

    @Resource
    private UserManager userManager;

    /**
     * 用户注册
     * @param userRegisterRequest
     * @return
     */
    @PostMapping("/register")
    @ApiOperation("用户注册")
    public BaseResponse<Long> userRegister(@RequestBody UserRegisterRequest userRegisterRequest) {
        return userManager.register(userRegisterRequest);
    }

    /**
     * 用户登录
     * @param userLoginRequest
     * @return
     */
    @PostMapping("/login")
    @ApiOperation("用户登录")
    public BaseResponse<LoginUserVO> userLogin(@RequestBody UserLoginRequest userLoginRequest, HttpServletRequest request) {
        return userManager.login(userLoginRequest, request);
    }

    @GetMapping("/get/login")
    public BaseResponse<LoginUserVO> getLoginUser(HttpServletRequest request) {
        return userManager.getLoginUser(request);
    }


    /**
     * 用户登出
     * @param request
     * @return
     */
    @PostMapping("/logout")
    public BaseResponse<Boolean> userLogout(HttpServletRequest request) {
        return userManager.userLogout(request);
    }


    // region 管理员接口

    /**
     * 创建用户
     */
    @PostMapping("/add")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Long> addUser(@RequestBody UserAddRequest userAddRequest) {
        return userManager.addUser(userAddRequest);
    }

    /**
     * 根据 id 获取用户（仅管理员）
     */
    @GetMapping("/get")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<User> getUserById(Long id) {
        return userManager.getUserById(id);
    }

    /**
     * 根据 id 获取包装类
     */
    @GetMapping("/get/vo")
    public BaseResponse<UserVO> getUserVOById(Long id) {
        return userManager.getUserVOById(id);
    }

    /**
     * 删除用户
     */
    @PostMapping("/delete")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> deleteUser(@RequestBody DeleteRequest deleteRequest) {
        return userManager.deleteUser(deleteRequest);
    }

    /**
     * 更新用户
     */
    @PostMapping("/update")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> updateUser(@RequestBody UserUpdateRequest userUpdateRequest) {
        return userManager.updateUser(userUpdateRequest);
    }

    /**
     * 分页获取用户封装列表（仅管理员）
     *
     * @param userQueryRequest 查询请求参数
     */
    @PostMapping("/list/page/vo")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Page<UserVO>> listUserVOByPage(@RequestBody UserQueryRequest userQueryRequest) {
        return userManager.listUserVOByPage(userQueryRequest);
    }


    // endregion


}
