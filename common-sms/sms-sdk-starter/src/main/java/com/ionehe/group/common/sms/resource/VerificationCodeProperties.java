package com.ionehe.group.common.sms.resource;

import lombok.Data;

/**
 * 验证码配置
 *
 * @author xiu
 *
 */
@Data
public class VerificationCodeProperties {

    /**
     * 验证码过期时间,小于等于0表示不过期,单位秒
     */
    private Long expirationTime;

    /**
     * 重新发送验证码间隔时间,小于等于0表示不启用,单位秒
     */
    private Long retryIntervalTime;

    /**
     * 验证码长度
     */
    private int codeLength = 6;

    /**
     * 是否使用识别码
     */
    private boolean useIdentificationCode = false;

    /**
     * 识别码长度
     */
    private int identificationCodeLength = 3;

    /**
     * 验证成功是否删除验证码
     */
    private boolean deleteByVerifySucceed = true;

    /**
     * 验证失败是否删除验证码
     */
    private boolean deleteByVerifyFail = false;

    /**
     * 模板中是否包含过期时间
     */
    private boolean templateHasExpirationTime = false;
}
