package com.ziroom.qa.quality.defende.provider.execTask.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ziroom.qa.quality.defende.provider.constant.enums.TestExecutionTypeEnum;
import com.ziroom.qa.quality.defende.provider.constant.enums.TestTaskStatusEnum;
import com.ziroom.qa.quality.defende.provider.constant.enums.TestTaskTypeEnum;
import com.ziroom.qa.quality.defende.provider.execTask.entity.TaskTestCase;
import com.ziroom.qa.quality.defende.provider.execTask.entity.TestExecution;
import com.ziroom.qa.quality.defende.provider.execTask.entity.TestTask;
import com.ziroom.qa.quality.defende.provider.execTask.entity.dto.ArkAppDTO;
import com.ziroom.qa.quality.defende.provider.execTask.entity.vo.ComplateAndEmailVO;
import com.ziroom.qa.quality.defende.provider.execTask.service.TaskTestCaseService;
import com.ziroom.qa.quality.defende.provider.execTask.service.TestTaskService;
import com.ziroom.qa.quality.defende.provider.result.RestResultVo;
import com.ziroom.qa.quality.defende.provider.vo.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Api(value = "测试执行(任务)管理", tags = {"测试执行(任务)管理"})
@Slf4j
@RestController
@RequestMapping("/testTask")
public class TestTaskController {

    @Autowired
    private TestTaskService testTaskService;
    @Autowired
    private TaskTestCaseService taskTestCaseService;

    @ApiOperation("获取测试执行状态列表")
    @GetMapping(value = "/getTestTaskStatusList")
    public RestResultVo<Map<String, String>> getTestTaskStatusList() {
        Map<String, String> map = new HashMap<>();
        TestTaskStatusEnum.getTestTaskStatusList().stream().forEach(status -> {
            map.put(status.getTestsTaskStatus(), status.getTestsTaskName());
        });
        return RestResultVo.fromData(map);
    }

    @ApiOperation("获取测试执行方法列表")
    @GetMapping(value = "/getTestExeMethodMap")
    public RestResultVo getTestExeMethodMap() {
        return RestResultVo.fromData(TestExecutionTypeEnum.getTestExeMethodMap());
    }

    @ApiOperation("获取任务类型列表")
    @GetMapping("/getTestTaskTypeList")
    public RestResultVo<Map<String, String>> getTestTaskTypeList() {
        Map<String, String> map = new HashMap<>();
        TestTaskTypeEnum.getTestTaskTypeList().stream().forEach(status -> {
            map.put(status.getTestsTaskType(), status.getTypeName());
        });
        return RestResultVo.fromData(map);
    }

    @ApiOperation("获取appid信息列表")
    @GetMapping("/getAppInfoList")
    public RestResultVo<List<ArkAppDTO>> getAppInfoList() {
        return RestResultVo.fromData(testTaskService.getAppInfoList());
    }


    @ApiOperation("分页查询")
    @PostMapping(value = "/queryTestTaskByPage", produces = {"application/json;charset=UTF-8"})
    public RestResultVo<Page<TestTask>> queryTestTaskByPage(@RequestHeader String userName, @RequestBody Pagination<TestTask> pagination) {
        //1. 分页查询参数处理
        TestTask testTask = pagination.getSearchObj();
        try {
            //2. 调用分页查询
            Page<TestTask> testTaskPageResult = testTaskService.queryTestTaskByPage(pagination.getPage(), testTask, userName);
            return RestResultVo.fromData(testTaskPageResult);
        } catch (Exception e) {
            log.error("/testTask/queryTestTaskByPage pagination=={} 分页查询测试执行任务失败!", pagination, e);
            return RestResultVo.fromErrorMessage(e.getMessage());
        }
    }


