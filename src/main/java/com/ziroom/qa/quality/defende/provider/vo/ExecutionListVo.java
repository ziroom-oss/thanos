package com.ziroom.qa.quality.defende.provider.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

@Data
public class ExecutionListVo {
    private Long caseId;
    private String caseName;
    private String caseLevel;
    private String executionUser;
    private String executionResult;
    private String relationBug;
    private String executionVersion;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm", timezone = "GMT+8")
    private Date executionTime;
    private Long taskId;
}
