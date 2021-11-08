package com.ziroom.qa.quality.defende.provider.constant.enums;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public enum BugLevelEnum {
    P0(10001L,"极高"),
    P1(10002L,"高"),
    P2(10003L,"中"),
    P3(10004L,"低");

    private Long levelId;
    private String levelValue;
    private static final Map<Long, BugLevelEnum> MAP = new HashMap<>();


    BugLevelEnum(Long levelId, String levelValue) {
        this.levelId = levelId;
        this.levelValue = levelValue;
    }

    static {
        for (BugLevelEnum item : BugLevelEnum.values()) {
            MAP.put(item.levelId,item);
        }
    }

    public Long getLevelId() {
        return levelId;
    }

    public String getLevelValue() {
        return levelValue;
    }

    public static List<Long> getLevelList(){
        return new ArrayList<>(MAP.keySet());
    }

    public static String getLevelValueByKey(Long key){
        return MAP.get(key).getLevelValue();
    }

}

