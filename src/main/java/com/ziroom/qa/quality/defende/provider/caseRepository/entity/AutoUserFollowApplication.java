package com.ziroom.qa.quality.defende.provider.caseRepository.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.time.LocalDateTime;
import java.io.Serializable;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotBlank;

/**
 * <p>
 * 
 * </p>
 *
 * @author mybatisPlusAutoGenerate
 * @since 2021-10-09
 */
@Data
  @EqualsAndHashCode(callSuper = false)
    @ApiModel(value="AutoUserFollowApplication对象", description="")
public class AutoUserFollowApplication implements Serializable {

    private static final long serialVersionUID = 1L;

      @TableId(value = "id", type = IdType.AUTO)
      private Integer id;

      @ApiModelProperty(value = "用户ID")
      private String userCode;

      @ApiModelProperty(value = "应用码")
      @NotBlank(message = "applicationCode不能为空")
      private String applicationCode;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;


}
