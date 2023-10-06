package com.ov.pojo.handle;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MessageForSend implements Serializable {

    String toId;
    String fromId;
    String coterieId;
    String userId;
    String username;

    String content;
    String createTime;
    String chatType;
    String messageType;
    String avatarUrl;
    String title;

    @Serial
    private static final long serialVersionUID = -3826954180993891499L;
}
