package com.ziroom.qa.quality.defende.provider.constant.enums;

import java.util.*;

public enum TestCaseStatusEnum {

    PENDING("pending","待审批"),
    APPROVED("approved","审批通过"),
    REJECTED("rejected","审批拒绝");


    private String testCaseStatus;
    private String testCaseStatusName;
    private static final Map<String, TestCaseStatusEnum> MAP = new HashMap<>();

    TestCaseStatusEnum(String testCaseStatus,String testCaseStatusName) {
        this.testCaseStatus = testCaseStatus;
        this.testCaseStatusName = testCaseStatusName;
    }

    static {
        for (TestCaseStatusEnum item : TestCaseStatusEnum.values()) {
            MAP.put(item.testCaseStatus,item);
        }
    }

    public static List<String> getTestCaseStatusList(){
        return new ArrayList<>(MAP.keySet());
    }

    public static List<TestCaseStatusEnum> getTestCaseStatusEnumList(){
        return new ArrayList<>(MAP.values());
    }

    public static String getTestCaseStatus(String key){
        return MAP.get(key).getTestCaseStatus();
    }

    public static String getTestCaseName(String key){
        return MAP.get(key).getTestCaseStatusName();
    }


    public String getTestCaseStatus() {
        return testCaseStatus;
    }

    public String getTestCaseStatusName() {
        return testCaseStatusName;
    }

}
