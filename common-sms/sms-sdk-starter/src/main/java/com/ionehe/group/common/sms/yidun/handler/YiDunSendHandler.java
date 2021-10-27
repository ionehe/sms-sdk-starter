package com.ionehe.group.common.sms.yidun.handler;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.google.common.base.Throwables;
import com.ionehe.group.common.sms.yidun.flag.YiDunKeyFlags;
import com.ionehe.group.common.sms.config.SmsConfiguration;
import com.ionehe.group.common.sms.config.SmsVendors;
import com.ionehe.group.common.sms.core.domain.NoticeData;
import com.ionehe.group.common.sms.core.exception.ClientException;
import com.ionehe.group.common.sms.core.handler.SendHandler;
import com.ionehe.group.common.sms.yidun.param.SendResponse;
import com.ionehe.group.common.sms.yidun.utils.ParamUtils;
import com.ionehe.group.common.sms.yidun.utils.RequestUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * 阿里云短信发送处理
 *
 * @author xiu
 */
@Slf4j
public class YiDunSendHandler implements SendHandler {
    private final String businessId;
    private final Map<String, String> templates;
    private final String endpoint;
    private final String accessKeyId;
    private final String accessKeySecret;


    public YiDunSendHandler(SmsConfiguration conf) {
        Map<String, Object> vendor = (Map<String, Object>) conf.getVendors().get(SmsVendors.yidun);
        businessId = YiDunKeyFlags.BUSINESS_ID.get(vendor);
        templates = YiDunKeyFlags.FLAG_TEMPLATES.get(vendor);
        endpoint = YiDunKeyFlags.FLAG_ENDPOINT.get(vendor);
        accessKeyId = YiDunKeyFlags.FLAG_ACCESS.get(vendor);
        accessKeySecret = YiDunKeyFlags.FLAG_SECRET.get(vendor);

        if (StrUtil.isBlank(endpoint) || StrUtil.isBlank(accessKeyId) || StrUtil.isBlank(accessKeySecret)
                || CollUtil.isEmpty(templates)) {
            throw new ClientException();
        }

        log.info("YiDunSendHandler init success");
    }

    @Override
    public boolean send(NoticeData noticeData, Collection<String> phones) {
        try {
            log.info("send sms start[param:{}, phones:{}]", noticeData, phones);
            Map<String, String> variables = noticeData.getParams();
            for (String phone : phones) {
                Map<String, String> param = createParam(businessId, templates.get(noticeData.getType()), variables, phone);
                SendResponse response = send(param);

                log.info("response: " + response);

                if (!response.isOk()) {
                    log.warn("send sms fail [code={}, errMsg={}]", response.getCode(), response.getMsg());
                }

                log.info("send sms success");
            }
            return true;
        } catch (Exception e) {
            log.error("send sms error! cause:{}", Throwables.getStackTraceAsString(e));
        }
        return false;
    }

    private SendResponse send(Map<String, String> params) {
        return RequestUtils.postForEntity(endpoint, params, SendResponse.class);
    }

    /**
     * 构建国内短信的请求参数
     *
     * @param businessId 业务ID
     * @param templateId 短信模板ID。你需要先在易盾官网创建模板并通过审核后才能使用。模板ID需要与业务ID匹配。即，该模板属于目标业务。
     * @param variables  短信模板中的变量值。如，你的模板内容为 “您的验证码为${code}，有效期${time}分钟。”，则此参数需指明 code 和 time 的值。
     * @param to         收信方的号码。如，134开头的号码一般是中国移动的号码。
     */
    private Map<String, String> createParam(
            String businessId, String templateId, Map<String, String> variables, String to) {
        return createSendParam(businessId, templateId, variables, to, null);
    }

    /**
     * 构建国际短信的请求参数
     *
     * @param businessId         业务ID
     * @param templateId         短信模板ID。你需要先在易盾官网创建模板并通过审核后才能使用。模板ID需要与业务ID匹配。即，该模板属于目标业务。
     * @param variables          短信模板中的变量值。如，你的模板内容为 “Your verification code is ${code}, valid for ${time} minutes.”，则此参数需指明 code
     *                           和 time 的值。
     * @param to                 收信方的号码。不包含国际电话区号。
     * @param countryCallingCode 收信方号码的国际电话区号。如，美国是1，英国是44，法国是33，俄罗斯是79。
     */
    private Map<String, String> createParamForInternational(
            String businessId, String templateId, Map<String, String> variables, String to, String countryCallingCode) {
        return createSendParam(businessId, templateId, variables, to, countryCallingCode);
    }

    private Map<String, String> createSendParam(
            String businessId, String templateId, Map<String, String> variables, String to, String countryCallingCode) {
        Map<String, String> params = new HashMap<>();

        params.put("nonce", ParamUtils.createNonce());
        params.put("timestamp", String.valueOf(System.currentTimeMillis()));
        params.put("version", "v2");

        params.put("secretId", accessKeyId);
        params.put("businessId", businessId);

        params.put("templateId", templateId);
        params.put("mobile", to);

        // 如果要发送国际短信，则需要指明国际电话区号。如果不是国际短信，则不要指定此参数
        if (StringUtils.isNotBlank(countryCallingCode)) {
            params.put("internationalCode", countryCallingCode);
        }

        params.put("paramType", "json");
        params.put("params", ParamUtils.serializeVariables(variables));

        // 在最后一步生成此次请求的签名
        params.put("signature", ParamUtils.genSignature(accessKeySecret, params));

        return params;
    }
}
