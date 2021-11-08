package com.ziroom.qa.quality.defende.provider.execTask.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.ziroom.qa.quality.defende.provider.constant.enums.TestExecutionTypeEnum;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 测试执行结果
 */
@Data
public class TestExecution {
    /**
     * 主键ID
     */
    private Long id;

    /**
     * 测试用例ID
     */
    private Long caseId;

    /**
     * 测试执行的任务ID
     */
    private Long executionTaskId;

    /**
     * 执行结果
     */
    private String executionResult;

    /**
     * 执行版本
     */
    private String executionVersion;

    /**
     * 关联BUG
     */
    private String relationBug;

    /**
     * 执行时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm", timezone = "GMT+8")
    private LocalDateTime executionTime;

    /**
     * 执行人
     */
    private String executionUser;

    /**
     * 用例是否为最新执行
     */
    private Integer isNew;

    /**
     * bug状态id
     */
    private Long bugStatusId;

    /**
     * bug优先级id
     */
    private Long bugLevelId;

    /**
     * 测试用例key，测试用例唯一标识
     */
    private String caseKey;

    /**
     * 测试执行备注
     */
    private String exeRemark;

    /**
     * requestParam
     */
    private String realInParam;

    /**
     * reqHeader
     */
    private String reqHeader;

    /**
     * reqBody
     */
    private String reqBody;

    /**
     * 真实结果
     */
    private String actualResult;

    /**
     * 请求地址
     */
    private String url;

    /**
     * 请求环境
     */
    private String env;

    /**
     * 请求方式
     */
    private String requestType;

    /**
     * 耗时（毫秒）
     */
    private String responseTime;

    /**
     * 测试执行方法，1：人工，2：自动
     */
    private Integer testExecutionType = 1;

    /**
     * 断言结果
     */
    private String assertRecordList;

    /**
     * 应用名称
     */
    private String applicationName;

    /**
     * 测试执行方法，1：人工，2：自动
     */
    @TableField(exist = false)
    private String testExecutionTypeStr = TestExecutionTypeEnum.getTestExeMethodMap().get(testExecutionType);


    @TableField(exist = false)
    private String testCaseVesion;

    @TableField(exist = false)
    private List<Long> testCaseIdList;

    @TableField(exist = false)
    private List<String> testCaseKeyList;

}
