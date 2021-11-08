package com.ziroom.qa.quality.defende.provider.controller;


import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ziroom.qa.quality.defende.provider.entity.DailyTestReport;
import com.ziroom.qa.quality.defende.provider.service.DailyTestReportService;
import com.ziroom.qa.quality.defende.provider.vo.Pagination;
import com.ziroom.qa.quality.defende.provider.result.RestResultVo;
import com.ziroom.qa.quality.defende.provider.vo.TestResultVo;
import com.ziroom.qa.quality.defende.provider.vo.dailyreport.DailyReportVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * <p>
 * 项目日报前端控制器
 * </p>
 *
 * @author liangh4
 * @since 2021-05-26
 */

@Api(value = "项目日报", tags = {"项目日报"})
@Slf4j
@RestController
@RequestMapping("/dailyTestReport")
public class DailyTestReportController {

    @Autowired
    private DailyTestReportService dailyTestReportService;

    @ApiOperation("分页查询")
    @PostMapping(value = "/queryListByPage", produces = {"application/json;charset=UTF-8"})
    public RestResultVo<Page<DailyTestReport>> queryListByPage(@RequestHeader String userName, @RequestBody Pagination<DailyTestReport> pagination) {
        RestResultVo restResultVo;
        try {
            Page<DailyTestReport> page = dailyTestReportService.queryDailyTestReportPage(pagination);
            restResultVo = RestResultVo.fromData(page);
        } catch (Exception e) {
            log.error("/dailyTestReport/queryListByPage queryVo=={} 分页查询失败!", JSON.toJSONString(pagination), e);
            restResultVo = RestResultVo.fromErrorMessage(e.getMessage());
        }
        return restResultVo;
    }

    @ApiOperation("创建日报")
    @GetMapping(value = "/createDailyTestReport")
    public RestResultVo createDailyTestReport(@RequestHeader String userName, @RequestParam Long topicId) {
        RestResultVo restResultVo;
        try {
            TestResultVo resultVo = dailyTestReportService.createDailyTestReport(userName, topicId);
            if (resultVo.getFlag()) {
                restResultVo = RestResultVo.fromData(resultVo.getData());
            } else {
                restResultVo = RestResultVo.fromErrorMessage(resultVo.getMsgRes());
            }
        } catch (Exception e) {
            log.error("/dailyTestReport/createDailyTestReport topicId=={} 创建日报失败!", topicId, e);
            restResultVo = RestResultVo.fromErrorMessage(e.getMessage());
        }
        return restResultVo;
    }

    @ApiOperation("获取日报详情")
    @GetMapping(value = "/findDailyReportInfo")
    public RestResultVo<DailyReportVo> findDailyReportInfo(@RequestHeader String userName, @RequestParam Long id) {
        RestResultVo restResultVo;
        try {
            DailyReportVo reportVo = dailyTestReportService.findDailyReportInfo(id);
            restResultVo = RestResultVo.fromData(reportVo);
        } catch (Exception e) {
            log.error("/dailyTestReport/findDailyReportInfo id=={} 获取日报详情失败!", id, e);
            restResultVo = RestResultVo.fromErrorMessage(e.getMessage());
        }
        return restResultVo;
    }

    @ApiOperation("创建日报邮件发送信息")
    @PostMapping(value = "/saveDailyTestReportEmail")
    public RestResultVo saveDailyTestReportEmail(@RequestHeader String userName, @RequestBody DailyTestReport report) {
        RestResultVo restResultVo;
        try {
            if (Objects.isNull(report.getId())) {
                report.setCreateUser(userName);
                report.setCreateTime(LocalDateTime.now());
            }
            report.setUpdateUser(userName);
            report.setUpdateTime(LocalDateTime.now());
            TestResultVo resultVo = dailyTestReportService.saveDailyTestReportEmail(report);
            if (resultVo.getFlag()) {
                restResultVo = RestResultVo.fromData(resultVo.getMsgRes());
            } else {
                restResultVo = RestResultVo.fromErrorMessage(resultVo.getMsgRes());
            }
        } catch (Exception e) {
            log.error("/dailyTestReport/saveDailyTestReportEmail report=={} 获取日报详情失败!", JSON.toJSONString(report), e);
            restResultVo = RestResultVo.fromErrorMessage(e.getMessage());
        }
        return restResultVo;
    }

