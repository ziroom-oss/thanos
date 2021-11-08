package com.ziroom.qa.quality.defende.provider.vo.outvo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SendTaskMsgVo {

    /**
     * jiraid
     */
    private String jiraId;

    /**
     * 测试执行名称
     */
    private String taskName;

    /**
     * 分支名称
     */
    private String branchName;

    /**
     * 终端类型：
     * service
     * FE
     * Android
     * IOS
     */
    private String endType;

    /**
     * 开发人员邮箱前缀 ps:liangh4
     */
    private String devUserName;

    /**
     * 测试人员邮箱前缀 ps:liangh4
     */
    private String testUserName;

    /**
     * 开发人员名称
     */
    private String devUserNameStr;

    /**
     * 测试人员名称
     */
    private String testUserNameStr;

    /**
     * jira地址
     */
    private String jiraUrl;

    /**
     * jira描述
     */
    private String jiraDesc;

    private String testTaskUrl;

    /**
     * 用例执行数
     */
    private String exeCaseStr;

    /**
     * 测试执行次数
     */
    private String taskCountStr;

    /**
     * 昨日新增数
     */
    private String creCaseStr;

    /**
     * 自动化执行成功率
     */
    private String taskRateStr;

    /**
     * 关联测试用例结果
     */
    private String testCaseResult;
}
