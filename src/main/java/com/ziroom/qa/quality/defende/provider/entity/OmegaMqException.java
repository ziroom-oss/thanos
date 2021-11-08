package com.ziroom.qa.quality.defende.provider.entity;

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
 * @since 2021-10-11
 */
@Data
  @EqualsAndHashCode(callSuper = false)
    @ApiModel(value="OmegaMqException对象", description="")
public class OmegaMqException implements Serializable {

    private static final long serialVersionUID = 1L;

      @TableId(value = "id", type = IdType.AUTO)
      private Integer id;

      @ApiModelProperty(value = "外鍵ID")
      private String msgId;

    private String msgExt;

    private LocalDateTime createTime;


}
