package com.ziroom.qa.quality.defende.provider.vo;

import lombok.Data;

@Data
public class TestReportBugListVo {
    /**
     * bug编号
     */
    private String issueKey;
    /**
     * bug描述
     */
    private String description;

    /**
     * bug状态id
     */
    private Long bugStatusId;

    /**
     * bug状态中文描述
     */
    private String bugStatus;


    /**
     * bug优先级id
     */
    private Long priorityId;

    /**
     * bug优先级中文描述
     */
    private String priority;

    /**
     * bug所属端id
     */
    private String bugEndType;

    /**
     * bug所属端中文描述
     */
    private String bugEndTypeStr;

    private String caseIdAndVersion;
    private String executionVersion;
    private String executorName;
}
