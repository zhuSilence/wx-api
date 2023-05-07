package com.github.niefy.common.handler.msg;

import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.mp.bean.message.WxMpXmlOutMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;

/**
 * <br>
 * <b>Function：</b><br>
 * <b>Author：</b>@author Silence<br>
 * <b>Date：</b>2023-05-04 20:45<br>
 * <b>Desc：</b>无<br>
 */
@Component
public class MessageHandlerChain {
    @Autowired
    private List<MessageHandler> messageHandlerList;

    @PostConstruct
    public void setHandlers() {
        for (int i = 0; i < this.messageHandlerList.size() - 1; i++) {
            this.messageHandlerList.get(i).setNext(this.messageHandlerList.get(i + 1));
        }
    }

    /**
     * 通过第一个开始处理请求
     *
     * @param request
     */
    public WxMpXmlOutMessage handleRequest(RequestContext request) throws WxErrorException {
        if (messageHandlerList != null && messageHandlerList.size() > 0) {
            return messageHandlerList.get(0).handle(request);
        }
        return null;
    }
}
