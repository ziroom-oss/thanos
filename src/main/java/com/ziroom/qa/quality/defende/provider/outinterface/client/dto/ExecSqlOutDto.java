package com.ziroom.qa.quality.defende.provider.outinterface.client.dto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.time.LocalDateTime;

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
public class ExecSqlOutDto implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "数据库ID")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "数据库类型")
    private String dbType;

    private String dbIp;
    private String sql;

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
    @DateTimeFormat(style = "yyyy-MM-dd hh:mm:ss")
    private LocalDateTime createTime;

    @ApiModelProperty(value = "修改时间")
    private LocalDateTime updateTime;

    @ApiModelProperty(value = "0未删除 1 已删除")
    private Boolean deleted;


}
