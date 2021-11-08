package com.ziroom.qa.quality.defende.provider.constant.enums;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public enum TestPlanStatusEnum {
    UNSUBMIT(1,"待提交"),
    PENDING(2,"待审核"),
    APPROVED(3,"审核通过"),
    REJECTED(4,"审核拒绝");


    private Integer testPlanStatus;
    private String testPlanStatusName;
    private static final Map<Integer, TestPlanStatusEnum> MAP = new HashMap<>();

    TestPlanStatusEnum(Integer testPlanStatus,String testPlanStatusName) {
        this.testPlanStatus = testPlanStatus;
        this.testPlanStatusName = testPlanStatusName;
    }

    static {
        for (TestPlanStatusEnum item : TestPlanStatusEnum.values()) {
            MAP.put(item.testPlanStatus,item);
        }
    }

    public static List<Integer> getTestCaseStatusList(){
        return new ArrayList<>(MAP.keySet());
    }

    public static Integer getTestPlanStatus(Integer key){
        return MAP.get(key).getTestPlanStatus();
    }

    public static String getTestPlanName(Integer key){
        return MAP.get(key).getTestPlanStatusName();
    }

    public Integer getTestPlanStatus() {
        return testPlanStatus;
    }

    public String getTestPlanStatusName() {
        return testPlanStatusName;
    }

    @Override
    public String toString() {
        return "TestPlanStatusEnum{" +
                "testPlanStatus='" + testPlanStatus + '\'' +
                ", testPlanStatusName='" + testPlanStatusName + '\'' +
                '}';
    }
}
