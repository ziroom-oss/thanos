package com.ziroom.qa.quality.defende.provider.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 项目风险关联
 * </p>
 *
 * @author liangh4
 * @since 2021-06-02
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("topic_risk_rel")
@ApiModel(value="TopicRiskRel对象", description="项目风险关联")
public class TopicRiskRel implements Serializable {

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

    @ApiModelProperty(value = "风险id")
    private Long riskId;

    /**
     * 项目名称
     */
    @TableField(exist = false)
    private String topicName;

    /**
     * 风险名称
     */
    @TableField(exist = false)
    private String testRiskName;

    /**
     * 关联标识 true 已关联，false 未关联
     */
    @TableField(exist = false)
    private boolean relFlag = false;


}
