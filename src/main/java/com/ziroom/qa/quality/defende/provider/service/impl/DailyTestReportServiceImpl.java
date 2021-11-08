package com.ziroom.qa.quality.defende.provider.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ziroom.qa.quality.defende.provider.constant.TestCenterConstants;
import com.ziroom.qa.quality.defende.provider.constant.enums.BugLevelEnum;
import com.ziroom.qa.quality.defende.provider.constant.enums.BugStatusEnum;
import com.ziroom.qa.quality.defende.provider.constant.enums.DailyChartEnum;
import com.ziroom.qa.quality.defende.provider.entity.*;
import com.ziroom.qa.quality.defende.provider.execTask.entity.TestTask;
import com.ziroom.qa.quality.defende.provider.execTask.service.TestTaskService;
import com.ziroom.qa.quality.defende.provider.mapper.DailyTestReportMapper;
import com.ziroom.qa.quality.defende.provider.outinterface.client.service.EhrService;
import com.ziroom.qa.quality.defende.provider.result.CustomException;
import com.ziroom.qa.quality.defende.provider.service.*;
import com.ziroom.qa.quality.defende.provider.util.JiraUtils;
import com.ziroom.qa.quality.defende.provider.util.MyUtil;
import com.ziroom.qa.quality.defende.provider.util.idgen.IdGenUtil;
import com.ziroom.qa.quality.defende.provider.vo.EhrUserDetail;
import com.ziroom.qa.quality.defende.provider.vo.Pagination;
import com.ziroom.qa.quality.defende.provider.vo.TestReportBugListVo;
import com.ziroom.qa.quality.defende.provider.vo.TestResultVo;
import com.ziroom.qa.quality.defende.provider.vo.dailyreport.DailyChartVo;
import com.ziroom.qa.quality.defende.provider.vo.dailyreport.DailyReportVo;
import com.ziroom.qa.quality.defende.provider.vo.email.SendEmailDTO;
import lombok.extern.slf4j.Slf4j;
import net.rcarz.jiraclient.Issue;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 * 项目日报服务实现类
 * </p>
 *
 * @author liangh4
 * @since 2021-05-26
 */
@Service
@Slf4j
public class DailyTestReportServiceImpl extends ServiceImpl<DailyTestReportMapper, DailyTestReport> implements DailyTestReportService {

    @Autowired
    private TestTopicService testTopicService;

    @Autowired
    private DailyTestReportTypeService dailyTestReportTypeService;

    @Autowired
    private TopicTaskRelService topicTaskRelService;

    @Autowired
    private TestTaskService testTaskService;

    @Autowired
    private TopicRiskRelService topicRiskRelService;

    @Autowired
    private DailyTestExeReportService dailyTestExeReportService;

    @Autowired
    private EhrService ehrService;

    @Autowired
    private SendMailService sendMailService;

