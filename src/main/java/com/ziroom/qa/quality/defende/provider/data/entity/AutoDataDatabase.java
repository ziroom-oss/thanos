package com.ziroom.qa.quality.defende.provider.data.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.time.LocalDateTime;
import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotEmpty;

/**
 * <p>
 * 
 * </p>
 *
 * @author mybatisPlusAutoGenerate
 * @since 2021-09-16
 */
@Data
  @EqualsAndHashCode(callSuper = false)
    @ApiModel(value="AutoDataDatabase对象", description="")
public class AutoDataDatabase implements Serializable {

    private static final long serialVersionUID = 1L;

      @ApiModelProperty(value = "数据库ID")
      @TableId(value = "id", type = IdType.AUTO)
      private Integer id;

      @ApiModelProperty(value = "数据库类型")
      private String dbType;

      private String dbIp;

      @ApiModelProperty(value = "数据库名称")
      private String dbName;

      @ApiModelProperty(value = "端口")
      private String dbPort;

      @ApiModelProperty(value = "用户名")
      private String userName;

      @ApiModelProperty(value = "密码?")
      private String userPassword;

      @ApiModelProperty(value = "描述")
      private String description;

      @ApiModelProperty(value = "应用名称")
      private String applicationName;

      @ApiModelProperty(value = "数据库类型.qa ,pre:预发环境")
      private String env;

      @ApiModelProperty(value = "插入时间")
      @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
      @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
      private LocalDateTime createTime;

      @ApiModelProperty(value = "修改时间")
      @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
      @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
      private LocalDateTime updateTime;

      @ApiModelProperty(value = "0未删除 1 已删除")
      private Boolean deleted;


}
