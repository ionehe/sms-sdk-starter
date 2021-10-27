package com.ionehe.group.common.sms.yidun.flag;


import com.ionehe.group.common.sms.core.key.KeyFlag;

/**
 * Copyright (c) 2021 ionehe.com
 * Date: 2021/4/21
 * Time: 1:22 下午
 *
 * @author 2021年 <a href="mailto:a@ionehe.com">秀</a>
 * 【oss配置key】
 */
public class YiDunKeyFlags {
    public static final KeyFlag FLAG_ENDPOINT = new KeyFlag("endpoint", true);
    public static final KeyFlag FLAG_ACCESS = new KeyFlag("accessKeyId", true);
    public static final KeyFlag FLAG_SECRET = new KeyFlag("accessKeySecret", true);
    public static final KeyFlag BUSINESS_ID = new KeyFlag("businessId", true);
    public static final KeyFlag FLAG_TEMPLATES = new KeyFlag("templates", true);
}
