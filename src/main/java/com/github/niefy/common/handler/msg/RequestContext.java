package com.github.niefy.common.handler.msg;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/**
 * <br>
 * <b>Function：</b><br>
 * <b>Author：</b>@author Silence<br>
 * <b>Date：</b>2023-05-04 20:42<br>
 * <b>Desc：</b>无<br>
 */
@Setter
@Getter
@Builder
public class RequestContext {
    private String appId;
    private String fromUser;
    private String toUser;
    private String requestContent;
    private String replyType;
    private String responseContent;
    private boolean exactMatch;
}
