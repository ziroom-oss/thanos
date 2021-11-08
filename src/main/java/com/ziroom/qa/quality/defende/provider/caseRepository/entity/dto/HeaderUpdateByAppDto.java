package com.ziroom.qa.quality.defende.provider.caseRepository.entity.dto;


import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * 单接口录入数据实体
 *
 * @author zhanghang
 * @date 2019-06-25
 */
@Data
public class HeaderUpdateByAppDto {


    /**
     * 头信息
     */
    String header;
    /**
     *
     */
    @NotBlank(message = "applicationName不能为空")
    private String applicationName;


}
