package com.power.powerpicturebackend.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.power.powerpicturebackend.constants.UserConstant;
import com.power.powerpicturebackend.exception.ErrorCode;
import com.power.powerpicturebackend.exception.ThrowUtils;
import com.power.powerpicturebackend.manager.common.FileManager;
import com.power.powerpicturebackend.mapper.PictureMapper;
import com.power.powerpicturebackend.model.convertor.PictureConvertor;
import com.power.powerpicturebackend.model.dto.UploadPictureResult;
import com.power.powerpicturebackend.model.dto.req.picture.PictureQueryRequest;
import com.power.powerpicturebackend.model.dto.req.picture.PictureUploadRequest;
import com.power.powerpicturebackend.model.entity.Picture;
import com.power.powerpicturebackend.model.entity.User;
import com.power.powerpicturebackend.model.vo.PictureVO;
import com.power.powerpicturebackend.service.PictureService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
* @author power
* @description 针对表【picture(图片)】的数据库操作Service实现
* @createDate 2025-01-23 01:25:33
*/
@Service
public class PictureServiceImpl extends ServiceImpl<PictureMapper, Picture>
    implements PictureService {

    @Resource
    private FileManager fileManager;

    @Override
    public PictureVO uploadPicture(MultipartFile multipartFile, PictureUploadRequest pictureUploadRequest, User loginUser) {

        // 校验参数
        ThrowUtils.throwIf(ObjectUtil.hasEmpty(multipartFile, pictureUploadRequest, loginUser), ErrorCode.PARAMS_ERROR);
        ThrowUtils.throwIf(!loginUser.getUserRole().equals(UserConstant.ADMIN_ROLE), ErrorCode.NO_AUTH_ERROR);

        // 判断是新增还是删除
        // 判断图片是否存在
        Long pictureId = pictureUploadRequest.getId();
        if (Objects.nonNull(pictureId)) {
            boolean exists = this.exists(new LambdaQueryWrapper<Picture>().eq(Picture::getId, pictureId));
            ThrowUtils.throwIf(!exists, ErrorCode.NOT_FOUND_ERROR, "图片不存在");
        }

        String uploadPathPrefix = String.format("public/%s", loginUser.getId());
        // 上传图片
        UploadPictureResult uploadPictureResult = fileManager.uploadPicture(multipartFile, uploadPathPrefix);
        Picture picture = PictureConvertor.INSTANCE.mapToPicture(uploadPictureResult);
        picture.setUserId(loginUser.getId());

        if (Objects.nonNull(pictureId)) {
            picture.setId(pictureId);
            picture.setEditTime(new Date());
        }

        boolean result = this.saveOrUpdate(picture);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR, "图片上传失败");
        // 操作数据库
        return PictureConvertor.INSTANCE.mapToPictureVo(picture);
    }

    @Override
    public LambdaQueryWrapper<Picture> getQueryWrapper(PictureQueryRequest pictureQueryRequest) {
        LambdaQueryWrapper<Picture> queryWrapper = new LambdaQueryWrapper<>();
        if (pictureQueryRequest == null) {
            return queryWrapper;
        }
        // 从对象中取值
        Long id = pictureQueryRequest.getId();
        String name = pictureQueryRequest.getName();
        String introduction = pictureQueryRequest.getIntroduction();
        String category = pictureQueryRequest.getCategory();
        List<String> tags = pictureQueryRequest.getTags();
        Long picSize = pictureQueryRequest.getPicSize();
        Integer picWidth = pictureQueryRequest.getPicWidth();
        Integer picHeight = pictureQueryRequest.getPicHeight();
        Double picScale = pictureQueryRequest.getPicScale();
        String picFormat = pictureQueryRequest.getPicFormat();
        String searchText = pictureQueryRequest.getSearchText();
        Long userId = pictureQueryRequest.getUserId();
        String sortField = pictureQueryRequest.getSortField();
        String sortOrder = pictureQueryRequest.getSortOrder();
        // 从多字段中搜索
        if (StrUtil.isNotBlank(searchText)) {
            // 需要拼接查询条件
            queryWrapper.and(qw -> qw.like(Picture::getName, searchText)
                    .or()
                    .like(Picture::getIntroduction, searchText)
            );
        }
        queryWrapper.eq(ObjUtil.isNotEmpty(id), Picture::getId, id);
        queryWrapper.eq(ObjUtil.isNotEmpty(userId), Picture::getUserId, userId);
        queryWrapper.like(StrUtil.isNotBlank(name), Picture::getName, name);
        queryWrapper.like(StrUtil.isNotBlank(introduction), Picture::getIntroduction, introduction);
        queryWrapper.like(StrUtil.isNotBlank(picFormat), Picture::getPicFormat, picFormat);
        queryWrapper.eq(StrUtil.isNotBlank(category), Picture::getCategory, category);
        queryWrapper.eq(ObjUtil.isNotEmpty(picWidth), Picture::getPicWidth, picWidth);
        queryWrapper.eq(ObjUtil.isNotEmpty(picHeight), Picture::getPicHeight, picHeight);
        queryWrapper.eq(ObjUtil.isNotEmpty(picSize), Picture::getPicSize, picSize);
        queryWrapper.eq(ObjUtil.isNotEmpty(picScale), Picture::getPicScale, picScale);
        // JSON 数组查询
        if (CollUtil.isNotEmpty(tags)) {
            for (String tag : tags) {
                queryWrapper.like(Picture::getTags, "\"" + tag + "\"");
            }
        }
        // 排序
        queryWrapper.last(StrUtil.isNotEmpty(sortField), "order by " + sortField + " " + (sortOrder.equals("ascend") ? "asc" : "desc"));
        return queryWrapper;
    }

    @Override
    public void validPicture(Picture picture) {
        ThrowUtils.throwIf(picture == null, ErrorCode.PARAMS_ERROR);
        // 从对象中取值
        Long id = picture.getId();
        String url = picture.getUrl();
        String introduction = picture.getIntroduction();
        // 修改数据时，id 不能为空，有参数则校验
        ThrowUtils.throwIf(ObjUtil.isNull(id), ErrorCode.PARAMS_ERROR, "id 不能为空");
        if (StrUtil.isNotBlank(url)) {
            ThrowUtils.throwIf(url.length() > 1024, ErrorCode.PARAMS_ERROR, "url 过长");
        }
        if (StrUtil.isNotBlank(introduction)) {
            ThrowUtils.throwIf(introduction.length() > 800, ErrorCode.PARAMS_ERROR, "简介过长");
        }
    }

    @Override
    public Picture getPictureById(long id) {
        ThrowUtils.throwIf(id <= 0, ErrorCode.PARAMS_ERROR);
        // 查询数据库
        Picture picture = this.getById(id);
        ThrowUtils.throwIf(picture == null, ErrorCode.NOT_FOUND_ERROR);
        return picture;
    }

    @Override
    public Page<Picture> listPictureVOByPage(PictureQueryRequest pictureQueryRequest, boolean isAdmin) {
        long current = pictureQueryRequest.getCurrent();
        long size = pictureQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(!isAdmin && size > 20, ErrorCode.PARAMS_ERROR);
        // 查询数据库
        return this.page(new Page<>(current, size), this.getQueryWrapper(pictureQueryRequest));
    }


}




