package com.ziroom.qa.quality.defende.provider.controller;

import com.alibaba.fastjson.JSON;
import com.ziroom.qa.quality.defende.provider.execTask.entity.TestTask;
import com.ziroom.qa.quality.defende.provider.result.RestResultVo;
import com.ziroom.qa.quality.defende.provider.execTask.service.TestExecutionService;
import com.ziroom.qa.quality.defende.provider.service.TestStatisticService;
import com.ziroom.qa.quality.defende.provider.execTask.service.TestTaskService;
import com.ziroom.qa.quality.defende.provider.vo.TestResultVo;
import com.ziroom.qa.quality.defende.provider.vo.statistics.StatisticSearchVo;
import com.ziroom.qa.quality.defende.provider.vo.statistics.TestCaseStatisticResultVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Api(value = "统计报表", tags = {"统计报表"})
@RequestMapping("/testStatistic")
@RestController
@Slf4j
public class TestStatisticController {

    @Autowired
    private TestStatisticService testStatisticService;

    @Autowired
    private TestTaskService testTaskService;

    @Autowired
    private TestExecutionService testExecutionService;

    @ApiOperation("查询测试用例和任务维度统计报表")
    @PostMapping(value = "/getTestCaseAndTaskStatistics")
    public RestResultVo<TestCaseStatisticResultVo> getTestCaseAndTaskStatistics(@RequestBody StatisticSearchVo searchVo) {
        RestResultVo restResultVo;
        try {
            TestResultVo resultVo = testStatisticService.getTestCaseAndTaskStatistics(searchVo);
            if (resultVo.getFlag()) {
                restResultVo = RestResultVo.fromData(resultVo.getData());
            } else {
                restResultVo = RestResultVo.fromErrorMessage(resultVo.getMsgRes());
            }
        } catch (Exception e) {
            log.error("/testStatistic/getTestCaseAndTaskStatistics searchVo=={} 查询失败!", JSON.toJSONString(searchVo), e);
            restResultVo = RestResultVo.fromErrorMessage(e.getMessage());
        }
        return restResultVo;
    }

    @ApiOperation("查询测试用例和任务维度统计报表 by people")
    @PostMapping(value = "/getTestCaseAndTaskStatisticsByPeople")
    public RestResultVo<TestCaseStatisticResultVo> getTestCaseAndTaskStatisticsByPeople(@RequestBody StatisticSearchVo searchVo) {
        RestResultVo restResultVo;
        try {
            TestResultVo resultVo = testStatisticService.getTestCaseAndTaskStatisticsByPeople(searchVo);
            if (resultVo.getFlag()) {
                restResultVo = RestResultVo.fromData(resultVo.getData());
            } else {
                restResultVo = RestResultVo.fromErrorMessage(resultVo.getMsgRes());
            }
        } catch (Exception e) {
            log.error("/testStatistic/getTestCaseAndTaskStatisticsByPeople searchVo=={} 查询失败!", JSON.toJSONString(searchVo), e);
            restResultVo = RestResultVo.fromErrorMessage(e.getMessage());
        }
        return restResultVo;
    }

    @ApiOperation("统计部门下人员创建用例和用例执行的数量")
    @PostMapping(value = "/getUserTestCaseStatisticsByDeptCode")
    public RestResultVo<TestCaseStatisticResultVo> getUserTestCaseStatisticsByDeptCode(@RequestBody StatisticSearchVo searchVo) {
        RestResultVo restResultVo;
        try {
            TestResultVo resultVo = testStatisticService.getUserTestCaseStatisticsByDeptCode(searchVo);
            if (resultVo.getFlag()) {
                restResultVo = RestResultVo.fromData(resultVo.getData());
            } else {
                restResultVo = RestResultVo.fromErrorMessage(resultVo.getMsgRes());
            }
        } catch (Exception e) {
            log.error("/testStatistic/getUserTestCaseStatisticsByDeptCode searchVo=={} 查询失败!", JSON.toJSONString(searchVo), e);
            restResultVo = RestResultVo.fromErrorMessage(e.getMessage());
        }
        return restResultVo;
    }

    @ApiOperation("用力热度排行")
    @PostMapping(value = "/getTestCaseTop")
    public RestResultVo getTestCaseTop(@RequestBody StatisticSearchVo searchVo) {
        RestResultVo restResultVo;
        try {
            TestResultVo resultVo = testStatisticService.getTestCaseTop(searchVo);
            if (resultVo.getFlag()) {
                restResultVo = RestResultVo.fromData(resultVo.getData());
            } else {
                restResultVo = RestResultVo.fromErrorMessage(resultVo.getMsgRes());
            }
        } catch (Exception e) {
            log.error("/testStatistic/getTestCaseTop searchVo=={} 查询失败!", JSON.toJSONString(searchVo), e);
            restResultVo = RestResultVo.fromErrorMessage(e.getMessage());
        }
        return restResultVo;
    }

    @PostMapping(value = "/updateTestExeinfo")
    public RestResultVo<TestCaseStatisticResultVo> updateTestExeinfo(@RequestParam(value = "taskId", required = false) Long taskId) {
        RestResultVo restResultVo = RestResultVo.fromSuccess("成功");
        try {
            List<TestTask> list = new ArrayList<>();
            if (Objects.isNull(taskId)) {
                list = testTaskService.list();
            } else {
                TestTask testTask = new TestTask();
                testTask.setId(taskId);
                list.add(testTask);
            }
            StringBuffer sb = new StringBuffer();
            for (TestTask testTask : list) {
                testExecutionService.updateCount(testTask.getId(),null);
                sb.append(testTask.getId() + ":" + testTask.getTaskName() + "/n");
            }
            restResultVo.setData(sb.toString());
        } catch (Exception e) {
            restResultVo = RestResultVo.fromErrorMessage(e.getMessage());
        }
        return restResultVo;
    }

}
