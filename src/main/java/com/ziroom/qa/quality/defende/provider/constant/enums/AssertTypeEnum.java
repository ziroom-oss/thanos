package com.ziroom.qa.quality.defende.provider.constant.enums;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public enum AssertTypeEnum {
    CONTAIN(0L,"包含字符串"),
    JSONPATH(1L,"JsonPath"),;

    private Long key;
    private String value;
    private static final Map<Long, AssertTypeEnum> MAP = new HashMap<>();


    AssertTypeEnum(Long key, String value) {
        this.key = key;
        this.value = value;
    }

    static {
        for (AssertTypeEnum item : AssertTypeEnum.values()) {
            MAP.put(item.key,item);
        }
    }

    public Long getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }

    public static List<Long> getLevelList(){
        return new ArrayList<>(MAP.keySet());
    }

    public static String getLevelValueByKey(Long key){
        return MAP.get(key).getValue();
    }

}

