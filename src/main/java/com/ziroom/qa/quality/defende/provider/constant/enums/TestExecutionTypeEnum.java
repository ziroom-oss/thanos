package com.ziroom.qa.quality.defende.provider.constant.enums;

import java.util.HashMap;
import java.util.Map;

public enum TestExecutionTypeEnum {
    ARTIFICIAL(1, "手动"),
    AUTO(2, "自动");

    private Integer key;
    private String value;

    TestExecutionTypeEnum(Integer key, String value) {
        this.key = key;
        this.value = value;
    }

    private static final Map<Integer, TestExecutionTypeEnum> MAP = new HashMap();

    static {
        for (TestExecutionTypeEnum item : TestExecutionTypeEnum.values()) {
            MAP.put(item.key, item);
        }
    }

    public static String getTestsTaskStatusName(Integer key) {
        return MAP.get(key) == null ? "" : MAP.get(key).getValue();
    }

    public static Map<Integer, String> getTestExeMethodMap() {
        Map<Integer, String> newMap = new HashMap<>();
        MAP.keySet().forEach(key -> {
            newMap.put(key, getTestsTaskStatusName(key));
        });
        return newMap;
    }

    public Integer getKey() {
        return key;
    }

    public void setKey(Integer key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}

