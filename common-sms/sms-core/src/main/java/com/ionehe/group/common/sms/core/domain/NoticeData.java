package com.ionehe.group.common.sms.core.domain;

import lombok.Data;

import java.util.Map;

/**
 * 通知内容
 *
 * @author xiu
 *
 */
@Data
public class NoticeData {

    /**
     * 类型
     */
    private String type;

    /**
     * 参数列表
     */
    private Map<String, String> params;
}
