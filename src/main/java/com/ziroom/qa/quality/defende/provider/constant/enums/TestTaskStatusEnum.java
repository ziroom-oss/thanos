package com.ziroom.qa.quality.defende.provider.constant.enums;

import java.util.*;

public enum TestTaskStatusEnum {
    NOT_STARTED("notStarted","未开始"),
    RUNNING("running","进行中"),
    SUBMITED("submited","已提测"),
    COMPLETE("complete","完成"),
    LAUNCH("launch","已上线"),
    ;

    private String testsTaskStatus;
    private String testsTaskName;

    TestTaskStatusEnum(String testsTaskStatus,String testsTaskName) {
        this.testsTaskStatus = testsTaskStatus;
        this.testsTaskName = testsTaskName;
    }

    private static final Map<String,TestTaskStatusEnum> MAP = new HashMap();

    static {
        for (TestTaskStatusEnum item : TestTaskStatusEnum.values()) {
            MAP.put(item.testsTaskStatus,item);
        }
    }

    public static List<TestTaskStatusEnum> getTestTaskStatusList(){
        return new ArrayList<>(MAP.values());
    }

    public static String getStatusNameByStatus(String status){
        return Optional.ofNullable(MAP.get(status)).map(testTaskStatusEnum -> testTaskStatusEnum.getTestsTaskName()).orElse("");
    }

    public String getTestsTaskStatus() {
        return testsTaskStatus;
    }

    public String getTestsTaskName() {
        return testsTaskName;
    }

    @Override
    public String toString() {
        return "TestTaskStatusEnum{" +
                "testsTaskStatus='" + testsTaskStatus + '\'' +
                ", testsTaskName='" + testsTaskName + '\'' +
                '}';
    }
}

