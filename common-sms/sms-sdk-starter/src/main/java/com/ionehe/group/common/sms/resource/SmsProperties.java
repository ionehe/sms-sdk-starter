package com.ionehe.group.common.sms.resource;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * 短信配置
 *
 * @author xiu
 *
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "sms")
public class SmsProperties {

    /**
     * 手机号码正则规则
     */
    private String reg;


    /**
     * 验证码配置
     */
    private VerificationCodeProperties verificationCode = new VerificationCodeProperties();
}
