package com.github.niefy.common.handler.msg;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.niefy.modules.wx.entity.MsgReplyRule;
import com.github.niefy.modules.wx.entity.WxUser;
import com.github.niefy.modules.wx.service.MsgReplyDefaultService;
import com.github.niefy.modules.wx.service.MsgReplyRuleService;
import com.github.niefy.modules.wx.service.WxUserService;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.mp.bean.message.WxMpXmlOutMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * 额度查询逻辑
 */
@Component
public class LeftChanceHandler implements MessageHandler {
    Logger logger = LoggerFactory.getLogger(this.getClass());
    private MessageHandler messageHandler;
    @Autowired
    private WxUserService wxUserService;

    @Autowired
    private MsgReplyDefaultService msgReplyDefaultService;

    @Autowired
    MsgReplyRuleService msgReplyRuleService;

    @Override
    public WxMpXmlOutMessage handle(RequestContext requestContext) throws WxErrorException {
        if (requestContext.getRequestContent().startsWith("额度查询")) {
            WxUser wxUser = wxUserService.getOne(new QueryWrapper<WxUser>()
                    .eq(StringUtils.hasText(requestContext.getAppId()), "appid", requestContext.getAppId())
                    .eq(StringUtils.hasText(requestContext.getFromUser()), "openid", requestContext.getFromUser()));

            if (null == wxUser) {
                logger.error("[额度查询]openId:{} 用户不存在", requestContext.getFromUser());
                requestContext.setRequestContent("用户不存在");
            } else {
                List<MsgReplyRule> rules = msgReplyRuleService.getMatchedRules(requestContext.getAppId(), requestContext.isExactMatch(), requestContext.getRequestContent());
                if (rules.isEmpty()) {
                    return msgReplyDefaultService.replyText(requestContext.getToUser(), requestContext.getFromUser(), "未匹配到关键字，请重新发送或者添加微信：zx1347023180");
                }
                WxUser.ExtraInfo newExtraInfo = JSONObject.parseObject(wxUser.getExtraInfo(), WxUser.ExtraInfo.class);
                //替换关注回复中的内容
                String replyContent = rules.get(0).getReplyContent().replace("${OPEN_AI_COUNT}", String.valueOf(newExtraInfo.getOpenApiCount()));
                requestContext.setReplyType(rules.get(0).getReplyType());
                requestContext.setResponseContent(replyContent);
                return msgReplyDefaultService.reply(requestContext.getToUser(), requestContext.getFromUser(), requestContext.getReplyType(), requestContext.getResponseContent());
            }
            return msgReplyDefaultService.tryAutoReply(requestContext);
        } else if (null != messageHandler) {
            return messageHandler.handle(requestContext);
        }
        return null;
    }

    @Override
    public void setNext(MessageHandler messageHandler) {
        this.messageHandler = messageHandler;
    }
}
