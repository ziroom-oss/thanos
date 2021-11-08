package com.ziroom.qa.quality.defende.provider.caseRepository.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.TableField;
import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 
 * </p>
 *
 * @author mybatisPlusAutoGenerate
 * @since 2021-09-22
 */
@Data
  @EqualsAndHashCode(callSuper = false)
    @ApiModel(value="AutoSingleApiCase对象", description="")
public class AutoSingleApiCase implements Serializable {

    private static final long serialVersionUID = 1L;

      @ApiModelProperty(value = "用例ID")
      @TableId(value = "id", type = IdType.AUTO)
      private Integer id;

      @ApiModelProperty(value = "接口ID")
      private Integer apiId;

      @ApiModelProperty(value = "自动排序")
      private Integer autoSort;

      @ApiModelProperty(value = "用例名称")
      private String caseName;

      @ApiModelProperty(value = "请求参数")
      private String requestParam;

      @ApiModelProperty(value = "post请求体")
      private String requestBody;

      @ApiModelProperty(value = "期望结果")
      private String expectedResults;

      @ApiModelProperty(value = "1是运行，2是关闭")
      private Boolean isRunnable;

      @ApiModelProperty(value = "前置SQL")
      private String preSql;

      @ApiModelProperty(value = "后置SQL")
      private String postSql;

      private Integer relyId;

      @ApiModelProperty(value = "依赖MQ消息id")
      private Integer relyMqId;

      @ApiModelProperty(value = "关联es_search_condition主键id")
      private Integer relyEsId;

      @ApiModelProperty(value = "备注")
      private String comment;

      @ApiModelProperty(value = "接口用例来源 0：人工录入 1：自动化创建")
      private Integer caseOrigin;

      @ApiModelProperty(value = "创建人")
      private String createUserCode;

      @ApiModelProperty(value = "更新人")
      private String updateUserCode;

      @ApiModelProperty(value = "创建时间")
      @JsonFormat(pattern = "yyyy-MM-dd HH:mm", timezone = "GMT+8")
      private LocalDateTime createTime;
      @JsonFormat(pattern = "yyyy-MM-dd HH:mm", timezone = "GMT+8")
      @ApiModelProperty(value = "更新时间")
      private LocalDateTime updateTime;

      @TableField("interfaceId")
      private String interfaceid;

      @ApiModelProperty(value = "协议类型")
      private String protocolType;

      @ApiModelProperty(value = "1:正常测试用例,0：异常测试用例")
      private Boolean caseType;

      @ApiModelProperty(value = "预处理所需字段 {appid:, secret: }")
      private String preRequest;

      @ApiModelProperty(value = "是否删除:0是未删除，1是删除")
      private Boolean deleted;


}
