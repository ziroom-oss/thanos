package com.ziroom.qa.quality.defende.provider.vo;

import com.ziroom.qa.quality.defende.provider.execTask.entity.TaskTestCase;
import lombok.Data;

import java.util.List;

@Data
public class TestReportVo {
    private String status;
    /**
     * 测试用例总数
     */
    private Integer testcaseCount;

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

    /**
     * 测试执行率:（runFailCount+runSuccessCount+runSkipCount）/runCount
     */
    private Double testExecutionRate;
    private List<TaskTestCase> taskTestCaseList;
}
