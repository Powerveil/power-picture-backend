package com.power.powerpicturebackend.model.vo;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @ClassName PictureTagCategory
 * @Description 图片标签分类列表视图
 * @Author power
 * @Date 2025/1/29 16:34
 * @Version 1.0
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class PictureTagCategory {
    private List<String> tagList;
    private List<String> categoryList;
}
