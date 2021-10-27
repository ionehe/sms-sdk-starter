package com.ionehe.group.common.sms.service;

/**
 * 验证码生成
 *
 * @author xiu
 *
 */
@FunctionalInterface
public interface ICodeGenerate {

    /**
     * 生成验证码
     *
     * @return 验证码
     */
    String generate();
}
