//package com.ziroom.qa.quality.defende.provider.service;
//
//import com.ziroom.qa.quality.defende.provider.QualityDefendProviderApplication;
//import com.ziroom.qa.quality.defende.provider.vo.TaskTestCaseVo;
//import lombok.extern.slf4j.Slf4j;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.test.context.ActiveProfiles;
//import org.springframework.test.context.junit4.SpringRunner;
//
//@Slf4j
//@SpringBootTest(classes = QualityDefendProviderApplication.class,webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
//@RunWith(SpringRunner.class)
//@ActiveProfiles("dev")
//public class CleanUserInfoTest {
//
//    @Autowired
//    private SystemModuleService systemModuleService;
//    @Autowired
//    private TaskTestCaseService taskTestCaseService;
//    @Autowired
//    private TestCaseService testCaseService;
//    @Autowired
//    private TestExecutionService testExecutionService;
//    @Autowired
//    private TestTaskService testTaskService;
//
//    @Test
//    public void updateSystemModuleUserInfoTest(){
//        systemModuleService.updateSystemModuleUserInfo();
//    }
//
//    @Test
//    public void updateTaskTestCaseUserInfoTest(){
//        taskTestCaseService.updateTaskTestCaseUserInfo();
//    }
//
//    @Test
//    public void updateTestCaseUserInfoTest(){
//        testCaseService.updateTestCaseUserInfo();
//    }
//
//    @Test
//    public void updateTestExecutionUserInfoTest(){
//        testExecutionService.updateTestExecutionUserInfo();
//    }
//
//    @Test
//    public void updateTestTaskUserInfoTest(){
//        testTaskService.updateTestTaskUserInfo();
//    }
//
//    /**
//     *  测试任务的执行人-和负责人需要确认是不是都使用的邮箱前缀
//     */
//
////    /**
////     * 测试用例执行人需要把Uid修改为userName
////     */
////    @Test
////    public void batchExecuteTestCaseTest(){
////        testExecutionService.batchExecuteTestCaseTest();
////    }
//
//
//    /**
//     * 这里有一个BUG，需要多个单元测试用例覆盖
//     */
//    @Test
//    public void batchSaveOrUpdateTaskTestCaseTest(){
//        TaskTestCaseVo taskTestCaseVo = new TaskTestCaseVo();
//        taskTestCaseVo.setTaskId(23L);
//        taskTestCaseService.batchSaveOrUpdateTaskTestCase(taskTestCaseVo,"zhangxl15");
//    }
//
//}
