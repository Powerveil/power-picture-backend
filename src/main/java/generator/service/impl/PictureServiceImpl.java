package generator.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.power.powerpicturebackend.model.entity.Picture;
import generator.service.PictureService;
import com.power.powerpicturebackend.mapper.PictureMapper;
import org.springframework.stereotype.Service;

/**
* @author power
* @description 针对表【picture(图片)】的数据库操作Service实现
* @createDate 2025-01-23 01:25:33
*/
@Service
public class PictureServiceImpl extends ServiceImpl<PictureMapper, Picture>
    implements PictureService{

}




