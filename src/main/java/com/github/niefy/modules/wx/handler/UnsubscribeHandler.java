package com.github.niefy.modules.wx.handler;

import java.util.Map;

import com.github.niefy.modules.wx.service.WxUserService;
import me.chanjar.weixin.mp.util.WxMpConfigStorageHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import me.chanjar.weixin.common.session.WxSessionManager;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.message.WxMpXmlMessage;
import me.chanjar.weixin.mp.bean.message.WxMpXmlOutMessage;

/**
 * @author Binary Wang
 */
@Component
public class UnsubscribeHandler extends AbstractHandler {
    @Autowired
    WxUserService userService;

    // todo 取消订阅的时候重置额度
    @Override
    public WxMpXmlOutMessage handle(WxMpXmlMessage wxMessage,
                                    Map<String, Object> context, WxMpService wxMpService,
                                    WxSessionManager sessionManager) {
        String openid = wxMessage.getFromUser();
        String appid = WxMpConfigStorageHolder.get();
        this.logger.info("取消关注用户 OPENID: " + openid + " APPID: " + appid);

        userService.updateUserOpenAiCount(openid, appid, 0);
        userService.unsubscribe(openid, appid);
        return null;
    }

}
