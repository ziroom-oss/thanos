package com.ziroom.qa.quality.defende.provider.caseRepository.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;


@Data
public class TestCase {

    private Long id;

    private Long systemModuleId;

    /**
     * 所属项目（是一个跨平台的项目）
     */
    private Long belongTopic;

    /**
     * 测试用例名称
     */
    private String casename;

    /**
     * 优先级
     */
    private String testCaseLevel;

    /**
     * 测试用例类型
     */
    private String type;

    /**
     * 所属平台（所属端）
     */
    private String belongPlatform;

    /**
     * 前置条件
     */
    private String preCondition;

    /**
     * 执行步骤
     */
    private String step;

    /**
     * 期望结果
     */
    private String expectedResults;

    /**
     * 测试用例所属应用
     */
    private Long belongToSystem;

    /**
     * 关联所属模块的ID ： test_application_module 的id字段
     */
    private Long belongToModule;

    /**
     * 审核状态
     */

    private String testCaseStatus = "";

    /**
     * 测试用例锁定状态：true为锁定，默认为false
     */

    private Boolean caseLock;

    /**
     * 测试用例唯一Key:
     */

    private String caseKey;

    /**
     * 测试用例版本
     */

    private String version;


    /**
     * 测试用例关联需求
     */

    private String relationRequirement;

    /**
     * 测试用例上传文件URL 暂时不用
     */

    private String sourceFileUrl;

    /**
     * 测试用例上传文件自动返回KEY
     */

    private String sourceFileKey;

    /**
     * 测试用例审批人
     */

    private String approveUser;

    /**
     * 测试用例审批时间
     */

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm", timezone = "GMT+8")
    private LocalDateTime approvedTime;

    /**
     * 测试用例所属组织结构full tree path
     */

    private String ehrTreePath;

    /**
     * 测试用例备注
     */

    private String remarks;

    /**
     * 是否为最新版本flag=true
     */

    private Boolean flag;

    /**
     * 删除标记：默认值为false
     */

    private Boolean deleteFlag;

    /**
     * 测试用例创建人
     */

    private String createUser;


    /**
     * 测试用例创建时间
     */

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm", timezone = "GMT+8")
    private LocalDateTime createTime;


    /**
     * 测试用例更新人
     */
    private String updateUser;

    /**
     * 测试用例更新时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm", timezone = "GMT+8")
    private LocalDateTime updateTime;


    /**
     * UI列表展示所属组织结构
     */

    private String ehrGroup;

    /**
     * 变更类型（给统计报表使用）
     */

    private String changeType;

    /**
     * 排除已经选中的测试执行任务
     */
    @TableField(exist = false)
    private Long taskId;

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
     * 人工新增修改标识（人工新增修改不会改动下拉框信息）
     */
    @TableField(exist = false)
    private boolean peopSaveFlag = false;

    /**
     * NOT_STARTED("notStarted","未开始"),
     * PASS("pass","通过"),
     * FAIL("fail","失败"),
     * SKIP("skip","跳过");
     */
    @TableField(exist = false)
    private String executionResult;

    /**
     * 前端传入创建时间
     */
    @TableField(exist = false)
    private String createTimeVal;

    /**
     * 前端传入更新时间
     */
    @TableField(exist = false)
    private String updateTimeVal;

    /**
     * 测试用例创建人
     */
    @TableField(exist = false)
    private String createUserStr;

    /**
     * 上传校验flag，true：需要校验名称重复，false：不校验名称重复，重复的用例直接覆盖
     */
    @TableField(exist = false)
    private boolean uploadFlag = true;

}
