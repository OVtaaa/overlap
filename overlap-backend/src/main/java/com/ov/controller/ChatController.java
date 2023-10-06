package com.ov.controller;

import com.ov.common.BaseResponse;
import com.ov.common.ResultUtil;
import com.ov.common.exception.BusinessException;
import com.ov.pojo.enums.MessageTypeStatusEnums;
import com.ov.pojo.enums.StatusCode;
import com.ov.pojo.User;
import com.ov.pojo.enums.ChatHttpTypeStatusEnums;
import com.ov.pojo.request.*;
import com.ov.pojo.vo.ChatCommonVO;
import com.ov.pojo.vo.MessagePage;
import com.ov.service.ChatService;
import com.ov.service.MessageService;
import com.ov.service.UserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/chat")
public class ChatController {

    @Autowired
    private MessageService messageService;
    @Autowired
    private UserService userService;
    @Autowired
    private ChatService chatService;

    @GetMapping("/list")
    public BaseResponse<List<ChatCommonVO>> queryChatList(HttpServletRequest request){
        User curUser = userService.getCurrentUser(request);
        if(curUser == null)
            throw new BusinessException(StatusCode.NO_LOGIN);
        Long id = curUser.getId();
        if(id == null || id <= 0)
            throw new BusinessException(StatusCode.NO_AUTH);
        return ResultUtil.success(chatService.getChatList(id));
    }

//    @GetMapping("/all/unRead")
//    public BaseResponse<Long> queryAllUnRead(Long id){
//        if(id == null)
//            throw new BusinessException(StatusCode.PARAMS_NULL);
//        if(id <= 0)
//            throw new BusinessException(StatusCode.PARAMS_ERROR);
//        return ResultUtil.success(chatService.getAllUnRead(id));
//    }

    @PostMapping("/query")
    public BaseResponse<MessagePage> queryMessage(@RequestBody ChatQueryRequest chatQueryRequest, HttpServletRequest request){
        if(chatQueryRequest == null || request == null)
            throw new BusinessException(StatusCode.PARAMS_NULL);
        User curUser = userService.getCurrentUser(request);
        Long pageNum = chatQueryRequest.getPageNum(),
                pageSize = chatQueryRequest.getPageSize(), to = chatQueryRequest.getTo();
        Integer type = chatQueryRequest.getType();
        if(curUser == null)
            throw new BusinessException(StatusCode.NO_LOGIN);
        if(pageNum == null || pageSize == null || to == null || to <= 0 || pageNum <= 0 || pageSize <= 0)
            throw new BusinessException(StatusCode.PARAMS_ERROR);
        if(curUser.getId() == null || curUser.getId() <= 0)
            throw new BusinessException(StatusCode.NO_AUTH);
        if(MessageTypeStatusEnums.getStatus(type) == null)
            throw new BusinessException(StatusCode.PARAMS_ERROR);
        return ResultUtil.success(messageService.getMessage(to,type,pageNum,pageSize,curUser));
    }

//    @PostMapping("/send/common")
//    public BaseResponse<Boolean> saveCommonMessage(@RequestBody ChatSendCommonRequest chatSendCommonRequest, HttpServletRequest request){
//        if(chatSendCommonRequest == null || request == null)
//            throw new BusinessException(StatusCode.PARAMS_NULL);
//        User curUser = userService.getCurrentUser(request);
//        if(curUser == null)
//            throw new BusinessException(StatusCode.NO_LOGIN);
//        Long fromId = curUser.getId(), toId = chatSendCommonRequest.getToId();
//        String content = chatSendCommonRequest.getContent(), avatarUrl = chatSendCommonRequest.getAvatarUrl();
//        Integer type = chatSendCommonRequest.getType();
//        if(fromId == null || toId == null || StringUtils.isAnyBlank(content, avatarUrl))
//            throw new BusinessException(StatusCode.PARAMS_NULL);
//        if(fromId <= 0 || toId <= 0 || MessageTypeStatusEnums.getStatus(type) == null)
//            throw new BusinessException(StatusCode.PARAMS_ERROR);
//        return ResultUtil.success(chatService.saveCommonMessage(fromId,toId,content,avatarUrl,type));
//    }

//    @PostMapping("/send/system/coterie")
//    public BaseResponse<Boolean> saveCoterieSystemMessage(@RequestBody ChatSendCoterieSystemRequest systemRequest){
//        if(systemRequest == null)
//            throw new BusinessException(StatusCode.PARAMS_NULL);
//        Long toId = systemRequest.getToId(), coterieId = systemRequest.getCoterieId(),userId = systemRequest.getUserId();
//        Integer type = systemRequest.getType();
//        if(toId == null || coterieId == null || userId == null)
//            throw new BusinessException(StatusCode.PARAMS_NULL);
//        if(toId <= 0 || coterieId <= 0 || userId <= 0 || MessageTypeStatusEnums.getStatus(type) == null)
//            throw new BusinessException(StatusCode.PARAMS_ERROR);
//        return ResultUtil.success(chatService.saveCoterieSystemMessage(toId,coterieId,userId,type));
//    }

    @DeleteMapping("/remove")
    public BaseResponse<Boolean> removeChat(ChatRemoveRequest chatRemoveRequest, HttpServletRequest request){
        if(chatRemoveRequest == null || request == null)
            throw new BusinessException(StatusCode.PARAMS_NULL);
        User curUser = userService.getCurrentUser(request);
        Long id = chatRemoveRequest.getId();
        Integer type = chatRemoveRequest.getType();
        if(curUser == null)
            throw new BusinessException(StatusCode.NO_LOGIN);
        if(id == null || id <= 0 || ChatHttpTypeStatusEnums.getStatus(type) == null)
            throw new BusinessException(StatusCode.PARAMS_ERROR);
        return ResultUtil.success(chatService.removeChat(id,curUser.getId(),type));
    }

    @PostMapping("/join/coterie")
    public BaseResponse<Boolean> joinCoterie(@RequestBody CoterieJoinRequest coterieJoinRequest, HttpServletRequest request){
        if(coterieJoinRequest == null || request == null)
            throw new BusinessException(StatusCode.PARAMS_NULL);
        Long coterieId = coterieJoinRequest.getCoterieId()
                , userId = coterieJoinRequest.getUserId(), messageId = coterieJoinRequest.getMessageId();
        User curUser = userService.getCurrentUser(request);
        if(curUser == null)
            throw new BusinessException(StatusCode.NO_LOGIN);
        if(coterieId == null || userId == null || messageId == null)
            throw new BusinessException(StatusCode.PARAMS_NULL);
        if(coterieId <= 0 || userId <= 0 || messageId <= 0)
            throw new BusinessException(StatusCode.PARAMS_ERROR);
        return ResultUtil.success(chatService.joinCoterie(coterieId,curUser,userId, messageId));
    }
}
