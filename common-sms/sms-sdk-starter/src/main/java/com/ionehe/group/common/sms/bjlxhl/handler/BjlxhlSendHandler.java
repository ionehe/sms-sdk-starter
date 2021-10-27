package com.ionehe.group.common.sms.bjlxhl.handler;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.google.common.base.Throwables;
import com.ionehe.group.common.sms.bjlxhl.flag.BjlxhlKeyFlags;
import com.ionehe.group.common.sms.config.SmsConfiguration;
import com.ionehe.group.common.sms.config.SmsVendors;
import com.ionehe.group.common.sms.core.domain.NoticeData;
import com.ionehe.group.common.sms.core.exception.ClientException;
import com.ionehe.group.common.sms.core.handler.SendHandler;
import lombok.extern.slf4j.Slf4j;

import java.util.Collection;
import java.util.Map;

/**
 * 阿里云短信发送处理
 *
 * @author xiu
 */
@Slf4j
public class BjlxhlSendHandler implements SendHandler {
    private final String signName;
    private final Map<String, String> templates;
    private final String endpoint;
    private final String accessKeyId;
    private final String accessKeySecret;


    public BjlxhlSendHandler(SmsConfiguration conf) {
        Map<String, Object> vendor = (Map<String, Object>) conf.getVendors().get(SmsVendors.bjlingxianhulian);
        signName = BjlxhlKeyFlags.FLAG_SIGN.get(vendor);
        templates = BjlxhlKeyFlags.FLAG_TEMPLATES.get(vendor);
        endpoint = BjlxhlKeyFlags.FLAG_ENDPOINT.get(vendor);
        accessKeyId = BjlxhlKeyFlags.FLAG_ACCESS.get(vendor);
        accessKeySecret = BjlxhlKeyFlags.FLAG_SECRET.get(vendor);

        if (StrUtil.isBlank(endpoint) || StrUtil.isBlank(accessKeyId) || StrUtil.isBlank(accessKeySecret)
                || StrUtil.isBlank(signName) || CollUtil.isEmpty(templates)) {
            throw new ClientException();
        }

        log.info("BjlxhlSendHandler init success");
    }

    @Override
    public boolean send(NoticeData noticeData, Collection<String> phones) {
        String code = noticeData.getParams().get("code");

        try {
            log.info("send sms start[param:{}, phones:{}]", noticeData, phones);
            String content = templates.get(noticeData.getType());
            content = content.replace("${code}", code);

            for (String phone : phones) {
                HttpResponse sendSmsResponse = HttpRequest.post(endpoint)
                        .form("CorpID", accessKeyId)
                        .form("Pwd", accessKeySecret)
                        .form("Mobile", phone)
                        .form("Content", "【" + signName + "】" + content)
                        .execute();

                if (!sendSmsResponse.isOk()) {
                    log.warn("send sms fail [code={}, errMsg={}]", sendSmsResponse.getStatus(), sendSmsResponse.body());
                }

                log.info("send sms success");
            }
            return true;
        } catch (Exception e) {
            log.error("send sms error! cause:{}", Throwables.getStackTraceAsString(e));
        }
        return false;
    }
}
