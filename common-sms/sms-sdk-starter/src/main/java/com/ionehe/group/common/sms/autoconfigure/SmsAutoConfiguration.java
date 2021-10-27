package com.ionehe.group.common.sms.autoconfigure;

import com.ionehe.group.common.sms.aliyun.handler.AliyunSendHandler;
import com.ionehe.group.common.sms.config.SmsConfiguration;
import com.ionehe.group.common.sms.qcloud.handler.QCloudSendHandler;
import com.ionehe.group.common.sms.bjlxhl.handler.BjlxhlSendHandler;
import com.ionehe.group.common.sms.core.handler.SendHandler;
import com.ionehe.group.common.sms.yidun.handler.YiDunSendHandler;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * Copyright (c) 2021 ionehe.com
 * Date: 2021/5/12
 * Time: 2:37 下午
 *
 * @author 2021年 <a href="mailto:a@ionehe.com">秀</a>
 * 【sms自动装配配置类】
 */

@Configuration
@ComponentScan({"com.ionehe.group.common.sms"})
@EnableConfigurationProperties({SmsConfiguration.class})
public class SmsAutoConfiguration {
    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(prefix = "sms", name = {"vendor"}, havingValue = "aliyun")
    public SendHandler aliyunSendHandler(SmsConfiguration configuration) {
        return new AliyunSendHandler(configuration);
    }

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(prefix = "sms", name = {"vendor"}, havingValue = "bjlingxianhulian")
    public SendHandler bjlxhlSendHandler(SmsConfiguration configuration) {
        return new BjlxhlSendHandler(configuration);
    }

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(prefix = "sms", name = {"vendor"}, havingValue = "qcloud")
    public SendHandler qcloudSendHandler(SmsConfiguration configuration) {
        return new QCloudSendHandler(configuration);
    }

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(prefix = "sms", name = {"vendor"}, havingValue = "yidun")
    public SendHandler yidunSendHandler(SmsConfiguration configuration) {
        return new YiDunSendHandler(configuration);
    }
}
