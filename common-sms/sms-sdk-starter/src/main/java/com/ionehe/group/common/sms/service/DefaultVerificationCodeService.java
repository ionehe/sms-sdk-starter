package com.ionehe.group.common.sms.service;

import com.ionehe.group.common.sms.entity.VerificationCode;
import com.ionehe.group.common.sms.repository.IVerificationCodeRepository;
import com.ionehe.group.common.sms.resource.SmsProperties;
import com.ionehe.group.common.sms.util.RandomUtil;
import com.ionehe.group.common.sms.core.domain.NoticeData;
import com.ionehe.group.common.sms.core.exception.PhoneIsNullException;
import com.ionehe.group.common.sms.core.exception.RetryTimeShortException;
import com.ionehe.group.common.sms.core.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * 手机验证码服务实现
 *
 * @author xiu
 */
@Service
public class DefaultVerificationCodeService implements VerificationCodeService {
    @Autowired
    private IVerificationCodeRepository repository;
    @Autowired
    private SmsProperties properties;
    @Autowired
    private NoticeService noticeService;
    @Autowired
    private ICodeGenerate codeGenerate;

    @Override
    public String find(String phone, String identificationCode) {
        if (StringUtils.isBlank(phone)) {
            return null;
        }

        phoneValidation(phone);

        VerificationCode verificationCode = repository.findOne(phone, identificationCode);

        return verificationCode == null ? null : verificationCode.getCode();
    }


    @Override
    public boolean send(String tempPhone) {
        String phone = StringUtils.trimToNull(tempPhone);

        if (phone == null) {
            throw new PhoneIsNullException();
        }

        phoneValidation(phone);

        String identificationCode = createIdentificationCode();
        VerificationCode verificationCode = repository.findOne(phone, identificationCode);

        Long expirationTime = properties.getVerificationCode().getExpirationTime();
        Long retryIntervalTime = properties.getVerificationCode().getRetryIntervalTime();

        if (verificationCode == null) {
            verificationCode = new VerificationCode();
            verificationCode.setPhone(phone);
            verificationCode.setIdentificationCode(identificationCode);

            if (expirationTime != null && expirationTime > 0) {
                verificationCode.setExpirationTime(LocalDateTime.now().plusSeconds(expirationTime));
            }
            if (retryIntervalTime != null && retryIntervalTime > 0) {
                verificationCode.setRetryTime(LocalDateTime.now().plusSeconds(retryIntervalTime));
            }

            verificationCode.setCode(codeGenerate.generate());
        } else {
            LocalDateTime retryTime = verificationCode.getRetryTime();

            if (retryTime != null) {
                long surplus =
                        retryTime.toEpochSecond(ZoneOffset.UTC) - LocalDateTime.now().toEpochSecond(ZoneOffset.UTC);

                if (surplus > 0) {
                    throw new RetryTimeShortException(surplus);
                }
            }
            verificationCode.setCode(codeGenerate.generate());
            if (expirationTime != null && expirationTime > 0) {
                verificationCode.setExpirationTime(LocalDateTime.now().plusSeconds(expirationTime));
            }
            if (retryIntervalTime != null && retryIntervalTime > 0L) {
                verificationCode.setRetryTime(LocalDateTime.now().plusSeconds(retryIntervalTime));
            }
        }

        Map<String, String> params = new HashMap<>(4);
        params.put(MSG_KEY_CODE, verificationCode.getCode());
        if (verificationCode.getIdentificationCode() != null) {
            params.put(MSG_KEY_IDENTIFICATION_CODE, verificationCode.getIdentificationCode());
        }
        if (properties.getVerificationCode().isTemplateHasExpirationTime()
                && expirationTime != null
                && expirationTime > 0) {
            params.put(MSG_KEY_EXPIRATION_TIME_OF_SECONDS, String.valueOf(expirationTime));
            params.put(MSG_KEY_EXPIRATION_TIME_OF_MINUTES, String.valueOf(expirationTime / 60));
        }

        NoticeData notice = new NoticeData();
        notice.setType(VerificationCode.TYPE);
        notice.setParams(params);

        boolean send = noticeService.send(notice, phone);
        if (!send) {
            return false;
        }
        repository.save(verificationCode);
        return true;
    }

    private String createIdentificationCode() {
        if (!properties.getVerificationCode().isUseIdentificationCode()) {
            return null;
        }

        return RandomUtil.nextString(properties.getVerificationCode().getIdentificationCodeLength());
    }

    @Override
    public boolean verify(String phone, String code, String identificationCode) {
        if (StringUtils.isAnyBlank(phone, code)) {
            return false;
        }

        phoneValidation(phone);

        VerificationCode verificationCode = repository.findOne(phone, identificationCode);

        if (verificationCode == null) {
            return false;
        }

        boolean verifyData = Objects.equals(verificationCode.getCode(), code);

        if (verifyData && properties.getVerificationCode().isDeleteByVerifySucceed()) {
            repository.delete(phone, identificationCode);
        }

        if (!verifyData && properties.getVerificationCode().isDeleteByVerifyFail()) {
            repository.delete(phone, identificationCode);
        }

        return verifyData;
    }

    private void phoneValidation(String phone) {
        if (!noticeService.phoneRegValidation(phone)) {
            throw new PhoneIsNullException();
        }
    }

}
