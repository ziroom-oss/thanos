package com.ziroom.qa.quality.defende.provider.execTask.entity.dto;

import lombok.Data;

import java.util.List;

@Data
public class ComplateAndEmailDTO {

    /**
     * 发件人
     */
    private String sender;

    /**
     * 收件人
     */
    private String addressee;

    /**
     * 抄送人
     */
    private String ccUser;

    /**
     * 邮件内容
     */
    private String emailContent;

    /**
     * 测试执行名称
     */
    private List<String> caseNameList;

    /**
     * 测试执行名称
     */
    private String taskName;

    /**
     * bug总数
     */
    private Integer bugCount = 0;

    /**
     * p1p2bug数量
     */
    private Integer p1p2BugCount = 0;

    /**
     * 测试执行地址
     */
    private String testTaskUrl;

    /**
     * 测试执行明细信息
     */
    private String testTaskDetail;

}
