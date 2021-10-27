package com.ionehe.group.common.sms.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.HashMap;
import java.util.Map;

/**
 * Copyright (c) 2021 ionehe.com
 * Date: 2021/5/12
 * Time: 3:00 下午
 *
 * @author 2021年 <a href="mailto:a@ionehe.com">秀</a>
 * 【短信配置类】
 */

@ConfigurationProperties(prefix = "sms", ignoreInvalidFields = true)
@Data
public class SmsConfiguration {
    private SmsVendors vendor;
    private Map<SmsVendors, Map> vendors = new HashMap<>(0);

    @Override
    protected SmsConfiguration clone() throws CloneNotSupportedException {
        SmsConfiguration configuration = new SmsConfiguration();

        configuration.setVendor(this.vendor);

        Map<SmsVendors, Map> map = new HashMap<>((SmsVendors.values()).length);

        if (null != this.vendors) {
            for (SmsVendors k : this.vendors.keySet()) {
                map.put(k, new HashMap<>(this.vendors.get(k)));
            }
        }

        configuration.setVendors(map);

        return configuration;
    }
}
