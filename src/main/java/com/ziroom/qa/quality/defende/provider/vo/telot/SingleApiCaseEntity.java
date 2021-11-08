package com.ziroom.qa.quality.defende.provider.vo.telot;

import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * 单接口用例
 *
 * @author zhanghang
 * @date 2019-06-27
 */
@Data
public class SingleApiCaseEntity {
    /**
     * 主键
     */
    private Integer id;
    /**
     * 接口ID
     */
    private Integer apiId;
    /**
     * 自定义排序
     */
    private Integer autoSort;

    /**
     * 用例名称
     */
    private String caseName;

    /**
     * 请求参数
     */
    private String requestParam;

    /**
     * 期望结果
     */
    private String expectedResults;

    /**
     * 是否可运行
     */
    private Boolean isRunnable;

    /**
     * 前置SQL
     */
    private String preSqlId;

    /**
     * 后置SQL
     */
    private String postSqlId;

    /**
     * 依赖接口ID
     */
    private Integer relyId;

    /**
     * 依赖的MQ消息ID
     */
    private Integer relyMqId;
    /**
     * 依赖的ES数据ID
     */
    private Integer relyEsId;
    /**
     * 备注
     */
    private String comment;
    /**
     * 用例来源，0godzilla，1simpleapi
     */
    private Integer caseOrigin;
    /**
     * 是否删除
     */
    private Boolean isDel;

    /**
     * 创建人
     */
    private String createUserCode;

    /**
     * 更新人
     */
    private String updateUserCode;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;
    /**
     * true:正常测试用例
     * false:异常测试用例
     */
    private Boolean caseType;

    private String interfaceId;

    private List<Integer> caseIdArray;

    private ExecutionRecordEntity executionRecordEntity;

    private String url;

    private Integer orderId;

    private Long caseId;

    private Integer assertType;

    private String preRequest;

    /**
     * 协议类型
     */
    private String protocolType;

    private String header;

    private List<SingleAssertEntity> assertList;

    private String deleteAssertIds;

    private String requestBody;

    private String applicationName;

}
