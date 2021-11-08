package com.ziroom.qa.quality.defende.provider.caseRepository.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 
 * </p>
 *
 * @author mybatisPlusAutoGenerate
 * @since 2021-09-23
 */
@Data
  @EqualsAndHashCode(callSuper = false)
    @ApiModel(value="AutoSingleAssert对象", description="")
public class AutoSingleAssert implements Serializable {

    private static final long serialVersionUID = 1L;

      @TableId(value = "id", type = IdType.AUTO)
      private Integer id;

      @ApiModelProperty(value = "关联auto_single_api_case表的id")
      private Integer caseId;

      @ApiModelProperty(value = "断言内容")
      private String assertContent;

      @ApiModelProperty(value = "断言类型 0:包字符串 1：JSONPATH")
      private Integer assertType;

      @ApiModelProperty(value = "创建时间")
      @JsonFormat(pattern = "yyyy-MM-dd HH:mm", timezone = "GMT+8")
      private LocalDateTime createTime;

      @ApiModelProperty(value = "更新时间")
      @JsonFormat(pattern = "yyyy-MM-dd HH:mm", timezone = "GMT+8")
      private LocalDateTime updateTime;

      @ApiModelProperty(value = "是否删除")
      private Integer deleted;


}
