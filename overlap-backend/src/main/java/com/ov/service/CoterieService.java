package com.ov.service;

import com.ov.pojo.vo.CoteriePage;
import com.ov.pojo.Coterie;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ov.pojo.User;
import com.ov.pojo.request.CoterieAddRequest;
import com.ov.pojo.request.CoterieUpdateRequest;
import com.ov.pojo.vo.UserCommonVO;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
* @author Ovta~
* @description 针对表【coterie】的数据库操作Service
* @createDate 2023-08-22 18:46:22
*/
public interface CoterieService extends IService<Coterie> {

    /**
     * 添加圈子
     * @param addCoterie 创建的圈子属性
     * @return 是否添加成功
     */
    Boolean addCoterie(CoterieAddRequest addCoterie, HttpServletRequest request);

    /**
     * 获取当前登录用户加入的圈子列表
     * @return 圈子列表
     */
    CoteriePage getCurUserCoterie(Long pageNum, Long pageSize, HttpServletRequest request);

    /**
     * 获取推荐圈子列表
     * @param pageNum   当前页数
     * @param pageSize  页面大小
     * @param request   请求
     * @return 圈子列表
     */
    CoteriePage getRecommendCoterie(Long pageNum, Long pageSize, HttpServletRequest request);

    /**
     * 更新圈子
     * @param updateCoterie   要更新的圈子对象
     * @return 是否更新成功
     */
    Boolean updateCoterie(CoterieUpdateRequest updateCoterie, HttpServletRequest request);

    /**
     * 由搜索文本，从 圈号，标题，标签，描述 中搜索圈子
     * @param text 搜索文本
     * @param pageNum 当前页
     * @param pageSize 页面大小
     * @return 返回搜索到的圈子数据
     */
    CoteriePage getSearchCoterie(String text, Long pageNum, Long pageSize);

    /**
     * 当前用户加入指定圈子
     * @param coterieId   要加入的圈子 id
     * @param curUser   当前登录用户
     * @param password  加入的圈子密码
     * @return  是否加入成功
     */
    Boolean checkJoinCoterie(Long coterieId, User curUser,String password);

    /**
     * 当前用户退出指定圈子
     * @param coterieId   要退出的圈子 ID
     * @param curUser   当前登录用户
     * @return  是否退出成功
     */
    Boolean exitCoterie(Long coterieId, User curUser);

    /**
     * 根据圈子 id 找出加入的用户具体信息
     * @param coterieId 圈子 id
     * @return 加入的用户列表
     */
    List<UserCommonVO> getUserByCoterie(Long coterieId);

    /**
     * 根据圈子 id 找出加入的用户 id
     * @param coterieId 圈子 id
     * @return  加入的用户 id 列表
     */
    List<Long> getUserIdByCoterie(Long coterieId);

    /**
     * 检查加入圈子条件（包括密码）
     * @param coterie 要加入的圈子
     * @param curUser  加入的用户
     * @param password  加入密码
     */
    void checkJoinWithPassword(Coterie coterie, User curUser, String password);

    /**
     *  检查加入圈子条件（不包括密码）
     * @param coterie   要加入的圈子
     * @param userId    加入的用户
     */
    void checkJoinNoPassword(Coterie coterie, Long userId);

    /**
     * 根据 coterieId 获取圈子信息，若无则抛出异常
     * @param coterieId 圈子 id
     * @return  圈子信息
     */
    Coterie getCoterie(Long coterieId);


    /**
     * 将 userId 所属用户加入到 coterie 圈子
     * @param userId   加入的用户
     * @param coterie  加入的圈子
     * @return  是否加入成功
     */
    boolean executeJoinCoterie(Long userId, Coterie coterie);


}
