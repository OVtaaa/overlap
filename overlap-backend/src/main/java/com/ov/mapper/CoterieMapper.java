package com.ov.mapper;

import com.ov.pojo.Coterie;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.RequestBody;

import javax.annotation.Resource;
import java.util.List;

/**
* @author Ovta~
* @description 针对表【coterie】的数据库操作Mapper
* @createDate 2023-08-22 18:46:22
* @Entity com.ov.pojo.Coterie
*/
@Repository
public interface CoterieMapper extends BaseMapper<Coterie> {

}




