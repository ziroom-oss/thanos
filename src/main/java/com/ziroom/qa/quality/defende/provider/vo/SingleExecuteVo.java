package com.ziroom.qa.quality.defende.provider.vo;

import lombok.Data;

import java.util.Date;

@Data
public class SingleExecuteVo {
    private String caseKey;
    private String executionResult;
    private String executionVersion;
    private Date executionTime;
    private String executionUser;
    private long executionTaskId;
    private String relationBug;
    /**
     * 测试执行备注
     */
    private String exeRemark;
    private String env;


    private String caseName;
    private String jiraId;

//-------------------------提bug使用的属性-----------------------
    /**
     * 缺陷类型
     */
    private String bugTypeStr;

    /**
     * bug所属中心
     */
    private String bugCenterStr;

    /**
     * bug所属部门
     */
    private String bugDeptStr;

    /**
     * bug模块
     */
    private String bugModuleStr;

    /**
     * bug修复日期
     */
    private String bugTimeStr;

    /**
     * bug经办人
     */
    private String bugUserStr;

    /**
     * bug描述
     */
    private String bugSummaryStr;

    /**
     * bug等级
     */
    private String bugLevelStr;

    /**
     * bug所属端
     */
    private String bugEndTypeStr;


}
