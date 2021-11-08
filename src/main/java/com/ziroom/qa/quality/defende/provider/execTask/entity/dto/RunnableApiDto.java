package com.ziroom.qa.quality.defende.provider.execTask.entity.dto;

/**
 * @author zhujj5
 * @Title:
 * @Description:
 * @date 2021/9/24 6:53 下午
 */

 import lombok.Data;
@Data

public class RunnableApiDto {

    //    任务ID
    private Long noticeId;

    private String apiId;
    //    请求地址
    private String requestUrl;
    //    请求参数
    private String requestPara;
    /**
     * 请求体
     */
    private String requestBody;
    //    请求类型
    private String requestType;
    //    请求头
    private String header;
    //    加密
    private int encryptType;
    //    依赖用例ID
    private Integer relyCaseId;
    //    依赖MQ
    private Integer relyMqId;
    //    依赖es
    private Integer relyEsId;
    //    依赖SQL
    private String preSqlId;
    private String postSqlId;
    //   依赖类型
    private String relyType;

    private RunnableApiDto relyRunnableApiDto;
    private String preRequest;
}
