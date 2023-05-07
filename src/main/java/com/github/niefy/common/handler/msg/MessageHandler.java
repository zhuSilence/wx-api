package com.github.niefy.common.handler.msg;

import me.chanjar.weixin.mp.bean.message.WxMpXmlOutMessage;

public interface MessageHandler {
    WxMpXmlOutMessage handle(RequestContext requestContext);

    void setNext(MessageHandler messageHandler);
}
