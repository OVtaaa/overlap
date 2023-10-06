package com.ov.controller;

import com.ov.common.BaseResponse;
import com.ov.pojo.request.CoterieJoinRequest;
import com.ov.pojo.vo.CoteriePage;
import com.ov.common.ResultUtil;
import com.ov.common.exception.BusinessException;
import com.ov.pojo.enums.StatusCode;
import com.ov.pojo.User;
import com.ov.pojo.request.CoterieAddRequest;
import com.ov.pojo.request.CoterieCheckJoinRequest;
import com.ov.pojo.request.CoterieUpdateRequest;
import com.ov.service.CoterieService;
import com.ov.service.UserCoterieService;
import com.ov.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/coterie")
public class CoterieController {

    @Autowired
    private CoterieService coterieService;
    @Autowired
    private UserCoterieService userCoterieService;
    @Autowired
    private UserService userService;

    @PostMapping("/add")
    public BaseResponse<Boolean> addCoterie(@RequestBody CoterieAddRequest addCoterie, HttpServletRequest request){
        if(addCoterie == null)
            throw new BusinessException(StatusCode.PARAMS_NULL);
        return ResultUtil.success(coterieService.addCoterie(addCoterie,request));
    }

    @PostMapping("/update")
    public BaseResponse<Boolean> updateCoterie(@RequestBody CoterieUpdateRequest updateCoterie, HttpServletRequest request){
        if(updateCoterie == null)
            throw new BusinessException(StatusCode.PARAMS_NULL);
        return ResultUtil.success(coterieService.updateCoterie(updateCoterie, request));
    }

    @GetMapping("/cur")
    public BaseResponse<CoteriePage> searchCoterieByCurrentUser(Long pageNum, Long pageSize, HttpServletRequest request){
        if(pageNum==null || pageSize==null)
            throw new BusinessException(StatusCode.PARAMS_NULL);
        return ResultUtil.success(coterieService.getCurUserCoterie(pageNum,pageSize,request));
    }

    @GetMapping("/recommend")
    public BaseResponse<CoteriePage> searchRecommendCoterie(Long pageNum,Long pageSize, HttpServletRequest request){
        if(pageNum==null || pageSize==null || request==null)
            throw new BusinessException(StatusCode.PARAMS_NULL);
        return ResultUtil.success(coterieService.getRecommendCoterie(pageNum,pageSize,request));
    }

    @GetMapping("/search/mul")
    public BaseResponse<CoteriePage> searchCoterieByText(Long pageNum,Long pageSize, String text){
        if(pageNum==null || pageSize==null || text==null)
            throw new BusinessException(StatusCode.PARAMS_NULL);
        if(text.length() > 50)
            throw new BusinessException(StatusCode.PARAMS_ERROR);
        return ResultUtil.success(coterieService.getSearchCoterie(text,pageNum,pageSize));
    }

    @PostMapping("/join/check")
    public BaseResponse<Boolean> CheckJoinCoterie(@RequestBody CoterieCheckJoinRequest coterieCheckJoinRequest, HttpServletRequest request){
        if(coterieCheckJoinRequest == null || request == null)
            throw new BusinessException(StatusCode.PARAMS_NULL);
        Long coterieId = coterieCheckJoinRequest.getCoterieId();
        String password = coterieCheckJoinRequest.getPassword();
        if(coterieId == null || coterieId <= 0)
            throw new BusinessException(StatusCode.PARAMS_ERROR);
        User curUser = userService.getCurrentUser(request);
        if(curUser == null || curUser.getId() <= 0)
            throw new BusinessException(StatusCode.NO_LOGIN);
        return ResultUtil.success(coterieService.checkJoinCoterie(coterieId,curUser,password));
    }


    @PostMapping("/exit")
    public BaseResponse<Boolean> exitCoterie(@RequestParam Long coterieId,HttpServletRequest request){
        if(coterieId == null || coterieId <= 0 || request == null)
            throw new BusinessException(StatusCode.PARAMS_ERROR);
        User curUser = userService.getCurrentUser(request);
        if(curUser == null || curUser.getId() <= 0)
            throw new BusinessException(StatusCode.NO_LOGIN);
        return ResultUtil.success(coterieService.exitCoterie(coterieId,curUser));
    }
}
