package com.github.niefy.modules.wx.handler;

import com.github.niefy.modules.wx.service.MsgReplyDefaultService;
import com.github.niefy.modules.wx.service.WxUserService;
import me.chanjar.weixin.common.session.WxSessionManager;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.message.WxMpXmlMessage;
import me.chanjar.weixin.mp.bean.message.WxMpXmlOutMessage;
import me.chanjar.weixin.mp.util.WxMpConfigStorageHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @author Binary Wang
 */
@Component
public class SubscribeHandler extends AbstractHandler {

    @Autowired
    private MsgReplyDefaultService msgReplyDefaultService;
    @Autowired
    WxUserService userService;

    // todo 订阅的时候增加初始额度，并回复额度赋予成功
    @Override
    public WxMpXmlOutMessage handle(WxMpXmlMessage wxMessage, Map<String, Object> context, WxMpService wxMpService,
                                    WxSessionManager sessionManager) {

        this.logger.info("new user subscribe OPENID is : " + wxMessage.getFromUser() + "，eventKye is ：" + wxMessage.getEventKey());
        String appid = WxMpConfigStorageHolder.get();
        this.logger.info("appid:{}", appid);
        userService.initSubScribe(wxMessage.getFromUser(), appid);
        return msgReplyDefaultService.tryAutoReply(appid, true, wxMessage.getFromUser(), wxMessage.getToUser(), "subscribe");
    }

}
