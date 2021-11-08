package com.ziroom.qa.quality.defende.provider.vo;

import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class BatchExecuteVo {
    private List<Long> idList;

    private List<String> caseKeyList;
    private String executionResult;
    private String executionVersion = "v1.0";
    private Date executionTime;
    private String executionUser;
    private long executionTaskId;
    /**
     * 测试执行备注
     */
    private String exeRemark;
    /**
     * 测试执行环境标识
     */
    private String env = "daily";
}