    /**
     * 创建日报信息
     *
     * @param userName 创建人
     * @param topicId  项目id
     * @return
     */
    @Override
    public TestResultVo createDailyTestReport(String userName, Long topicId) {
        //1.获取项目信息
        TestTopic testTopic = testTopicService.findInfoById(topicId);
        if (Objects.isNull(testTopic)) {
            throw new CustomException("该项目为空！");
        }
        //2.校验当前日报是否已被创建,创建则修改，未创建则新增
        DailyTestReport dailyTestReport = this.findDailyTestReport(userName, topicId);

        //3.查询项目关联的测试执行任务信息
        List<TopicTaskRel> topicTaskRel = topicTaskRelService.findByTopicIds(Arrays.asList(topicId));
        if (CollectionUtils.isEmpty(topicTaskRel)) {
            throw new CustomException("该项目没有对应的测试执行任务！");
        }
        //3.1 给项目日报增加风险标识
        List<TopicRiskRel> topicRiskRelList = topicRiskRelService.queryRelList(topicId);
        if (CollectionUtils.isNotEmpty(topicRiskRelList)) {
            List<String> riskNames = topicRiskRelList.stream().map(TopicRiskRel::getTestRiskName).collect(Collectors.toList());
            StringBuilder sb = new StringBuilder();
            String resultString = "";
            for (int i = 0; i < riskNames.size(); i++) {
                if (i < riskNames.size() - 1) {
                    sb.append(riskNames.get(i));
                    sb.append(",");
                } else {
                    sb.append(riskNames.get(i));
                }
            }
            resultString = sb.toString();
            dailyTestReport.setDailyRiskNames(resultString);
        }
        List<Long> taskIdList = topicTaskRel.stream().map(TopicTaskRel::getTaskId).collect(Collectors.toList());
        List<TestTask> taskList = testTaskService.listByIds(taskIdList);
        //4.查询测试执行任务对应的执行结果和测试用例信息，组装统计数据
        //4.1 获取日报信息id
        Long dailyId = Objects.isNull(dailyTestReport.getId()) ? IdGenUtil.nextId() : dailyTestReport.getId();
        //4.2 计算并组装日报明细信息
        List<DailyTestReportType> reportTypeList = new ArrayList<>();
        taskList.forEach(taskInfo -> {
            DailyTestReportType reportType = dailyTestReportTypeService.assableReportInfoByTask(taskInfo, dailyId, topicId, userName);
            reportTypeList.add(reportType);
        });
        //5.去保存日报信息
        TestResultVo resultVo;
        dailyTestReport.setId(dailyId);
        boolean saveFlag = super.saveOrUpdate(dailyTestReport);
        if (saveFlag) {
            resultVo = dailyTestReportTypeService.batchSaveAndDelReportInfo(dailyId, reportTypeList);
        } else {
            resultVo = TestResultVo.builder().flag(false).msgRes("日志主信息保存失败！").build();
        }
        resultVo.setData(dailyId);
        return resultVo;
    }

    /**
     * 查询日报分页信息
     *
     * @param pagination 分页信息
     * @return
     */
    @Override
    public Page<DailyTestReport> queryDailyTestReportPage(Pagination<DailyTestReport> pagination) {
        //1. 格式化查询参数
        QueryWrapper<DailyTestReport> queryWrapper = this.getInfoQueryWrapper(pagination.getSearchObj());
        //2. 分页查询
        Page<DailyTestReport> page = this.page(pagination.getPage(), queryWrapper);
        if (Objects.nonNull(page) && CollectionUtils.isNotEmpty(page.getRecords())) {
            page.getRecords().forEach(deilyTest -> {
                try {
                    deilyTest.setDetailVo(this.findDetailById(deilyTest.getId()));
                } catch (Exception e) {
                    log.error("列表获取日报明细失败！", e);
                }
            });
        }
        return page;
    }

    /**
     * 给历史列表的查询
     *
     * @param id
     * @return
     */
    private DailyReportVo findDetailById(Long id) {
        DailyReportVo reportVo = new DailyReportVo();
        //1.日报信息
        DailyTestReport dailyTestReport = super.getById(id);
        if (Objects.isNull(dailyTestReport)) {
            throw new CustomException("日报信息不存在！");
        }
        //2.项目信息
        TestTopic testTopic = testTopicService.findInfoById(dailyTestReport.getTopicId());
        if (Objects.isNull(testTopic)) {
            throw new CustomException("项目信息不存在！");
        }
        //3.日报详情信息
        List<DailyTestReportType> dailyTestReportTypeList = dailyTestReportTypeService.getListByDailyId(dailyTestReport.getId());
        if (CollectionUtils.isEmpty(dailyTestReportTypeList)) {
            throw new CustomException("日报详情信息不存在！");
        }
        reportVo.setJiraFlag(false);
        //4.获取日报详情计算比例
        this.culReportInfo(reportVo, dailyTestReportTypeList);

        return reportVo;
    }

