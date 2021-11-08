package com.ziroom.qa.quality.defende.provider.execTask.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.ziroom.qa.quality.defende.provider.constant.enums.TestExecutionTypeEnum;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 测试任务关联测试用例
 */
@Data
public class TaskTestCase {
    /**
     * 主键ID
     */
    private Long id;

    /**
     * 测试执行任务ID
     */
    private Long taskId;

    /**
     * 测试用例ID
     */
    private Long testCaseId;


    /**
     * 测试用例名称
     */
    private String casename;


    /**
     * 创建人
     */
    private String createUser;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm", timezone = "GMT+8")
    private LocalDateTime createTime;

    /**
     * 更新人
     */
    private String updateUser;

    /**
     * 更新时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm", timezone = "GMT+8")
    private LocalDateTime updateTime;

    // 最后执行人
    private String executionUser;

    // 最后执行结果
    private String executionResult;

    // 最后关联 bug
    private String relationBug;

    // 执行版本号
    private String executionVersion;

    // 最后执行时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm", timezone = "GMT+8")
    private LocalDateTime executionTime;

    // 执行次数
    private Long executionNumber;

    private Boolean deleteFlag;

    /**
     * 测试用例key，测试用例唯一标识
     */
    private String caseKey;

    /**
     * 测试执行备注
     */
    private String exeRemark;

    /**
     * 测试执行方法，1：人工，2：自动
     */
    private Integer testExecutionType = 1;

    /**
     * 用例应用名
     */
    private String applicationName;

    /**
     * requestParam
     */
    @TableField(exist = false)
    private String realInParam;

    /**
     * reqHeader
     */
    @TableField(exist = false)
    private String reqHeader;

    /**
     * reqBody
     */
    @TableField(exist = false)
    private String reqBody;

    /**
     * 真实结果
     */
    @TableField(exist = false)
    private String actualResult;

    /**
     * 请求地址
     */
    @TableField(exist = false)
    private String url;

    /**
     * 请求地址uri
     */
    @TableField(exist = false)
    private String requestUri;

    /**
     * 请求环境
     */
    @TableField(exist = false)
    private String env;

    /**
     * 请求方式
     */
    @TableField(exist = false)
    private String requestType;

    /**
     * 耗时（毫秒）
     */
    @TableField(exist = false)
    private String responseTime;

    /**
     * 断言
     */
    @TableField(exist = false)
    private String assertRecordList;

    // 查询的 case 类型
    @TableField(exist = false)
    private String type;

    // 查询的 case 所属模块
    @TableField(exist = false)
    private String belongToModule;

    /**
     * 测试执行方法，1：人工，2：自动
     */
    @TableField(exist = false)
    private String testExecutionTypeStr = TestExecutionTypeEnum.getTestExeMethodMap().get(testExecutionType);


    @TableField(exist = false)
    private String testCaseLevelStr;

    @TableField(exist = false)
    private String testCaseStatusStr;

    @TableField(exist = false)
    private String belongPlatformStr;

    @TableField(exist = false)
    private String typeStr;

    @TableField(exist = false)
    private String belongTopicStr;

    @TableField(exist = false)
    private String belongToSystemStr;

    @TableField(exist = false)
    private String belongToModuleStr;

    @TableField(exist = false)
    private String moduleTreePath;

    /**
     * 所属平台（所属端）
     */
    @TableField(exist = false)
    private String belongPlatform;
    /**
     * 测试用例关联需求
     */
    @TableField(exist = false)
    private String relationRequirement;

    /**
     * 协议类型
     */
    @TableField(exist = false)
    private String protocolType;

    /**
     * 测试用例等级
     */
    @TableField(exist = false)
    private String testCaseLevel;

    /**
     * 前置条件
     */
    @TableField(exist = false)
    private String preCondition;

    /**
     * 执行步骤
     */
    @TableField(exist = false)
    private String step;

    /**
     * 期望结果
     */
    @TableField(exist = false)
    private String expectedResults;

    /**
     * 测试用例版本
     */
    @TableField(exist = false)
    private String version;

    /**
     * bug连接地址
     */
    @TableField(exist = false)
    private String relationBugUrl;
}
