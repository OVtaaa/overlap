package com.ov.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ov.pojo.Coterie;
import com.ov.pojo.User;
import com.ov.pojo.UserCoterie;
import com.ov.service.CoterieService;
import com.ov.service.UserCoterieService;
import com.ov.mapper.UserCoterieMapper;
import com.ov.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

/**
* @author Ovta~
* @description 针对表【user_coterie】的数据库操作Service实现
* @createDate 2023-08-23 11:09:48
*/
@Service
public class UserCoterieServiceImpl extends ServiceImpl<UserCoterieMapper, UserCoterie>
    implements UserCoterieService{

}