    /**
     * 显示日报详情信息
     *
     * @param id 日报id
     * @return
     */
    @Override
    public DailyReportVo findDailyReportInfo(Long id) {
        DailyReportVo reportVo = new DailyReportVo();
        //1.日报信息
        DailyTestReport dailyTestReport = super.getById(id);
        if (Objects.isNull(dailyTestReport)) {
            throw new CustomException("日报信息不存在！");
        }
        //2.项目信息
        TestTopic testTopic = testTopicService.findInfoById(dailyTestReport.getTopicId());
        if (Objects.isNull(testTopic)) {
            throw new CustomException("项目信息不存在！");
        }
        //3.日报详情信息
        List<DailyTestReportType> dailyTestReportTypeList = dailyTestReportTypeService.getListByDailyId(dailyTestReport.getId());
        if (CollectionUtils.isEmpty(dailyTestReportTypeList)) {
            throw new CustomException("日报详情信息不存在！");
        }
        reportVo.setDailyTestTaskList(dailyTestReportTypeList);
        //4.获取日报详情计算比例
        this.culReportInfo(reportVo, dailyTestReportTypeList);
        reportVo.setTestTopic(testTopic);
        reportVo.setDailyReportName(dailyTestReport.getDailyDate());
        reportVo.setDailyRiskNames(dailyTestReport.getDailyRiskNames());
        reportVo.getDailyReportSum().setDailyId(id);
        //5.统计图表信息
        this.culChartInfo(reportVo);
        return reportVo;
    }


    /**
     * 根据项目id获取日报列表
     *
     * @param topicId
     * @return
     */
    @Override
    public List<DailyTestReport> getListByTopicId(Long topicId) {
        QueryWrapper<DailyTestReport> infoQueryWrapper = new QueryWrapper<>();
        infoQueryWrapper.eq("topic_id", topicId);
        infoQueryWrapper.orderByDesc("id");
        return super.list(infoQueryWrapper);
    }

    /**
     * 根据项目id集合删除日报信息
     *
     * @param topicIds
     * @return
     */
    @Override
    public TestResultVo delByTopicIds(List<Long> topicIds) {
        QueryWrapper<DailyTestReport> infoQueryWrapper = new QueryWrapper<>();
        infoQueryWrapper.in("topic_id", topicIds);
        List<DailyTestReport> list = super.list(infoQueryWrapper);
        if (CollectionUtils.isEmpty(list)) {
            return TestResultVo.builder().flag(true).msgRes(TestCenterConstants.RES_MSG_APPROVED_EMPTY).build();
        }
        List<Long> reportIds = list.stream().map(DailyTestReport::getId).collect(Collectors.toList());
        super.removeByIds(reportIds);

        QueryWrapper<DailyTestReportType> typeQueryWrapper = new QueryWrapper<>();
        typeQueryWrapper.in("daily_id", reportIds);
        dailyTestReportTypeService.remove(typeQueryWrapper);
        return TestResultVo.builder().flag(true).msgRes(TestCenterConstants.RES_MSG_SUCCESS).build();
    }

    /**
     * 获取日报邮件信息，如果没有则创建
     *
     * @param userName
     * @param topicId
     * @param id
     * @return
     */
    @Override
    public DailyTestReport findDailyTestReportEmail(String userName, Long topicId, Long id) {
        if (Objects.isNull(topicId) || Objects.isNull(id)) {
            return null;
        }
        DailyTestReport report = super.getById(id);
        if (Objects.isNull(report)) {
            return null;
        }
        if (StringUtils.isBlank(report.getAddressee()) || StringUtils.isBlank(report.getAddresser())) {
            QueryWrapper<DailyTestReport> infoQueryWrapper = new QueryWrapper<>();
            infoQueryWrapper.eq("topic_id", topicId);
            infoQueryWrapper.notIn("id", id);
            infoQueryWrapper.orderByDesc("id");
            List<DailyTestReport> dailyTestReportList = super.list(infoQueryWrapper);
            if (CollectionUtils.isNotEmpty(dailyTestReportList)) {
                for (DailyTestReport old : dailyTestReportList) {
                    if (StringUtils.isNotBlank(old.getAddressee()) && StringUtils.isNotBlank(old.getAddresser())) {
                        report.setAddressee(old.getAddressee());
                        report.setAddresser(old.getAddresser());
                        report.setCc(old.getCc());
                        report.setTestVersion(old.getTestVersion());
                        report.setTestStage(old.getTestStage());
                        report.setCcGroup(old.getCcGroup());
                        report.setSendGroup(old.getSendGroup());
                        report.setEmailRemark(old.getEmailRemark());
                        report.setBugUrl(old.getBugUrl());
                        super.updateById(report);
                        break;
                    }
                }
            }
        }
        return report;
    }

