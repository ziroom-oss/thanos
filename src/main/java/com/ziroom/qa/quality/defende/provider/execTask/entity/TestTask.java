package com.ziroom.qa.quality.defende.provider.execTask.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.ziroom.qa.quality.defende.provider.constant.enums.TestExecutionTypeEnum;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 测试任务
 */
@Data
public class TestTask {
    /**
     * 主键ID
     */
    private Long id;

    /**
     * 任务名称
     */
    private String taskName;

    /**
     * 任务状态
     */
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

    /**
     * 测试执行类型
     */
    private String taskType;

    /**
     * 测试执行负责人
     */
    private String testTaskMaster;

    /**
     * 测试执行人,多人用逗号分隔
     */
    private String testTaskExecutors;

    /**
     * 关联需求
     */
    private String relationRequirement;

    /**
     * 组织结构
     */
    private String ehrTreePath;

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

    /**
     * 组织结构中文名称
     */
    private String ehrGroup;

    /**
     * 测试执行方法，1：人工，2：自动
     */
    private Integer testExecutionType = TestExecutionTypeEnum.ARTIFICIAL.getKey();

    /**
     * 终端类型：服务端、ios、android、fe
     */
    private String endType;

    /**
     * 分支名称
     */
    private String branchName;

    /**
     * 移动端的appid
     */
    private String appId;

    /**
     * 父级jiraid
     */
    private String parentJiraId;

    /**
     * 模板标识（默认false）
     */
    private Boolean templateFlag;

    /**
     * 必验项标识，如果是必验项（统计使用）
     * 1.代表方舟平台release的标识
     */
    private String checkTag;

    @TableField(exist = false)
    private String testExecutionTypeStr = TestExecutionTypeEnum.getTestExeMethodMap().get(testExecutionType);

    @TableField(exist = false)
    private Long testTopicId;

    /**
     * 运行按钮flag
     */
    @TableField(exist = false)
    private boolean runButtonFlag = true;
    /**
     * 编辑按钮flag
     */
    @TableField(exist = false)
    private boolean editButtonFlag = true;
    /**
     * 删除按钮flag
     */
    @TableField(exist = false)
    private boolean delButtonFlag = true;
    /**
     * 完成按钮flag
     */
    @TableField(exist = false)
    private boolean completeButtonFlag = true;
    /**
     * 重启按钮flag
     */
    @TableField(exist = false)
    private boolean restartButtonFlag = true;

    @TableField(exist = false)
    public String createUserStr;

    @TableField(exist = false)
    private String startTimeVal;

    @TableField(exist = false)
    private String endTimeVal;

    /**
     * 测试执行负责人,表现值
     */
    @TableField(exist = false)
    private String testTaskMasterStr;

    /**
     * 测试执行人,多人用逗号分隔，表现值
     */
    @TableField(exist = false)
    private String testTaskExecutorsStr;

    /**
     * 测试执行类型
     */
    @TableField(exist = false)
    private String taskTypeStr;

    /**
     * 任务状态
     */
    @TableField(exist = false)
    private String statusStr;
}
