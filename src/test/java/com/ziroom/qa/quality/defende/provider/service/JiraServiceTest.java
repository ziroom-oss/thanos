package com.ziroom.qa.quality.defende.provider.service;

import com.ziroom.qa.quality.defende.provider.QualityDefendProviderApplication;
import com.ziroom.qa.quality.defende.provider.execTask.entity.vo.ComplateAndEmailVO;
import com.ziroom.qa.quality.defende.provider.execTask.service.TestTaskService;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

@Slf4j
@SpringBootTest(classes = QualityDefendProviderApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@RunWith(SpringRunner.class)
@ActiveProfiles("dev")
public class JiraServiceTest {

    @Autowired
    private TestTaskService testTaskService;

    @Test
    public void testCreateBug() {

//        Issue issue = JiraUtils.getJiraIssueByIssueKey("JZCP-2");
//        System.out.println(JSON.toJSONString(issue));

//        testExecutionService.changeJiraStatus("ISSUE-5415");

        ComplateAndEmailVO complateAndEmailVO = new ComplateAndEmailVO();
        complateAndEmailVO.setComplateAndEmailFlag(false);
        complateAndEmailVO.setEmailContent("测试发送邮件");
        complateAndEmailVO.setSender("zhangw7");
        complateAndEmailVO.setAddressee("zhaohp,liangh4");
        complateAndEmailVO.setCcUser("zhangw7");
        complateAndEmailVO.setTaskId(246L);
        testTaskService.completeTaskAndEmail(complateAndEmailVO, "liangh4");

    }
}
