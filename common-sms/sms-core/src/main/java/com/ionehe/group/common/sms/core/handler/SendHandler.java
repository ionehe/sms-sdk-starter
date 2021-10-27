package com.ionehe.group.common.sms.core.handler;


import cn.hutool.core.util.StrUtil;
import com.ionehe.group.common.sms.core.domain.NoticeData;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

/**
 * 发送处理
 *
 * @author xiu
 */
public interface SendHandler {

    /**
     * 发送通知
     *
     * @param noticeData 通知内容
     * @param phones     手机号列表
     * @return 是否发送成功
     */
    boolean send(NoticeData noticeData, Collection<String> phones);

    /**
     * 发送通知
     *
     * @param noticeData 通知内容
     * @param phone      手机号列表
     * @return 是否发送成功
     */
    default boolean send(NoticeData noticeData, String phone) {
        if (StrUtil.isBlank(phone)) {
            return false;
        }

        return send(noticeData, Collections.singletonList(phone));
    }

    /**
     * 发送通知
     *
     * @param noticeData 通知内容
     * @param phones     手机号列表
     * @return 是否发送成功
     */
    default boolean send(NoticeData noticeData, String... phones) {
        if (phones == null) {
            return false;
        }

        return send(noticeData, Arrays.asList(phones));
    }
}
