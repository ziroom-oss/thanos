package com.ziroom.qa.quality.defende.provider.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.time.LocalDateTime;
import java.io.Serializable;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 项目执行报表信息
 * </p>
 *
 * @author liangh4
 * @since 2021-06-09
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("daily_test_exe_report")
@ApiModel(value="DailyTestExeReport对象", description="项目执行报表信息")
public class DailyTestExeReport implements Serializable {

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

    @ApiModelProperty(value = "项目id")
    private Long topicId;

    @ApiModelProperty(value = "日报id")
    private Long dailyId;

    @ApiModelProperty(value = "caseId")
    private Long caseId;

    @ApiModelProperty(value = "taskId")
    private Long taskId;

    @ApiModelProperty(value = "权限id（bugid）")
    private String bugId;

    @ApiModelProperty(value = "bug状态id")
    private Long bugStatusId;

    @ApiModelProperty(value = "bug级别id")
    private Long bugLevelId;

    @ApiModelProperty(value = "执行类型（未执行、跳过、通过、失败）")
    private String exeType;
    /**
     * 测试用例key，测试用例唯一标识
     */
    @ApiModelProperty(value = "测试用例key，测试用例唯一标识")
    private String caseKey;

}
