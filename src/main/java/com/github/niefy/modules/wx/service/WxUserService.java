package com.github.niefy.modules.wx.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.github.niefy.modules.wx.entity.WxUser;

import java.util.List;
import java.util.Map;

public interface WxUserService extends IService<WxUser> {
    /**
     * 分页查询用户数据
     * @param params 查询参数
     * @return PageUtils 分页结果
     */
    IPage<WxUser> queryPage(Map<String, Object> params);

    /**
     * 根据openid更新用户信息
     *
     * @param openid
     * @return
     */
    WxUser refreshUserInfo(String openid,String appid);

    /**
     * 设置用户的 OpenAI 额度
     * @param openid
     * @param appid
     * @param count
     * @return
     */
    WxUser updateUserOpenAiCount(String openid,String appid, WxUser.ExtraInfo extraInfo);

    /**
     * 初始订阅时初始化额度
     * @param openid
     * @param appid
     * @return
     */
    WxUser initSubScribe(String openid,String appid);

    /**
     * 查询用户的剩余额度
     * @param openid
     * @param appid
     * @return
     */
    WxUser.ExtraInfo leftChance(String openid,String appid);

    /**
     * 降低额度
     * @param openid
     * @param appid
     * @return
     */
    WxUser.ExtraInfo reduceChance(String openid,String appid);

    /**
     * 异步批量更新用户信息
     * @param openidList
     */
    void refreshUserInfoAsync(String[] openidList,String appid);

    /**
     * 数据存在时更新，否则新增
     *
     * @param user
     */
    void updateOrInsert(WxUser user);

    /**
     * 取消关注，更新关注状态
     *
     * @param openid
     */
    void unsubscribe(String openid, String appid);
    /**
     * 同步用户列表
     */
    void syncWxUsers(String appid);

    /**
     * 通过传入的openid列表，同步用户列表
     * @param openids
     */
    void syncWxUsers(List<String> openids,String appid);

}
