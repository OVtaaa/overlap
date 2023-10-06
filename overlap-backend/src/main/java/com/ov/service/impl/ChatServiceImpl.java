package com.ov.service.impl;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ov.common.exception.BusinessException;
import com.ov.mapper.MessageMapper;
import com.ov.pojo.enums.MessageTypeStatusEnums;
import com.ov.pojo.enums.StatusCode;
import com.ov.pojo.Coterie;
import com.ov.pojo.Message;
import com.ov.pojo.enums.ChatHttpTypeStatusEnums;
import com.ov.pojo.vo.UserCommonVO;
import com.ov.service.CoterieService;
import com.ov.service.MessageService;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ov.pojo.Chat;
import com.ov.pojo.User;
import com.ov.pojo.vo.ChatCommonVO;
import com.ov.service.ChatService;
import com.ov.mapper.ChatMapper;
import com.ov.service.UserService;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.ov.constant.CoterieConstant.UPDATE_COTERIE_KEY_PRE;
import static com.ov.constant.MessageConstant.*;

/**
* @author Ovta~
* @description 针对表【chat】的数据库操作Service实现
* @createDate 2023-09-20 20:42:46
*/
@Service
public class ChatServiceImpl extends ServiceImpl<ChatMapper, Chat>
    implements ChatService{

    @Autowired
    private UserService userService;
    @Autowired
    private MessageService messageService;
    @Autowired
    private CoterieService coterieService;
    @Resource
    private ChatMapper chatMapper;
    @Autowired
    private RedissonClient redissonClient;

    @Override
    public List<ChatCommonVO> getChatList(Long id) {
        List<ChatCommonVO> resList = new ArrayList<>();
        //  根据 id 搜索聊天对象的具体信息（单聊）
        List<Long> singleChatListId = chatMapper.getChatListIdByType(id, ChatHttpTypeStatusEnums.SINGLE_CHAT_TYPE.getKey());
        for(Long toId : singleChatListId){
            User toUser = userService.getById(toId);
            ChatCommonVO commonVO = new ChatCommonVO();
            commonVO.setId(toId);
            commonVO.setAvatarUrl(toUser.getAvatarUrl());
            commonVO.setTitle(toUser.getUsername());
            commonVO.setType(ChatHttpTypeStatusEnums.SINGLE_CHAT_TYPE.getKey());

            Message latestMessage = messageService.getLatestMessage(id, toId, MessageTypeStatusEnums.SINGLE_CHAT.getKey());
            if(latestMessage != null) {
                commonVO.setLatestNews(latestMessage.getContent());
                commonVO.setLatestTime(latestMessage.getCreateTime());
            }
//            commonVO.setUnRead(messageService.getUnRead(id,toId,MessageTypeStatusEnums.SINGLE_CHAT.getKey()));
            commonVO.setUnRead(0L);
            resList.add(commonVO);
        }
        //  圈子聊天列表
        List<Long> groupChatListId = chatMapper.getChatListIdByType(id,ChatHttpTypeStatusEnums.GROUP_CHAT_TYPE.getKey());
        for(Long toId : groupChatListId){
            Coterie toCoterie = coterieService.getById(toId);
            ChatCommonVO commonVO = new ChatCommonVO();
            commonVO.setId(toId);
            commonVO.setAvatarUrl(toCoterie.getAvatarUrl());
            commonVO.setTitle(toCoterie.getTitle() + "（" + toCoterie.getCurNum() + "）");
            commonVO.setType(ChatHttpTypeStatusEnums.GROUP_CHAT_TYPE.getKey());

            Message latestMessage = messageService.getLatestMessage(id, toId, MessageTypeStatusEnums.GROUP_CHAT.getKey());
            if(latestMessage != null) {
                commonVO.setLatestNews(latestMessage.getContent());
                commonVO.setLatestTime(latestMessage.getCreateTime());
            }
            //  群聊未读暂不考虑
            commonVO.setUnRead(0L);
            resList.add(commonVO);
        }
        //  系统信息
        List<Long> systemChatList = chatMapper.getChatListIdByType(id, ChatHttpTypeStatusEnums.SYSTEM.getKey());
        if(systemChatList.size() > 0){
            ChatCommonVO commonVO = new ChatCommonVO();
            commonVO.setId(id);
            commonVO.setAvatarUrl(ROBOT_AVATAR_URL);
            commonVO.setTitle(SYSTEM_TITLE);
            commonVO.setType(ChatHttpTypeStatusEnums.SYSTEM.getKey());

            Message latestMessage = messageService.getLatestMessage(id, id, MessageTypeStatusEnums.COTERIE_JOIN_REQUEST.getKey());
            if(latestMessage != null) {
                commonVO.setLatestNews(latestMessage.getContent());
                commonVO.setLatestTime(latestMessage.getCreateTime());
            }
            commonVO.setUnRead(0L);
            resList.add(commonVO);
        }

        //  按最新数据排序
        resList.sort(new Comparator<ChatCommonVO>() {
            @Override
            public int compare(ChatCommonVO o1, ChatCommonVO o2) {
                Date o1Time = o1.getLatestTime(), o2Time = o2.getLatestTime();
                if(o1Time == null && o2Time == null) {
                    return o1.getTitle().compareTo(o2.getTitle());
                }else if(o1Time == null || o2Time == null){
                    return o1Time == null ? 1 : -1;
                }
                return o2Time.after(o1Time) ? 1 : -1;
            }
        });
        return resList;
    }

    @Override
    public Boolean removeChat(Long id, Long curId, Integer type) {
        LambdaQueryWrapper<Chat> lqw = new LambdaQueryWrapper<>();
        lqw.eq(Chat::getType, type).and(andWrapper -> andWrapper
                .or(wrapper -> wrapper.eq(Chat::getSide1, id).eq(Chat::getSide2, curId))
                .or(wrapper -> wrapper.eq(Chat::getSide2, id).eq(Chat::getSide1, curId)));
        return remove(lqw);
    }

    @Override
    @Transactional
    public Boolean saveCommonMessage(Long fromId, Long toId, String username, String content, String avatarUrl, Integer type) {
        if(fromId==null || toId==null || MessageTypeStatusEnums.getStatus(type)==null)
            throw new BusinessException(StatusCode.PARAMS_ERROR);
        //  判断聊天列表是否存在，若不存在则添加
        addCommonChatIfAbsent(fromId, toId, type);
        //  群聊对其他人也要判断聊天列表是否存在
        if(MessageTypeStatusEnums.GROUP_CHAT.getKey().equals(type)) {
            List<Long> userIdList = coterieService.getUserIdByCoterie(toId);
            for(Long userId : userIdList){
                addCommonChatIfAbsent(userId,toId,type);
            }
        }

        Message message = new Message();
        message.setFromId(fromId);
        if(MessageTypeStatusEnums.GROUP_CHAT.getKey().equals(type))
            message.setCoterieId(toId);
        if(MessageTypeStatusEnums.SINGLE_CHAT.getKey().equals(type))
            message.setToId(toId);
        message.setUsername(username);
        message.setContent(content);
        message.setAvatarUrl(avatarUrl);
        message.setType(type);
        return messageService.save(message);
    }

    @Override
    @Transactional
    public Boolean saveCoterieSystemMessage(Long toId, Long coterieId, Long userId, Integer messageType) {
        if(toId == null || coterieId == null || userId == null || messageType == null)
            throw new BusinessException(StatusCode.PARAMS_NULL);
        //  判断系统聊天是否存在，若不存在则添加
        Integer chatType = MessageTypeStatusEnums.toChatType(messageType).getKey();
        addSystemChatIfAbsent(toId, chatType);
        addSystemChatIfAbsent(userId, chatType);

        User toUser = userService.getById(userId);
        Coterie toCoterie = coterieService.getById(coterieId);
        Message message = new Message();
        message.setFromId(userId);
        message.setToId(toId);
        message.setCoterieId(coterieId);
        message.setContent(MessageTypeStatusEnums.handleCoterieContent(toUser,toCoterie,messageType));
        message.setAvatarUrl(ROBOT_AVATAR_URL);
        message.setType(messageType);

        boolean saveFlag = messageService.save(message);
        // 若是加入成功消息
        if(saveFlag && MessageTypeStatusEnums.COTERIE_JOIN_SUCCESS.getKey().equals(messageType)){
            //  获取请求加入的消息，将其消息内容设为已同意
            Message joinMessage = messageService.getJoinCoterieLatestMessage(userId,coterieId);
            String content = joinMessage.getContent();
            String substring = content.substring(0, content.indexOf("handle="));
            joinMessage.setContent(substring + "handle=已同意");
            return messageService.updateById(joinMessage);
        }
        //  若是解散圈子，则删除相关对话框
        if(saveFlag && MessageTypeStatusEnums.COTERIE_DISSOLVE_NOTICE.getKey().equals(messageType)){
            LambdaQueryWrapper<Chat> lqw = new LambdaQueryWrapper<>();
            lqw.eq(Chat::getType,ChatHttpTypeStatusEnums.GROUP_CHAT_TYPE.getKey())
                    .and(andWrapper -> andWrapper.eq(Chat::getSide1,coterieId).or().eq(Chat::getSide2,coterieId));
            return remove(lqw);
        }
        //  若是退出圈子，则删除相关对话框
        if(saveFlag && MessageTypeStatusEnums.COTERIE_EXIT_NOTICE.getKey().equals(messageType)){
            LambdaQueryWrapper<Chat> lqw = new LambdaQueryWrapper<>();
            lqw.eq(Chat::getType,ChatHttpTypeStatusEnums.GROUP_CHAT_TYPE.getKey())
                    .eq(Chat::getSide1,userId).eq(Chat::getSide2,coterieId);
            return remove(lqw);
        }
        return saveFlag;
    }

    private void addSystemChatIfAbsent(Long toId, Integer chatType) {
        List<Long> chatListIdByType = chatMapper.getChatListIdByType(toId, chatType);
        if(chatListIdByType.size() == 0){
            Chat chat = new Chat();
            chat.setSide1(toId);
            chat.setSide2(toId);
            chat.setType(chatType);
            boolean saveChat = save(chat);
            if(!saveChat)
                throw new BusinessException(StatusCode.DB_ERROR);
        }
    }

    private void addCommonChatIfAbsent(Long fromId, Long toId, Integer type) {
        Integer chatType = MessageTypeStatusEnums.toChatType(type).getKey();
        List<Long> existChat = chatMapper.getChatListIdByType(fromId, chatType);
        if(!existChat.contains(toId)){
            Chat chat = new Chat();
            chat.setSide1(fromId);
            chat.setSide2(toId);
            chat.setType(chatType);
            boolean saveChat = save(chat);
            if(!saveChat)
                throw new BusinessException(StatusCode.DB_ERROR);
        }
    }

    @Override
    @Transactional
    public Boolean joinCoterie(Long coterieId, User curUser, Long userId, Long messageId) {
        RLock lock = redissonClient.getLock(UPDATE_COTERIE_KEY_PRE+coterieId);
        try {
            boolean getLock = lock.tryLock(30, TimeUnit.SECONDS);
            if(getLock) {
                //  判断当前审核的是否为圈主 或是 管理员
                Coterie coterie = coterieService.getCoterie(coterieId);
                if(!coterie.getUserId().equals(curUser.getId()) && !userService.isAdmin(curUser))
                    throw new BusinessException(StatusCode.NO_AUTH);
                
                coterieService.checkJoinNoPassword(coterie, userId);
                boolean flag = coterieService.executeJoinCoterie(userId,coterie);
                if (flag)
                    addCommonChatIfAbsent(userId,coterieId,MessageTypeStatusEnums.GROUP_CHAT.getKey());
                return flag;
            }
        } catch (InterruptedException e) {
            log.error(e.getMessage());
            throw new BusinessException(StatusCode.DB_ERROR,"加入圈子失败");
        } finally {
            if(lock.isHeldByCurrentThread())
                lock.unlock();
        }
        return false;
    }

//    @Override
//    public Long getAllUnRead(Long id) {
//        LambdaQueryWrapper<Message> lqw = new LambdaQueryWrapper<>();
//        //  群聊未读不考虑
//        lqw.eq(Message::getIsRead,UN_READ).ne(Message::getType,MessageTypeStatusEnums.GROUP_CHAT.getKey())
//                .and(wrapper -> wrapper.eq(Message::getToId,id).or().eq(Message::getFromId,id));
//        return messageService.count(lqw);
//    }
}




