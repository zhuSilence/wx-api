package com.github.niefy.modules.wx.dto;

import lombok.Data;

/**
 * <br>
 * <b>Function：</b><br>
 * <b>Author：</b>@author Silence<br>
 * <b>Date：</b>2023-05-11 22:01<br>
 * <b>Desc：</b>无<br>
 */
@Data
public class RequestDTO {
    private String id;
    private Integer count;
    private Integer imgCount;
    private int enableGpt4;
    private Long t;
}
