package com.power.powerpicturebackend.manager;


import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.power.powerpicturebackend.common.BaseResponse;
import com.power.powerpicturebackend.common.DeleteRequest;
import com.power.powerpicturebackend.common.ResultUtils;
import com.power.powerpicturebackend.exception.BusinessException;
import com.power.powerpicturebackend.exception.ErrorCode;
import com.power.powerpicturebackend.exception.ThrowUtils;
import com.power.powerpicturebackend.model.convertor.PictureConvertor;
import com.power.powerpicturebackend.model.convertor.UserConvertor;
import com.power.powerpicturebackend.model.dto.req.picture.PictureEditRequest;
import com.power.powerpicturebackend.model.dto.req.picture.PictureQueryRequest;
import com.power.powerpicturebackend.model.dto.req.picture.PictureUpdateRequest;
import com.power.powerpicturebackend.model.dto.req.picture.PictureUploadRequest;
import com.power.powerpicturebackend.model.entity.Picture;
import com.power.powerpicturebackend.model.entity.User;
import com.power.powerpicturebackend.model.vo.PictureVO;
import com.power.powerpicturebackend.model.vo.UserVO;
import com.power.powerpicturebackend.service.PictureService;
import com.power.powerpicturebackend.service.UserService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @ClassName PictureManager
 * @Description TODO
 * @Author power
 * @Date 2025/1/29 7:59
 * @Version 1.0
 */
@Service
public class PictureManager {

    @Resource
    private PictureService pictureService;

    @Resource
    private UserService userService;

    public BaseResponse<PictureVO> uploadPicture(MultipartFile multipartFile, PictureUploadRequest pictureUploadRequest, HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);
        PictureVO pictureVO = pictureService.uploadPicture(multipartFile, pictureUploadRequest, loginUser);
        return ResultUtils.success(pictureVO);
    }

    public PictureVO getPictureVO(Picture picture) {
        // 对象转封装类
        PictureVO pictureVO = PictureConvertor.INSTANCE.mapToPictureVo(picture);
        // 关联查询用户信息
        Long userId = picture.getUserId();
        if (userId != null && userId > 0) {
            User user = userService.getById(userId);
            UserVO userVO = UserConvertor.INSTANCE.mapToUserVo(user);
            pictureVO.setUser(userVO);
        }
        return pictureVO;
    }




    /**
     * 分页获取图片封装
     */
    public Page<PictureVO> getPictureVOPage(Page<Picture> picturePage) {
        List<Picture> pictureList = picturePage.getRecords();
        Page<PictureVO> pictureVOPage = new Page<>(picturePage.getCurrent(), picturePage.getSize(), picturePage.getTotal());
        if (CollUtil.isEmpty(pictureList)) {
            return pictureVOPage;
        }
        // 对象列表 => 封装对象列表
        List<PictureVO> pictureVOList = pictureList.stream().map(PictureConvertor.INSTANCE::mapToPictureVo).collect(Collectors.toList());
        // 1. 关联查询用户信息
        Set<Long> userIdSet = pictureList.stream().map(Picture::getUserId).collect(Collectors.toSet());
        Map<Long, List<User>> userIdUserListMap = userService.listByIds(userIdSet).stream()
                .collect(Collectors.groupingBy(User::getId));
        // 2. 填充信息
        pictureVOList.forEach(pictureVO -> {
            Long userId = pictureVO.getUserId();
            User user = null;
            if (userIdUserListMap.containsKey(userId)) {
                user = userIdUserListMap.get(userId).get(0);
            }
            pictureVO.setUser(UserConvertor.INSTANCE.mapToUserVo(user));
        });
        pictureVOPage.setRecords(pictureVOList);
        return pictureVOPage;
    }






    public BaseResponse<Boolean> deletePicture(DeleteRequest deleteRequest, HttpServletRequest request) {
        if (deleteRequest == null || deleteRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userService.getLoginUser(request);
        long id = deleteRequest.getId();
        // 判断是否存在
        Picture oldPicture = pictureService.getById(id);
        ThrowUtils.throwIf(oldPicture == null, ErrorCode.NOT_FOUND_ERROR);
        // 仅本人或管理员可删除
        if (!oldPicture.getUserId().equals(loginUser.getId()) && !userService.isAdmin(loginUser)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        // 操作数据库
        boolean result = pictureService.removeById(id);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(true);
    }

    public BaseResponse<Boolean> updatePicture(PictureUpdateRequest pictureUpdateRequest) {
        if (pictureUpdateRequest == null || pictureUpdateRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 将实体类和 DTO 进行转换
        Picture picture = PictureConvertor.INSTANCE.mapToPicture(pictureUpdateRequest);
        // 数据校验
        pictureService.validPicture(picture);
        // 判断是否存在
        long id = pictureUpdateRequest.getId();
        Picture oldPicture = pictureService.getById(id);
        ThrowUtils.throwIf(oldPicture == null, ErrorCode.NOT_FOUND_ERROR);
        // 操作数据库
        boolean result = pictureService.updateById(picture);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(true);
    }

    public BaseResponse<Picture> getPictureById(long id) {
        Picture picture = pictureService.getPictureById(id);
        // 获取封装类
        return ResultUtils.success(picture);
    }

    public BaseResponse<PictureVO> getPictureVOById(long id, HttpServletRequest request) {
        Picture picture = pictureService.getPictureById(id);
        // 获取封装类
        return ResultUtils.success(this.getPictureVO(picture));
    }

    public BaseResponse<Page<Picture>> listPictureByPage(PictureQueryRequest pictureQueryRequest) {
        Page<Picture> picturePage = pictureService.listPictureVOByPage(pictureQueryRequest, false);
        return ResultUtils.success(picturePage);
    }

    public BaseResponse<Page<PictureVO>> listPictureVOByPage(PictureQueryRequest pictureQueryRequest, HttpServletRequest request) {
        Page<Picture> picturePage = pictureService.listPictureVOByPage(pictureQueryRequest, true);
        // 获取封装类
        return ResultUtils.success(this.getPictureVOPage(picturePage));
    }

    public BaseResponse<Boolean> editPicture(PictureEditRequest pictureEditRequest, HttpServletRequest request) {
        if (pictureEditRequest == null || pictureEditRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 在此处将实体类和 DTO 进行转换
        Picture picture = PictureConvertor.INSTANCE.mapToPicture(pictureEditRequest);
        // 数据校验
        pictureService.validPicture(picture);
        User loginUser = userService.getLoginUser(request);
        // 判断是否存在
        long id = pictureEditRequest.getId();
        Picture oldPicture = pictureService.getById(id);
        ThrowUtils.throwIf(oldPicture == null, ErrorCode.NOT_FOUND_ERROR);
        // 仅本人或管理员可编辑
        if (!oldPicture.getUserId().equals(loginUser.getId()) && !userService.isAdmin(loginUser)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        // 操作数据库
        boolean result = pictureService.updateById(picture);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(true);
    }
}
