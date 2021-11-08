package com.ziroom.qa.quality.defende.provider.constant.enums;

public enum TestExecutionStatusEnum {
    PASS(0, "pass"),
    FAIL(1, "fail"),
    SKIP(2, "skip");

    private Integer value;
    private String description;

    TestExecutionStatusEnum(Integer value, String description) {
        this.value = value;
        this.description = description;
    }

    public Integer getValue() {
        return value;
    }

    public String getDescription() {
        return description;
    }
}
