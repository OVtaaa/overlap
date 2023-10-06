package com.ov.controller;

import com.ov.common.BaseResponse;
import com.ov.common.ResultUtil;
import com.ov.pojo.vo.UserPage;
import com.ov.common.exception.BusinessException;
import com.ov.pojo.enums.StatusCode;
import com.ov.pojo.Tag;
import com.ov.pojo.User;
import com.ov.pojo.request.UserLoginRequest;
import com.ov.pojo.request.UserRegisterRequest;
import com.ov.pojo.request.UserUpdateRequest;
import com.ov.service.UserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.Map;

import static com.ov.constant.UserConstant.USER_LOGIN_STATUS;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public BaseResponse<Long> userRegister(@RequestBody UserRegisterRequest userRegisterRequest, HttpServletRequest request){
        //  验证数据的合法性
        if(userRegisterRequest == null || request == null)
            throw new BusinessException(StatusCode.PARAMS_NULL);

        String userAccount = userRegisterRequest.getUserAccount();
        String password = userRegisterRequest.getPassword();
        String email = userRegisterRequest.getEmail();
        String checkPassword = userRegisterRequest.getCheckPassword();
        if(StringUtils.isAnyBlank(userAccount,password,checkPassword,email))
            throw  new BusinessException(StatusCode.PARAMS_NULL);

        //  传入数据到 service 层
        long resultID = userService.registerUser(userAccount,email, password, checkPassword, request);
        return ResultUtil.success(resultID);
    }

//    退出登录
    @DeleteMapping("/outLogin")
    public BaseResponse<Boolean> outLogin(HttpServletRequest request){
        HttpSession session = request.getSession();
        session.setAttribute(USER_LOGIN_STATUS,null);
        return ResultUtil.success(true);
    }

//    获取当前用户信息
    @GetMapping("/current")
    public BaseResponse<User> getCurrentUser(HttpServletRequest request){
        User currentUser = userService.getCurrentUser(request);
        if(currentUser == null){
            throw new BusinessException(StatusCode.NO_LOGIN);
        }
        return ResultUtil.success(currentUser);
    }

    @PostMapping("/login")
    public BaseResponse<User> userLogin(@RequestBody UserLoginRequest userLoginRequest, HttpServletRequest request){
        if(userLoginRequest == null)
            throw new BusinessException(StatusCode.PARAMS_NULL);
        //  验证数据的合法性
        String userAccount = userLoginRequest.getUserAccount();
        String password = userLoginRequest.getPassword();
        if(StringUtils.isAnyBlank(userAccount,password))
            throw new BusinessException(StatusCode.PARAMS_NULL);
        //  传入数据到 service 层
        User loginUser = userService.loginUser(userAccount, password, request);
        return ResultUtil.success(loginUser);
    }

    @PostMapping("/update")
    public BaseResponse<Boolean> updateUser(@RequestBody UserUpdateRequest updateUser, HttpServletRequest request){
        if(request==null || updateUser==null || updateUser.getId()==null)
            throw new BusinessException(StatusCode.PARAMS_NULL);
        if(updateUser.getId() <= 0)
            throw new BusinessException(StatusCode.PARAMS_ERROR);
        return ResultUtil.success(userService.updateUser(updateUser,request));
    }

    @GetMapping ("/recommend")
    public BaseResponse<UserPage> searchRecommendUser(Integer pageNum, Integer pageSize, HttpServletRequest request){
        if(pageNum==null || pageSize==null)
            throw new BusinessException(StatusCode.PARAMS_ERROR);
        return ResultUtil.success(userService.searchRecommendUser(pageNum, pageSize, request));
    }

    @GetMapping ("/search/tags")
    public BaseResponse<UserPage> searchUserByTags(@RequestParam(required = false)List<String> tags,
                                                   Integer pageNum, Integer pageSize){
        if(pageNum==null || pageSize==null)
            throw new BusinessException(StatusCode.PARAMS_ERROR);
        return ResultUtil.success(userService.searchUserByTags(tags,pageNum,pageSize));
    }

    @GetMapping ("/search/multi")
    public BaseResponse<UserPage> searchUserByInfo(@RequestParam(required = false)String text,
                                                   Integer pageNum, Integer pageSize){
        if(pageNum==null || pageSize==null)
            throw new BusinessException(StatusCode.PARAMS_ERROR);
        return ResultUtil.success(userService.searchUserByInfo(text,pageNum,pageSize));
    }

    @GetMapping("/tag/show/init")
    private BaseResponse<List<Tag>> showTag(){
        return ResultUtil.success(userService.searchTagsOnInit());
    }

    @GetMapping("/search/show/tag")
    private BaseResponse<List<Tag>> searchSubTagByTagName(@RequestParam(required = false)String tagName){
        return ResultUtil.success(userService.searchSubTagsByTag(tagName));
    }

    @DeleteMapping("/delete")
    public BaseResponse<Boolean> deleteUserById(@RequestBody Map<String,Object> map,HttpServletRequest request){
        if(!userService.isAdmin(userService.getCurrentUser(request)))
            throw new BusinessException(StatusCode.NO_AUTH);
        Long id = (Long) map.get("id");
        if(id == null || id < 1)
            throw new BusinessException(StatusCode.PARAMS_ERROR);
        boolean resultBool = userService.removeById(id);
        return ResultUtil.success(resultBool);
    }
}