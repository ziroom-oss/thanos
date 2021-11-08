package com.ziroom.qa.quality.defende.provider.data.entity.dto;

import com.ziroom.qa.quality.defende.provider.data.entity.AutoDataDatabase;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;

/**
 * @author zhujj5
 * @Title:
 * @Description:
 * @date 2021/9/16 4:58 下午
 */
@Data
public class AutoDataDatabaseDto  extends AutoDataDatabase {
    @ApiModelProperty(value = "应用名称")
    @NotEmpty(message = "applicationName不能为空")
    private String applicationName;
}
