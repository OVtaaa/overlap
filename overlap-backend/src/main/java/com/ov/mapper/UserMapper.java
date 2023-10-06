package com.ov.mapper;
import java.util.List;
import org.apache.ibatis.annotations.Param;

import com.ov.pojo.User;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.springframework.stereotype.Repository;

/**
* @author Ovta~
* @description 针对表【user】的数据库操作Mapper
* @createDate 2022-10-10 20:55:44
* @Entity com.ov.pojo.User
*/
@Repository
public interface UserMapper extends BaseMapper<User> {


}




