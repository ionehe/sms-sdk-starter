package com.ionehe.group.common.sms.core.exception;

import java.util.Locale;

/**
 * 验证失败
 *
 * @author xiu
 *
 */
public class ClientException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    private static final String DEFAULT_MSG;

    static {
        Locale locale = Locale.getDefault();

        if (Locale.CHINA.equals(locale)) {
            DEFAULT_MSG = "初始化客户端失败，配置参数异常";
        } else {
            DEFAULT_MSG = "Client init failed";
        }
    }

    /**
     * 初始化客户端失败
     */
    public ClientException() {
        super(DEFAULT_MSG);
    }
}
