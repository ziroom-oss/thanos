package com.ziroom.qa.quality.defende.provider.execTask.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ziroom.qa.quality.defende.provider.caseRepository.entity.TestCase;
import com.ziroom.qa.quality.defende.provider.caseRepository.entity.dto.SingleApiCaseListDto;
import com.ziroom.qa.quality.defende.provider.execTask.entity.TaskTestCase;
import com.ziroom.qa.quality.defende.provider.execTask.service.TaskTestCaseService;
import com.ziroom.qa.quality.defende.provider.result.RestResultVo;
import com.ziroom.qa.quality.defende.provider.vo.Pagination;
import com.ziroom.qa.quality.defende.provider.vo.TaskTestCaseVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(value = "测试执行任务关联的测试用例集合", tags = {"测试执行任务关联的测试用例集合"})
@Slf4j
@RestController
@RequestMapping("/taskTestCase")
public class TaskTestCaseController {

    @Autowired
    private TaskTestCaseService taskTestCaseService;

    @ApiOperation("测试执行批量关联测试用例")
    @PostMapping("/batchSaveOrUpdateTaskTestCase")
    public RestResultVo batchSaveOrUpdateTaskTestCase(@RequestHeader String userName, @RequestBody TaskTestCaseVo taskTestCaseVo) {
        try {
            taskTestCaseService.batchSaveOrUpdateTaskTestCase(taskTestCaseVo, userName);
            return RestResultVo.fromSuccess("关联成功");
        } catch (Exception e) {
            return RestResultVo.fromErrorMessage("关联失败");
        }
    }

    @ApiOperation("根据测试执行任务ID获取关联用例集合列表")
    @GetMapping("/getTaskTestCaseListByTaskId")
    public RestResultVo<List<TaskTestCase>> getTaskTestCaseListByTaskId(@RequestParam Long taskId) {
        return RestResultVo.fromData(taskTestCaseService.getTaskTestCaseByTaskId(taskId));
    }

    @ApiOperation("批量删除关联测试执行用例列表")
    @PostMapping("/batchDeleteTaskTestCase")
    public RestResultVo batchDeleteTaskTestCase(@RequestHeader String userName, @RequestBody TaskTestCaseVo taskTestCaseVo) {
        taskTestCaseVo.setUserName(userName);
        taskTestCaseService.batchDeleteTaskTestCase(taskTestCaseVo);
        return RestResultVo.fromSuccess("删除成功");
    }

    @ApiOperation("数据清洗-清洗创建人和更新的用户信息")
    @GetMapping("/updateTaskTestCaseUserInfo")
    public RestResultVo updateTaskTestCaseUserInfo() {
        boolean result = taskTestCaseService.updateTaskTestCaseUserInfo();
        return RestResultVo.fromData(result);
    }

    @ApiOperation("分页查询已关联的测试用例")
    @PostMapping(value = "/queryRelTestCaseByPage", produces = {"application/json;charset=UTF-8"})
    public RestResultVo<Page<TaskTestCase>> queryRelTestCaseByPage(@RequestBody Pagination<TaskTestCase> pagination) {
        RestResultVo restResultVo;
        try {
            Page<TaskTestCase> page = taskTestCaseService.queryRelTestCaseByPage(pagination);
            restResultVo = RestResultVo.fromData(page);
        } catch (Exception e) {
            log.error("/taskTestCase/queryRelTestCaseByPage uid=={} 分页查询已关联的测试用例失败!", e);
            restResultVo = RestResultVo.fromErrorMessage(e.getMessage());
        }
        return restResultVo;
    }

    @ApiOperation("分页查询未关联的测试用例（自动）")
    @PostMapping(value = "/queryUnRelApiCaseByPage", produces = {"application/json;charset=UTF-8"})
    public RestResultVo queryUnRelApiCaseByPage(@RequestBody Pagination<SingleApiCaseListDto> pagination) {
        return RestResultVo.fromData(taskTestCaseService.queryUnRelApiCaseByPage(pagination));
    }

    @ApiOperation("分页查询未关联的测试用例（人工）")
    @PostMapping(value = "/queryUnRelTestCaseByPage", produces = {"application/json;charset=UTF-8"})
    public RestResultVo<Page<TestCase>> queryUnRelTestCaseByPage(@RequestBody Pagination<TestCase> pagination) {
        Page<TestCase> page = taskTestCaseService.queryUnRelTestCaseByPage(pagination);
        return RestResultVo.fromData(page);
    }

    @ApiOperation("获取测试用例详情信息（已关联的）")
    @GetMapping("/getCaseDetailById")
    public RestResultVo getCaseDetailById(@RequestHeader String userName, @RequestParam Long id) {
        TaskTestCase taskTestCase = taskTestCaseService.getCaseDetailById(id);
        return RestResultVo.fromData(taskTestCase);
    }

}
