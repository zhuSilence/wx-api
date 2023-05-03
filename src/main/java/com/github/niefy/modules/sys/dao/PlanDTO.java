package com.github.niefy.modules.sys.dao;

import lombok.Data;

import java.io.Serializable;

/**
 * <br>
 * <b>Function：</b><br>
 * <b>Author：</b>@author Silence<br>
 * <b>Date：</b>2023-05-03 21:30<br>
 * <b>Desc：</b>无<br>
 */
@Data
public class PlanDTO implements Serializable {

    /**
     * name : 套餐一
     * key : planA
     * price : 10
     * count : 30
     */

    private String name;
    private String key;
    private Integer price;
    private Integer count;
}

