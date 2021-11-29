package com.ziroom.qa.quality.defende.provider.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ziroom.qa.quality.defende.provider.constant.TestCenterConstants;
import com.ziroom.qa.quality.defende.provider.constant.enums.TestExecutionStatusEnum;
import com.ziroom.qa.quality.defende.provider.entity.DailyTestExeReport;
import com.ziroom.qa.quality.defende.provider.entity.DailyTestReportType;
import com.ziroom.qa.quality.defende.provider.execTask.entity.TestExecution;
import com.ziroom.qa.quality.defende.provider.execTask.entity.TestTask;
import com.ziroom.qa.quality.defende.provider.execTask.service.TestExecutionService;
import com.ziroom.qa.quality.defende.provider.execTask.service.TestTaskService;
import com.ziroom.qa.quality.defende.provider.mapper.DailyTestReportTypeMapper;
import com.ziroom.qa.quality.defende.provider.service.DailyTestExeReportService;
import com.ziroom.qa.quality.defende.provider.service.DailyTestReportTypeService;
import com.ziroom.qa.quality.defende.provider.vo.TestResultVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * <p>
 * 日报类型详情服务实现类
 * </p>
 *
 * @author liangh4
 * @since 2021-05-26
 */
@Service
@Slf4j
public class DailyTestReportTypeServiceImpl extends ServiceImpl<DailyTestReportTypeMapper, DailyTestReportType> implements DailyTestReportTypeService {

    @Autowired
    private TestExecutionService testExecutionService;

    @Autowired
    private TestTaskService testTaskService;

    @Autowired
    private DailyTestExeReportService dailyTestExeReportService;

