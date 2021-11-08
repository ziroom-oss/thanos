package com.ziroom.qa.quality.defende.provider.constant.enums;

/**
 * @Author: yinm5
 * @Description: 用例执行结果枚举
 * @Date: 11:00 2018/6/6
 */
public enum ExecutionResultEnum {
    SUCCESS("SUCCESS"), FAILURE("FAILURE"), SKIPPED("SKIPPED"), NOTRUN("NOTRUN");

    private String status;

    ExecutionResultEnum(String status) {
        this.status = status;
    }

    @Override
    public String toString(){
        return status;
    }
}
