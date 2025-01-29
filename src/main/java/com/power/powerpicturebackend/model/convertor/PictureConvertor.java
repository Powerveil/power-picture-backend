package com.power.powerpicturebackend.model.convertor;


import com.power.powerpicturebackend.model.dto.UploadPictureResult;
import com.power.powerpicturebackend.model.dto.req.picture.PictureEditRequest;
import com.power.powerpicturebackend.model.dto.req.picture.PictureUpdateRequest;
import com.power.powerpicturebackend.model.entity.Picture;
import com.power.powerpicturebackend.model.vo.PictureVO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.factory.Mappers;

/**
 * @ClassName PictureConvertor
 * @Description TODO
 * @Author power
 * @Date 2025/1/23 1:36
 * @Version 1.0
 */
@Mapper(nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
public interface PictureConvertor {
    PictureConvertor INSTANCE = Mappers.getMapper(PictureConvertor.class);


    /**
     * 将实体类转换为VO
     * @param request
     * @return
     */
    @Mapping(target = "tags", expression = "java(request.getTagsList())")
    public PictureVO mapToPictureVo(Picture request);


    /**
     * 将实体类转换为Picture
     * @param request
     * @return
     */
    @Mapping(target = "tags", expression = "java(request.getTagsStr())")
    public Picture mapToPicture(PictureVO request);


    /**
     * 将实体类转换为Picture
     * @param uploadPictureResult
     * @return
     */
    @Mapping(target = "name", source = "picName")
    public Picture mapToPicture(UploadPictureResult uploadPictureResult);


    /**
     * 将实体类转换为Picture
     * @param request
     * @return
     */
    @Mapping(target = "tags", expression = "java(request.getTagsStr())")
    public Picture mapToPicture(PictureUpdateRequest request);

    /**
     * 将实体类转换为Picture
     * @param request
     * @return
     */
    @Mapping(target = "editTime", expression = "java(new java.util.Date())")
    @Mapping(target = "tags", expression = "java(request.getTagsStr())")
    public Picture mapToPicture(PictureEditRequest request);
}
