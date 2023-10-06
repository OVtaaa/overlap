package com.ov.mapper;

import com.ov.pojo.Message;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.springframework.stereotype.Repository;

/**
* @author Ovta~
* @description 针对表【message】的数据库操作Mapper
* @createDate 2023-09-15 20:23:56
* @Entity com.ov.pojo.Message
*/
@Repository
public interface MessageMapper extends BaseMapper<Message> {

}




