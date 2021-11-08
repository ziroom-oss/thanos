package com.ziroom.qa.quality.defende.provider.execTask.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
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
 * @since 2021-09-26
 */
@Data
  @EqualsAndHashCode(callSuper = false)
    @ApiModel(value="AutoExecutionRecordAssert对象", description="")
public class AutoExecutionRecordAssert implements Serializable {

    private static final long serialVersionUID = 1L;

      @TableId(value = "id", type = IdType.AUTO)
      private Integer id;

      @ApiModelProperty(value = "关联execution_record表")
      private Integer recordId;

      @ApiModelProperty(value = "断言内容")
      private String assertContent;

      @ApiModelProperty(value = "断言类型")
      private Integer assertType;

      @ApiModelProperty(value = "断言执行结果 SUCCESS/FAILURE")
      private String assertResult;


}
