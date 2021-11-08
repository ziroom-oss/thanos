package com.ziroom.qa.quality.defende.provider.vo.telot;


import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import java.util.List;

/**
 * 单接口录入数据实体
 *
 * @author zhanghang
 * @date 2019-06-25
 */
@Getter
@Setter
public class SingleApiEnterDto {
    /**
     * 系统ID
     */
    Integer systemId;
    /**
     * 前置SQL的id集合
     */
    String preSqlIds;
    /**
     * 后置SQL的id集合
     */
    String postSqlIds;
    /**
     * 请求类型
     */
    @NotBlank(message = "requestType不能为空")
    String requestType;
    /**
     * url
     */
    String url;
    /**
     * 参数
     */
    String requestParam;
    /**
     * 头信息
     */
    String header;
    /**
     * 用例名称
     */
    @NotBlank(message = "caseName不能为空")
    String caseName;
    /**
     * 接口名称
     */
    String apiName;
    /**
     * 备注
     */
    String comment;
    /**
     * 加密类型
     */
    int encryptType;
    /**
     * 是否为依赖
     */
    Integer isRely;
    /**
     * 依赖ID
     */
    Integer relyId;
    /**
     * 依赖的MQ消息ID
     */
    private Integer relyMqId;
    /**
     * 依赖的ES数据ID
     */
    private Integer relyEsId;
    /**
     * 期望结果
     */
    private String expectedResults;
    /**
     * 环境
     */
    private String env;

    /**
     * 测试环境域名
     */
    private String testEnvUrl;

    /**
     * 准生产环境域名
     */
    private String preEnvUrl;

    /**
     * 接口名
     */
    @NotBlank(message = "requestUri不能为空")
    private String requestUri;

    /**
     * 接口ID
     */
    private Integer apiId;
    /**
     * 协议类型
     */
    @NotBlank(message = "protocolType不能为空")
    private String protocolType;

    /**
     * 如果有caseId，返回
     */
    private Integer caseId;
    /**
     * 用例来源，0godzilla，1simpleapi
     */
    private Integer caseOrigin;

    /**
     * 断言类型
     */
    private Integer assertType;

    /**
     * 预处理信息
     */
    private String preRequest;

    /**
     * 断言列表
     */
    private List<SingleAssertEntity> assertList;

    /**
     * 删除的断言id
     */
    private String deleteApiIds;
    @NotBlank(message = "applicationName不能为空")
    private String applicationName;
    private Integer applicationId;
    private String controllerName;
    /**
     * post 请求体
     */
    private String requestBody;
    private String updateUserCode;

}
