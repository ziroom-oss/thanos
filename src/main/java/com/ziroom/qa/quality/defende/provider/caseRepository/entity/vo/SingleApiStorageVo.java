package com.ziroom.qa.quality.defende.provider.caseRepository.entity.vo;

import com.ziroom.qa.quality.defende.provider.caseRepository.entity.AutoSingleApiCase;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * @Author: yinm5
 * @Description: 接口导入前端展示Vo
 * @Date: 14:48 2019/9/19
 */
@Getter
@Setter
public class SingleApiStorageVo implements Serializable {


    private static final long serialVersionUID = 7687589078335788960L;
    /**
     * 单接口ID
     */
    private long apiId;

    /**
     * 请求uri
     */
    private String requestUri;

    /**
     * 单接口名
     */
    private String apiName;

    /**
     *  分类controllerName
     */
    private String controllerName;


    /**
     * 请求类型
     */
    private String requestType;

    /**
     * 请求头
     */
    private String header;

    /**
     * 单接口用例信息
     * 默认格式为 [{param: xxxxxxx, caseName: xxxxxxx}......]
     */
    private AutoSingleApiCase apiCase;

    public String applicationName;
    @ApiModelProperty(value = "创建人")
    private String createUserCode;

    @ApiModelProperty(value = "更新人")
    private String updateUserCode;
}
