package com.ziroom.qa.quality.defende.provider.service;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.ziroom.qa.quality.defende.provider.QualityDefendProviderApplication;
import com.ziroom.qa.quality.defende.provider.constant.enums.FieldTypeEnum;
import com.ziroom.qa.quality.defende.provider.entity.TestConfig;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

@Slf4j
@SpringBootTest(classes = QualityDefendProviderApplication.class,webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@RunWith(SpringRunner.class)
@ActiveProfiles("dev")
public class TestConfigServiceTest {

    @Autowired
    private TestConfigService testConfigService;

    @Test
    public void saveTestConfigTest(){
        String username = "zhangxl15";
        TestConfig casename = new TestConfig();
        casename.setLable("用例名称");
        casename.setName("casename");
        casename.setFieldType(FieldTypeEnum.INPUT.getFieldType());
        casename.setRequired(true);
        casename.setPool("testcase");
        casename.setSystemConfig(true);

        TestConfig level = new TestConfig();
        level.setLable("用例等級");
        level.setName("testCaseLevel");
        level.setFieldType(FieldTypeEnum.SELECT.getFieldType());
        level.setRequired(true);
        level.setPool("testcase");
        level.setSystemConfig(true);
        testConfigService.saveOrUpdateTestConfig(casename,username);

        TestConfig preCondition = new TestConfig();
        preCondition.setLable("前置條件");
        preCondition.setName("preCondition");
        preCondition.setFieldType(FieldTypeEnum.TEXTAREA.getFieldType());
        preCondition.setPool("testcase");
        preCondition.setSystemConfig(true);

        TestConfig step = new TestConfig();
        step.setLable("執行步驟");
        step.setName("step");
        step.setFieldType(FieldTypeEnum.TEXTAREA.getFieldType());
        step.setRequired(true);
        step.setPool("testcase");
        step.setSystemConfig(true);

        TestConfig expectedResults = new TestConfig();
        expectedResults.setLable("期望結果");
        expectedResults.setName("expectedResults");
        expectedResults.setFieldType(FieldTypeEnum.TEXTAREA.getFieldType());
        expectedResults.setRequired(true);
        expectedResults.setPool("testcase");
        expectedResults.setSystemConfig(true);

        List<TestConfig> testConfigList = Lists.newArrayList();
        testConfigList.add(casename);
        testConfigList.add(level);
        testConfigList.add(preCondition);
        testConfigList.add(step);
        testConfigList.add(expectedResults);

        testConfigService.batchSaveOrUpdateTestConfig(testConfigList,username);

    }

    @Test
    public void saveOrUpdateTestConfigTest(){
        TestConfig expectedResults = new TestConfig();
        expectedResults.setLable("期望結果TEST");
        expectedResults.setName("expectedResults-Tes1t");
        expectedResults.setFieldType(FieldTypeEnum.TEXTAREA.getFieldType());
        expectedResults.setRequired(true);
        expectedResults.setPool("testcase");
        expectedResults.setSystemConfig(true);
        Boolean result = testConfigService.saveOrUpdateTestConfig(expectedResults, "zhangxl15");
        log.info(JSON.toJSONString(result));
    }
}
