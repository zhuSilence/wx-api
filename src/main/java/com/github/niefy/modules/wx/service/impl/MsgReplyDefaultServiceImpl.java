package com.github.niefy.modules.wx.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.github.niefy.common.handler.msg.RequestContext;
import com.github.niefy.modules.wx.entity.MsgReplyRule;
import com.github.niefy.modules.wx.entity.WxMsg;
import com.github.niefy.modules.wx.service.MsgReplyDefaultService;
import com.github.niefy.modules.wx.service.MsgReplyRuleService;
import com.github.niefy.modules.wx.service.WxMsgService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.api.WxConsts;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.kefu.WxMpKefuMessage;
import me.chanjar.weixin.mp.bean.message.WxMpXmlOutMessage;
import me.chanjar.weixin.mp.bean.message.WxMpXmlOutNewsMessage;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 微信公众号消息处理 被动回复接口
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class MsgReplyDefaultServiceImpl implements MsgReplyDefaultService {
    @Autowired
    MsgReplyRuleService msgReplyRuleService;
    @Autowired
    WxMpService wxMpService;
    @Value("${wx.mp.autoReplyInterval:1000}")
    Long autoReplyInterval;
    @Autowired
    WxMsgService wxMsgService;

    /**
     * 根据规则配置通过普通消息接口自动回复消息
     *
     * @param requestContext 请求参数
     * @return 是否已自动回复，无匹配规则则不自动回复
     */
    @Override
    public WxMpXmlOutMessage tryAutoReply(RequestContext requestContext) {
        try {
            if (StringUtils.isEmpty(requestContext.getResponseContent())) {
                List<MsgReplyRule> rules = msgReplyRuleService.getMatchedRules(requestContext.getAppId(), requestContext.isExactMatch(), requestContext.getRequestContent());
                if (rules.isEmpty()) {
                    return this.replyText(requestContext.getFromUser(), requestContext.getToUser(), "未匹配到关键字，请重新发送或者添加微信：zx1347023180");
                }
                requestContext.setReplyType(rules.get(0).getReplyType());
                requestContext.setResponseContent(rules.get(0).getReplyContent());
            }
            return this.reply(requestContext.getFromUser(), requestContext.getToUser(), requestContext.getReplyType(), requestContext.getResponseContent());
        } catch (Exception e) {
            log.error("自动回复出错：", e);
        }
        return null;
    }

    @Override
    public WxMpXmlOutMessage replyText(String toUser, String fromUser, String content) throws WxErrorException {
        JSONObject json = new JSONObject().fluentPut("content", content);
        wxMsgService.addWxMsg(WxMsg.buildOutMsg(WxConsts.KefuMsgType.TEXT, toUser, json));
        return WxMpXmlOutMessage.TEXT().content(content).toUser(toUser).fromUser(fromUser).build();
    }

    @Override
    public WxMpXmlOutMessage replyImage(String toUser, String fromUser, String mediaId) throws WxErrorException {
        JSONObject json = new JSONObject().fluentPut("mediaId", mediaId);
        wxMsgService.addWxMsg(WxMsg.buildOutMsg(WxConsts.KefuMsgType.IMAGE, toUser, json));
        return WxMpXmlOutMessage.IMAGE().mediaId(mediaId).toUser(toUser).fromUser(fromUser).build();
    }

    @Override
    public WxMpXmlOutMessage replyVoice(String toUser, String fromUser, String mediaId) throws WxErrorException {
        JSONObject json = new JSONObject().fluentPut("mediaId", mediaId);
        wxMsgService.addWxMsg(WxMsg.buildOutMsg(WxConsts.KefuMsgType.VOICE, toUser, json));
        return WxMpXmlOutMessage.VOICE().mediaId(mediaId).toUser(toUser).fromUser(fromUser).build();
    }

    @Override
    public WxMpXmlOutMessage replyVideo(String toUser, String fromUser, String mediaId) throws WxErrorException {
        JSONObject json = new JSONObject().fluentPut("mediaId", mediaId);
        wxMsgService.addWxMsg(WxMsg.buildOutMsg(WxConsts.KefuMsgType.VIDEO, toUser, json));
        return WxMpXmlOutMessage.VIDEO().mediaId(mediaId).toUser(toUser).fromUser(fromUser).build();
    }

    @Override
    public WxMpXmlOutMessage replyMusic(String toUser, String fromUser, String musicInfoJson) throws WxErrorException {
        JSONObject json = JSON.parseObject(musicInfoJson);
        wxMsgService.addWxMsg(WxMsg.buildOutMsg(WxConsts.KefuMsgType.IMAGE, toUser, json));
        return WxMpXmlOutMessage.MUSIC().toUser(toUser).fromUser(fromUser)
                .musicUrl(json.getString("musicurl"))
                .hqMusicUrl(json.getString("hqmusicurl"))
                .title(json.getString("title"))
                .description(json.getString("description"))
                .thumbMediaId(json.getString("thumb_media_id"))
                .build();
    }

    /**
     * 发送图文消息（点击跳转到外链） 图文消息条数限制在1条以内
     *
     * @param toUser
     * @param newsInfoJson
     * @throws WxErrorException
     */
    @Override
    public WxMpXmlOutMessage replyNews(String toUser, String fromUser, String newsInfoJson) throws WxErrorException {
        WxMpKefuMessage.WxArticle wxArticle = JSON.parseObject(newsInfoJson, WxMpKefuMessage.WxArticle.class);
        wxMsgService.addWxMsg(WxMsg.buildOutMsg(WxConsts.KefuMsgType.NEWS, toUser, JSON.parseObject(newsInfoJson)));

        WxMpXmlOutNewsMessage.Item item = new WxMpXmlOutNewsMessage.Item();
        item.setDescription(wxArticle.getDescription());
        item.setPicUrl(wxArticle.getPicUrl());
        item.setTitle(wxArticle.getTitle());
        item.setUrl(wxArticle.getUrl());

        return WxMpXmlOutMessage.NEWS().toUser(toUser).fromUser(fromUser).addArticle(item).build();
    }

    /**
     * 发送图文消息（点击跳转到图文消息页面） 图文消息条数限制在1条以内
     *
     * @param toUser
     * @param mediaId
     * @throws WxErrorException
     */
    @Override
    public WxMpXmlOutMessage replyMpNews(String toUser, String fromUser, String mediaId) throws WxErrorException {

        return null;
    }

    @Override
    public WxMpXmlOutMessage replyWxCard(String toUser, String fromUser, String cardId) throws WxErrorException {
        return null;
    }

    @Override
    public WxMpXmlOutMessage replyMiniProgram(String toUser, String fromUser, String miniProgramInfoJson) throws WxErrorException {
        return null;
    }

    @Override
    public WxMpXmlOutMessage replyMsgMenu(String toUser, String fromUser, String msgMenusJson) throws WxErrorException {
        return null;
    }

}
