package com.ziroom.qa.quality.defende.provider.vo;

import lombok.Data;

@Data
public class ExecutionSummaryVo {
    /**
     * 运行成功总数
     */
    private Integer runSuccessCount;
    /**
     * 运行失败总数
     */
    private Integer runFailCount;

    /**
     * 运行总数
     */
    private Integer runCount;

    /**
     * 未运行总数
     */
    private Integer notRunCount;

    /**
     * 运行跳过总数
     */
    private Integer runSkipCount;

    // jira requirement summary
    private String requirementSummary;

    /**
     * 测试执行状态
     */
    private String taskStatusStr;

    /**
     * 测试执行名称
     */
    private String taskName;

    private boolean comButtonFlag;
}
