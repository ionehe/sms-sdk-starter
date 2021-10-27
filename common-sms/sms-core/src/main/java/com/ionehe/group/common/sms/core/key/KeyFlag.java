package com.ionehe.group.common.sms.core.key;

import java.beans.ConstructorProperties;
import java.util.Map;
import java.util.Properties;

/**
 * Copyright (c) 2021 ionehe.com
 * Date: 2021/4/21
 * Time: 1:22 下午
 *
 * @author 2021年 <a href="mailto:a@ionehe.com">秀</a>
 * 【KeyFlag】
 */
public class KeyFlag {
    private String key;
    private boolean necessary;
    private Object value;

    @ConstructorProperties({"key", "necessary", "value"})
    public KeyFlag(String key, boolean necessary, Object value) {
        this.key = key;
        this.necessary = necessary;
        this.value = value;
    }

    public String getKey() {
        return this.key;
    }

    public boolean isNecessary() {
        return this.necessary;
    }

    public Object getValue() {
        return this.value;
    }

    public KeyFlag(String key, boolean necessary) {
        this.key = key;
        this.necessary = necessary;
    }


    public <T> T get(Properties properties) {
        return (null != properties && properties.containsKey(this.key)) ? (T) properties.get(this.key) : (this.necessary ? (T) this.value : null);
    }


    public <T> T get(Map properties) {
        return (null != properties && properties.containsKey(this.key)) ? (T) properties.get(this.key) : (this.necessary ? (T) this.value : null);
    }
}
