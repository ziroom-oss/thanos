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
 * ???????????????????????????
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
     * ??????????????????
     *
     * @param userName ?????????
     * @param topicId  ??????id
     * @return
     */
    @Override
    public TestResultVo createDailyTestReport(String userName, Long topicId) {
        //1.??????????????????
        TestTopic testTopic = testTopicService.findInfoById(topicId);
        if (Objects.isNull(testTopic)) {
            throw new CustomException("??????????????????");
        }
        //2.????????????????????????????????????,????????????????????????????????????
        DailyTestReport dailyTestReport = this.findDailyTestReport(userName, topicId);

        //3.?????????????????????????????????????????????
        List<TopicTaskRel> topicTaskRel = topicTaskRelService.findByTopicIds(Arrays.asList(topicId));
        if (CollectionUtils.isEmpty(topicTaskRel)) {
            throw new CustomException("?????????????????????????????????????????????");
        }
        //3.1 ?????????????????????????????????
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
        //4.???????????????????????????????????????????????????????????????????????????????????????
        //4.1 ??????????????????id
        Long dailyId = Objects.isNull(dailyTestReport.getId()) ? IdGenUtil.nextId() : dailyTestReport.getId();
        //4.2 ?????????????????????????????????
        List<DailyTestReportType> reportTypeList = new ArrayList<>();
        taskList.forEach(taskInfo -> {
            DailyTestReportType reportType = dailyTestReportTypeService.assableReportInfoByTask(taskInfo, dailyId, topicId, userName);
            reportTypeList.add(reportType);
        });
        //5.?????????????????????
        TestResultVo resultVo;
        dailyTestReport.setId(dailyId);
        boolean saveFlag = super.saveOrUpdate(dailyTestReport);
        if (saveFlag) {
            resultVo = dailyTestReportTypeService.batchSaveAndDelReportInfo(dailyId, reportTypeList);
        } else {
            resultVo = TestResultVo.builder().flag(false).msgRes("??????????????????????????????").build();
        }
        resultVo.setData(dailyId);
        return resultVo;
    }

    /**
     * ????????????????????????
     *
     * @param pagination ????????????
     * @return
     */
    @Override
    public Page<DailyTestReport> queryDailyTestReportPage(Pagination<DailyTestReport> pagination) {
        //1. ?????????????????????
        QueryWrapper<DailyTestReport> queryWrapper = this.getInfoQueryWrapper(pagination.getSearchObj());
        //2. ????????????
        Page<DailyTestReport> page = this.page(pagination.getPage(), queryWrapper);
        if (Objects.nonNull(page) && CollectionUtils.isNotEmpty(page.getRecords())) {
            page.getRecords().forEach(deilyTest -> {
                try {
                    deilyTest.setDetailVo(this.findDetailById(deilyTest.getId()));
                } catch (Exception e) {
                    log.error("?????????????????????????????????", e);
                }
            });
        }
        return page;
    }

    /**
     * ????????????????????????
     *
     * @param id
     * @return
     */
    private DailyReportVo findDetailById(Long id) {
        DailyReportVo reportVo = new DailyReportVo();
        //1.????????????
        DailyTestReport dailyTestReport = super.getById(id);
        if (Objects.isNull(dailyTestReport)) {
            throw new CustomException("????????????????????????");
        }
        //2.????????????
        TestTopic testTopic = testTopicService.findInfoById(dailyTestReport.getTopicId());
        if (Objects.isNull(testTopic)) {
            throw new CustomException("????????????????????????");
        }
        //3.??????????????????
        List<DailyTestReportType> dailyTestReportTypeList = dailyTestReportTypeService.getListByDailyId(dailyTestReport.getId());
        if (CollectionUtils.isEmpty(dailyTestReportTypeList)) {
            throw new CustomException("??????????????????????????????");
        }
        reportVo.setJiraFlag(false);
        //4.??????????????????????????????
        this.culReportInfo(reportVo, dailyTestReportTypeList);

        return reportVo;
    }

    /**
     * ????????????????????????
     *
     * @param id ??????id
     * @return
     */
    @Override
    public DailyReportVo findDailyReportInfo(Long id) {
        DailyReportVo reportVo = new DailyReportVo();
        //1.????????????
        DailyTestReport dailyTestReport = super.getById(id);
        if (Objects.isNull(dailyTestReport)) {
            throw new CustomException("????????????????????????");
        }
        //2.????????????
        TestTopic testTopic = testTopicService.findInfoById(dailyTestReport.getTopicId());
        if (Objects.isNull(testTopic)) {
            throw new CustomException("????????????????????????");
        }
        //3.??????????????????
        List<DailyTestReportType> dailyTestReportTypeList = dailyTestReportTypeService.getListByDailyId(dailyTestReport.getId());
        if (CollectionUtils.isEmpty(dailyTestReportTypeList)) {
            throw new CustomException("??????????????????????????????");
        }
        reportVo.setDailyTestTaskList(dailyTestReportTypeList);
        //4.??????????????????????????????
        this.culReportInfo(reportVo, dailyTestReportTypeList);
        reportVo.setTestTopic(testTopic);
        reportVo.setDailyReportName(dailyTestReport.getDailyDate());
        reportVo.setDailyRiskNames(dailyTestReport.getDailyRiskNames());
        reportVo.getDailyReportSum().setDailyId(id);
        //5.??????????????????
        this.culChartInfo(reportVo);
        return reportVo;
    }


    /**
     * ????????????id??????????????????
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
     * ????????????id????????????????????????
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
     * ????????????????????????????????????????????????
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
     * ??????????????????????????????????????????
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
     * ????????????
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
     * ????????????
     *
     * @param userName
     * @param dailyId
     * @return
     */
    @Override
    public TestResultVo emailPreview(String userName, Long dailyId) {
        DailyTestReport dailyTestReport = super.getById(dailyId);
        String emailContent = sendMailService.getEmailContent(this.getEmailInfo(userName, dailyTestReport));
        return TestResultVo.builder().flag(true).msgRes(TestCenterConstants.RES_MSG_SUCCESS).data(emailContent).build();
    }


    /**
     * ?????????????????????????????????
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
            return TestResultVo.builder().flag(false).msgRes("????????????????????????????????????????????????").build();
        }
    }

    /**
     * ??????????????????
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
     * ?????????????????????
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
     * ??????????????????????????????
     *
     * @param reportVo
     * @param dailyTestReportTypeList
     */
    private void culReportInfo(DailyReportVo reportVo, List<DailyTestReportType> dailyTestReportTypeList) {
        //????????????????????????????????????
        int testcaseCount = 0;
        int runSuccessCount = 0;
        int runFailCount = 0;
        int runSkipCount = 0;
        int notRunCount = 0;
        double testcaseRate = 0;
        //????????????bug?????????????????????
        int bugCount = 0;
        int bugSuccessCount = 0;
        int bugUnresolvedCount = 0;
        double bugRate = 0;
        //??????????????????????????????????????????
        int dailyCount = 0;
        int dailySuccessCount = 0;
        int dailyFailCount = 0;
        int dailySkipCount = 0;
        DecimalFormat df = new DecimalFormat("0.00");
        df.setRoundingMode(RoundingMode.HALF_UP);
        List<TestReportBugListVo> newBugList = new ArrayList<>();
        for (DailyTestReportType reportType : dailyTestReportTypeList) {
            //????????????????????????????????????
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
            //????????????bug?????????????????????
            int thisBugCount = Objects.isNull(reportType.getBugCount()) ? 0 : reportType.getBugCount();
            bugCount += thisBugCount;
            int thisBugSuccessCount = Objects.isNull(reportType.getBugSuccessCount()) ? 0 : reportType.getBugSuccessCount();
            bugSuccessCount += thisBugSuccessCount;
            int thisBugUnresolvedCount = Objects.isNull(reportType.getBugUnresolvedCount()) ? 0 : reportType.getBugUnresolvedCount();
            bugUnresolvedCount += thisBugUnresolvedCount;
            //??????????????????????????????????????????
            int thisDailyCount = Objects.isNull(reportType.getDailyCount()) ? 0 : reportType.getDailyCount();
            dailyCount += thisDailyCount;
            int thisDailySuccessCount = Objects.isNull(reportType.getDailySuccessCount()) ? 0 : reportType.getDailySuccessCount();
            dailySuccessCount += thisDailySuccessCount;
            int thisDailyFailCount = Objects.isNull(reportType.getDailyFailCount()) ? 0 : reportType.getDailyFailCount();
            dailyFailCount += thisDailyFailCount;
            int thisDailySkipCount = Objects.isNull(reportType.getDailySkipCount()) ? 0 : reportType.getDailySkipCount();
            dailySkipCount += thisDailySkipCount;
            // ??????bug????????????
            if (Objects.nonNull(reportType.getTestTaskId())) {
                List<DailyTestExeReport> bugList = dailyTestExeReportService.getBugListByTaskId(reportType.getTestTaskId());
                if (CollectionUtils.isNotEmpty(bugList) && reportVo.isJiraFlag()) {
                    bugList.forEach(bug -> {
                        TestReportBugListVo bugListVo = new TestReportBugListVo();
                        bugListVo.setBugStatusId(bug.getBugStatusId());
                        bugListVo.setPriorityId(bug.getBugLevelId());
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
                // TODO lianghao by ??????????????? ???????????????????????????????????????+??????+?????????/????????????
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
            // ?????????????????????????????????
            Integer taskType = testTaskService.getById(reportType.getTestTaskId()).getTestExecutionType();
            if (Objects.nonNull(taskType)) {
                reportType.setTestTaskType(taskType + "");
            }
        }
        DailyTestReportType dailyReportSum = new DailyTestReportType();
        //????????????????????????????????????
        dailyReportSum.setTestcaseCount(testcaseCount);
        dailyReportSum.setRunSuccessCount(runSuccessCount);
        dailyReportSum.setRunFailCount(runFailCount);
        dailyReportSum.setRunSkipCount(runSkipCount);
        dailyReportSum.setNotRunCount(notRunCount);
        //????????????????????????
        if (testcaseCount > 0) {
            BigDecimal tcr = BigDecimal.valueOf(testcaseCount - notRunCount)
                    .divide(BigDecimal.valueOf(testcaseCount), 4, RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(100));

            dailyReportSum.setTestcaseRate(tcr.doubleValue());
            dailyReportSum.setTestcaseRateStr(dailyReportSum.getTestcaseRate() + "%");

            // TODO lianghao by ??????????????? ???????????????????????????????????????+??????+?????????/????????????
            BigDecimal drr = BigDecimal.valueOf(dailySuccessCount + dailyFailCount + dailySkipCount)
                    .divide(BigDecimal.valueOf(testcaseCount), 4, RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(100));
            dailyReportSum.setDailyRate(drr.doubleValue());
            dailyReportSum.setDailyRateStr(dailyReportSum.getDailyRate() + "%");
        }

        //????????????bug?????????????????????
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
        //??????????????????????????????????????????
        dailyReportSum.setDailyCount(dailyCount);
        dailyReportSum.setDailySuccessCount(dailySuccessCount);
        dailyReportSum.setDailyFailCount(dailyFailCount);
        dailyReportSum.setDailySkipCount(dailySkipCount);

        //??????bug????????????
        if (CollectionUtils.isNotEmpty(newBugList)) {
            // ???????????????bug??????
            List<TestReportBugListVo> unBugList = newBugList.stream().distinct().filter(bug -> bug.getBugStatusId() < 5).collect(Collectors.toList());
            dailyReportSum.setUnBugInfoList(unBugList);

            List<TestReportBugListVo> bugList = newBugList.stream().filter(MyUtil.distinctByKey(TestReportBugListVo::getIssueKey)).collect(Collectors.toList());
            dailyReportSum.setBugInfoList(bugList);
        }
        reportVo.setDailyReportSum(dailyReportSum);
    }


    /**
     * ?????????????????????
     *
     * @param reportVo
     */
    private void culChartInfo(DailyReportVo reportVo) {
        if (Objects.isNull(reportVo)) {
            return;
        }
        Map<String, List<DailyChartVo>> reportMap = new HashMap<>();
        //1.???????????????
        List<DailyChartVo> totalList = new ArrayList<>();
        DailyChartVo dailyChartVo = new DailyChartVo();
        dailyChartVo.setChartKey("???????????????");
        dailyChartVo.setChartValue(reportVo.getDailyReportSum().getRunSuccessCount());
        totalList.add(dailyChartVo);
        dailyChartVo = new DailyChartVo();
        dailyChartVo.setChartKey("???????????????");
        dailyChartVo.setChartValue(reportVo.getDailyReportSum().getRunFailCount());
        totalList.add(dailyChartVo);
        dailyChartVo = new DailyChartVo();
        dailyChartVo.setChartKey("???????????????");
        dailyChartVo.setChartValue(reportVo.getDailyReportSum().getRunSkipCount());
        totalList.add(dailyChartVo);
        dailyChartVo = new DailyChartVo();
        dailyChartVo.setChartKey("???????????????");
        dailyChartVo.setChartValue(reportVo.getDailyReportSum().getNotRunCount());
        totalList.add(dailyChartVo);
        reportMap.put(DailyChartEnum.total.getChartKey(), totalList);

        reportVo.setChartMap(reportMap);
    }

    /**
     * ????????????email?????????
     *
     * @param userName
     * @param dailyTestReport
     * @return
     */
    private SendEmailDTO getEmailInfo(String userName, DailyTestReport dailyTestReport) {
        if (Objects.isNull(dailyTestReport)) {
            throw new CustomException(TestCenterConstants.RES_MSG_PARAMS_EMPTY);
        }
        if (StringUtils.isBlank(dailyTestReport.getAddressee())
                || StringUtils.isBlank(dailyTestReport.getAddresser())
                || StringUtils.isBlank(dailyTestReport.getEmailRemark())) {
            throw new CustomException("?????????????????????????????????????????????????????????");
        }
        DailyReportVo dailyReportVo = this.findDailyReportInfo(dailyTestReport.getId());
        if (Objects.isNull(dailyReportVo)) {
            throw new CustomException("????????????????????????????????????");
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
