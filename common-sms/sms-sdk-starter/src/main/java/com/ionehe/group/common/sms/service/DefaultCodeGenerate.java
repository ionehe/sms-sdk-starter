package com.ionehe.group.common.sms.service;

import com.ionehe.group.common.sms.resource.SmsProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.NumberFormat;
import java.util.concurrent.ThreadLocalRandom;

/**
 * 默认验证码生成
 * 
 * @author xiu
 *
 */
@Service
public class DefaultCodeGenerate implements ICodeGenerate {
    @Autowired
    private SmsProperties properties;


    @Override
    public String generate() {
        int codeLength = properties.getVerificationCode().getCodeLength();

        NumberFormat format = NumberFormat.getInstance();
        format.setGroupingUsed(false);
        format.setMaximumIntegerDigits(codeLength);
        format.setMinimumIntegerDigits(codeLength);

        return format.format(ThreadLocalRandom.current().nextInt((int) Math.pow(10, codeLength)));
    }

}
