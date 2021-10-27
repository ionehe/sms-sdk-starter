package com.ionehe;

import com.ionehe.group.common.sms.core.domain.NoticeData;
import com.ionehe.group.common.sms.service.NoticeService;
import com.ionehe.group.common.sms.service.VerificationCodeService;
import org.assertj.core.util.Maps;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class SmsSeverTest {
    /**
     * 手机验证码服务
     */
    @Autowired
    private VerificationCodeService verificationCodeService;

    @Autowired
    private NoticeService noticeService;

    @Test
    void code() {
        String phone = "13250818103";
        // 发送
        boolean send = verificationCodeService.send(phone);
        System.out.println(send);

        // 获取
        String code = verificationCodeService.find(phone, null);
        System.out.println(code);

        // 验证
        boolean verify = verificationCodeService.verify(phone, code, null);
        System.out.println(verify);
    }

    @Test
    void msg() {
        NoticeData data = new NoticeData();

        data.setType("leaveComments");
        data.setParams(Maps.newHashMap("time", "5"));

        boolean send = noticeService.send(data, "13250818103");

        System.out.println(send);
    }

}
