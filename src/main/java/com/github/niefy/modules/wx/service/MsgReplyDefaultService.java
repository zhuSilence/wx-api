package com.github.niefy.modules.wx.service;

import me.chanjar.weixin.common.api.WxConsts;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.mp.bean.message.WxMpXmlOutMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 公众号消息处理
 * 官方文档：https://developers.weixin.qq.com/doc/offiaccount/Message_Management/Service_Center_messages.html#7
 * WxJava客服消息文档：https://github.com/Wechat-Group/WxJava/wiki/MP_主动发送消息（客服消息）
 */
public interface MsgReplyDefaultService {
    Logger logger = LoggerFactory.getLogger(MsgReplyDefaultService.class);

    /**
     * 根据规则配置通过微信客服消息接口自动回复消息
     *
     *
     * @param appid
     * @param exactMatch 是否精确匹配
     * @param toUser     用户openid
     * @param keywords   匹配关键词
     * @return 是否已自动回复，无匹配规则则不自动回复
     */
    WxMpXmlOutMessage tryAutoReply(String appid, boolean exactMatch, String toUser, String fromUser, String keywords);

    default WxMpXmlOutMessage reply(String toUser, String fromUser, String replyType, String replyContent){
        try {
            if (WxConsts.KefuMsgType.TEXT.equals(replyType)) {
                return this.replyText(toUser, fromUser, replyContent);
            } else if (WxConsts.KefuMsgType.IMAGE.equals(replyType)) {
                return this.replyImage(toUser, fromUser, replyContent);
            } else if (WxConsts.KefuMsgType.VOICE.equals(replyType)) {
                return this.replyVoice(toUser, fromUser, replyContent);
            } else if (WxConsts.KefuMsgType.VIDEO.equals(replyType)) {
                return this.replyVideo(toUser, fromUser, replyContent);
            } else if (WxConsts.KefuMsgType.MUSIC.equals(replyType)) {
                return this.replyMusic(toUser, fromUser, replyContent);
            } else if (WxConsts.KefuMsgType.NEWS.equals(replyType)) {
                return this.replyNews(toUser, fromUser, replyContent);
            } else if (WxConsts.KefuMsgType.MPNEWS.equals(replyType)) {
                return this.replyMpNews(toUser, fromUser, replyContent);
            } else if (WxConsts.KefuMsgType.WXCARD.equals(replyType)) {
                return this.replyWxCard(toUser, fromUser, replyContent);
            } else if (WxConsts.KefuMsgType.MINIPROGRAMPAGE.equals(replyType)) {
                return this.replyMiniProgram(toUser, fromUser, replyContent);
            } else if (WxConsts.KefuMsgType.MSGMENU.equals(replyType)) {
                return this.replyMsgMenu(toUser, fromUser, replyContent);
            }
        } catch (Exception e) {
            logger.error("自动回复出错：", e);
        }
        return null;
    }

    /**
     * 回复文字消息
     */
    WxMpXmlOutMessage replyText(String toUser, String fromUser, String replyContent) throws WxErrorException;

    /**
     * 回复图片消息
     */
    WxMpXmlOutMessage replyImage(String toUser, String fromUser, String mediaId) throws WxErrorException;

    /**
     * 回复录音消息
     */
    WxMpXmlOutMessage replyVoice(String toUser, String fromUser, String mediaId) throws WxErrorException;

    /**
     * 回复视频消息
     */
    WxMpXmlOutMessage replyVideo(String toUser, String fromUser, String mediaId) throws WxErrorException;

    /**
     * 回复音乐消息
     */
    WxMpXmlOutMessage replyMusic(String toUser, String fromUser, String mediaId) throws WxErrorException;

    /**
     * 回复图文消息（点击跳转到外链）
     * 图文消息条数限制在1条以内
     */
    WxMpXmlOutMessage replyNews(String toUser, String fromUser, String newsInfoJson) throws WxErrorException;

    /**
     * 回复公众号文章消息（点击跳转到图文消息页面）
     * 图文消息条数限制在1条以内
     */
    WxMpXmlOutMessage replyMpNews(String toUser, String fromUser, String mediaId) throws WxErrorException;

    /**
     * 回复卡券消息
     */
    WxMpXmlOutMessage replyWxCard(String toUser, String fromUser, String cardId) throws WxErrorException;

    /**
     * 回复小程序消息
     */
    WxMpXmlOutMessage replyMiniProgram(String toUser, String fromUser, String miniProgramInfoJson) throws WxErrorException;

    /**
     * 回复菜单消息
     */
    WxMpXmlOutMessage replyMsgMenu(String toUser, String fromUser, String msgMenusJson) throws WxErrorException;
}
