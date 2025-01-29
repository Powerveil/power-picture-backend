package com.power.powerpicturebackend.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.power.powerpicturebackend.model.dto.req.picture.PictureQueryRequest;
import com.power.powerpicturebackend.model.dto.req.picture.PictureUploadRequest;
import com.power.powerpicturebackend.model.entity.Picture;
import com.power.powerpicturebackend.model.entity.User;
import com.power.powerpicturebackend.model.vo.PictureVO;
import org.springframework.web.multipart.MultipartFile;

/**
* @author power
* @description 针对表【picture(图片)】的数据库操作Service
* @createDate 2025-01-23 01:25:33
*/
public interface PictureService extends IService<Picture> {
    /**
     * 上传图片
     *
     * @param multipartFile
     * @param pictureUploadRequest
     * @param loginUser
     * @return
     */
    PictureVO uploadPicture(MultipartFile multipartFile,
                            PictureUploadRequest pictureUploadRequest,
                            User loginUser);


    LambdaQueryWrapper<Picture> getQueryWrapper(PictureQueryRequest pictureQueryRequest);

    void validPicture(Picture picture);

    Picture getPictureById(long id);

    Page<Picture> listPictureVOByPage(PictureQueryRequest pictureQueryRequest, boolean isAdmin);
}
