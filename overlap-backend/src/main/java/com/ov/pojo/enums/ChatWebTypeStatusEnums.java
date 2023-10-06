package com.ov.pojo.enums;

public enum ChatWebTypeStatusEnums {

    SINGLE_CHECK(-1,"单聊首次访问"),
    GROUP_CHECK(-2,"群聊首次访问"),
    MESSAGE_PAGE_CHECK(-3,"消息页面首次访问"),
    WEB_PAGE_CHECK(-4,"网页首次访问"),
    SINGLE(0,"单聊"),
    GROUP(1,"群聊"),
    SYSTEM(2,"系统");

    public ChatWebTypeStatusEnums getStatus(Integer key){
        for(ChatWebTypeStatusEnums enums : values()){
            if(enums.getKey().equals(key))
                return enums;
        }
        return null;
    }

    private Integer key;
    private String value;


    ChatWebTypeStatusEnums(Integer key, String value) {
        this.key = key;
        this.value = value;
    }

    ChatWebTypeStatusEnums() {
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
