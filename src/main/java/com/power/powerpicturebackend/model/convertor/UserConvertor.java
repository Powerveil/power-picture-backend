package com.power.powerpicturebackend.model.convertor;

import com.power.powerpicturebackend.model.dto.req.user.UserAddRequest;
import com.power.powerpicturebackend.model.dto.req.user.UserUpdateRequest;
import com.power.powerpicturebackend.model.entity.User;
import com.power.powerpicturebackend.model.vo.LoginUserVO;
import com.power.powerpicturebackend.model.vo.UserVO;
import org.mapstruct.Mapper;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
public interface UserConvertor {
    UserConvertor INSTANCE = Mappers.getMapper(UserConvertor.class);

    /**
     * 转换为LoginUserVO
     *
     * @param user
     * @return
     */
    public LoginUserVO mapToLoginUserVo(User user);


    /**
     * 转换为UserVO
     *
     * @param user
     * @return 脱敏后的用户
     */
    public UserVO mapToUserVo(User user);


    /**
     * 转换为UserVOList
     *
     * @param userList
     * @return 脱敏后的用户列表
     */
    public List<UserVO> mapToUserVoList(List<User> userList);



    /**
     * 转换为User
     *
     * @param userAddRequest
     * @return
     */
    public User mapToUser(UserAddRequest userAddRequest);


    /**
     * 转换为User
     *
     * @param userUpdateRequest
     * @return
     */
    public User mapToUser(UserUpdateRequest userUpdateRequest);

}