    @ApiOperation("保存或更新测试执行任务")
    @PostMapping("/saveOrUpdateTestTask")
    public RestResultVo saveOrUpdateTestTask(@RequestBody TestTask testTask, HttpServletRequest request) {
        String userName = request.getHeader("userName");
        RestResultVo restResultVo;
        try {
            long id = testTaskService.saveOrUpdateTestTask(testTask, userName);
            restResultVo = RestResultVo.fromData(id);
        } catch (Exception e) {
            log.error("/testTask/saveOrUpdateTestTask uid=={} 保存或更新测试执行任务失败!", userName, e);
            restResultVo = RestResultVo.fromErrorMessage(e.getMessage());
        }
        return restResultVo;
    }

    @ApiOperation("验证测试执行任务名称唯一")
    @GetMapping("/validateTestTaskName")
    public RestResultVo validateTestTaskName(@RequestParam("taskName") String taskName,
                                             @RequestParam(value = "taskId", required = false) Long taskId) {
        RestResultVo restResultVo;
        try {
            testTaskService.validateTestTaskName(taskName, taskId);
            restResultVo = RestResultVo.fromSuccess("成功");
        } catch (Exception e) {
            log.error("/testTask/validateTestTaskName taskName=={} 验证测试执行任务名称唯一失败!", taskName, e);
            restResultVo = RestResultVo.fromErrorMessage(e.getMessage());
        }
        return restResultVo;
    }


    @ApiOperation("获取测试 task 对应的测试结果")
    @GetMapping("/getTestReportStatusByTaskId")
    public RestResultVo getTestReportStatusByTaskId(@RequestParam("taskId") Long taskId) {
        RestResultVo restResultVo;
        try {
            TestReportStatusVo statusVo = taskTestCaseService.getTestReportStatusByTaskId(taskId);
            restResultVo = RestResultVo.fromData(statusVo);
        } catch (Exception e) {
            log.error("/testTask/getTestReportStatusByTaskId taskId=={} 获取测试 task 对应的测试结果失败!", taskId, e);
            restResultVo = RestResultVo.fromErrorMessage(e.getMessage());
        }
        return restResultVo;
    }

    @ApiOperation("获取测试 task 对应的测试报告")
    @GetMapping("/getTestReportSummaryByTaskId")
    public RestResultVo getTestReportSummaryByTaskId(@RequestParam("taskId") Long taskId) {
        TestTask testTask = testTaskService.getById(taskId);
        TestReportSummaryVo summaryVo = new TestReportSummaryVo();
        Integer totalCount = Objects.isNull(testTask.getTestcaseCount()) ? 0 : testTask.getTestcaseCount();
        summaryVo.setTaskName(testTask.getTaskName());
        summaryVo.setTotalCount(totalCount);
        DecimalFormat df = new DecimalFormat("0.00");
        df.setRoundingMode(RoundingMode.HALF_UP);
        if (totalCount == 0) {
            summaryVo.setPassedRatio(0 + "%");
            summaryVo.setFailedRatio(0 + "%");
            summaryVo.setSkippedRatio(0 + "%");
        } else {
            Integer successCount = Objects.isNull(testTask.getRunSuccessCount()) ? 0 : testTask.getRunSuccessCount();
            Integer failCount = Objects.isNull(testTask.getRunFailCount()) ? 0 : testTask.getRunFailCount();
            Integer skipCount = Objects.isNull(testTask.getRunSkipCount()) ? 0 : testTask.getRunSkipCount();
            summaryVo.setPassedRatio(df.format(((double) successCount / (double) totalCount) * 100) + "%");
            summaryVo.setFailedRatio(df.format(((double) failCount / (double) totalCount) * 100) + "%");
            summaryVo.setSkippedRatio(df.format(((double) skipCount / (double) totalCount) * 100) + "%");
        }
        summaryVo.setJiraRequirement("TEST BUG");
        return RestResultVo.fromData(summaryVo);
    }

    @ApiOperation("获取测试 task 对应的bug列表")
    @PostMapping("/getTestReportBugListByTaskId")
    public RestResultVo<Page<TestReportBugListVo>> getTestReportBugListByTaskId(@RequestBody Pagination<TestExecution> pagination) {
        TestExecution executeVo = pagination.getSearchObj();
        Long taskId = executeVo.getExecutionTaskId();
        return testTaskService.getTestReportBugListByTaskId(pagination.getPage(), taskId);
    }