    @ApiOperation("获取邮箱发送信息")
    @GetMapping(value = "/findDailyTestReportEmail")
    public RestResultVo<DailyTestReport> findDailyTestReportEmail(@RequestHeader String userName,
                                                                  @RequestParam Long topicId,
                                                                  @RequestParam Long dailyId) {
        RestResultVo restResultVo;
        try {
            DailyTestReport report = dailyTestReportService.findDailyTestReportEmail(userName, topicId, dailyId);
            restResultVo = RestResultVo.fromData(report);
        } catch (Exception e) {
            log.error("/dailyTestReport/findDailyTestReportEmail topicId=={},dailyId=={} 获取邮箱发送信息失败!", topicId, dailyId, e);
            restResultVo = RestResultVo.fromErrorMessage(e.getMessage());
        }
        return restResultVo;
    }

    @ApiOperation("校验当日有没有创建日报")
    @GetMapping(value = "/checkNewDailyReport")
    public RestResultVo checkNewDailyReport(@RequestHeader String userName, @RequestParam Long topicId) {
        RestResultVo restResultVo;
        try {
            TestResultVo resultVo = dailyTestReportService.checkNewDailyReport(userName, topicId);
            if (resultVo.getFlag()) {
                restResultVo = RestResultVo.fromData(resultVo.getMsgRes());
            } else {
                restResultVo = RestResultVo.fromErrorMessage(resultVo.getMsgRes());
            }
        } catch (Exception e) {
            log.error("/dailyTestReport/checkNewDailyReport topicId=={} 校验当日有没有创建日报失败!", topicId, e);
            restResultVo = RestResultVo.fromErrorMessage(e.getMessage());
        }
        return restResultVo;
    }

    @ApiOperation("发送日报邮件")
    @GetMapping(value = "/sendDailyTestReportEmail")
    public RestResultVo sendDailyTestReportEmail(@RequestHeader String userName,
                                                 @RequestParam Long dailyId) {
        RestResultVo restResultVo;
        try {
            TestResultVo resultVo = dailyTestReportService.sendDailyTestReportEmail(userName, dailyId);
            if (resultVo.getFlag()) {
                restResultVo = RestResultVo.fromData(resultVo.getMsgRes());
            } else {
                restResultVo = RestResultVo.fromErrorMessage(resultVo.getMsgRes());
            }
        } catch (Exception e) {
            log.error("/dailyTestReport/sendDailyTestReportEmail dailyId=={} 邮件发送信息失败!", dailyId, e);
            restResultVo = RestResultVo.fromErrorMessage(e.getMessage());
        }
        return restResultVo;
    }

    @ApiOperation("邮件预览")
    @GetMapping(value = "/emailPreview")
    public RestResultVo emailPreview(@RequestHeader String userName,
                                     @RequestParam Long dailyId) {
        RestResultVo restResultVo;
        try {
            TestResultVo resultVo = dailyTestReportService.emailPreview(userName, dailyId);
            if (resultVo.getFlag()) {
                restResultVo = RestResultVo.fromData(resultVo.getData());
            } else {
                restResultVo = RestResultVo.fromErrorMessage(resultVo.getMsgRes());
            }
        } catch (Exception e) {
            log.error("/dailyTestReport/emailPreview dailyId=={} 邮件发送信息失败!", dailyId, e);
            restResultVo = RestResultVo.fromErrorMessage(e.getMessage());
        }
        return restResultVo;
    }


}

