package com.github.niefy.common.handler.msg;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.niefy.common.utils.ConfigConstant;
import com.github.niefy.modules.sys.dao.PlanDTO;
import com.github.niefy.modules.sys.entity.SysConfigEntity;
import com.github.niefy.modules.sys.service.SysConfigService;
import com.github.niefy.modules.wx.entity.WxUser;
import com.github.niefy.modules.wx.service.MsgReplyDefaultService;
import com.github.niefy.modules.wx.service.WxUserService;
import me.chanjar.weixin.mp.bean.message.WxMpXmlOutMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 处理充值逻辑
 */
@Component
public class ChargeHandler implements MessageHandler {
    Logger logger = LoggerFactory.getLogger(this.getClass());
    private MessageHandler messageHandler;
    @Autowired
    private WxUserService wxUserService;

    @Autowired
    private SysConfigService sysConfigService;

    @Autowired
    private MsgReplyDefaultService msgReplyDefaultService;

    @Override
    public WxMpXmlOutMessage handle(RequestContext requestContext) {
        logger.info("process in messageHandler chargeHandler...requestContext is " + JSON.toJSONString(requestContext));
        try {
            if ("o7PL_v6rWmB4CR14PkMBNVrJzBlY".equalsIgnoreCase(requestContext.getFromUser())
                    && requestContext.getRequestContent().startsWith("额度充值")) {
                String[] split = requestContext.getRequestContent().split("@");
                //额度充值@o7PL_v6rWmB4CR14PkMBNVrJzBlY@planA
                if (split.length == 3) {
                    String openId = split[1].trim();
                    String plan = split[2].trim();
                    if (openId.length() > 0 && plan.length() > 0) {
                        SysConfigEntity sysConfig = sysConfigService.getSysConfig(ConfigConstant.PLAN_SETTING_JSON);
                        List<PlanDTO> planDTOS = JSON.parseArray(sysConfig.getParamValue(), PlanDTO.class);
                        List<PlanDTO> collect = planDTOS.stream().filter(planDTO -> planDTO.getKey().equalsIgnoreCase(plan)).collect(Collectors.toList());
                        if (!CollectionUtils.isEmpty(collect)) {
                            PlanDTO planDTO = collect.get(0);
                            Integer count = planDTO.getCount();
                            WxUser wxUser = wxUserService.getOne(new QueryWrapper<WxUser>()
                                    .eq(StringUtils.hasText(requestContext.getAppId()), "appid", requestContext.getAppId())
                                    .eq(StringUtils.hasText(openId), "openid", openId));
                            if (null == wxUser) {
                                logger.error("[额度充值]openId:{} 用户不存在", openId);
                                requestContext.setRequestContent("充值失败");
                            } else {
                                WxUser.ExtraInfo newExtraInfo = JSONObject.parseObject(wxUser.getExtraInfo(), WxUser.ExtraInfo.class);
                                wxUser.setSubscribe(true);
                                int newCount = Math.max(0, newExtraInfo.getOpenApiCount()) + count;
                                newExtraInfo.setOpenApiCount(newCount);
                                wxUser.setExtraInfo(JSONObject.toJSONString(newExtraInfo));
                                wxUserService.saveOrUpdate(wxUser);
                                logger.info("[额度充值]openId:{}", openId + ",count:" + count);
                            }
                        }
                    }
                }
                return msgReplyDefaultService.tryAutoReply(requestContext);
            } else if (messageHandler != null) {
                messageHandler.handle(requestContext);
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("message handler error " + e.getMessage());
        }
        return null;
    }

    @Override
    public void setNext(MessageHandler messageHandler) {
        this.messageHandler = messageHandler;
    }
}