    /**
     * 创建日报邮件信息，没有则创建
     *
     * @param report
     * @return
     */
    @Override
    public TestResultVo saveDailyTestReportEmail(DailyTestReport report) {
        if (Objects.nonNull(report.getId())) {
            boolean flag = super.updateById(report);
            return TestResultVo.builder().flag(flag).msgRes(flag ? TestCenterConstants.RES_MSG_SUCCESS : TestCenterConstants.RES_MSG_ERROR).build();
        } else {
            return TestResultVo.builder().flag(false).msgRes(TestCenterConstants.RES_MSG_APPROVED_EMPTY).build();
        }
    }

    /**
     * 发送邮件
     *
     * @param userName
     * @param dailyId
     * @return
     */
    @Override
    public TestResultVo sendDailyTestReportEmail(String userName, Long dailyId) throws Exception {
        DailyTestReport dailyTestReport = super.getById(dailyId);
        sendMailService.sendDailyReportMail(this.getEmailInfo(userName, dailyTestReport));
        dailyTestReport.setSendTime(LocalDateTime.now());
        super.updateById(dailyTestReport);
        return TestResultVo.builder().flag(true).msgRes(TestCenterConstants.RES_MSG_SUCCESS).build();
    }

    /**
     * 邮件预览
     *
     * @param userName
     * @param dailyId
     * @return
     */
    @Override
    public TestResultVo emailPreview(String userName, Long dailyId) throws Exception {
        DailyTestReport dailyTestReport = super.getById(dailyId);
        String emailContent = sendMailService.getEmailContent(this.getEmailInfo(userName, dailyTestReport));
        return TestResultVo.builder().flag(true).msgRes(TestCenterConstants.RES_MSG_SUCCESS).data(emailContent).build();
    }


    /**
     * 校验当日有没有创建日报
     *
     * @param userName
     * @param topicId
     * @return
     */
    @Override
    public TestResultVo checkNewDailyReport(String userName, Long topicId) {
        DailyTestReport dailyTestReport = new DailyTestReport();
        dailyTestReport.setTopicId(topicId);
        dailyTestReport.setDailyDate(DateTimeFormatter.ofPattern("yyyyMMdd").format(LocalDateTime.now()));
        List<DailyTestReport> list = super.list(getInfoQueryWrapper(dailyTestReport));
        if (CollectionUtils.isNotEmpty(list)) {
            return TestResultVo.builder().flag(true).msgRes(TestCenterConstants.RES_MSG_SUCCESS).build();
        } else {
            return TestResultVo.builder().flag(false).msgRes("今天还没有创建日报，请先去创建！").build();
        }
    }

    /**
     * 组织查询条件
     *
     * @param dailyTestReport
     * @return
     */
    private QueryWrapper<DailyTestReport> getInfoQueryWrapper(DailyTestReport dailyTestReport) {
        QueryWrapper<DailyTestReport> infoQueryWrapper = new QueryWrapper<>();
        infoQueryWrapper
                .eq(Objects.nonNull(dailyTestReport.getTopicId()), "topic_id", dailyTestReport.getTopicId())
                .eq(StringUtils.isNotBlank(dailyTestReport.getDailyDate()), "daily_date", dailyTestReport.getDailyDate())
        ;
        infoQueryWrapper.orderByDesc("id");
        return infoQueryWrapper;
    }