    @ApiOperation("获取测试 task 对应的测试用例列表")
    @PostMapping("/getTestReportCaseListByTaskId")
    public RestResultVo<Map<String, Page<TaskTestCase>>> getTestReportCaseListByTaskId(@RequestBody Pagination<TaskTestCase> pagination) {
        TaskTestCase taskTestCase = pagination.getSearchObj();
        Long taskId = taskTestCase.getTaskId();
        String type = taskTestCase.getType();
        return testTaskService.getTestReportCaseListByTaskId(pagination.getPage(), taskId, type);
    }

    @ApiOperation("获取测试执行任务详情")
    @GetMapping("/getTestTaskDetailById")
    public RestResultVo<TestTask> getTestTaskDetailById(@RequestParam("id") Long id) {
        return RestResultVo.fromData(testTaskService.getTestTaskDetailById(id));
    }

    @ApiOperation("批量删除测试执行任务")
    @GetMapping(value = "/batchDeleteTestTask", produces = {"application/json;charset=UTF-8"})
    public RestResultVo<String> batchDeleteTestTask(@RequestHeader String userName, @RequestParam("idList") List<Long> idList) {
        try {
            TestResultVo resultVo = testTaskService.batchDeleteTestTask(idList, userName);
            if (resultVo.getFlag()) {
                return RestResultVo.fromSuccess(resultVo.getMsgRes());
            } else {
                return RestResultVo.fromErrorMessage(resultVo.getMsgRes());
            }
        } catch (Exception e) {
            return RestResultVo.fromErrorMessage(e.getMessage());
        }
    }

    @ApiOperation("更新用户信息")
    @GetMapping("/updateTestTaskUserInfo")
    public RestResultVo<Boolean> updateTestTaskUserInfo() {
        boolean result = testTaskService.updateTestTaskUserInfo();
        return RestResultVo.fromData(result);
    }

    @ApiOperation("完成测试执行任务")
    @GetMapping("/completeTaskStatus")
    public RestResultVo completeTaskStatus(@RequestHeader String userName, @RequestParam("idList") List<Long> idList) {
        try {
            TestResultVo resultVo = testTaskService.completeTaskStatus(idList, userName);
            if (resultVo.getFlag()) {
                return RestResultVo.fromSuccess(resultVo.getMsgRes());
            } else {
                return RestResultVo.fromErrorMessage(resultVo.getMsgRes());
            }
        } catch (Exception e) {
            return RestResultVo.fromErrorMessage(e.getMessage());
        }
    }

    @ApiOperation("重启测试执行任务")
    @GetMapping("/restartTaskStatus")
    public RestResultVo restartTaskStatus(@RequestHeader String userName, @RequestParam("idList") List<Long> idList) {
        try {
            TestResultVo resultVo = testTaskService.restartTaskStatus(idList, userName);
            if (resultVo.getFlag()) {
                return RestResultVo.fromSuccess(resultVo.getMsgRes());
            } else {
                return RestResultVo.fromErrorMessage(resultVo.getMsgRes());
            }
        } catch (Exception e) {
            return RestResultVo.fromErrorMessage(e.getMessage());
        }
    }

    @ApiOperation("完成测试执行任务并发送邮件")
    @PostMapping("/completeTaskAndEmail")
    public RestResultVo completeTaskAndEmail(@RequestHeader String userName, @RequestBody ComplateAndEmailVO complateAndEmailVO) {
        complateAndEmailVO.setComplateAndEmailFlag(true);
        testTaskService.completeTaskAndEmail(complateAndEmailVO, userName);
        return RestResultVo.fromSuccess("成功");
    }

    @ApiOperation("完成测试执行任务并发送邮件")
    @PostMapping("/sendTestTaskEmail")
    public RestResultVo sendTestTaskEmail(@RequestHeader String userName, @RequestBody ComplateAndEmailVO complateAndEmailVO) {
        testTaskService.completeTaskAndEmail(complateAndEmailVO, userName);
        return RestResultVo.fromSuccess("成功");
    }


}
