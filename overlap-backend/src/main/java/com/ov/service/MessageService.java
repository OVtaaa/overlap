package com.ov.service;

import com.ov.pojo.Message;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ov.pojo.User;
import com.ov.pojo.vo.MessagePage;

/**
* @author Ovta~
* @description 针对表【message】的数据库操作Service
* @createDate 2023-09-15 20:23:56
*/
public interface MessageService extends IService<Message> {

    MessagePage getMessage(Long to, Integer type, Long pageNum, Long pageSize, User curUser);

    Message getLatestMessage(Long fromId, Long toId, Integer type);

    Message getJoinCoterieLatestMessage(Long userId, Long coterieId);

//    Long getUnRead(Long fromId, Long toId, Integer type);

}