    /**
     * 获取日报主信息
     *
     * @param userName
     * @param topicId
     * @return
     */
    private DailyTestReport findDailyTestReport(String userName, Long topicId) {
        String nowDateStr = DateTimeFormatter.ofPattern("yyyyMMdd").format(LocalDateTime.now());
        DailyTestReport reportQuery = DailyTestReport.builder().topicId(topicId).dailyDate(nowDateStr).build();
        DailyTestReport dailyTestReport = super.getOne(getInfoQueryWrapper(reportQuery));
        if (Objects.isNull(dailyTestReport)) {
            dailyTestReport = reportQuery;
        }
        dailyTestReport.setUpdateUser(userName);
        dailyTestReport.setUpdateTime(LocalDateTime.now());
        if (StringUtils.isBlank(dailyTestReport.getCreateUser())) {
            dailyTestReport.setCreateUser(userName);
            dailyTestReport.setCreateTime(LocalDateTime.now());
        }
        return dailyTestReport;
    }

    /**
     * 计算日报详情信息汇总
     *
     * @param reportVo
     * @param dailyTestReportTypeList
     */
    private void culReportInfo(DailyReportVo reportVo, List<DailyTestReportType> dailyTestReportTypeList) {
        //测试用例执行结果信息汇总
        int testcaseCount = 0;
        int runSuccessCount = 0;
        int runFailCount = 0;
        int runSkipCount = 0;
        int notRunCount = 0;
        double testcaseRate = 0;
        //测试用例bug数量总信息汇总
        int bugCount = 0;
        int bugSuccessCount = 0;
        int bugUnresolvedCount = 0;
        double bugRate = 0;
        //当日测试用例执行结果信息汇总
        int dailyCount = 0;
        int dailySuccessCount = 0;
        int dailyFailCount = 0;
        int dailySkipCount = 0;
        DecimalFormat df = new DecimalFormat("0.00");
        df.setRoundingMode(RoundingMode.HALF_UP);
        List<TestReportBugListVo> newBugList = new ArrayList<>();
        for (DailyTestReportType reportType : dailyTestReportTypeList) {
            //测试用例执行结果信息汇总
            int thisTestcaseCount = Objects.isNull(reportType.getTestcaseCount()) ? 0 : reportType.getTestcaseCount();
            testcaseCount += thisTestcaseCount;
            int thisRunSuccessCount = Objects.isNull(reportType.getRunSuccessCount()) ? 0 : reportType.getRunSuccessCount();
            runSuccessCount += thisRunSuccessCount;
            int thisRunFailCount = Objects.isNull(reportType.getRunFailCount()) ? 0 : reportType.getRunFailCount();
            runFailCount += thisRunFailCount;
            int thisRunSkipCount = Objects.isNull(reportType.getRunSkipCount()) ? 0 : reportType.getRunSkipCount();
            runSkipCount += thisRunSkipCount;
            int thisNotRunCount = Objects.isNull(reportType.getNotRunCount()) ? 0 : reportType.getNotRunCount();
            notRunCount += thisNotRunCount;
            //测试用例bug数量总信息汇总
            int thisBugCount = Objects.isNull(reportType.getBugCount()) ? 0 : reportType.getBugCount();
            bugCount += thisBugCount;
            int thisBugSuccessCount = Objects.isNull(reportType.getBugSuccessCount()) ? 0 : reportType.getBugSuccessCount();
            bugSuccessCount += thisBugSuccessCount;
            int thisBugUnresolvedCount = Objects.isNull(reportType.getBugUnresolvedCount()) ? 0 : reportType.getBugUnresolvedCount();
            bugUnresolvedCount += thisBugUnresolvedCount;
            //当日测试用例执行结果信息汇总
            int thisDailyCount = Objects.isNull(reportType.getDailyCount()) ? 0 : reportType.getDailyCount();
            dailyCount += thisDailyCount;
            int thisDailySuccessCount = Objects.isNull(reportType.getDailySuccessCount()) ? 0 : reportType.getDailySuccessCount();
            dailySuccessCount += thisDailySuccessCount;
            int thisDailyFailCount = Objects.isNull(reportType.getDailyFailCount()) ? 0 : reportType.getDailyFailCount();
            dailyFailCount += thisDailyFailCount;
            int thisDailySkipCount = Objects.isNull(reportType.getDailySkipCount()) ? 0 : reportType.getDailySkipCount();
            dailySkipCount += thisDailySkipCount;
            // 加入bug列表信息
            if (Objects.nonNull(reportType.getTestTaskId())) {
                List<DailyTestExeReport> bugList = dailyTestExeReportService.getBugListByTaskId(reportType.getTestTaskId());
                if (CollectionUtils.isNotEmpty(bugList) && reportVo.isJiraFlag()) {
                    bugList.forEach(bug -> {
                        TestReportBugListVo bugListVo = new TestReportBugListVo();
                        Issue issueBug = null;
                        try {
                            issueBug = JiraUtils.getJiraIssueByIssueKey(bug.getBugId());
                        } catch (Exception e) {
                            log.error("culReportInfo 获取jira信息失败，jiraid == {}", bug.getBugId(), e);
                        }
                        bugListVo.setBugStatusId(Objects.nonNull(issueBug) ? Long.valueOf(issueBug.getStatus().getId()) : bug.getBugStatusId());
                        bugListVo.setPriorityId(Objects.nonNull(issueBug) ? Long.valueOf(issueBug.getPriority().getId()) : bug.getBugLevelId());
                        bugListVo.setIssueKey(bug.getBugId());
                        bugListVo.setPriority(Objects.isNull(bugListVo.getPriorityId()) ? "-" : BugLevelEnum.getLevelValueByKey(bugListVo.getPriorityId()));
                        bugListVo.setBugStatus(Objects.isNull(bugListVo.getBugStatusId()) ? "-" : BugStatusEnum.getStatusValueByKey(bugListVo.getBugStatusId()));
                        newBugList.add(bugListVo);
                    });
                }
            }

            if (thisTestcaseCount > 0) {
                BigDecimal thisTestcaseRate = BigDecimal.valueOf(thisTestcaseCount - thisNotRunCount)
                        .divide(BigDecimal.valueOf(thisTestcaseCount), 4, RoundingMode.HALF_UP)
                        .multiply(BigDecimal.valueOf(100));
                reportType.setTestcaseRate(thisTestcaseRate.doubleValue());
                reportType.setTestcaseRateStr(reportType.getTestcaseRate() + "%");
                // TODO lianghao by 孟祥云提需 今日进度调整为：今日（成功+失败+跳过）/总用例数
                BigDecimal thisDailyRate = BigDecimal.valueOf(thisDailySuccessCount + thisDailyFailCount + thisDailySkipCount)
                        .divide(BigDecimal.valueOf(thisTestcaseCount), 4, RoundingMode.HALF_UP)
                        .multiply(BigDecimal.valueOf(100));
                reportType.setDailyRate(thisDailyRate.doubleValue());
                reportType.setDailyRateStr(reportType.getDailyRate() + "%");
            }
            if (thisBugCount > 0) {
                BigDecimal thisBugRate = BigDecimal.valueOf(thisBugSuccessCount)
                        .divide(BigDecimal.valueOf(thisBugCount), 4, RoundingMode.HALF_UP)
                        .multiply(BigDecimal.valueOf(100));
                reportType.setBugRate(thisBugRate.doubleValue());
                reportType.setBugRateStr(reportType.getBugRate() + "%");

            }
//            if (thisDailyCount > 0) {
//                BigDecimal thisDailyRate = BigDecimal.valueOf(thisDailySuccessCount + thisDailyFailCount + thisDailySkipCount)
//                        .divide(BigDecimal.valueOf(thisDailyCount), 4, RoundingMode.HALF_UP)
//                        .multiply(BigDecimal.valueOf(100));
//                reportType.setDailyRate(thisDailyRate.doubleValue());
//                reportType.setDailyRateStr(reportType.getDailyRate() + "%");
//            }
            // 手动用例和自动用例标识
            Integer taskType = testTaskService.getById(reportType.getTestTaskId()).getTestExecutionType();
            if (Objects.nonNull(taskType)) {
                reportType.setTestTaskType(taskType + "");
            }
        }
        DailyTestReportType dailyReportSum = new DailyTestReportType();
        //测试用例执行结果信息汇总
        dailyReportSum.setTestcaseCount(testcaseCount);
        dailyReportSum.setRunSuccessCount(runSuccessCount);
        dailyReportSum.setRunFailCount(runFailCount);
        dailyReportSum.setRunSkipCount(runSkipCount);
        dailyReportSum.setNotRunCount(notRunCount);
        //测试用例日报比率
        if (testcaseCount > 0) {
            BigDecimal tcr = BigDecimal.valueOf(testcaseCount - notRunCount)
                    .divide(BigDecimal.valueOf(testcaseCount), 4, RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(100));

            dailyReportSum.setTestcaseRate(tcr.doubleValue());
            dailyReportSum.setTestcaseRateStr(dailyReportSum.getTestcaseRate() + "%");

            // TODO lianghao by 孟祥云提需 今日进度调整为：今日（成功+失败+跳过）/总用例数
            BigDecimal drr = BigDecimal.valueOf(dailySuccessCount + dailyFailCount + dailySkipCount)
                    .divide(BigDecimal.valueOf(testcaseCount), 4, RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(100));
            dailyReportSum.setDailyRate(drr.doubleValue());
            dailyReportSum.setDailyRateStr(dailyReportSum.getDailyRate() + "%");
        }

        //测试用例bug数量总信息汇总
        dailyReportSum.setBugCount(bugCount);
        dailyReportSum.setBugSuccessCount(bugSuccessCount);
        dailyReportSum.setBugUnresolvedCount(bugUnresolvedCount);

        if (bugCount > 0) {
            BigDecimal br = BigDecimal.valueOf(bugSuccessCount)
                    .divide(BigDecimal.valueOf(bugCount), 4, RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(100));

            dailyReportSum.setBugRate(br.doubleValue());
            dailyReportSum.setBugRateStr(dailyReportSum.getBugRate() + "%");
        }
        //当日测试用例执行结果信息汇总
        dailyReportSum.setDailyCount(dailyCount);
        dailyReportSum.setDailySuccessCount(dailySuccessCount);
        dailyReportSum.setDailyFailCount(dailyFailCount);
        dailyReportSum.setDailySkipCount(dailySkipCount);

//        if (dailyCount > 0) {
//            BigDecimal drr = BigDecimal.valueOf(dailySuccessCount + dailyFailCount + dailySkipCount)
//                    .divide(BigDecimal.valueOf(dailyCount), 2, RoundingMode.HALF_UP)
//                    .multiply(BigDecimal.valueOf(100));
//            dailyReportSum.setDailyRate(drr.doubleValue());
//            dailyReportSum.setDailyRateStr(dailyReportSum.getDailyRate() + "%");
//        }
        //获取bug信息列表
        if (CollectionUtils.isNotEmpty(newBugList)) {
            // 获取未关闭bug列表
            List<TestReportBugListVo> unBugList = newBugList.stream().distinct().filter(bug -> bug.getBugStatusId() < 5).collect(Collectors.toList());
            dailyReportSum.setUnBugInfoList(unBugList);

            List<TestReportBugListVo> bugList = newBugList.stream().filter(MyUtil.distinctByKey(TestReportBugListVo::getIssueKey)).collect(Collectors.toList());
            dailyReportSum.setBugInfoList(bugList);
        }
        reportVo.setDailyReportSum(dailyReportSum);
    }


