package com.power.powerpicturebackend.service.impl;

import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.power.powerpicturebackend.common.DeleteRequest;
import com.power.powerpicturebackend.constants.UserConstant;
import com.power.powerpicturebackend.exception.ErrorCode;
import com.power.powerpicturebackend.exception.ThrowUtils;
import com.power.powerpicturebackend.mapper.UserMapper;
import com.power.powerpicturebackend.model.convertor.UserConvertor;
import com.power.powerpicturebackend.model.dto.req.user.UserAddRequest;
import com.power.powerpicturebackend.model.dto.req.user.UserQueryRequest;
import com.power.powerpicturebackend.model.dto.req.user.UserUpdateRequest;
import com.power.powerpicturebackend.model.entity.User;
import com.power.powerpicturebackend.model.enums.UserRoleEnum;
import com.power.powerpicturebackend.model.vo.LoginUserVO;
import com.power.powerpicturebackend.model.vo.UserVO;
import com.power.powerpicturebackend.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Objects;

/**
 * @author power
 * @description 针对表【user(用户)】的数据库操作Service实现
 * @createDate 2025-01-19 22:33:48
 */
@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
        implements UserService {

    @Override
    public long userRegister(String userAccount, String userPassword, String checkPassword) {
        // 1. 校验参数
        ThrowUtils.throwIf(StrUtil.hasBlank(userAccount, userPassword, checkPassword), ErrorCode.PARAMS_ERROR, "参数为空");

        ThrowUtils.throwIf(userAccount.length() < 4, ErrorCode.PARAMS_ERROR, "用户账号太短");

        ThrowUtils.throwIf(userPassword.length() < 8 || checkPassword.length() < 8, ErrorCode.PARAMS_ERROR, "用户密码太短");

        ThrowUtils.throwIf(!userPassword.equals(checkPassword), ErrorCode.PARAMS_ERROR, "两次密码不一致");

        // 2. 判断账号是否已存在
        ThrowUtils.throwIf(this.count(new QueryWrapper<User>().eq("userAccount", userAccount)) > 0, ErrorCode.PARAMS_ERROR, "用户账号已存在");

        // 3. 密码加密
        String encryptPassWord = this.getEncryptPassWord(userPassword);

        // 4. 插入数据
        User user = User.createRegisterUser(userAccount, encryptPassWord, "无名");
        boolean saveResult = this.save(user);

        ThrowUtils.throwIf(!saveResult, ErrorCode.SYSTEM_ERROR, "注册失败，数据库错误");
        return user.getId();
    }

    @Override
    public LoginUserVO userLogin(String userAccount, String userPassword, HttpServletRequest request) {
        // 1.参数校验
        ThrowUtils.throwIf(StrUtil.hasBlank(userAccount, userPassword), ErrorCode.PARAMS_ERROR, "参数为空");

        ThrowUtils.throwIf(userAccount.length() < 4, ErrorCode.PARAMS_ERROR, "用户账号错误");

        ThrowUtils.throwIf(userPassword.length() < 8, ErrorCode.PARAMS_ERROR, "用户密码错误");

        // 2.查询是否存在
        // 密码加盐
        String encryptPassWord = this.getEncryptPassWord(userPassword);

        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getUserAccount, userAccount)
                .eq(User::getUserPassword, encryptPassWord);

        User user = this.getOne(queryWrapper);
        // 不存在，抛异常
        // todo 改成有lambda 打印日志
        ThrowUtils.throwIf(ObjUtil.isNull(user), ErrorCode.PARAMS_ERROR, "用户不存在或密码错误");

        // 3.绑定session
        request.getSession().setAttribute(UserConstant.USER_LOGIN_STATE, user);

        // 4.封装VO返回
        return UserConvertor.INSTANCE.mapToLoginUserVo(user);
    }

    @Override
    public User getLoginUser(HttpServletRequest request) {
        Object userObj = request.getSession().getAttribute(UserConstant.USER_LOGIN_STATE);
        User currentUser = (User) userObj;
        ThrowUtils.throwIf(ObjUtil.isNull(currentUser) || ObjUtil.isNull(currentUser.getId()), ErrorCode.NOT_LOGIN_ERROR);
        // 从数据库中查询（追求性能的话可以注释，直接返回上述结果）
        currentUser = this.getById(currentUser.getId());
        ThrowUtils.throwIf(ObjUtil.isNull(currentUser), ErrorCode.NOT_LOGIN_ERROR);
        return currentUser;
    }

    @Override
    public boolean userLogout(HttpServletRequest request) {
        // 判断用户是否已经登录
        Object userObj = request.getSession().getAttribute(UserConstant.USER_LOGIN_STATE);
        ThrowUtils.throwIf(ObjUtil.isNull(userObj), ErrorCode.OPERATION_ERROR, "未登录");
        // 一出登录态
        request.getSession().removeAttribute(UserConstant.USER_LOGIN_STATE);
        return true;
    }




    @Override
    public LambdaQueryWrapper<User> getQueryWrapper(UserQueryRequest userQueryRequest) {
        Long id = userQueryRequest.getId();
        String userName = userQueryRequest.getUserName();
        String userAccount = userQueryRequest.getUserAccount();
        String userProfile = userQueryRequest.getUserProfile();
        String userRole = userQueryRequest.getUserRole();
        String sortField = userQueryRequest.getSortField();
        String sortOrder = userQueryRequest.getSortOrder();

        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();

        queryWrapper.eq(Objects.nonNull(id), User::getId, id);
        queryWrapper.like(StrUtil.isNotBlank(userName), User::getUserName, userName);
        queryWrapper.like(StrUtil.isNotBlank(userAccount), User::getUserAccount, userAccount);
        queryWrapper.like(StrUtil.isNotBlank(userProfile), User::getUserProfile, userProfile);
        queryWrapper.eq(StrUtil.isNotBlank(userRole), User::getUserRole, userRole);
        queryWrapper.last(StrUtil.isNotBlank(sortField), "order by " + sortField + " " + (sortOrder.equals("ascend") ? "asc" : "desc"));

        return queryWrapper;
    }

    @Override
    public Long addUser(UserAddRequest userAddRequest) {
        ThrowUtils.throwIf(Objects.isNull(userAddRequest), ErrorCode.PARAMS_ERROR);
        User user = UserConvertor.INSTANCE.mapToUser(userAddRequest);
        // 默认密码 12345678
        final String DEFAULT_PASSWORD = "12345678";
        String encryptPassword = this.getEncryptPassWord(DEFAULT_PASSWORD);
        user.setUserPassword(encryptPassword);
        boolean result = this.save(user);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);

        return user.getId();
    }

    @Override
    public User getUserById(Long id) {
        ThrowUtils.throwIf(Objects.isNull(id) || id <= 0, ErrorCode.PARAMS_ERROR);
        User user = this.getById(id);
        ThrowUtils.throwIf(Objects.isNull(user), ErrorCode.NOT_FOUND_ERROR);
        return user;
    }

    @Override
    public UserVO getUserVOById(Long id) {
        return UserConvertor.INSTANCE.mapToUserVo(this.getUserById(id));
    }

    @Override
    public Boolean deleteUser(DeleteRequest deleteRequest) {
        ThrowUtils.throwIf(Objects.isNull(deleteRequest) || deleteRequest.getId() <= 0, ErrorCode.PARAMS_ERROR);
        return this.removeById(deleteRequest.getId());
    }

    @Override
    public Boolean updateUser(UserUpdateRequest userUpdateRequest) {
        ThrowUtils.throwIf(Objects.isNull(userUpdateRequest) || Objects.isNull(userUpdateRequest.getId()), ErrorCode.PARAMS_ERROR);
        User user = UserConvertor.INSTANCE.mapToUser(userUpdateRequest);
        boolean result = this.updateById(user);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return true;
    }

    @Override
    public Page<UserVO> listUserVOByPage(UserQueryRequest userQueryRequest) {
        ThrowUtils.throwIf(Objects.isNull(userQueryRequest), ErrorCode.PARAMS_ERROR);
        long current = userQueryRequest.getCurrent();
        long pageSize = userQueryRequest.getPageSize();
        Page<User> userPage = this.page(new Page<>(current, pageSize),
                this.getQueryWrapper(userQueryRequest));
        Page<UserVO> userVOPage = new Page<>(current, pageSize, userPage.getTotal());
        List<UserVO> userVOList = UserConvertor.INSTANCE.mapToUserVoList(userPage.getRecords());
        userVOPage.setRecords(userVOList);
        return userVOPage;
    }


    @Override
    public String getEncryptPassWord(String userPassWord) {
        // 加盐，混淆密码
        String salt = "power-picture";
        return DigestUtils.md5DigestAsHex((salt + userPassWord).getBytes());
    }

    @Override
    public boolean isAdmin(User user) {
        return user != null && UserRoleEnum.ADMIN.getValue().equals(user.getUserRole());
    }

}




