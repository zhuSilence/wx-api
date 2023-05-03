package com.github.niefy.modules.wx.handler;


import com.alibaba.fastjson.JSON;
import com.github.niefy.common.utils.ConfigConstant;
import com.github.niefy.modules.sys.dao.PlanDTO;
import com.github.niefy.modules.sys.entity.SysConfigEntity;
import com.github.niefy.modules.sys.service.SysConfigService;
import com.github.niefy.modules.wx.service.MsgReplyDefaultService;
import com.github.niefy.modules.wx.service.MsgReplyService;
import com.github.niefy.modules.wx.service.WxMsgService;
import com.github.niefy.modules.wx.service.WxUserService;
import me.chanjar.weixin.common.session.WxSessionManager;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.message.WxMpXmlMessage;
import me.chanjar.weixin.mp.bean.message.WxMpXmlOutMessage;
import me.chanjar.weixin.mp.util.WxMpConfigStorageHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author Binary Wang
 */
@Component
public class MsgHandler extends AbstractHandler {
    Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    MsgReplyService msgReplyService;

    @Autowired
    private WxUserService wxUserService;

    @Autowired
    private SysConfigService sysConfigService;

    @Autowired
    private MsgReplyDefaultService msgReplyDefaultService;

    @Autowired
    WxMsgService wxMsgService;
    private static final String TRANSFER_CUSTOMER_SERVICE_KEY = "人工";

    @Override
    public WxMpXmlOutMessage handle(WxMpXmlMessage wxMessage,
                                    Map<String, Object> context, WxMpService wxMpService,
                                    WxSessionManager sessionManager) {

        String textContent = wxMessage.getContent();
        String fromUser = wxMessage.getFromUser();
        String toUser = wxMessage.getToUser();
        String appid = WxMpConfigStorageHolder.get();
        // 特定消息进行充值
        if ("o7PL_v6rWmB4CR14PkMBNVrJzBlY".equalsIgnoreCase(fromUser)
                && textContent.startsWith("额度充值")) {
            String[] split = textContent.split("@");
            //额度充值@o7PL_v6rWmB4CR14PkMBNVrJzBlY@planA
            if (split.length == 3) {
                String openId = split[1];
                SysConfigEntity sysConfig = sysConfigService.getSysConfig(ConfigConstant.PLAN_SETTING_JSON);
                List<PlanDTO> planDTOS = JSON.parseArray(sysConfig.getParamValue(), PlanDTO.class);
                List<PlanDTO> collect = planDTOS.stream().filter(planDTO -> planDTO.getKey().equalsIgnoreCase(split[2])).collect(Collectors.toList());
                if (!CollectionUtils.isEmpty(collect)) {
                    PlanDTO planDTO = collect.get(0);
                    Integer count = planDTO.getCount();
                    wxUserService.updateUserOpenAiCount(openId, appid, count);
                    logger.info("[额度充值]openId:{}", openId + ",count:" + count);
                }
            }
        }
        return msgReplyDefaultService.tryAutoReply(appid, false, fromUser, toUser, textContent);

    }

}
