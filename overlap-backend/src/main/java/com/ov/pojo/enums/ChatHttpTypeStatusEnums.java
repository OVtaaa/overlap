package com.ov.pojo.enums;


public enum ChatHttpTypeStatusEnums {

    SINGLE_CHAT_TYPE(0,"单聊"),
    GROUP_CHAT_TYPE(1,"群聊"),
    SYSTEM(2,"系统消息");

    private Integer key;
    private String value;

    public static ChatHttpTypeStatusEnums getStatus(Integer key){
        for(ChatHttpTypeStatusEnums enums : values()){
            if(enums.getKey().equals(key))
                return enums;
        }
        return null;
    }

    ChatHttpTypeStatusEnums(Integer key, String value) {
        this.key = key;
        this.value = value;
    }

    ChatHttpTypeStatusEnums() {
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
