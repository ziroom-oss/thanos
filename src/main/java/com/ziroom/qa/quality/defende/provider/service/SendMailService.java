package com.ziroom.qa.quality.defende.provider.service;

import com.ziroom.qa.quality.defende.provider.execTask.entity.dto.ComplateAndEmailDTO;
import com.ziroom.qa.quality.defende.provider.vo.email.SendEmailDTO;
import com.ziroom.qa.quality.defende.provider.vo.outvo.SendTaskMsgVo;

import javax.mail.MessagingException;

public interface SendMailService {

    /**
     * 发送日报邮件
     *
     * @param dto
     * @throws MessagingException
     */
    void sendDailyReportMail(SendEmailDTO dto) throws MessagingException;


    /**
     * 发送测试执行创建消息
     *
     * @param sendTaskMsgVo
     * @throws MessagingException
     */
    void sendTestTaskMail(SendTaskMsgVo sendTaskMsgVo) throws MessagingException;

    /**
     * 获取日报邮件内容（预览时使用）
     *
     * @param dto
     * @throws MessagingException
     */
    String getEmailContent(SendEmailDTO dto);

    /**
     * 发送测试执行创建消息
     *
     * @param complateAndEmailDTO
     * @throws MessagingException
     */
    void sendTaskReportEmail(ComplateAndEmailDTO complateAndEmailDTO) throws MessagingException;

    /**
     * 发送jira创建测试执行任务邮件
     *
     * @param testLeader
     * @param url
     * @param jiraUrl
     * @param summary
     */
    void sendJiraTestTaskMail(String testLeader, String url, String jiraUrl, String summary) throws MessagingException;
}
