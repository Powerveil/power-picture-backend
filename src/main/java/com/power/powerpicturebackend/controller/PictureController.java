package com.power.powerpicturebackend.controller;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.power.powerpicturebackend.annotation.AuthCheck;
import com.power.powerpicturebackend.common.BaseResponse;
import com.power.powerpicturebackend.common.DeleteRequest;
import com.power.powerpicturebackend.common.ResultUtils;
import com.power.powerpicturebackend.constants.UserConstant;
import com.power.powerpicturebackend.manager.PictureManager;
import com.power.powerpicturebackend.model.dto.req.picture.PictureEditRequest;
import com.power.powerpicturebackend.model.dto.req.picture.PictureQueryRequest;
import com.power.powerpicturebackend.model.dto.req.picture.PictureUpdateRequest;
import com.power.powerpicturebackend.model.dto.req.picture.PictureUploadRequest;
import com.power.powerpicturebackend.model.entity.Picture;
import com.power.powerpicturebackend.model.vo.PictureTagCategory;
import com.power.powerpicturebackend.model.vo.PictureVO;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.List;

/**
 * @ClassName PictureController
 * @Description TODO
 * @Author power
 * @Date 2025/1/29 7:56
 * @Version 1.0
 */
@RestController
@RequestMapping("/picture")
public class PictureController {

    @Resource
    private PictureManager pictureManager;


    /**
     * 上传图片（可重新上传）
     */
    @PostMapping("/upload")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<PictureVO> uploadPicture(
            @RequestPart("file") MultipartFile multipartFile,
            PictureUploadRequest pictureUploadRequest,
            HttpServletRequest request) {

        return pictureManager.uploadPicture(multipartFile, pictureUploadRequest, request);
    }

    /**
     * 删除图片
     */
    @PostMapping("/delete")
    public BaseResponse<Boolean> deletePicture(@RequestBody DeleteRequest deleteRequest, HttpServletRequest request) {
        return pictureManager.deletePicture(deleteRequest, request);
    }

    /**
     * 更新图片（仅管理员可用）
     */
    @PostMapping("/update")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> updatePicture(@RequestBody PictureUpdateRequest pictureUpdateRequest) {
        return pictureManager.updatePicture(pictureUpdateRequest);
    }

    /**
     * 根据 id 获取图片（仅管理员可用）
     */
    @GetMapping("/get")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Picture> getPictureById(long id, HttpServletRequest request) {
        return pictureManager.getPictureById(id);
    }

    /**
     * 根据 id 获取图片（封装类）
     */
    @GetMapping("/get/vo")
    public BaseResponse<PictureVO> getPictureVOById(long id, HttpServletRequest request) {
        return pictureManager.getPictureVOById(id, request);
    }

    /**
     * 分页获取图片列表（仅管理员可用）
     */
    @PostMapping("/list/page")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Page<Picture>> listPictureByPage(@RequestBody PictureQueryRequest pictureQueryRequest) {
        return pictureManager.listPictureByPage(pictureQueryRequest);
    }

    /**
     * 分页获取图片列表（封装类）
     */
    @PostMapping("/list/page/vo")
    public BaseResponse<Page<PictureVO>> listPictureVOByPage(@RequestBody PictureQueryRequest pictureQueryRequest,
                                                             HttpServletRequest request) {
        return pictureManager.listPictureVOByPage(pictureQueryRequest, request);
    }

    /**
     * 编辑图片（给用户使用）
     */
    @PostMapping("/edit")
    public BaseResponse<Boolean> editPicture(@RequestBody PictureEditRequest pictureEditRequest, HttpServletRequest request) {
        return pictureManager.editPicture(pictureEditRequest, request);
    }

    @GetMapping("/tag_category")
    public BaseResponse<PictureTagCategory> listPictureTagCategory() {
        // 动态修改标签
        // 1.设置内存变量，初始化变量，但是提供接口修改变量
        // 2.数据库存储，定时任务修改
        // 3.定时任务，每天定时修改
        PictureTagCategory pictureTagCategory = new PictureTagCategory();
        List<String> tagList = Arrays.asList("热门", "搞笑", "生活", "高清", "艺术", "校园", "背景", "简历", "创意");
        List<String> categoryList = Arrays.asList("模板", "电商", "表情包", "素材", "海报");
        pictureTagCategory.setTagList(tagList);
        pictureTagCategory.setCategoryList(categoryList);
        return ResultUtils.success(pictureTagCategory);
    }

}
