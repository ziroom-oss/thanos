package com.ziroom.qa.quality.defende.provider.execTask.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * <p>
 * 
 * </p>
 *
 * @author mybatisPlusAutoGenerate
 * @since 2021-10-19
 */
@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="AutoExecutionRecord对象", description="")
public class AutoExecutionRecord implements Serializable {

    private static final long serialVersionUID = 1L;

      @ApiModelProperty(value = "编号")
      @TableId(value = "id", type = IdType.AUTO)
      private Integer id;

      @ApiModelProperty(value = "接口编号")
      private Integer apiId;

      @ApiModelProperty(value = "用例编号")
      private Integer caseId;

      @ApiModelProperty(value = "用户编码")
      private String userCode;

      @ApiModelProperty(value = "记录名")
      private String recordName;

      @ApiModelProperty(value = "测试结果")
      private String recordResult;

      @ApiModelProperty(value = "接口")
      private String requestUrl;

      @ApiModelProperty(value = "请求类型get post")
      private String requestType;

      @ApiModelProperty(value = "请求header")
      private String header;

      @ApiModelProperty(value = "返回header")
      private String responseHeader;

      @ApiModelProperty(value = "请求body")
      private String requestBody;

      @ApiModelProperty(value = "实际结果")
      private String actualResult;

      @ApiModelProperty(value = "请求开始时间 时间戳")
      private LocalDateTime startTime;

      @ApiModelProperty(value = "响应结果时间 时间戳")
      private String responseTime;

      @ApiModelProperty(value = "执行完成时间 时间戳")
      private LocalDateTime endTime;

      private String env;

      @ApiModelProperty(value = "版本")
      private String version;

      @ApiModelProperty(value = "是否删除，1：删除 0；不删除")
      private Boolean deleted;

      @ApiModelProperty(value = "SQL 0CASE 1API 2MULTI_API 3MAKE_DATA 6")
      private Integer dataType;

      @ApiModelProperty(value = "步骤执行顺序")
      private Integer orderId;

      @ApiModelProperty(value = "成功覆盖率")
      private BigDecimal successCoverage;


}
