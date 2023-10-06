package com.ov.pojo.enums;

public enum CoterieStatusEnums {

    PUBLIC(0,"公开"),
    PRIVATE(1,"私密");

    private Integer key;
    private String status;

    public static CoterieStatusEnums getStatus(Integer key){
        if(key == null)
            return null;
        for(CoterieStatusEnums enums : values()){
            if(enums.key.equals(key))
                return enums;
        }
        return null;
    }

    CoterieStatusEnums(Integer key, String status) {
        this.key = key;
        this.status = status;
    }

    public Integer getKey() {
        return key;
    }

    public void setKey(Integer key) {
        this.key = key;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
