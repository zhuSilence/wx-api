package com.github.niefy.modules.wx.handler;

import java.util.Map;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.niefy.modules.wx.entity.WxUser;
import com.github.niefy.modules.wx.service.WxUserService;
import me.chanjar.weixin.mp.util.WxMpConfigStorageHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import me.chanjar.weixin.common.session.WxSessionManager;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.message.WxMpXmlMessage;
import me.chanjar.weixin.mp.bean.message.WxMpXmlOutMessage;
import org.springframework.util.StringUtils;

/**
 * @author Binary Wang
 */
@Component
public class UnsubscribeHandler extends AbstractHandler {
    @Autowired
    WxUserService userService;

    @Override
    public WxMpXmlOutMessage handle(WxMpXmlMessage wxMessage,
                                    Map<String, Object> context, WxMpService wxMpService,
                                    WxSessionManager sessionManager) {
        String openid = wxMessage.getFromUser();
        String appid = WxMpConfigStorageHolder.get();
        this.logger.info("取消关注用户 OPENID: " + openid + " APPID: " + appid);

        WxUser wxUser = userService.getOne(new QueryWrapper<WxUser>()
                        .eq(StringUtils.hasText(appid), "appid", appid)
                        .eq(StringUtils.hasText(openid), "openid", openid));
        if (null != wxUser) {
            WxUser.ExtraInfo extraInfo = new WxUser.ExtraInfo(0, 0);
            userService.updateUserOpenAiCount(openid, appid, extraInfo);
            userService.unsubscribe(openid, appid);
        }

        return null;
    }

}
