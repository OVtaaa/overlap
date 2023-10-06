package com.ov.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ov.common.exception.BusinessException;
import com.ov.constant.MessageConstant;
import com.ov.mapper.ChatMapper;
import com.ov.pojo.Message;
import com.ov.pojo.User;
import com.ov.pojo.enums.ChatHttpTypeStatusEnums;
import com.ov.pojo.enums.MessageTypeStatusEnums;
import com.ov.pojo.enums.StatusCode;
import com.ov.pojo.vo.MessagePage;
import com.ov.pojo.vo.MessageCommonVO;
import com.ov.service.MessageService;
import com.ov.mapper.MessageMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
* @author Ovta~
* @description 针对表【message】的数据库操作Service实现
* @createDate 2023-09-15 20:23:56
*/
@Service
public class MessageServiceImpl extends ServiceImpl<MessageMapper, Message>
    implements MessageService {

    @Override
    public MessagePage getMessage(Long to, Integer type, Long pageNum, Long pageSize, User curUser) {
        LambdaQueryWrapper<Message> lqw = new LambdaQueryWrapper<>();
        Long fromId = curUser.getId();
        if(type.equals(MessageTypeStatusEnums.SINGLE_CHAT.getKey())) {
            //  单聊
            lqw.eq(Message::getType,type)
                    .and(andWrapper ->andWrapper
                            .or(wrapper -> wrapper.eq(Message::getFromId, fromId).eq(Message::getToId, to))
                            .or(wrapper -> wrapper.eq(Message::getFromId, to).eq(Message::getToId, fromId)));
        }else if(type.equals(MessageTypeStatusEnums.GROUP_CHAT.getKey())){
            //  群聊
            lqw.eq(Message::getType,type).eq(Message::getCoterieId,to);
        }else {
            lqw.ne(Message::getType,MessageTypeStatusEnums.SINGLE_CHAT.getKey())
                    .ne(Message::getType,MessageTypeStatusEnums.GROUP_CHAT.getKey())
                    .and(warpper -> warpper.eq(Message::getToId,to).or().eq(Message::getFromId,to));
        }
        lqw.orderByDesc(Message::getCreateTime);
        Page<Message> page = page(Page.of(pageNum, pageSize), lqw);
        Collections.reverse(page.getRecords());
        return new MessagePage(page.getRecords().stream().map(this::toMessageCommonVo).collect(Collectors.toList()),
                page.getTotal(),page.getSize(),page.getCurrent());
    }

    @Override
    public Message getLatestMessage(Long fromId, Long toId, Integer type) {
        LambdaQueryWrapper<Message> lqw = new LambdaQueryWrapper<>();
        if(type.equals(MessageTypeStatusEnums.SINGLE_CHAT.getKey())) {
            lqw.eq(Message::getType, type).and(andWrapper -> andWrapper
                    .or(wrapper -> wrapper.eq(Message::getFromId, fromId).eq(Message::getToId, toId))
                    .or(wrapper -> wrapper.eq(Message::getFromId, toId).eq(Message::getToId, fromId)));
        }else if(type.equals(MessageTypeStatusEnums.GROUP_CHAT.getKey())){
            lqw.eq(Message::getType,type)
                    .eq(Message::getCoterieId,toId);
        }else{
            lqw.ne(Message::getType,MessageTypeStatusEnums.SINGLE_CHAT.getKey())
                    .ne(Message::getType,MessageTypeStatusEnums.GROUP_CHAT.getKey())
                    .and(wrapper -> wrapper.eq(Message::getFromId,fromId).or().eq(Message::getToId,toId));
        }
        lqw.orderByDesc(Message::getCreateTime).last("limit 1");
        return getOne(lqw);
    }

    @Override
    public Message getJoinCoterieLatestMessage(Long userId, Long coterieId) {
        if(userId == null || coterieId == null)
            throw new BusinessException(StatusCode.PARAMS_ERROR);
        //  获取 userId 加入 coterieId 申请加入的最新消息
        LambdaQueryWrapper<Message> lqw = new LambdaQueryWrapper<>();
        lqw.eq(Message::getFromId,userId).eq(Message::getCoterieId,coterieId)
                .eq(Message::getType,MessageTypeStatusEnums.COTERIE_JOIN_REQUEST.getKey())
                .orderByDesc(Message::getCreateTime).last("limit 1");
        List<Message> list = list(lqw);
        return list.get(0);
    }

//    @Override
//    public Long getUnRead(Long fromId, Long toId, Integer type) {
//        LambdaQueryWrapper<Message> lqw = new LambdaQueryWrapper<>();
//        lqw.eq(Message::getIsRead, MessageConstant.UN_READ);
//        if(MessageTypeStatusEnums.SINGLE_CHAT.getKey().equals(type)){
//            lqw.eq(Message::getType,type).and(andWrapper -> andWrapper
//                    .or(wrapper -> wrapper.eq(Message::getFromId, fromId).eq(Message::getToId, toId))
//                    .or(wrapper -> wrapper.eq(Message::getFromId, toId).eq(Message::getToId, fromId)));
//        }else if(MessageTypeStatusEnums.GROUP_CHAT.getKey().equals(type)){
//            lqw.eq(Message::getType,type)
//                    .and(wrapper -> wrapper.eq(Message::getFromId,fromId).or().eq(Message::getToId,toId));
//        }else {
//            lqw.eq(Message::getToId,toId).eq(Message::getFromId,fromId);
//        }
//        return count(lqw);
//    }

    private MessageCommonVO toMessageCommonVo(Message message){
        MessageCommonVO commonVO = new MessageCommonVO();
        BeanUtils.copyProperties(message,commonVO);
        return commonVO;
    }
}




