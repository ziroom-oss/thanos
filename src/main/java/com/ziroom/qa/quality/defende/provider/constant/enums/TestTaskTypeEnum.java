package com.ziroom.qa.quality.defende.provider.constant.enums;

import java.util.*;

public enum TestTaskTypeEnum {

    /**
     * 测试执行任务类型
     */
    INTEGRATION_TESTING("integrationTesting", "集成测试"),
    SMOKE_TESTING("smokeTesting", "冒烟测试"),
    FUNCTIONAL_TESTING("functionalTesting", "功能测试"),
    REGRESSION_TESTING("regressionTesting", "回归测试"),
    COMPATIBILITY_TESTING("compatibilityTesting", "兼容性测试"),
    PERFORMANCE_TESTING("performanceTesting", "性能测试"),
    SECURITY_TESTING("securityTesting", "安全性测试");

    private String testsTaskType;
    private String typeName;

    private static final Map<String, TestTaskTypeEnum> MAP = new HashMap<>();

    static {
        for (TestTaskTypeEnum item : TestTaskTypeEnum.values()) {
            MAP.put(item.testsTaskType, item);
        }
    }

    TestTaskTypeEnum(String testsTaskType, String typeName) {
        this.testsTaskType = testsTaskType;
        this.typeName = typeName;

    }

    public static String getTypeNameByType(String type) {
        return Optional.ofNullable(MAP.get(type)).map(testTaskTypeEnum -> testTaskTypeEnum.getTypeName()).orElse("");
    }


    public static List<TestTaskTypeEnum> getTestTaskTypeList() {
        return new ArrayList<>(MAP.values());
    }

    public String getTestsTaskType() {
        return testsTaskType;
    }


    public String getTypeName() {
        return typeName;
    }

}
