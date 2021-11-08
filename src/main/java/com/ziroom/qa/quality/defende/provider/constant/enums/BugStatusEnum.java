package com.ziroom.qa.quality.defende.provider.constant.enums;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public enum BugStatusEnum {
    OPEN(1L,"打开"),
    INPROGRES(3L,"处理中"),
    REOPENED(4L,"重新打开"),
    RESOLVED(5L,"已关闭"),
    CLOSED(6L,"已解决");

    private Long statusId;
    private String statusValue;
    private static final Map<Long, BugStatusEnum> MAP = new HashMap<>();


    BugStatusEnum(Long statusId, String statusValue) {
        this.statusId = statusId;
        this.statusValue = statusValue;
    }

    static {
        for (BugStatusEnum item : BugStatusEnum.values()) {
            MAP.put(item.statusId,item);
        }
    }

    public Long getStatusId() {
        return statusId;
    }

    public String getStatusValue() {
        return statusValue;
    }

    public static List<Long> getStatusList(){
        return new ArrayList<>(MAP.keySet());
    }

    public static String getStatusValueByKey(Long key){
        return MAP.get(key).getStatusValue();
    }

}

