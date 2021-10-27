package com.ionehe.rest;

import com.ionehe.group.common.sms.core.exception.RetryTimeShortException;
import com.ionehe.group.common.sms.service.VerificationCodeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;


/**
 * Copyright (c) 2021 ionehe.com
 * Date: 2021/5/14
 * Time: 10:18 上午
 *
 * @author 2021年 <a href="mailto:a@ionehe.com">秀</a>
 * 【短信发送测试controller】
 */
@RestController
@Slf4j
public class TestController {
    /**
     * 手机验证码服务
     */
    @Autowired
    private VerificationCodeService verificationCodeService;

    @GetMapping("/send")
    public String send(String phone) {
        try {
            boolean send = verificationCodeService.send(phone);

            if (!send) {
                return "发送失败";
            }

            log.info("获取验证码：{}", verificationCodeService.find(phone, null));

            return "发送成功";
        } catch (RetryTimeShortException e) {
            return e.getMessage();
        }
    }

    @GetMapping("/verify")
    public String verify(String phone, String code) {
        boolean verify = verificationCodeService.verify(phone, code, null);

        if (!verify) {
            return "验证码有误";
        }

        log.info("获取验证码：{}", verificationCodeService.find(phone, null));

        return "验证通过";
    }
}