    /**
     * 计算图表的信息
     *
     * @param reportVo
     */
    private void culChartInfo(DailyReportVo reportVo) {
        if (Objects.isNull(reportVo)) {
            return;
        }
        Map<String, List<DailyChartVo>> reportMap = new HashMap<>();
        //1.总进度图表
        List<DailyChartVo> totalList = new ArrayList<>();
        DailyChartVo dailyChartVo = new DailyChartVo();
        dailyChartVo.setChartKey("成功用例数");
        dailyChartVo.setChartValue(reportVo.getDailyReportSum().getRunSuccessCount());
        totalList.add(dailyChartVo);
        dailyChartVo = new DailyChartVo();
        dailyChartVo.setChartKey("失败用例数");
        dailyChartVo.setChartValue(reportVo.getDailyReportSum().getRunFailCount());
        totalList.add(dailyChartVo);
        dailyChartVo = new DailyChartVo();
        dailyChartVo.setChartKey("跳过用例数");
        dailyChartVo.setChartValue(reportVo.getDailyReportSum().getRunSkipCount());
        totalList.add(dailyChartVo);
        dailyChartVo = new DailyChartVo();
        dailyChartVo.setChartKey("待测用例数");
        dailyChartVo.setChartValue(reportVo.getDailyReportSum().getNotRunCount());
        totalList.add(dailyChartVo);
        reportMap.put(DailyChartEnum.total.getChartKey(), totalList);

        reportVo.setChartMap(reportMap);
    }

