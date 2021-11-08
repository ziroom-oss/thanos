package com.ziroom.qa.quality.defende.provider.vo;

import lombok.Data;

@Data
public class TestReportSummaryVo {
    private String taskName;
    private String jiraRequirement;
    private Integer totalCount;
    private String passedRatio;
    private String failedRatio;
    private String skippedRatio;
}
