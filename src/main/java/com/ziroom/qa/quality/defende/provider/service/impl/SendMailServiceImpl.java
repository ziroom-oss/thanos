package com.ziroom.qa.quality.defende.provider.service.impl;

import com.ziroom.qa.quality.defende.provider.constant.TestCenterConstants;
import com.ziroom.qa.quality.defende.provider.execTask.entity.dto.ComplateAndEmailDTO;
import com.ziroom.qa.quality.defende.provider.service.SendMailService;
import com.ziroom.qa.quality.defende.provider.vo.email.SendEmailDTO;
import com.ziroom.qa.quality.defende.provider.vo.outvo.SendTaskMsgVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class SendMailServiceImpl implements SendMailService {

    @Autowired
    private JavaMailSender javaMailSender;
    @Autowired
    private TemplateEngine templateEngine;

    private static final String ZIROOM_EMAIL_POSTFIX = "@ziroom.com";

    @Value("${quality.baseurl}")
    private String qualityUrl;
    /**
     * 效能研发组邮箱
     */
    private static final String quality_mail = "devops@ziroom.com";


    @Override
    public void sendDailyReportMail(SendEmailDTO dto) throws MessagingException {
        // 1. 初始化构造
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage, true);
        messageHelper.setFrom(dto.getAddresser() + ZIROOM_EMAIL_POSTFIX);
        // 2. 收件人
        String[] toArray = dto.getAddressee().split(",");
        for (int i = 0; i < toArray.length; i++) {
            toArray[i] += ZIROOM_EMAIL_POSTFIX;
        }
        messageHelper.setTo(toArray);
        // 3. 抄送人
        if (StringUtils.isNotBlank(dto.getCc())) {
            String[] ccsArray = dto.getCc().split(",");
            for (int i = 0; i < ccsArray.length; i++) {
                ccsArray[i] += ZIROOM_EMAIL_POSTFIX;
            }
            messageHelper.setCc(ccsArray);
        }
        // 4. 主题
        messageHelper.setSubject(dto.getDailyReportVo().getTestTopic().getTopicName() + "项目" + dto.getDailyReportVo().getDailyReportName() + "日报");
        // 5. 正文
        // 5.1 上下文(用户获取邮箱模板)
        Context context = new Context();
        // 5.2 上下文传递
        Map<String, Object> map = new HashMap<>();
        // 5.3 传递对象
        // 邮件内容
        map.put("emailRemark", dto.getEmailRemark());
        // bug地址
        map.put("bugUrl", dto.getBugUrl());
        // 测试阶段
        map.put("testStage", dto.getTestStage());
        // 提测版本
        map.put("testVersion", dto.getTestVersion());
        // 日报数据信息
        map.put("dailyReportVo", dto.getDailyReportVo());

        map.put("projectHistoryUrl", qualityUrl + "projectHitoryReport?id=" + dto.getDailyReportVo().getTestTopic().getId());

        // 5.4 为上下文设置传递对象
        context.setVariables(map);
        // 5.5 将数据拼接至邮箱模板, 并设置为正文
        String emailContent = templateEngine.process("mail/hmail.html", context);
        messageHelper.setText(emailContent, true);
        // 6. 发送邮件
        javaMailSender.send(mimeMessage);
    }

    /**
     * 发送测试执行创建消息
     *
     * @param sendTaskMsgVo
     * @throws MessagingException
     */
    @Override
    public void sendTestTaskMail(SendTaskMsgVo sendTaskMsgVo) throws MessagingException {
        // 1. 初始化构造
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage, true);
        messageHelper.setFrom(quality_mail);
        // 2. 收件人
        String[] toArray = sendTaskMsgVo.getTestUserName().split(",");
        for (int i = 0; i < toArray.length; i++) {
            toArray[i] += ZIROOM_EMAIL_POSTFIX;
        }
        messageHelper.setTo(toArray);
        // 3. 抄送人
        String[] ccsArray = sendTaskMsgVo.getDevUserName().split(",");
        for (int i = 0; i < ccsArray.length; i++) {
            ccsArray[i] += ZIROOM_EMAIL_POSTFIX;
        }
        messageHelper.setCc(ccsArray);
        // 4. 主题
        messageHelper.setSubject(sendTaskMsgVo.getEndType() + "测试执行创建通知");
        // 5. 正文
        // 5.1 上下文(用户获取邮箱模板)
        Context context = new Context();
        // 5.2 上下文传递
        Map<String, Object> map = new HashMap<>();
        // 5.3 传递对象
        // 邮件内容
        map.put("testTaskName", sendTaskMsgVo.getTaskName());
        // bug地址
        map.put("testTaskUrl", sendTaskMsgVo.getTestTaskUrl());
        // jiraUrl
        map.put("jiraUrl", sendTaskMsgVo.getJiraUrl());
        // jiraDesc
        map.put("jiraDesc", sendTaskMsgVo.getJiraDesc());
        // branchName
        map.put("branchName", sendTaskMsgVo.getBranchName());
        map.put("devUserName", sendTaskMsgVo.getDevUserNameStr());
        map.put("testUserName", sendTaskMsgVo.getTestUserNameStr());

        map.put("testCaseResult", StringUtils.isNotBlank(sendTaskMsgVo.getTestCaseResult()) ? sendTaskMsgVo.getTestCaseResult() : TestCenterConstants.RES_MSG_SUCCESS);
        // 5.4 为上下文设置传递对象
        context.setVariables(map);
        // 5.5 将数据拼接至邮箱模板, 并设置为正文
        String emailContent = templateEngine.process("mail/taskmail.html", context);
        messageHelper.setText(emailContent, true);
        // 6. 发送邮件
        javaMailSender.send(mimeMessage);
    }

    /**
     * 获取日报邮件内容（预览时使用）
     *
     * @param dto
     * @throws MessagingException
     */
    @Override
    public String getEmailContent(SendEmailDTO dto) {
        Context context = new Context();
        Map<String, Object> map = new HashMap<>();
        map.put("emailRemark", dto.getEmailRemark());
        map.put("bugUrl", dto.getBugUrl());
        map.put("testStage", dto.getTestStage());
        map.put("testVersion", dto.getTestVersion());
        map.put("dailyReportVo", dto.getDailyReportVo());
        map.put("projectHistoryUrl", qualityUrl + "projectHitoryReport?id=" + dto.getDailyReportVo().getTestTopic().getId());
        context.setVariables(map);
        String emailContent = templateEngine.process("mail/hmail.html", context);
        return emailContent;
    }

    /**
     * 发送测试执行创建消息
     *
     * @param complateAndEmailDTO
     * @throws MessagingException
     */
    @Override
    public void sendTaskReportEmail(ComplateAndEmailDTO complateAndEmailDTO) throws MessagingException {
        // 1. 初始化构造
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage, true);
        messageHelper.setFrom(complateAndEmailDTO.getSender() + ZIROOM_EMAIL_POSTFIX);
        // 2. 收件人
        String[] toArray = complateAndEmailDTO.getAddressee().split(",");
        for (int i = 0; i < toArray.length; i++) {
            toArray[i] += ZIROOM_EMAIL_POSTFIX;
        }
        messageHelper.setTo(toArray);
        // 3. 抄送人
        String[] ccsArray = complateAndEmailDTO.getCcUser().split(",");
        for (int i = 0; i < ccsArray.length; i++) {
            ccsArray[i] += ZIROOM_EMAIL_POSTFIX;
        }
        messageHelper.setCc(ccsArray);
        // 4. 主题
        messageHelper.setSubject(complateAndEmailDTO.getTaskName() + "测试完成");
        // 5. 正文
        // 5.1 上下文(用户获取邮箱模板)
        Context context = new Context();
        // 5.2 上下文传递
        Map<String, Object> map = new HashMap<>();
        // 5.3 传递对象
        // 邮件内容
        map.put("emailContent", complateAndEmailDTO.getEmailContent());
        // caseNameList
        map.put("caseNameList", complateAndEmailDTO.getCaseNameList());
        // bugCount
        map.put("bugCount", complateAndEmailDTO.getBugCount());
        // p1p2BugCount
        map.put("p1p2BugCount", complateAndEmailDTO.getP1p2BugCount());
        // testTaskUrl
        map.put("testTaskUrl", complateAndEmailDTO.getTestTaskUrl());
        // 测试执行明细信息
        map.put("testTaskDetail", complateAndEmailDTO.getTestTaskDetail());
        // 5.4 为上下文设置传递对象
        context.setVariables(map);
        // 5.5 将数据拼接至邮箱模板, 并设置为正文
        String emailContent = templateEngine.process("mail/testtaskmail.html", context);
        messageHelper.setText(emailContent, true);
        // 6. 发送邮件
        javaMailSender.send(mimeMessage);
    }

    /**
     * 发送jira创建测试执行任务邮件
     *
     * @param testLeader
     * @param url
     * @param jiraUrl
     * @param summary
     */
    @Override
    public void sendJiraTestTaskMail(String testLeader, String url, String jiraUrl, String summary) throws MessagingException {
        // 1. 初始化构造
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage, true);
        messageHelper.setFrom(quality_mail);
        // 2. 收件人
        String[] toArray = testLeader.split(",");
        for (int i = 0; i < toArray.length; i++) {
            toArray[i] += ZIROOM_EMAIL_POSTFIX;
        }
        messageHelper.setTo(toArray);
        // 3. 抄送人
        String[] ccsArray = testLeader.split(",");
        for (int i = 0; i < ccsArray.length; i++) {
            ccsArray[i] += ZIROOM_EMAIL_POSTFIX;
        }
        messageHelper.setCc(ccsArray);
        // 4. 主题
        messageHelper.setSubject(summary + "：测试执行创建完成");
        // 5. 正文
        // 5.1 上下文(用户获取邮箱模板)
        Context context = new Context();
        // 5.2 上下文传递
        Map<String, Object> map = new HashMap<>();
        // 5.3 传递对象
        // 邮件内容
        map.put("testTaskName", summary);
        map.put("testTaskUrl", url);
        map.put("jiraUrl", jiraUrl);
        map.put("jiraDesc", summary);
        // 5.4 为上下文设置传递对象
        context.setVariables(map);
        // 5.5 将数据拼接至邮箱模板, 并设置为正文
        String emailContent = templateEngine.process("mail/jirataskmail.html", context);
        messageHelper.setText(emailContent, true);
        // 6. 发送邮件
        javaMailSender.send(mimeMessage);
    }
}
