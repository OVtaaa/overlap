package com.ov.config.netty;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ov.common.exception.BusinessException;
import com.ov.pojo.Coterie;
import com.ov.pojo.User;
import com.ov.pojo.enums.ChatWebTypeStatusEnums;
import com.ov.pojo.enums.MessageTypeStatusEnums;
import com.ov.pojo.enums.StatusCode;
import com.ov.pojo.handle.MessageForSend;
import com.ov.pojo.vo.UserCommonVO;
import com.ov.service.ChatService;
import com.ov.service.CoterieService;
import com.ov.service.UserService;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDateTime;
import java.util.*;

import static com.ov.constant.MessageConstant.ROBOT_AVATAR_URL;
import static com.ov.constant.MessageConstant.SYSTEM_TITLE;

/**
 * Handles a server-side channel.
 */
@Configuration
@ChannelHandler.Sharable
public class WebServerHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {

    private Map<String, Channel> channelMap = new HashMap<>();
//    private final String singleChatPre = "user";
    //  使用时：groupChatPre + toId + fromId（toId 为圈子 id）
//    private final String groupChatPre = "coterie";
//    private final String messagePageChatPre = "messagePage";
    private final String webPageChatPre = "webPage";

    @Autowired
    private CoterieService coterieService;
    @Autowired
    private ChatService chatService;
    @Autowired
    private UserService userService;

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println(new Date() + "与客户端："+ctx.channel().remoteAddress() + "建立连接");
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        System.out.println(new Date() + "与客户端："+ctx.channel().remoteAddress() + "断开连接");
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, TextWebSocketFrame msg) throws Exception {
        Channel channel = channelHandlerContext.channel();
        System.out.println(LocalDateTime.now() + channel.remoteAddress().toString()+"发送了消息："+msg.text());
        //  解析消息
        Gson gson = new Gson();
        HashMap<String,Object> dataMap = gson.fromJson(msg.text()
                , new TypeToken<HashMap<String,Object>>() {}.getType());

        MessageForSend message = BeanUtil.mapToBean(dataMap, MessageForSend.class, true,
                CopyOptions.create().setFieldValueEditor((fieldName, fieldValue) -> {
                    if(fieldValue == null){
                        return "0";
                    }else
                        return fieldValue+"";
                }));
        String fromId = message.getFromId(), username = message.getUsername();
        Integer chatTypeInt = null, messageType = null;
        Long fromIdLong = null, toIdLong = null, coterieId = null, userId = null;
        try {
            chatTypeInt = Integer.parseInt(message.getChatType());
            if(message.getFromId() != null)
                fromIdLong = Long.parseLong(message.getFromId());
            if(message.getToId() != null)
                toIdLong = Long.parseLong(message.getToId());
            if(message.getMessageType() != null)
                messageType = Integer.parseInt(message.getMessageType());
            if(message.getCoterieId() != null)
                coterieId = Long.parseLong(message.getCoterieId());
            if(message.getUserId() != null)
                userId = Long.parseLong(message.getUserId());
        } catch (NumberFormatException e) {
            throw new BusinessException(StatusCode.PARAMS_ERROR);
        }

//        String type = (String) dataMap.getOrDefault("type","0");
//                toId = (String) dataMap.getOrDefault("toId","-1"),
//                fromId = (String) dataMap.getOrDefault("fromId","-1"),
//                content = (String) dataMap.getOrDefault("content",""),
//                createTime = (String) dataMap.getOrDefault("createTime","");


//        if(ChatWebTypeStatusEnums.GROUP_CHECK.getKey().equals(type)){
//            //  群聊首次访问
//            channelMap.put(groupChatPre + toId + fromId,channel);
//        }else if(ChatWebTypeStatusEnums.SINGLE_CHECK.getKey().equals(type)){
//            //  单聊首次访问
//            channelMap.put(singleChatPre + fromId,channel);
//        }
//        if(ChatWebTypeStatusEnums.MESSAGE_PAGE_CHECK.getKey().equals(type)){
//            //  消息页面首次访问
//            channelMap.put(messagePageChatPre + fromId,channel);
//        }
        if(ChatWebTypeStatusEnums.WEB_PAGE_CHECK.getKey().equals(chatTypeInt)){
            //  网页首次访问
            channelMap.put(webPageChatPre + fromId, channel);
        }else if (ChatWebTypeStatusEnums.SINGLE.getKey().equals(chatTypeInt)){
            //  单聊
            chatService.saveCommonMessage(fromIdLong,toIdLong,username,message.getContent(),
                    message.getAvatarUrl(),messageType);
            singleSend(message);
        }else if(ChatWebTypeStatusEnums.GROUP.getKey().equals(chatTypeInt)){
            //  群聊，此时 toId 为圈子 id
            chatService.saveCommonMessage(fromIdLong,toIdLong,username,message.getContent(),
                    message.getAvatarUrl(),messageType);
            groupSend(message);
        }else if(ChatWebTypeStatusEnums.SYSTEM.getKey().equals(chatTypeInt)){
            //  系统消息
            if(chatService.saveCoterieSystemMessage(toIdLong,coterieId,userId,messageType))
                systemSend(message);
        }
    }

    private void systemSend(MessageForSend message) {
        try {
            String userId = message.getUserId(), coterieId = message.getCoterieId(), messageType = message.getMessageType();
            if(StringUtils.isAnyBlank(userId,coterieId,messageType))
                throw new BusinessException(StatusCode.PARAMS_NULL);
            Long userIdLong = Long.parseLong(userId), coterieIdLong = Long.parseLong(coterieId);
            Integer messageTypeInt = Integer.parseInt(messageType);
            if(userIdLong <= 0 || coterieIdLong <= 0 || MessageTypeStatusEnums.getStatus(messageTypeInt) == null)
                throw new BusinessException(StatusCode.PARAMS_ERROR);
            User toUser = userService.getById(userIdLong);
            Coterie toCoterie = coterieService.getById(coterieIdLong);
            String content = MessageTypeStatusEnums.handleCoterieContent(toUser, toCoterie, messageTypeInt);

            message.setContent(content);
            message.setAvatarUrl(ROBOT_AVATAR_URL);
            message.setTitle(SYSTEM_TITLE);

            Map<String, Object> map = BeanUtil.beanToMap(message, new HashMap<>(), CopyOptions.create().ignoreNullValue());
            Gson gson = new Gson();

            // 若是解散圈子，则圈子所有成员都需要收到解散的系统信息
            if(String.valueOf(MessageTypeStatusEnums.COTERIE_DISSOLVE_NOTICE.getKey()).equals(message.getMessageType())){
                List<Long> userByCoterie = coterieService.getUserIdByCoterie(coterieIdLong);
                for(Long tempId : userByCoterie){
                    Channel webPageChannel = channelMap.getOrDefault(webPageChatPre + tempId, null);
                    if(webPageChannel != null)
                        webPageChannel.writeAndFlush(new TextWebSocketFrame(gson.toJson(map)));
                }
                return;
            }

            Channel toChannel = channelMap.getOrDefault(webPageChatPre + message.getToId(), null),
                    fromChannel = channelMap.getOrDefault(webPageChatPre + message.getUserId(), null);
            if(toChannel != null)
                toChannel.writeAndFlush(new TextWebSocketFrame(gson.toJson(map)));
            if(fromChannel != null)
                fromChannel.writeAndFlush(new TextWebSocketFrame(gson.toJson(map)));
        } catch (NumberFormatException e) {
            throw new BusinessException(StatusCode.PARAMS_ERROR);
        }
    }

    private void groupSend(MessageForSend fromMessage) {
        try {
            Long toIdLong = Long.parseLong(fromMessage.getToId());
            // 根据 toId 找出圈子所有人
            List<Long> userByCoterie = coterieService.getUserIdByCoterie(toIdLong);
            Gson gson = new Gson();
            Map<String, Object> map = BeanUtil.beanToMap(fromMessage, new HashMap<>(),
                    CopyOptions.create().setFieldValueEditor((fieldName, fieldValue) -> {
                        if (fieldValue == null) {
                            return "0";
                        } else
                            return fieldValue + "";
                    }));
            for(Long userId : userByCoterie){
//                Channel channel = channelMap.getOrDefault(groupChatPre + toIdLong + user.getId(), null);
//                Channel messageChannel = channelMap.getOrDefault(messagePageChatPre + user.getId(), null);
                Channel webPageChannel = channelMap.getOrDefault(webPageChatPre + userId, null);

//                if(channel != null)
//                    channel.writeAndFlush(new TextWebSocketFrame(gson.toJson(map)));

//                if(messageChannel != null)
//                    messageChannel.writeAndFlush(new TextWebSocketFrame(gson.toJson(map)));
                if(webPageChannel != null)
                    webPageChannel.writeAndFlush(new TextWebSocketFrame(gson.toJson(map)));
            }

        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
    }

    private void singleSend(MessageForSend message) {
        String toId = message.getToId(), fromId = message.getFromId();

//        Channel toChannel = channelMap.getOrDefault(singleChatPre + toId, null);
//        Channel fromChannel = channelMap.getOrDefault(singleChatPre + fromId, null);
        //  消息页面连接
//        Channel toMessageChannel = channelMap.getOrDefault(messagePageChatPre + toId, null);
//        Channel fromMessageChannel = channelMap.getOrDefault(messagePageChatPre + fromId, null);
        //  网页连接
        Channel toWebPageChannel = channelMap.getOrDefault(webPageChatPre + toId, null);
        Channel fromWebPageChannel = channelMap.getOrDefault(webPageChatPre + fromId, null);
        Gson gson = new Gson();

//        Map<String,Object> map = new HashMap<>();
//        map.put("toId",toId);
//        map.put("fromId",fromId);
//        map.put("content",content);
//        map.put("createTime",createTime);

        Map<String, Object> map = BeanUtil.beanToMap(message, new HashMap<>(), CopyOptions.create().ignoreNullValue());
//        if(toChannel == null) {
//            System.out.println("channel 未找到");
//        }else {
//            toChannel.writeAndFlush(new TextWebSocketFrame(gson.toJson(map)));
//        }
//        if(fromChannel == null){
//            System.out.println("channel 未找到");
//        }else {
//            fromChannel.writeAndFlush(new TextWebSocketFrame(gson.toJson(map)));
//        }
//        if(toMessageChannel != null)
//            toMessageChannel.writeAndFlush(new TextWebSocketFrame(gson.toJson(map)));
//        if(fromMessageChannel != null)
//            fromMessageChannel.writeAndFlush(new TextWebSocketFrame(gson.toJson(map)));
        if(toWebPageChannel != null)
            toWebPageChannel.writeAndFlush(new TextWebSocketFrame(gson.toJson(map)));
        if(fromWebPageChannel != null)
            fromWebPageChannel.writeAndFlush(new TextWebSocketFrame(gson.toJson(map)));
    }
}
