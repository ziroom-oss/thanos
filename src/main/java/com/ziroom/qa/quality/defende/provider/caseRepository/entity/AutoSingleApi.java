package com.ziroom.qa.quality.defende.provider.caseRepository.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.TableField;
import java.io.Serializable;
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
    @ApiModel(value="AutoSingleApi对象", description="")
public class AutoSingleApi implements Serializable {

    private static final long serialVersionUID = 1L;

      @ApiModelProperty(value = "接口ID")
        @TableId(value = "id", type = IdType.AUTO)
      private Integer id;

      @ApiModelProperty(value = "系统ID")
      private Integer systemId;

      @ApiModelProperty(value = "模块ID")
      private Integer moduleId;

      @ApiModelProperty(value = "接口名称")
      private String apiName;

      @ApiModelProperty(value = "请求路径")
      private String requestUri;

      @ApiModelProperty(value = "接口地址ID，MD5加密")
      @TableField("interfaceId")
      private String interfaceid;

      @ApiModelProperty(value = "请求类型")
      private String requestType;

      @ApiModelProperty(value = "请求头")
      private String header;

      @ApiModelProperty(value = "omega 的应用ID；0代表历史数据")
      private Integer applicationId;

      @ApiModelProperty(value = "接口分类名称，一般指controller名称")
      private String controllerName;

      @ApiModelProperty(value = "应用名称")
      private String applicationName;

      private Integer encryptType;

      @ApiModelProperty(value = "接口是否为依赖接口1为依赖接口0为非依赖接口")
      private Integer isRely;

      @ApiModelProperty(value = "依赖API")
      private Integer relyId;

      @ApiModelProperty(value = "依赖MQ消息ID")
      private Integer relyMqId;

      @ApiModelProperty(value = "关联es_search_condition主键id")
      private Integer relyEsId;

      @ApiModelProperty(value = "维护人")
      private String defenderCode;

      @ApiModelProperty(value = "是否已删除:0未删除，1：已删除")
      private Boolean deleted;

      @ApiModelProperty(value = "创建人")
      private String createUserCode;

      @ApiModelProperty(value = "更新人")
      private String updateUserCode;

      @ApiModelProperty(value = "创建时间")
      private LocalDateTime createTime;

      @ApiModelProperty(value = "更新时间")
      private LocalDateTime updateTime;


}