    /**
     * 组织日报统计数据(根据测试类型)
     *
     * @param testTask
     * @param dailyId
     * @param topicId
     * @param userName
     * @return
     */
    @Override
    public DailyTestReportType assableReportInfoByTask(TestTask testTask, Long dailyId, Long topicId, String userName) {
        DailyTestReportType reportType = new DailyTestReportType();
        reportType.setTestExecutionType(testTask.getTaskType());
        reportType.setTestTaskId(testTask.getId());
        reportType.setTestTaskName(testTask.getTaskName());
        //测试用例执行结果信息汇总
        int testcaseCount = 0;
        int runSuccessCount = 0;
        int runFailCount = 0;
        int runSkipCount = 0;
        int notRunCount = 0;
        //测试用例bug数量总信息汇总
        int bugCount = 0;
        int bugSuccessCount = 0;
        int bugUnresolvedCount = 0;
        //当日测试用例执行结果信息汇总
        int dailyCount = 0;
        int dailySuccessCount = 0;
        int dailyFailCount = 0;
        int dailySkipCount = 0;

        //1.计算测试用例执行结果信息
        testcaseCount = Objects.isNull(testTask.getTestcaseCount()) ? 0 : testTask.getTestcaseCount();
        runSuccessCount = Objects.isNull(testTask.getRunSuccessCount()) ? 0 : testTask.getRunSuccessCount();
        runFailCount = Objects.isNull(testTask.getRunFailCount()) ? 0 : testTask.getRunFailCount();
        runSkipCount = Objects.isNull(testTask.getRunSkipCount()) ? 0 : testTask.getRunSkipCount();
        notRunCount = Objects.isNull(testTask.getNotRunCount()) ? 0 : testTask.getNotRunCount();
        //2.计算测试用例bug数量总信息
        List<TestExecution> exeList = testExecutionService.getBugTestExecutionByTaskId(testTask.getId());
        if (CollectionUtils.isNotEmpty(exeList)) {
            List<DailyTestExeReport> exeReportList = new ArrayList<>();
            for (TestExecution testExecution : exeList) {
                DailyTestExeReport exeReport = new DailyTestExeReport();
                if (Objects.nonNull(testExecution.getRelationBug())) {
//                    Issue bugIssue;
//                    try {
//                        bugIssue = JiraUtils.getJiraIssueByIssueKey(testExecution.getRelationBug());
//                        if (Long.parseLong(bugIssue.getStatus().getId()) > BugStatusEnum.REOPENED.getStatusId()) {
//                            bugSuccessCount++;
//                        } else {
//                            bugUnresolvedCount++;
//                        }
//                    } catch (Exception e) {
//                        log.error("查询bug信息失败，bug编号：{}", testExecution.getRelationBug(), e);
//                    }
                    exeReport.setBugId(testExecution.getRelationBug());
                    exeReport.setBugStatusId(testExecution.getBugStatusId());
                    exeReport.setBugLevelId(testExecution.getBugLevelId());

                    bugCount++;
                }
                exeReport.setDailyId(dailyId);
                exeReport.setTopicId(topicId);
                exeReport.setExeType(testExecution.getExecutionResult());
                exeReport.setCaseId(testExecution.getCaseId());
                exeReport.setCaseKey(testExecution.getCaseKey());
                exeReport.setTaskId(testExecution.getExecutionTaskId());
                exeReport.setCreateUser(userName);
                exeReport.setUpdateUser(userName);
                exeReport.setCreateTime(LocalDateTime.now());
                exeReport.setUpdateTime(LocalDateTime.now());

                exeReportList.add(exeReport);
            }
            //塞入bug列表信息
            if (CollectionUtils.isNotEmpty(exeReportList)) {
                dailyTestExeReportService.saveBatch(exeReportList);
            }
        }

        //3.计算当日测试用例执行结果信息
        String startDateStr = DateTimeFormatter.ofPattern("yyyy-MM-dd").format(LocalDateTime.now());
        String endDateStr = DateTimeFormatter.ofPattern("yyyy-MM-dd").format(LocalDateTime.now().plusDays(1));
        List<TestExecution> exeDailyList = testExecutionService.getTestExecutionByDate(testTask.getId(), startDateStr, endDateStr);
        if (CollectionUtils.isNotEmpty(exeDailyList)) {
            dailyCount += exeDailyList.size();
            for (TestExecution testExecution : exeDailyList) {
                if (TestExecutionStatusEnum.PASS.getDescription().equals(testExecution.getExecutionResult())) {
                    dailySuccessCount++;
                } else if (TestExecutionStatusEnum.FAIL.getDescription().equals(testExecution.getExecutionResult())) {
                    dailyFailCount++;
                } else {
                    dailySkipCount++;
                }
            }
        }

        //组织测试用例执行结果信息
        reportType.setTestcaseCount(testcaseCount);
        reportType.setRunSuccessCount(runSuccessCount);
        reportType.setRunFailCount(runFailCount);
        reportType.setRunSkipCount(runSkipCount);
        reportType.setNotRunCount(notRunCount);
        //组织测试用例bug数量总信息
        reportType.setBugCount(bugCount);
        reportType.setBugSuccessCount(bugSuccessCount);
        reportType.setBugUnresolvedCount(bugUnresolvedCount);
        //组织当日测试用例执行结果信息
        reportType.setDailyCount(dailyCount);
        reportType.setDailySuccessCount(dailySuccessCount);
        reportType.setDailyFailCount(dailyFailCount);
        reportType.setDailySkipCount(dailySkipCount);

        reportType.setDailyId(dailyId);
        reportType.setCreateUser(userName);
        reportType.setUpdateUser(userName);
        reportType.setCreateTime(LocalDateTime.now());
        reportType.setUpdateTime(LocalDateTime.now());

        return reportType;
    }

    /**
     * 批量保存日报统计详情数据并删除旧数据
     *
     * @param dailyId
     * @param reportTypes
     * @return
     */
    @Override
    public TestResultVo batchSaveAndDelReportInfo(Long dailyId, List<DailyTestReportType> reportTypes) {
        if (Objects.isNull(dailyId) || CollectionUtils.isEmpty(reportTypes)) {
            return TestResultVo.builder().flag(false).msgRes(TestCenterConstants.RES_MSG_PARAMS_EMPTY).build();
        }
        QueryWrapper<DailyTestReportType> infoQueryWrapper = new QueryWrapper<>();
        infoQueryWrapper.eq("daily_id", dailyId);
        int count = super.count(infoQueryWrapper);
        boolean flag = true;
        if (count > 0) {
            flag = super.remove(infoQueryWrapper);
        }
        if (flag) {
            flag = super.saveBatch(reportTypes);
        }
        return TestResultVo.builder().flag(flag).msgRes(flag ? TestCenterConstants.RES_MSG_SUCCESS : TestCenterConstants.RES_MSG_ERROR).build();
    }

    /**
     * 根据日报id查询日报详情
     *
     * @param dailyId
     * @return
     */
    @Override
    public List<DailyTestReportType> getListByDailyId(Long dailyId) {
        QueryWrapper<DailyTestReportType> infoQueryWrapper = new QueryWrapper<>();
        infoQueryWrapper.eq("daily_id", dailyId);
        return super.list(infoQueryWrapper);
    }

}
