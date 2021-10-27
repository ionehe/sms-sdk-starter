package com.ionehe.group.common.sms.qcloud.handler;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.github.qcloudsms.SmsMultiSender;
import com.github.qcloudsms.SmsMultiSenderResult;
import com.google.common.base.Throwables;
import com.ionehe.group.common.sms.config.SmsConfiguration;
import com.ionehe.group.common.sms.config.SmsVendors;
import com.ionehe.group.common.sms.qcloud.flag.QCloudKeyFlags;
import com.ionehe.group.common.sms.core.domain.NoticeData;
import com.ionehe.group.common.sms.core.exception.ClientException;
import com.ionehe.group.common.sms.core.handler.SendHandler;
import com.ionehe.group.common.sms.core.utils.StringUtils;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * 腾讯云发送处理
 *
 * @author xiu
 */
@Slf4j
public class QCloudSendHandler implements SendHandler {

    private static final String DEFAULT_NATION_CODE = "86";

    private final String signName;

    private final Map<String, Integer> templates;

    private final SmsMultiSender sender;

    private final Map<String, Map<String, String>> paramsOrder;


    public QCloudSendHandler(SmsConfiguration conf) {
        Map<String, Object> vendor = (Map<String, Object>) conf.getVendors().get(SmsVendors.qcloud);
        signName = QCloudKeyFlags.FLAG_SIGN.get(vendor);
        templates = QCloudKeyFlags.FLAG_TEMPLATES.get(vendor);
        paramsOrder = QCloudKeyFlags.FLAG_PARAMS_ORDERS.get(vendor);
        Integer appId = QCloudKeyFlags.FLAG_APP_ID.get(vendor);
        String appKey = QCloudKeyFlags.FLAG_APP_KEY.get(vendor);

        if (StrUtil.isBlank(signName) || CollUtil.isEmpty(templates) || CollUtil.isEmpty(paramsOrder)
                || Objects.isNull(appId) || Objects.isNull(appKey)) {
            throw new ClientException();
        }

        sender = new SmsMultiSender(appId, appKey);

        log.info("QCloudSendHandler init success!");
    }

    @Override
    public boolean send(NoticeData noticeData, Collection<String> phones) {
        String type = noticeData.getType();

        Integer templateId = templates.get(type);

        if (templateId == null) {
            log.debug("templateId invalid");
            return false;
        }

        ArrayList<String> params = new ArrayList<>();

        Map<String, String> pOrder = paramsOrder.get(type);

        if (CollUtil.isNotEmpty(pOrder)) {
            Map<String, String> paramMap = noticeData.getParams();
            for (String paramName : pOrder.values()) {
                String paramValue = paramMap.get(paramName);

                params.add(paramValue);
            }
        }

        Map<String, ArrayList<String>> phoneMap = new HashMap<>(phones.size());

        for (String phone : phones) {
            if (StringUtils.isBlank(phone)) {
                continue;
            }
            if (phone.startsWith("+")) {
                String[] values = phone.split(" ");

                if (values.length == 1) {
                    getList(phoneMap, DEFAULT_NATION_CODE).add(phone);
                } else {
                    String nationCode = values[0];
                    String phoneNumber = StringUtils.join(values, "", 1, values.length);

                    getList(phoneMap, nationCode).add(phoneNumber);
                }

            } else {
                getList(phoneMap, DEFAULT_NATION_CODE).add(phone);
            }
        }

        return phoneMap.entrySet().parallelStream()
                .allMatch(entry -> send0(templateId, params, entry.getKey(), entry.getValue()));
    }

    private Collection<String> getList(Map<String, ArrayList<String>> phoneMap, String nationCode) {
        return phoneMap.computeIfAbsent(nationCode, k -> new ArrayList<>());
    }

    private boolean send0(int templateId, ArrayList<String> params, String nationCode, ArrayList<String> phones) {
        log.info("send sms start[param:{}, phones:{}]", params, phones);

        try {
            SmsMultiSenderResult result = sender
                    .sendWithParam(nationCode, phones, templateId, params, signName, "", "");

            if (result.result != 0) {
                log.warn("send sms fail[code={}, errMsg={}]", result.result, result.errMsg);
                return false;
            }

            log.info("send sms success");
            return true;
        } catch (Exception e) {
            log.error("send sms error! cause:{}", Throwables.getStackTraceAsString(e));
        }

        return false;
    }
}
