package com.ziroom.qa.quality.defende.provider.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.ziroom.qa.quality.defende.provider.vo.dailyreport.DailyReportVo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 项目日报信息
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
@TableName("daily_test_report")
@ApiModel(value="DailyTestReport对象", description="项目日报信息")
public class DailyTestReport implements Serializable {

    private static final long serialVersionUID=1L;

    @ApiModelProperty(value = "自增主键")
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    @ApiModelProperty(value = "创建人")
    private String createUser;

    @ApiModelProperty(value = "创建时间")
    private LocalDateTime createTime;

    @ApiModelProperty(value = "更新人")
    private String updateUser;

    @ApiModelProperty(value = "更新时间")
    private LocalDateTime updateTime;

    @ApiModelProperty(value = "项目id")
    private Long topicId;

    @ApiModelProperty(value = "日报日期，格式yyyymmdd")
    private String dailyDate;

    @ApiModelProperty(value = "日报关联的风险名称")
    private String dailyRiskNames;

    @ApiModelProperty(value = "发件人")
    private String addresser;

    @ApiModelProperty(value = "收件人")
    private String addressee;

    @ApiModelProperty(value = "抄送人")
    private String cc;

    @ApiModelProperty(value = "提测版本")
    private String testVersion;

    @ApiModelProperty(value = "提测阶段")
    private String testStage;

    @ApiModelProperty(value = "邮件备注")
    private String emailRemark;

    @ApiModelProperty(value = "发送邮箱组地址（格式邮箱地址1,邮箱地址2）")
    private String sendGroup;

    @ApiModelProperty(value = "抄送邮箱组地址（格式邮箱地址1,邮箱地址2）")
    private String ccGroup;

    @ApiModelProperty(value = "项目bug地址")
    private String bugUrl;

    @ApiModelProperty(value = "发送邮件的时间")
    private LocalDateTime sendTime;

    @TableField(exist = false)
    private DailyReportVo detailVo;


}
