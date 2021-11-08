package com.ziroom.qa.quality.defende.provider.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.ziroom.qa.quality.defende.provider.vo.TestReportBugListVo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * <p>
 *
 * </p>
 *
 * @author liangh4
 * @since 2021-05-26
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("daily_test_report_type")
@ApiModel(value="DailyTestReportType对象", description="")
public class DailyTestReportType implements Serializable {

    private static final long serialVersionUID=1L;

    @ApiModelProperty(value = "自增主键")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "创建人")
    private String createUser;

    @ApiModelProperty(value = "创建时间")
    private LocalDateTime createTime;

    @ApiModelProperty(value = "更新人")
    private String updateUser;

    @ApiModelProperty(value = "更新时间")
    private LocalDateTime updateTime;

    @ApiModelProperty(value = "日报id")
    private Long dailyId;

    @ApiModelProperty(value = "测试执行类型id")
    private Long testTaskId;

    @ApiModelProperty(value = "测试执行类型 例如：回归测试，功能测试")
    private String testExecutionType;

    @ApiModelProperty(value = "测试执行任务名称")
    private String testTaskName;

    @ApiModelProperty(value = "测试用例总数")
    private Integer testcaseCount;

    @ApiModelProperty(value = "执行成功总数")
    private Integer runSuccessCount;

    @ApiModelProperty(value = "执行失败总数")
    private Integer runFailCount;

    @ApiModelProperty(value = "运行跳过总数")
    private Integer runSkipCount;

    @ApiModelProperty(value = "未执行总数")
    private Integer notRunCount;


    @ApiModelProperty(value = "bug总数")
    private Integer bugCount;

    @ApiModelProperty(value = "已解决bug数")
    private Integer bugSuccessCount;

    @ApiModelProperty(value = "未解决bug数")
    private Integer bugUnresolvedCount;


    @ApiModelProperty(value = "当日用例总数")
    private Integer dailyCount;

    @ApiModelProperty(value = "当日成功总数")
    private Integer dailySuccessCount;

    @ApiModelProperty(value = "当日失败总数")
    private Integer dailyFailCount;

    @ApiModelProperty(value = "当日跳过总数")
    private Integer dailySkipCount;

    @ApiModelProperty(value = "测试用例总进度")
    @TableField(exist = false)
    private double testcaseRate;

    @ApiModelProperty(value = "当日用例总进度")
    @TableField(exist = false)
    private double dailyRate;

    @ApiModelProperty(value = "bug解决总进度")
    @TableField(exist = false)
    private double bugRate;

    @ApiModelProperty(value = "测试用例总进度")
    @TableField(exist = false)
    private String testcaseRateStr = "0%";

    @ApiModelProperty(value = "当日用例总进度")
    @TableField(exist = false)
    private String dailyRateStr = "0%";

    @ApiModelProperty(value = "bug解决总进度")
    @TableField(exist = false)
    private String bugRateStr = "0%";

    @ApiModelProperty(value = "bug信息列表")
    @TableField(exist = false)
    private List<TestReportBugListVo> bugInfoList;

    @ApiModelProperty(value = "unbug信息列表")
    @TableField(exist = false)
    private List<TestReportBugListVo> unBugInfoList;

    @ApiModelProperty(value = "testExecutionType前端页面展示是自动还是手动")
    @TableField(exist = false)
    private String testTaskType;

}
