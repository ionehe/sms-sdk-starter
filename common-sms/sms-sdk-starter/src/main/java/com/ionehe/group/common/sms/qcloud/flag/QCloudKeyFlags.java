package com.ionehe.group.common.sms.qcloud.flag;


import com.ionehe.group.common.sms.core.key.KeyFlag;

/**
 * Copyright (c) 2021 ionehe.com
 * Date: 2021/4/21
 * Time: 1:22 下午
 *
 * @author 2021年 <a href="mailto:a@ionehe.com">秀</a>
 * 【oss配置key】
 */
public class QCloudKeyFlags {
    public static final KeyFlag FLAG_APP_ID = new KeyFlag("appId", true);
    public static final KeyFlag FLAG_APP_KEY = new KeyFlag("appKey", true);
    public static final KeyFlag FLAG_SIGN = new KeyFlag("signName", true);
    public static final KeyFlag FLAG_TEMPLATES = new KeyFlag("templates", true);
    public static final KeyFlag FLAG_PARAMS_ORDERS = new KeyFlag("paramsOrders", true);
}
