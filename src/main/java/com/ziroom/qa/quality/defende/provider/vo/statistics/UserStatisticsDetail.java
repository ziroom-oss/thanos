package com.ziroom.qa.quality.defende.provider.vo.statistics;

import lombok.Data;

@Data
public class UserStatisticsDetail {
    /**
     * 组织结构
     */
    private String deptCode;

    /**
     * 组织结构treePath
     */
    private String treePath;

    /**
     * 用户名
     */
    private String userName;

    /**
     * 用户邮箱前缀
     */
    private String emailPre;

    /**
     * 用户类型 类型 001 正式 002 外包
     */
    private String userType;

    /**
     * 员工id
     */
    private String emplId;

    /**
     * 测试用例统计总数
     */
    private int testCaseCount;

    /**
     * 测试执行统计总数
     */
    private int testExecutionCount;
}
