package com.ht.commonactivity.entity.enums;

import com.baomidou.mybatisplus.enums.IEnum;
import com.fasterxml.jackson.annotation.JsonValue;

import java.io.Serializable;

/**
 * 测试枚举
 */
public enum DataTypeEnum implements IEnum {
    /**
     * 整形
     */
    INTEGER("Integer", "整型"),
    /**
     * 布尔类型
     */
    BOOLEAN("Boolean", "布尔类型"),
    /**
     * 数字类型
     */
    DOUBLE("Double", "数字类型"),
    /**
     * 字符串类型
     */
    STRING("String", "字符串"),
    /**
     * 常量类型
     */
    CONSTANT("CONSTANT", "常量类型");

    private String value;
    private String desc;

    DataTypeEnum(final String value, final String desc) {
        this.value = value;
        this.desc = desc;
    }

    @Override
    public Serializable getValue() {
        return this.value;
    }

    @JsonValue
    public String getDesc(){
        return this.desc;
    }
}
