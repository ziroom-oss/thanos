package com.ziroom.qa.quality.defende.provider.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Monitoring {

    /**
     * 主键
     */
    private Long id;

    /**
     * 功能模块
     */
    private String monitorInfo;

    /**
     * 查询/删除/修改/运行
     */
    private String type;

    /**
     * 操作人中文名称
     */
    private String opPerson;

    /**
     * 操作人系统号全路径
     */
    private String deptEhrCode;

    /**
     * 操作人OA账号
     */
    private String opPersonUserName;

    /**
     * 操作时间
     */
    private LocalDateTime opTime;

    /**
     * 服务器IP
     */
    private String serverIp;

    /**
     * 服务器请求时间
     */
    private LocalDateTime serverIpReqTime;

    /**
     * 客户端Ip
     */
    private String clientIp;

    /**
     * 客户端请求时
     */
    private LocalDateTime clientIpReqTime;

    /**
     * 请求url
     */
    private String reqUrl;

    /**
     * 类名称加方法名
     */
    private String descript;

    /**
     * 执行状态码
     */
    private Integer exeStatus;

    /**
     * 执行开始时间
     */
    private LocalDateTime exeStartTime;

    /**
     * 执行结束时间
     */
    private LocalDateTime exeEndTime;

    /**
     * 参数
     */
    private String params;

    /**
     *
     */
    private String userCode;


}
