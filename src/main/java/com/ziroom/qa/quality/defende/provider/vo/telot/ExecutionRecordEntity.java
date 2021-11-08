package com.ziroom.qa.quality.defende.provider.vo.telot;

import lombok.Data;

import java.util.List;

/**
 * 执行记录信息
 *
 * @author zhanghang
 * @date 2019-07-12
 */
@Data
public class ExecutionRecordEntity {

    /**
     * 主键ID
     */
    private Integer id;

    /**
     * 接口ID
     */
    private Integer apiId;

    /**
     * 用例ID
     */
    private Integer caseId;

    /**
     * 记录结果
     */
    private String recordResult;
    private String failCause;

    /**
     * 开始时间
     */
//    private Date startTime;

    /**
     * 响应时间
     */
    private String responseTime;

    /**
     * 结束时间
     */
//    private Date endTime;

    /**
     * 用户编码
     */
    private String userCode;

    /**
     * 测试环境
     */
    private String envFlag;

    /**
     * 时间戳
     */
    private String version;

    /**
     * 是否删除
     */
    private Boolean isDel;

    /**
     * 运行类型
     */
    private Integer dataType;

    /**
     * 记录名称
     */
    private String recordName;

    /**
     * 实际结果
     */
    private String actualResult;

    /**
     * 真实参数
     */
    private String realInParam;

    /**
     * 用例IDS
     */
    private List<Integer> caseIds;

    /**
     * 用例名称
     */
    private String caseName;

    /**
     * 期望结果
     */
    private String expectedResults;

    /**
     * API名称
     */
    private String apidName;

    /**
     * 请求URI
     */
    private String requestUri;

    /**
     * 真是名称
     */
    private String realName;

    /**
     * 测试环境域名
     */
    private String testEnvUrl;

    /**
     * 准生产环境域名
     */
    private String preEnvUrl;

    /**
     * 接口全路径
     */
    private String url;

    /**
     * 执行效率
     */
    private String executionRate;

    /**
     * 请求类型GET/POST
     */
    private String requestType;

    private Integer orderId;

    /**
     * 断言类型
     */
    private Integer assertType;

    /**
     * 前置sql记录
     */
    private ExecutionRecordEntity preSqlRecord;

    /**
     * 后置sql记录
     */
    private ExecutionRecordEntity postSqlRecord;

    /**
     * 断言结果列表
     */
    private List<ExecutionRecordAssertEntity> assertRecordList;


    private String responseHeader;

    private String requestBody;
    private String requestHeader;
    /**
     * 应用名
     */
    private String applicationName;

}
