package com.ziroom.qa.quality.defende.provider.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ziroom.qa.quality.defende.provider.QualityDefendProviderApplication;
import com.ziroom.qa.quality.defende.provider.constant.QualityDefendeConstants;
import com.ziroom.qa.quality.defende.provider.constant.enums.TestPlanStatusEnum;
import com.ziroom.qa.quality.defende.provider.entity.TestPlan;
import com.ziroom.qa.quality.defende.provider.vo.Pagination;
import com.ziroom.qa.quality.defende.provider.vo.TestResultVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@SpringBootTest(classes = QualityDefendProviderApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@RunWith(SpringRunner.class)
@ActiveProfiles("dev")
public class TestPlanServiceTest {

    String planName1 = "我的first testplan";

    String planName2 = "我的second testplan";

    String planName3 = "我的three testplan";

    String planNameUpdate = "修改后的testplan";

    String uid = "60032509";

    @Autowired
    private TestPlanService testPlanService;

    @Before
    public void setStart() throws Exception {
        this.clearAllInfo();
    }

    @After
    public void setEnd() throws Exception {
        this.clearAllInfo();
//        TestPlan testPlan1 = this.getTestPlanInfo(planName1);
//        TestPlan testPlan2 = this.getTestPlanInfo(planName2);
//        TestPlan testPlan3 = this.getTestPlanInfo(planName3);
//        testPlanService.insertTestPlan(testPlan1);
//        testPlanService.insertTestPlan(testPlan2);
//        testPlanService.insertTestPlan(testPlan3);
    }


    @Test
    public void queryTestPlanByPageTest() {
        //1.增加数据
        TestPlan testPlan1 = this.getTestPlanInfo(planName1);
        TestPlan testPlan2 = this.getTestPlanInfo(planName2);
        TestPlan testPlan3 = this.getTestPlanInfo(planName3);
        testPlanService.insertTestPlan(testPlan1);
        testPlanService.insertTestPlan(testPlan2);
        testPlanService.insertTestPlan(testPlan3);
        //2.查询数据 全查
        Pagination<TestPlan> pagination = new Pagination<>();
        TestPlan test = new TestPlan();
        pagination.setSearchObj(test);
        pagination.setPage(new Page());
        Page<TestPlan> page = testPlanService.queryTestPlanByPage(pagination);
        List<TestPlan> queryList = page.getRecords();
        Assert.assertEquals(queryList.size(), 3);
        //3.查询planName1
        pagination.getSearchObj().setPlanName(planName1);
        page = testPlanService.queryTestPlanByPage(pagination);
        queryList = page.getRecords();
        Assert.assertEquals(queryList.size(), 1);
        Assert.assertEquals(queryList.get(0).getPlanName(), planName1);
    }

    @Test
    public void updateTestPlanTest() {
        //1.增加数据
        TestPlan testPlan1 = this.getTestPlanInfo(planName1);
        testPlanService.insertTestPlan(testPlan1);
        //2.查询数据 全查
        Pagination<TestPlan> pagination = new Pagination<>();
        TestPlan test = new TestPlan();
        pagination.setSearchObj(test);
        pagination.setPage(new Page());
        Page<TestPlan> page = testPlanService.queryTestPlanByPage(pagination);
        List<TestPlan> queryList = page.getRecords();
        Assert.assertEquals(queryList.size(), 1);
        Assert.assertEquals(queryList.get(0).getPlanName(), planName1);
        String oldVersion = queryList.get(0).getTestPlanVersion();
        Assert.assertEquals(oldVersion, QualityDefendeConstants.TEST_CASE_DEFAULT_VERSION);
        //3.去修改
        queryList.get(0).setPlanName(planNameUpdate);
        TestResultVo vo = testPlanService.updateTestPlan(queryList.get(0));
        Assert.assertTrue(vo.getFlag());
        //4.查询planNameUpdate -> 数量1
        pagination.getSearchObj().setPlanName(planNameUpdate);
        page = testPlanService.queryTestPlanByPage(pagination);
        queryList = page.getRecords();
        Assert.assertEquals(queryList.size(), 1);
        Assert.assertEquals(queryList.get(0).getTestPlanVersion(), String.format("%.1f", Double.parseDouble(oldVersion)+0.1));
        //5.查询planName1 -> 数量0
        pagination.getSearchObj().setPlanName(planName1);
        page = testPlanService.queryTestPlanByPage(pagination);
        queryList = page.getRecords();
        Assert.assertEquals(queryList.size(), 0);
    }

    @Test
    public void insertTestPlanTest() {
        //1.增加数据
        TestPlan testPlan1 = this.getTestPlanInfo(planName1);
        testPlanService.insertTestPlan(testPlan1);
        //2.查询数据 全查
        Pagination<TestPlan> pagination = new Pagination<>();
        TestPlan test = new TestPlan();
        pagination.setSearchObj(test);
        pagination.setPage(new Page());
        Page<TestPlan> page = testPlanService.queryTestPlanByPage(pagination);
        List<TestPlan> queryList = page.getRecords();
        Assert.assertEquals(queryList.size(), 1);
        Assert.assertEquals(queryList.get(0).getPlanName(), planName1);
    }

    @Test
    public void deleteTestPlanTest() {
        //1.增加数据
        TestPlan testPlan1 = this.getTestPlanInfo(planName1);
        testPlanService.insertTestPlan(testPlan1);
        //2.查询数据 全查
        Pagination<TestPlan> pagination = new Pagination<>();
        TestPlan test = new TestPlan();
        pagination.setSearchObj(test);
        pagination.setPage(new Page());
        Page<TestPlan> page = testPlanService.queryTestPlanByPage(pagination);
        List<TestPlan> queryList = page.getRecords();
        Assert.assertEquals(queryList.size(), 1);
        //3.去删除
        List<Long> idList = queryList.stream().map(TestPlan::getId).collect(Collectors.toList());
        TestResultVo vo = testPlanService.deleteTestPlan(uid,idList);
        Assert.assertTrue(vo.getFlag());
        //4.查询planNameUpdate -> 数量1
        pagination.getSearchObj().setPlanName(planName1);
        page = testPlanService.queryTestPlanByPage(pagination);
        queryList = page.getRecords();
        Assert.assertEquals(queryList.size(), 0);
    }

    @Test
    public void checkNewPlanNameTest() {
        //1.增加数据
        TestPlan testPlan1 = this.getTestPlanInfo(planName1);
        testPlanService.insertTestPlan(testPlan1);
        //2.校验名称是否存在
        TestResultVo vo = testPlanService.checkNewPlanName(null,planName1);
        Assert.assertFalse(vo.getFlag());
        //3.查询数据
        Pagination<TestPlan> pagination = new Pagination<>();
        TestPlan test = new TestPlan();
        test.setPlanName(planName1);
        pagination.setSearchObj(test);
        pagination.setPage(new Page());
        Page<TestPlan> page = testPlanService.queryTestPlanByPage(pagination);
        List<TestPlan> queryList = page.getRecords();
        Assert.assertEquals(queryList.size(), 1);
        //4.校验已修改名称是否存在
        vo = testPlanService.checkNewPlanName(queryList.get(0).getId(),planName1);
        Assert.assertTrue(vo.getFlag());
    }

    @Test
    public void queryIdsByUncheckTest() {
        //1.增加数据
        TestPlan testPlan1 = this.getTestPlanInfo(planName1);
        TestPlan testPlan2 = this.getTestPlanInfo(planName2);
        TestPlan testPlan3 = this.getTestPlanInfo(planName3);
        testPlanService.insertTestPlan(testPlan1);
        testPlanService.insertTestPlan(testPlan2);
        testPlanService.insertTestPlan(testPlan3);
        //2.批量提交用户信息去待审核
        TestPlan testPlan = new TestPlan();
        testPlan.setTestPlanStatus(TestPlanStatusEnum.UNSUBMIT.getTestPlanStatus());
        List<Long> uncheckList = testPlanService.queryIdsByUncheck(testPlan);
        testPlanService.submitTestPlanList(uid,uncheckList);
        //3.去查询待审核的执行计划id集合
        testPlan.setTestPlanStatus(TestPlanStatusEnum.PENDING.getTestPlanStatus());
        uncheckList = testPlanService.queryIdsByUncheck(testPlan);
        Assert.assertEquals(uncheckList.size(), 3);
    }

//    @Test
//    public void queryTestPlanHistoryByPlanKeyTest() {
//    }

    @Test
    public void batchApprovedTestPlanTest() {
        //1.增加数据
        TestPlan testPlan1 = this.getTestPlanInfo(planName1);
        TestPlan testPlan2 = this.getTestPlanInfo(planName2);
        TestPlan testPlan3 = this.getTestPlanInfo(planName3);
        testPlanService.insertTestPlan(testPlan1);
        testPlanService.insertTestPlan(testPlan2);
        testPlanService.insertTestPlan(testPlan3);
        //2.批量提交用户信息去待审核
        TestPlan testPlan = new TestPlan();
        testPlan.setTestPlanStatus(TestPlanStatusEnum.UNSUBMIT.getTestPlanStatus());
        List<Long> uncheckList = testPlanService.queryIdsByUncheck(testPlan);
        testPlanService.submitTestPlanList(uid,uncheckList);
        //3.去查询待审核的执行计划id集合
        testPlan.setTestPlanStatus(TestPlanStatusEnum.PENDING.getTestPlanStatus());
        uncheckList = testPlanService.queryIdsByUncheck(testPlan);
        Assert.assertEquals(uncheckList.size(), 3);
        //4.去进行批量审批通过
        TestResultVo vo = testPlanService.batchApprovedTestPlan(uid,TestPlanStatusEnum.APPROVED.getTestPlanStatus(),"审批通过",uncheckList);
        Assert.assertTrue(vo.getFlag());
        //5.查询审核通过的数据
        Pagination<TestPlan> pagination = new Pagination<>();
        TestPlan test = new TestPlan();
        test.setTestPlanStatus(TestPlanStatusEnum.APPROVED.getTestPlanStatus());
        pagination.setSearchObj(test);
        pagination.setPage(new Page());
        Page<TestPlan> page = testPlanService.queryTestPlanByPage(pagination);
        List<TestPlan> queryList = page.getRecords();
        Assert.assertEquals(3, queryList.size());
        Assert.assertEquals(QualityDefendeConstants.TEST_CASE_PASS_DEFAULT_VERSION,queryList.get(0).getTestPlanVersion());
    }

    @Test
    public void submitTestPlanListTest() {
        //1.增加数据
        TestPlan testPlan1 = this.getTestPlanInfo(planName1);
        TestPlan testPlan2 = this.getTestPlanInfo(planName2);
        TestPlan testPlan3 = this.getTestPlanInfo(planName3);
        testPlanService.insertTestPlan(testPlan1);
        testPlanService.insertTestPlan(testPlan2);
        testPlanService.insertTestPlan(testPlan3);
        //2.批量提交用户信息去待审核
        TestPlan testPlan = new TestPlan();
        testPlan.setTestPlanStatus(TestPlanStatusEnum.UNSUBMIT.getTestPlanStatus());
        List<Long> uncheckList = testPlanService.queryIdsByUncheck(testPlan);
        testPlanService.submitTestPlanList(uid,uncheckList);
        //3.去查询待审核的执行计划id集合
        testPlan.setTestPlanStatus(TestPlanStatusEnum.PENDING.getTestPlanStatus());
        uncheckList = testPlanService.queryIdsByUncheck(testPlan);
        Assert.assertEquals(uncheckList.size(), 3);
    }

    /**
     * 清空数据
     */
    private void clearAllInfo(){
        List<TestPlan> list = testPlanService.list();
        if(CollectionUtils.isNotEmpty(list)){
            testPlanService.removeByIds(list.stream().map(TestPlan::getId).collect(Collectors.toList()));
        }
    }

    /**
     * 创建测试计划
     * @param planName
     * @return
     */
    private TestPlan getTestPlanInfo(String planName){
        TestPlan testPlan = new TestPlan();
        testPlan.setPlanName(planName);
        testPlan.setRelationRequirement(planName+":"+"JIRA001");
        //需要关联字典id 测试环境
        testPlan.setTestEnvironment("tset");
        //需要关联字典id 测试策略
        testPlan.setTestStrategy("integrationTesting");
        testPlan.setTestPersionList("liangh4");
        testPlan.setTestPlanMaster("liangh4");
        testPlan.setTestPlanStartTime(LocalDateTime.now());
        testPlan.setTestPlanEndTime(LocalDateTime.now());
        //测试范围
        testPlan.setTestRange("我的测试xxx1");
        testPlan.setTestRisk("风险xxx1");
        testPlan.setCreateTime(LocalDateTime.now());
        testPlan.setCreateUser(uid);
        testPlan.setUpdateTime(LocalDateTime.now());
        testPlan.setUpdateUser(uid);
        return testPlan;
    }
}
