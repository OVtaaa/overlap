package com.ov.mapper;

import com.ov.pojo.Chat;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
* @author Ovta~
* @description 针对表【chat】的数据库操作Mapper
* @createDate 2023-09-20 20:42:46
*/
@Repository
public interface ChatMapper extends BaseMapper<Chat> {

    List<Long> getChatListIdByType(Long id,Integer type);
}




