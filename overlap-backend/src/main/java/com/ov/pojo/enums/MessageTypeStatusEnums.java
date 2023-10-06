package com.ov.pojo.enums;


import com.ov.pojo.Coterie;
import com.ov.pojo.User;

import java.util.ArrayList;
import java.util.List;

public enum MessageTypeStatusEnums {

    SINGLE_CHAT(0,"单聊"),
    GROUP_CHAT(1,"群聊"),
    COTERIE_JOIN_REQUEST(2,"加入圈子申请"),
    COTERIE_JOIN_SUCCESS(3,"加入圈子成功"),
    COTERIE_DISSOLVE_NOTICE(4,"解散圈子通知"),
    COTERIE_EXIT_NOTICE(5,"退出圈子通知");

    private Integer key;
    private String value;

    //  获取非系统消息 key 列表
    public static List<Integer> getNoSystemKey(){
        List<Integer> res = new ArrayList<>();
        for(MessageTypeStatusEnums enums : values()){
            if(!isSystemMessage(enums.getKey()))
                res.add(enums.getKey());
        }
        return res;
    }

    // 根据 type 对 content 处理
    public static String handleCoterieContent(User user, Coterie coterie, Integer type){
        if(!isSystemMessage(type))
            return "";
        if(COTERIE_JOIN_REQUEST.getKey().equals(type)) {
            return user.getUsername() + " 申请加入 " + coterie.getTitle()
                    + "?handle=未处理";
//                    + "?userId=" + user.getId() + "&coterieId=" + coterie.getId() + "&handle=未处理";
        }else if(COTERIE_JOIN_SUCCESS.getKey().equals(type)){
            return user.getUsername() + " 已成功加入 " + coterie.getTitle() + "小圈！";
        }else if(COTERIE_DISSOLVE_NOTICE.getKey().equals(type)){
            return coterie.getTitle() + "小圈已解散！";
        }else if(COTERIE_EXIT_NOTICE.getKey().equals(type)){
            return user.getUsername() + " 已退出 " + coterie.getTitle() + "小圈！";
        }
        return "";
    }

    //  根据 message type 对应上 chat type
    public static ChatHttpTypeStatusEnums toChatType(Integer type){
        if(SINGLE_CHAT.getKey().equals(type))
            return ChatHttpTypeStatusEnums.SINGLE_CHAT_TYPE;
        else if(GROUP_CHAT.getKey().equals(type))
            return ChatHttpTypeStatusEnums.GROUP_CHAT_TYPE;
        return ChatHttpTypeStatusEnums.SYSTEM;
    }

    //  判断是否为系统消息
    public static Boolean isSystemMessage(Integer type){
        return !SINGLE_CHAT.getKey().equals(type) && !GROUP_CHAT.getKey().equals(type);
    }

    public static MessageTypeStatusEnums getStatus(Integer key){
        for(MessageTypeStatusEnums enums : values()){
            if(enums.getKey().equals(key))
                return enums;
        }
        return null;
    }

    MessageTypeStatusEnums(Integer key, String value) {
        this.key = key;
        this.value = value;
    }

    MessageTypeStatusEnums() {
    }

    public Integer getKey() {
        return key;
    }

    public void setKey(Integer key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
