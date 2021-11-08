package com.ziroom.qa.quality.defende.provider.entity;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;

/**
 * <p>
 *
 * </p>
 *
 * @author liangh4
 * @since 2021-04-26
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class TestPlan implements Serializable {

    private static final long serialVersionUID = 1111111111111L;

    /**
     * 自增主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 测试计划Key
     */
    private String testPlanKey;

    /**
     * 计划名称
     */
    private String planName;

    /**
     * 关联需求
     */
    private String relationRequirement;

    /**
     * 测试环境
     */
    private String testEnvironment;

    /**
     * 测试范围
     */
    private String testRange;

    /**
     * 测试策略
     */
    private String testStrategy;

    /**
     * 测试计划负责人
     */
    private String testPlanMaster;
    /**
     * 测试计划负责人表现值
     */
    @TableField(exist = false)
    private String testPlanMasterStr;
    /**
     * 测试计划开始时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm", timezone = "GMT+8")
    private LocalDateTime testPlanStartTime;

    /**
     * 测试计划结束时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm", timezone = "GMT+8")
    private LocalDateTime testPlanEndTime;

    /**
     * 测试计划人员
     */
    private String testPersionList;

    /**
     * 测试风险
     */
    private String testRisk;

    /**
     * 测试计划版本
     */
    private String testPlanVersion;

    /**
     * 计划状态：1.待提交 2.待审核 3.审核通过 4.审核拒绝
     */
    private Integer testPlanStatus;

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
     * 删除标记 1 删除 0 未删除
     */
    private Boolean deleteFlag;

    /**
     * 锁定标记 1 锁定 0 未锁定
     */
    private Boolean lockFlag;

    /**
     * 是否最新标记 1 最新 0 非最新
     */
    private Boolean latestFlag;

    /**
     * 备注
     */
    private String remark;

    /**
     * 审核人
     */
    private String approveUser;

    /**
     * 审核时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm", timezone = "GMT+8")
    private LocalDateTime approvedTime;

    /**
     * 前端传入计划时间起
     */
    @TableField(exist = false)
    private String testPlanStartVal;

    /**
     * 前端传入计划时间止
     */
    @TableField(exist = false)
    private String testPlanEndVal;

    /**
     * 提交按钮 true 保存并提交，false 提交
     */
    @TableField(exist = false)
    private boolean submitFlag;

    /**
     * 计划状态：1.待提交 2.待审核 3.审核通过 4.审核拒绝
     */
    @TableField(exist = false)
    private String testPlanStatusStr;

    /**
     * 测试策略
     */
    @TableField(exist = false)
    private String testStrategyStr;

    /**
     * 测试环境
     */
    @TableField(exist = false)
    private String testEnvironmentStr;

    /**
     * 测试计划时间起止
     */
    @TableField(exist = false)
    private String testPlanTime;

}