    /**
     * 获取发送email的内容
     *
     * @param userName
     * @param dailyTestReport
     * @return
     */
    private SendEmailDTO getEmailInfo(String userName, DailyTestReport dailyTestReport) throws Exception {
        if (Objects.isNull(dailyTestReport)) {
            throw new CustomException(TestCenterConstants.RES_MSG_PARAMS_EMPTY);
        }
        if (StringUtils.isBlank(dailyTestReport.getAddressee())
                || StringUtils.isBlank(dailyTestReport.getAddresser())
                || StringUtils.isBlank(dailyTestReport.getEmailRemark())) {
            throw new CustomException("邮件发送人、收件人和邮件内容不能为空！");
        }
        DailyReportVo dailyReportVo = this.findDailyReportInfo(dailyTestReport.getId());
        if (Objects.isNull(dailyReportVo)) {
            throw new CustomException("邮件失败，邮件内容为空！");
        }
        TestTopic testTopic = dailyReportVo.getTestTopic();
        if (StringUtils.isNotBlank(testTopic.getTopicMaster())) {
            EhrUserDetail ehrUserDetail = ehrService.getEhrUserDetailByUsername(testTopic.getTopicMaster());
            if (Objects.nonNull(ehrUserDetail)) {
                dailyReportVo.getTestTopic().setTopicMaster(ehrUserDetail.getName());
            }
        }
        if (StringUtils.isNotBlank(testTopic.getTopicParticipant())) {
            String[] participants = testTopic.getTopicParticipant().split(",");
            StringBuilder sb = new StringBuilder();
            if (Objects.nonNull(participants)) {
                for (int i = 0; i < participants.length; i++) {
                    EhrUserDetail ehrUserDetail = ehrService.getEhrUserDetailByUsername(participants[i]);
                    if (Objects.nonNull(ehrUserDetail)) {
                        if (i == participants.length - 1) {
                            sb.append(ehrUserDetail.getName());
                        } else {
                            sb.append(ehrUserDetail.getName() + ",");
                        }
                    }
                }
            }
            dailyReportVo.getTestTopic().setTopicParticipant(sb.toString());
        }
        dailyReportVo.setTaskSize(dailyReportVo.getDailyTestTaskList().size());
        String upTimeStr = DateTimeFormatter.ofPattern("yyyy.MM.dd").format(dailyReportVo.getTestTopic().getUpTime());
        dailyReportVo.getTestTopic().setUpTimeStr(upTimeStr);
        SendEmailDTO sendEmailDTO = new SendEmailDTO();
        sendEmailDTO.setEmailRemark(dailyTestReport.getEmailRemark());
        sendEmailDTO.setAddressee(dailyTestReport.getAddressee());
        sendEmailDTO.setAddresser(dailyTestReport.getAddresser());
        sendEmailDTO.setTestStage(dailyTestReport.getTestStage());
        sendEmailDTO.setTestVersion(dailyTestReport.getTestVersion());
        sendEmailDTO.setCc(dailyTestReport.getCc());
        sendEmailDTO.setBugUrl(dailyTestReport.getBugUrl());
        sendEmailDTO.setSendGroup(dailyTestReport.getSendGroup());
        sendEmailDTO.setCcGroup(dailyTestReport.getCc());
        sendEmailDTO.setDailyReportVo(dailyReportVo);
        return sendEmailDTO;
    }
}
