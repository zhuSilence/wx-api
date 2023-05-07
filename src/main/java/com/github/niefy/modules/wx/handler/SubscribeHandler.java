package com.github.niefy.modules.wx.handler;

import com.github.niefy.common.handler.msg.RequestContext;
import com.github.niefy.modules.wx.entity.MsgReplyRule;
import com.github.niefy.modules.wx.service.MsgReplyDefaultService;
import com.github.niefy.modules.wx.service.MsgReplyRuleService;
import com.github.niefy.modules.wx.service.WxUserService;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.common.session.WxSessionManager;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.message.WxMpXmlMessage;
import me.chanjar.weixin.mp.bean.message.WxMpXmlOutMessage;
import me.chanjar.weixin.mp.util.WxMpConfigStorageHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * @author Binary Wang
 */
@Component
public class SubscribeHandler extends AbstractHandler {

    @Autowired
    private MsgReplyDefaultService msgReplyDefaultService;

    @Autowired
    MsgReplyRuleService msgReplyRuleService;
    @Autowired
    WxUserService userService;

    // todo 订阅的时候增加初始额度，并回复额度赋予成功
    @Override
    public WxMpXmlOutMessage handle(WxMpXmlMessage wxMessage, Map<String, Object> context, WxMpService wxMpService,
                                    WxSessionManager sessionManager) throws WxErrorException {

        this.logger.info("new user subscribe OPENID is : " + wxMessage.getFromUser() + "，eventKye is ：" + wxMessage.getEventKey());
        String appid = WxMpConfigStorageHolder.get();
        this.logger.info("appid:{}", appid);
        userService.initSubScribe(wxMessage.getFromUser(), appid);

        RequestContext requestContext = RequestContext.builder()
                .appId(appid)
                .fromUser(wxMessage.getFromUser())
                .toUser(wxMessage.getToUser())
                .exactMatch(true)
                .requestContent("subscribe").build();

        List<MsgReplyRule> rules = msgReplyRuleService.getMatchedRules(requestContext.getAppId(), requestContext.isExactMatch(), requestContext.getRequestContent());
        if (rules.isEmpty()) {
            return msgReplyDefaultService.replyText(requestContext.getToUser(), requestContext.getFromUser(), "未匹配到关键字，请重新发送或者添加微信：zx1347023180");
        }
        requestContext.setReplyType(rules.get(0).getReplyType());
        String replyContent = rules.get(0).getReplyContent().replace("${OPEN_ID}", requestContext.getToUser());
        requestContext.setResponseContent(replyContent);
        return msgReplyDefaultService.tryAutoReply(requestContext);
    }

}
