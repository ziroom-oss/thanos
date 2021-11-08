package com.ziroom.qa.quality.defende.provider.constant.enums;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public enum TestTaskCaseStatusEnum {
    NOT_STARTED("notStarted","未开始"),
    PASS("pass","通过"),
    FAIL("fail","失败"),
    SKIP("skip","跳过");

    private String testsTaskStatus;
    private String testsTaskName;

    TestTaskCaseStatusEnum(String testsTaskStatus, String testsTaskName) {
        this.testsTaskStatus = testsTaskStatus;
        this.testsTaskName = testsTaskName;
    }

    private static final Map<String, TestTaskCaseStatusEnum> MAP = new HashMap();

    static {
        for (TestTaskCaseStatusEnum item : TestTaskCaseStatusEnum.values()) {
            MAP.put(item.testsTaskStatus,item);
        }
    }

    public static List<String> getTestTaskStatusList(){
        return new ArrayList<>(MAP.keySet());
    }

    public static String getTestsTaskStatusName(String key){
        return MAP.get(key)==null?"":MAP.get(key).getTestsTaskName();
    }

    public static Map<String,String> getTestExecutionMap(){
        Map<String,String> newMap = new HashMap<>();
        MAP.keySet().forEach(key->{
            newMap.put(key,getTestsTaskStatusName(key));
        });
        return newMap;
    }

    public String getTestsTaskStatus() {
        return testsTaskStatus;
    }

    public String getTestsTaskName() {
        return testsTaskName;
    }

}

