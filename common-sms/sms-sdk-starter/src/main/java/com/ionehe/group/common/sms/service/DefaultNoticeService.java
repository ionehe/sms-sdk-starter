package com.ionehe.group.common.sms.service;

import com.ionehe.group.common.sms.resource.SmsProperties;
import com.ionehe.group.common.sms.core.domain.NoticeData;
import com.ionehe.group.common.sms.core.handler.SendHandler;
import com.ionehe.group.common.sms.core.utils.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 短信通知服务实现
 *
 * @author xiu
 */
@Slf4j
@Service
public class DefaultNoticeService implements NoticeService {
    @Autowired
    private SendHandler sendHandler;
    @Autowired
    private SmsProperties properties;


    @Override
    public boolean phoneRegValidation(String phone) {
        return StringUtils.isNotBlank(phone) && (StringUtils.isBlank(properties.getReg()) || phone
                .matches(properties.getReg()));
    }

    @Override
    public boolean send(NoticeData noticeData, Collection<String> phones) {
        if (noticeData == null) {
            log.debug("noticeData is null");
            return false;
        }

        if (phones == null || phones.isEmpty()) {
            log.debug("phones is empty");
            return false;
        }

        List<String> phoneList = phones.stream().filter(this::phoneRegValidation).collect(Collectors.toList());

        if (phoneList.isEmpty()) {
            log.debug("after filter phones is empty");
            return false;
        }
        return sendHandler.send(noticeData, phones);
    }
}
