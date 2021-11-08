package com.ziroom.qa.quality.defende.provider.vo.email;

import com.ziroom.qa.quality.defende.provider.vo.dailyreport.DailyReportVo;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class SendEmailDTO {

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

    @ApiModelProperty(value = "日报报表信息")
    private DailyReportVo dailyReportVo;
}
