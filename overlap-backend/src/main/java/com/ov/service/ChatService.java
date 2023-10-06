package com.ov.service;

import com.ov.pojo.Chat;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ov.pojo.User;
import com.ov.pojo.vo.ChatCommonVO;

import java.util.List;

/**
* @author Ovta~
* @description 针对表【chat】的数据库操作Service
* @createDate 2023-09-20 20:42:46
*/
public interface ChatService extends IService<Chat> {

    /**
     * 根据 id 查询与之聊天的列表
     * @param id 查询的 id
     * @return 聊天列表
     */
    List<ChatCommonVO> getChatList(Long id);

    /**
     * 按照给出的 id 删除聊天对话
     * @param id 目标聊天 id
     * @param curId 己方 id
     * @param type 聊天类型
     * @return 是否删除成功
     */
    Boolean removeChat(Long id, Long curId, Integer type);

    /**
     * 保存信息
     * @param fromId 信息发送方
     * @param toId  信息接收方
     * @param username 信息发送方昵称
     * @param content   信息内容
     * @param avatarUrl 头像地址
     * @param type  信息类型
     * @return  是否保存成功
     */
    Boolean saveCommonMessage(Long fromId, Long toId, String username, String content, String avatarUrl, Integer type);

    /**
     *  保存圈子相关的系统信息
     * @param toId  向 toId 的用户发送系统信息
     * @param coterieId 圈子
     * @param userId  相关的用户 id
     * @param messageType  信息类型
     * @return  是否保存成功
     */
    Boolean saveCoterieSystemMessage(Long toId, Long coterieId, Long userId, Integer messageType);

    /**
     * 将 userId 该名用户加入 coterieId 这个圈子
     * @param coterieId 要加入的圈子
     * @param curUser   审核者
     * @param userId  改名用户
     * @return  是否加入成功
     */
    Boolean joinCoterie(Long coterieId, User curUser, Long userId, Long messageId);

//    /**
//     * 根据 id 查询所有未读信息条数
//     * @param id    用户 id
//     * @return  未读信息总条数
//     */
//    Long getAllUnRead(Long id);
}
