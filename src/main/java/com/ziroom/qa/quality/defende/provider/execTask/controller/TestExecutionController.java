package com.ziroom.qa.quality.defende.provider.execTask.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ziroom.qa.quality.defende.provider.constant.enums.TestTaskCaseStatusEnum;
import com.ziroom.qa.quality.defende.provider.execTask.entity.TaskTestCase;
import com.ziroom.qa.quality.defende.provider.execTask.entity.TestExecution;
import com.ziroom.qa.quality.defende.provider.execTask.entity.vo.DemandVO;
import com.ziroom.qa.quality.defende.provider.execTask.service.TestExecutionService;
import com.ziroom.qa.quality.defende.provider.result.RestResultVo;
import com.ziroom.qa.quality.defende.provider.vo.BatchExecuteVo;
import com.ziroom.qa.quality.defende.provider.vo.Pagination;
import com.ziroom.qa.quality.defende.provider.vo.SingleExecuteVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(value = "测试执行", tags = {"测试执行"})
@Slf4j
@RestController
@RequestMapping("/testExecution")
public class TestExecutionController {

    @Autowired
    private TestExecutionService testExecutionService;

    @ApiOperation("分页查询")
    @PostMapping(value = "/queryTestCaseByPage", produces = {"application/json;charset=UTF-8"})
    public RestResultVo<Page<TaskTestCase>> queryTestCaseByPage(@RequestBody Pagination<TaskTestCase> pagination) {

        TaskTestCase taskTestCase = pagination.getSearchObj();
        Long taskId = taskTestCase.getTaskId();
        Page page = pagination.getPage();

        return testExecutionService.queryTestCaseByPage(taskId, page);
    }

    @ApiOperation("批量执行测试用例")
    @PostMapping(value = "/batchExecuteTestCase", produces = {"application/json;charset=UTF-8"})
    public RestResultVo batchExecuteTestCase(@RequestHeader String userName, @RequestBody BatchExecuteVo batchExecute) {
        try {
            String result = testExecutionService.batchExecuteTestCase(batchExecute, userName);
            return RestResultVo.fromSuccess(result);
        } catch (Exception e) {
            log.error("/testExecution/batchExecuteTestCase 批量执行测试用例失败! ", e);
            return RestResultVo.fromErrorMessage(e.getMessage());
        }
    }

    @ApiOperation("将用例标记为失败")
    @PostMapping(value = "/markTestCaseFail", produces = {"application/json;charset=UTF-8"})
    public RestResultVo markTestCaseFail(@RequestHeader String userName,
                                         @RequestBody SingleExecuteVo singleExecute) {
        if (StringUtils.isBlank(singleExecute.getRelationBug())) {
            return RestResultVo.fromErrorMessage("失败需要关联jira bug");
        }
        if (StringUtils.isBlank(singleExecute.getExecutionVersion())) {
            return RestResultVo.fromErrorMessage("版本号不能为空");
        }
        try {
            testExecutionService.markTestCaseFail(singleExecute, userName);
            return RestResultVo.fromSuccess("BUG关联成功");
        } catch (Exception e) {
            log.error("/testExecution/markTestCaseFail 将用例标记为失败! ", e);
            return RestResultVo.fromErrorMessage(e.getMessage());
        }
    }

    @ApiOperation("获取用例各执行状态数目")
    @GetMapping(value = "/getCaseStatusNumber", produces = {"application/json;charset=UTF-8"})
    public RestResultVo getCaseStatusNumber(@RequestParam("taskId") Long taskId, @RequestHeader String userName) {

        return testExecutionService.getCaseStatusNumber(taskId, userName);
    }

    @ApiOperation("获取执行计结果划枚举信息")
    @GetMapping(value = "/getTestExecutionMap")
    public RestResultVo getTestExecutionMap() {
        return RestResultVo.fromData(TestTaskCaseStatusEnum.getTestExecutionMap());
    }

    @ApiOperation("查询测试执行历史")
    @PostMapping(value = "/getTestExecutionHistory", produces = {"application/json;charset=UTF-8"})
    public RestResultVo<List<TestExecution>> getTestExecutionHistory(@RequestHeader String userName, @RequestBody TestExecution testExecution) {
        try {
            List<TestExecution> list = testExecutionService.getTestExecutionHistory(testExecution);
            return RestResultVo.fromData(list);
        } catch (Exception e) {
            log.error("/testExecution/getTestExecutionHistory 查询测试执行历史失败! ", e);
            return RestResultVo.fromErrorMessage(e.getMessage());
        }
    }

    @ApiOperation("一键生成bug")
    @PostMapping(value = "/createBugByInfo")
    public RestResultVo createBugByInfo(@RequestHeader String userName,
                                        @RequestBody SingleExecuteVo singleExecute) {
        testExecutionService.createBugByInfo(singleExecute,userName);
        return RestResultVo.fromSuccess("成功！");
    }

    @ApiOperation("查询需求池对应的必要信息")
    @PostMapping(value = "/queryDemandInfo")
    public RestResultVo queryDemandInfo(@RequestHeader String userName,
                                        @RequestParam Long taskId) {
        DemandVO demandVO = testExecutionService.queryDemandInfo(taskId,userName);
        return RestResultVo.fromData(demandVO);
    }

//    @ApiOperation("校验关联的测试用例版本是否变更")
//    @PostMapping(value = "/checkTestCaseVersion", produces = {"application/json;charset=UTF-8"})
//    public RestResultVo checkTestCaseVersion(@RequestHeader String userName, @RequestBody BatchExecuteVo batchExecute) {
//        try {
//            TestResultVo resultVo = testExecutionService.checkTestCaseVersion(batchExecute, userName);
//            if (resultVo.getFlag()) {
//                return RestResultVo.fromSuccess(resultVo.getMsgRes());
//            } else {
//                return RestResultVo.fromErrorMessage(resultVo.getMsgRes());
//            }
//        } catch (Exception e) {
//            log.error("/testExecution/checkTestCaseVersion 校验关联的测试用例版本是否变更失败! ", e);
//            return RestResultVo.fromErrorMessage(e.getMessage());
//        }
//    }

}
