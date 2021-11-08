package com.ziroom.qa.quality.defende.provider.service;

import com.alibaba.fastjson.JSON;
import com.ziroom.qa.quality.defende.provider.QualityDefendProviderApplication;
import com.ziroom.qa.quality.defende.provider.execTask.entity.AutoExecutionRecordAssert;
import com.ziroom.qa.quality.defende.provider.execTask.entity.vo.AutoExecutionRecordVo;
import com.ziroom.qa.quality.defende.provider.execTask.service.TestTaskOutService;
import com.ziroom.qa.quality.defende.provider.vo.outvo.OutReqVo;
import com.ziroom.qa.quality.defende.provider.vo.outvo.OutResVo;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@SpringBootTest(classes = QualityDefendProviderApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@RunWith(SpringRunner.class)
@ActiveProfiles("dev")
public class TestApplicationServiceTest {

    @Autowired
    private TestApplicationService testApplicationService;
    @Autowired
    private TestTaskOutService testTaskOutService;


    @Test
    public void test111() {
        testTaskOutService.sendAutoTaskMsg("2021-09-07,2021-09-08");
    }

    @Test
    public void test() {
        OutReqVo outReqVo = new OutReqVo();
        outReqVo.setJiraId("JZCP-2");
        outReqVo.setTaskName("我的测试444444");
        outReqVo.setTestUserName("zhangw7");

        List<AutoExecutionRecordVo> autoRecordList = new ArrayList<>();
        for (int i = 0; i < 185; i++) {
            AutoExecutionRecordVo entity = new AutoExecutionRecordVo();
            entity.setCaseName("caseName200" + i);
            entity.setCaseId(i + 1);
            if (i % 7 == 0) {
                entity.setRecordResult("FAIL");
            } else {
                entity.setRecordResult("SUCCESS");
            }
            entity.setActualResult("啊哈哈");
            entity.setResponseTime(i + 100 + "");
            entity.setEnv("qa");
            entity.setRequestUrl("ziroom.com");
            entity.setRequestType("http");
            entity.setHeader("12333");
            entity.setRequestBody("{\"account\":\"6212260503002795295\",\"accountBank\":\"光大银行\",\"accountBankCode\":\"303100000006\",\"accountBranchBank\":\"string\",\"accountCity\":\"string\",\"accountIdNo\":\"140321199409291211\",\"accountIdType\":2,\"accountName\":\"赵家仪\",\"accountPhone\":\"15340771477\",\"accountType\":1,\"agentIdNo\":\"null\",\"agentIdType\":null,\"agentName\":\"null\",\"agentPhone\":\"null\",\"applyCode\":\"string\",\"applyName\":\"string\",\"builtUpArea\":\"string\",\"cityCode\":\"string\",\"cityK1Code\":\"string\",\"cityK1Name\":\"string\",\"cityK1Time\":\"2021-08-25T01:39:31.713Z\",\"contractCode\":\"BJ0162\",\"contractOwnerReqs\":[{\"contractCode\":\"string\",\"createTime\":\"2021-08-25T01:39:31.713Z\",\"ownerIdNo\":\"string\",\"ownerIdType\":0,\"ownerName\":\"string\",\"updateTime\":\"2021-08-25T01:39:31.713Z\"}],\"email\":\"string\",\"fczNo\":\"string\",\"floor\":\"string\",\"groupK1Code\":\"string\",\"groupK1Name\":\"string\",\"groupK1Time\":\"2021-08-25T01:39:31.713Z\",\"heatingMode\":0,\"houseType\":\"string\",\"id\":0,\"idNo\":\"string\",\"idType\":0,\"isAgent\":0,\"isGycq\":0,\"oaCode\":\"string\",\"oaName\":\"string\",\"oaTime\":\"2021-08-25T01:39:31.713Z\",\"ownerName\":\"string\",\"ownerType\":0,\"phone\":\"string\",\"postalAddr\":\"string\",\"propertyAddress\":\"string\",\"propertyNature\":\"string\",\"propertyNatureType\":0,\"propertyType\":0,\"refuseDesc\":\"string\",\"remark\":\"string\",\"status\":0,\"storeCode\":\"BJ2108250000310\",\"storeType\":0,\"urgentName\":\"string\",\"urgentPhone\":\"string\",\"version\":\"string\",\"wyAddress\":\"string\"}");
            entity.setApplicationName("store");
            entity.setList(JSON.parseArray("[{\"assertContent\":\"200\",\"assertResult\":\"FAILURE\",\"assertType\":0,\"recordId\":1285},{\"assertContent\":\"BJ0162\",\"assertResult\":\"FAILURE\",\"assertType\":0,\"recordId\":1285}]", AutoExecutionRecordAssert.class));
            autoRecordList.add(entity);
        }
        outReqVo.setAutoRecordList(autoRecordList);
        OutResVo resVo = testTaskOutService.createAutoTestExecution(outReqVo);
        System.out.println(JSON.toJSONString(resVo));
    }


//    @Test
//    public void queryTestApplicationByPage(){
//        Pagination<TestApplication> pagination = new Pagination<>();
//        Page page = new Page<>();
//        page.setCurrent(1);
//        page.setSize(10);
//        pagination.setPage(page);
//        Page<TestApplication> result = testApplicationService.queryTestApplicationByPage(pagination);
//        log.info(JSON.toJSONString(result));
//    }
//
//    @Test
//    public void queryApplicationAndModule(){
//        List<ApplicationModuleTree> newList = testApplicationService.queryApplicationAndModule("liangh4",null);
//        log.info(JSON.toJSONString(newList));
//    }
}

