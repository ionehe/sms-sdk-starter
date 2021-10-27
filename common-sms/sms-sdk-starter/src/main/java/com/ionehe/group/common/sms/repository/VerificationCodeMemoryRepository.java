package com.ionehe.group.common.sms.repository;

import cn.hutool.core.util.StrUtil;
import com.ionehe.group.common.sms.entity.VerificationCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 验证码内存储存实现
 *
 * @author xiu
 *
 */
@Slf4j
@Repository
public class VerificationCodeMemoryRepository implements IVerificationCodeRepository {

    private final Map<String, VerificationCode> cache = new ConcurrentHashMap<>();

    @Override
    public VerificationCode findOne(String phone, String identificationCode) {
        String key = key(phone, identificationCode);
        VerificationCode verificationCode = cache.get(key);

        if (verificationCode == null) {
            log.debug("verificationCode is null, key: {}", key);
            return null;
        }

        LocalDateTime expirationTime = verificationCode.getExpirationTime();
        if (expirationTime != null && expirationTime.isBefore(LocalDateTime.now())) {
            log.debug("verificationCode is not null, but timeout, key: {}", key);
            cache.remove(key);
            return null;
        }

        return verificationCode;
    }

    @Override
    public void save(VerificationCode verificationCode) {
        String key = key(verificationCode.getPhone(), verificationCode.getIdentificationCode());

        cache.put(key, verificationCode);
    }

    @Override
    public void delete(String phone, String identificationCode) {
        cache.remove(key(phone, identificationCode));
    }

    private String key(String phone, String identificationCode) {
        if (StrUtil.isBlank(identificationCode)) {
            return phone;
        }

        return phone + "_" + identificationCode;
    }

}
