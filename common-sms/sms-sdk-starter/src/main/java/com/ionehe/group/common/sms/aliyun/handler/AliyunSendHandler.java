package com.ionehe.group.common.sms.aliyun.handler;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsRequest;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsResponse;
import com.aliyuncs.http.MethodType;
import com.aliyuncs.profile.DefaultProfile;
import com.aliyuncs.profile.IClientProfile;
import com.google.common.base.Throwables;
import com.ionehe.group.common.sms.aliyun.flag.AliyunKeyFlags;
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
public class AliyunSendHandler implements SendHandler {
    private final IAcsClient acsClient;
    private final String signName;
    private final Map<String, String> templates;

    private static final String OK = "OK";
    private static final String PRODUCT = "Dysmsapi";
    private static final String DOMAIN = "dysmsapi.aliyuncs.com";

    /**
     * 构造阿里云短信发送处理
     */
    public AliyunSendHandler(SmsConfiguration conf) {
        Map<String, Object> vendor = (Map<String, Object>) conf.getVendors().get(SmsVendors.aliyun);
        signName = AliyunKeyFlags.FLAG_SIGN.get(vendor);
        templates = AliyunKeyFlags.FLAG_TEMPLATES.get(vendor);
        String endpoint = AliyunKeyFlags.FLAG_ENDPOINT.get(vendor);
        String accessKeyId = AliyunKeyFlags.FLAG_ACCESS.get(vendor);
        String accessKeySecret = AliyunKeyFlags.FLAG_SECRET.get(vendor);

        if (StrUtil.isBlank(endpoint) || StrUtil.isBlank(accessKeyId) || StrUtil.isBlank(accessKeySecret)
                || StrUtil.isBlank(signName) || CollUtil.isEmpty(templates)) {
            throw new ClientException();
        }

        IClientProfile profile = DefaultProfile.getProfile(endpoint, accessKeyId, accessKeySecret);
        DefaultProfile.addEndpoint(endpoint, PRODUCT, DOMAIN);

        acsClient = new DefaultAcsClient(profile);

        log.info("AliyunSendHandler init success");
    }

    @Override
    public boolean send(NoticeData noticeData, Collection<String> phones) {
        String paramString = JSONUtil.toJsonStr(noticeData.getParams());

        SendSmsRequest request = new SendSmsRequest();
        request.setSysMethod(MethodType.POST);
        request.setPhoneNumbers(StrUtil.join(",", StrUtil.join(",", phones)));
        request.setSignName(signName);
        request.setTemplateCode(templates.get(noticeData.getType()));
        request.setTemplateParam(paramString);

        try {
            log.info("send sms start[param:{}]", paramString);

            SendSmsResponse sendSmsResponse = acsClient.getAcsResponse(request);

            if (!OK.equals(sendSmsResponse.getCode())) {
                log.warn("send sms fail [code={}, errMsg={}]", sendSmsResponse.getCode(), sendSmsResponse.getMessage());
                return false;
            }

            log.info("send sms success[code:{}, message:{}]", sendSmsResponse.getCode(), sendSmsResponse.getMessage());
            return true;
        } catch (Exception e) {
            log.error("send sms error! cause:{}", Throwables.getStackTraceAsString(e));
            return false;
        }
    }
}
