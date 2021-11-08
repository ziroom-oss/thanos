package com.ziroom.qa.quality.defende.provider.caseRepository.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.time.LocalDateTime;
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
 * @since 2021-10-12
 */
@Data
  @EqualsAndHashCode(callSuper = false)
    @ApiModel(value="AutoApplicationProperties对象", description="")
public class AutoApplicationProperties implements Serializable {

    private static final long serialVersionUID = 1L;

      @ApiModelProperty(value = "数据库ID")
        @TableId(value = "id", type = IdType.AUTO)
      private Integer id;

      @ApiModelProperty(value = "应用名称")
      private String applicationName;

      @ApiModelProperty(value = "数据库类型.qua")
      private String env;

      @ApiModelProperty(value = "swagger地址")
      private String swaggerUrl;

      @ApiModelProperty(value = "插入时间")
      private LocalDateTime createTime;

      @ApiModelProperty(value = "修改时间")
      private LocalDateTime updateTime;

      @ApiModelProperty(value = "0未删除 1 已删除")
      private Boolean deleted;


}
