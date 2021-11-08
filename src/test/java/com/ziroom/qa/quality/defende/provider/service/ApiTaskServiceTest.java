package com.ziroom.qa.quality.defende.provider.service;

import com.ziroom.qa.quality.defende.provider.QualityDefendProviderApplication;
import com.ziroom.qa.quality.defende.provider.execTask.service.IAutoTestCaseExecuteService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author zhujj5
 * @Title:
 * @Description:
 * @date 2021/8/23 3:11 下午
 */
@SpringBootTest(classes = QualityDefendProviderApplication.class)
@RunWith(SpringRunner.class)
public class ApiTaskServiceTest {
    @Autowired
    private IAutoTestCaseExecuteService autoTestCaseExecuteService;

    @Test
    public void test(){
        autoTestCaseExecuteService.runAllCaseByAppName("qua","telot","aaa");
    }
}
