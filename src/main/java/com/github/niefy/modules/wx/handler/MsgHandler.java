package com.github.niefy.modules.wx.handler;


import com.github.niefy.common.handler.msg.MessageHandlerChain;
import com.github.niefy.common.handler.msg.RequestContext;
import com.github.niefy.modules.wx.service.MsgReplyDefaultService;
import com.github.niefy.modules.wx.service.MsgReplyService;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.common.session.WxSessionManager;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.message.WxMpXmlMessage;
import me.chanjar.weixin.mp.bean.message.WxMpXmlOutMessage;
import me.chanjar.weixin.mp.util.WxMpConfigStorageHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @author Binary Wang
 */
@Component
public class MsgHandler extends AbstractHandler {
    Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    MsgReplyService msgReplyService;
    @Autowired
    private MsgReplyDefaultService msgReplyDefaultService;

    @Autowired
    private MessageHandlerChain messageHandlerChain;

    @Override
    public WxMpXmlOutMessage handle(WxMpXmlMessage wxMessage,
                                    Map<String, Object> context, WxMpService wxMpService,
                                    WxSessionManager sessionManager) throws WxErrorException {

        String textContent = wxMessage.getContent();
        String fromUser = wxMessage.getFromUser();
        String toUser = wxMessage.getToUser();
        String appid = WxMpConfigStorageHolder.get();

        RequestContext requestContext = RequestContext.builder()
                .appId(appid)
                .fromUser(fromUser)
                .toUser(toUser)
                .requestContent(textContent).build();
        WxMpXmlOutMessage wxMpXmlOutMessage = messageHandlerChain.handleRequest(requestContext);
        if (null == wxMpXmlOutMessage) {
            wxMpXmlOutMessage = msgReplyDefaultService.tryAutoReply(requestContext);
        }
        return wxMpXmlOutMessage;
    }

}
