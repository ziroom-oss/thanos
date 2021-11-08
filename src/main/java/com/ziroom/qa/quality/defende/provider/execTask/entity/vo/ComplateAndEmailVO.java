package com.ziroom.qa.quality.defende.provider.execTask.entity.vo;

import com.ziroom.qa.quality.defende.provider.execTask.entity.TestTask;
import lombok.Data;

@Data
public class ComplateAndEmailVO {

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
     * 测试执行任务id
     */
    private Long taskId;

    /**
     * 完成并发送邮件
     */
    private boolean complateAndEmailFlag;

    private TestTask testTask;
}
