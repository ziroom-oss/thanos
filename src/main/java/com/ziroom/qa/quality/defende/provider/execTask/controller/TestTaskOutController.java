package com.ziroom.qa.quality.defende.provider.execTask.controller;

import com.ziroom.qa.quality.defende.provider.execTask.service.TestTaskOutService;
import com.ziroom.qa.quality.defende.provider.result.RestResultVo;
import com.ziroom.qa.quality.defende.provider.util.JiraUtils;
import com.ziroom.qa.quality.defende.provider.vo.outvo.OutReqVo;
import com.ziroom.qa.quality.defende.provider.vo.outvo.OutResVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import net.rcarz.jiraclient.Component;
import net.rcarz.jiraclient.Issue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.List;
import java.util.Objects;

@Api(value = "测试执行(任务)对外api", tags = {"测试执行(任务)对外api"})
@Slf4j
@RestController
@RequestMapping("/outTestTask")
public class TestTaskOutController {

    @Autowired
    private TestTaskOutService testTaskOutService;

    /**
     * 如果是新增，则需要企业微信通知相应的负责人
     * 需要返回测试执行的地址，需要让负责人去关联相应的testcase
     *
     * @param outReqVo
     * @return
     */
    @ApiOperation("保存测试执行任务")
    @PostMapping("/outCreateOrGetTestTask")
    public RestResultVo outCreateOrGetTestTask(@RequestBody OutReqVo outReqVo) {
        OutResVo outResVo = testTaskOutService.outCreateOrGetTestTask(outReqVo);
        return RestResultVo.fromData(outResVo);
    }

    @ApiOperation("获取测试执行报告以及bug信息")
    @PostMapping("/getTestTaskDetailInfo")
    public RestResultVo getTestTaskDetailInfo(@RequestBody OutReqVo outReqVo) {
        OutResVo outResVo = testTaskOutService.getTestTaskDetailInfo(outReqVo);
        return RestResultVo.fromData(outResVo);
    }

    @ApiOperation("测试执行任务提测")
    @PostMapping("/submitTestTaskByInfo")
    public RestResultVo submitTestTask(@RequestBody OutReqVo outReqVo) {
        testTaskOutService.submitTestTaskByInfo(outReqVo);
        return RestResultVo.fromSuccess("成功");
    }

    @ApiOperation("测试执行任务上线")
    @PostMapping("/launchTestTaskByInfo")
    public RestResultVo launchTestTask(@RequestBody OutReqVo outReqVo) {
        testTaskOutService.launchTestTaskByInfo(outReqVo);
        return RestResultVo.fromSuccess("成功");
    }

    @ApiOperation("创建自动的测试执行（zhujj 需要调用的接口）")
    @PostMapping("/createAutoTestExecution")
    public RestResultVo createAutoTestExecution(@RequestBody OutReqVo outReqVo) {
        OutResVo outResVo = testTaskOutService.createAutoTestExecution(outReqVo);
        return RestResultVo.fromData(outResVo);
    }

    @ApiOperation("发送自动化测试使用情况报表（企业微信）")
    @GetMapping("/sendAutoTaskMsg")
    public RestResultVo sendAutoTaskMsg(@RequestParam(required = false) String dateTimeStr) {
        testTaskOutService.sendAutoTaskMsg(dateTimeStr);
        return RestResultVo.fromData("成功");
    }

    @ApiOperation("JIRA的hook回调创建测试执行（监听产品需求，技术需求，小需状态变更为测试中）")
    @PostMapping("/jiraCreateTestExe")
    public RestResultVo jiraCreateTestExe(HttpServletRequest request) throws Exception {
        BufferedReader reader = null;
        try {
            reader = request.getReader();
            StringBuffer result = new StringBuffer();
            String line = "";
            while ((line = reader.readLine()) != null) {
                result.append(line);
            }
            log.info("jiraCreateTestExe监听jira需求 :{}", result);
            testTaskOutService.jiraCreateTestExe(result.toString());
        } catch (IOException e) {
            log.error("jiraCreateTestExe监听jira需求error", e);
        } finally {
            if (Objects.nonNull(reader)) {
                reader.close();
            }
        }
        return RestResultVo.fromData("成功");
    }

    @GetMapping("/mytest")
    public RestResultVo mytest(@RequestParam String jiraId) {
//        /outTestTask/mytest
        Issue issue = JiraUtils.getJiraIssueByIssueKey(jiraId);
        List<String> list = JiraUtils.getComponentsAllowedValues(issue.getProject().getKey(), issue.getIssueType().getName());
        return RestResultVo.fromData(list);
    }


}
