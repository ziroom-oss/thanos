package com.ziroom.qa.quality.defende.provider.execTask.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ziroom.qa.quality.defende.provider.caseRepository.entity.TestCase;
import com.ziroom.qa.quality.defende.provider.caseRepository.service.TestCaseService;
import com.ziroom.qa.quality.defende.provider.constant.QualityDefendeConstants;
import com.ziroom.qa.quality.defende.provider.constant.enums.TestExecutionStatusEnum;
import com.ziroom.qa.quality.defende.provider.constant.enums.TestExecutionTypeEnum;
import com.ziroom.qa.quality.defende.provider.constant.enums.TestTaskCaseStatusEnum;
import com.ziroom.qa.quality.defende.provider.constant.enums.TestTaskStatusEnum;
import com.ziroom.qa.quality.defende.provider.entity.User;
import com.ziroom.qa.quality.defende.provider.execTask.entity.TaskTestCase;
import com.ziroom.qa.quality.defende.provider.execTask.entity.TestExecution;
import com.ziroom.qa.quality.defende.provider.execTask.entity.TestTask;
import com.ziroom.qa.quality.defende.provider.execTask.entity.dto.RunCaseByIdListDto;
import com.ziroom.qa.quality.defende.provider.execTask.entity.vo.AutoExecutionRecordVo;
import com.ziroom.qa.quality.defende.provider.execTask.entity.vo.DemandVO;
import com.ziroom.qa.quality.defende.provider.execTask.mapper.TestExecutionMapper;
import com.ziroom.qa.quality.defende.provider.execTask.service.IAutoTestCaseExecuteService;
import com.ziroom.qa.quality.defende.provider.execTask.service.TaskTestCaseService;
import com.ziroom.qa.quality.defende.provider.execTask.service.TestExecutionService;
import com.ziroom.qa.quality.defende.provider.execTask.service.TestTaskService;
import com.ziroom.qa.quality.defende.provider.result.CustomException;
import com.ziroom.qa.quality.defende.provider.result.RestResultVo;
import com.ziroom.qa.quality.defende.provider.service.UserService;
import com.ziroom.qa.quality.defende.provider.util.DicUtil;
import com.ziroom.qa.quality.defende.provider.vo.BatchExecuteVo;
import com.ziroom.qa.quality.defende.provider.vo.ExecutionSummaryVo;
import com.ziroom.qa.quality.defende.provider.vo.SingleExecuteVo;
import lombok.extern.slf4j.Slf4j;
import net.rcarz.jiraclient.Issue;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Slf4j
public class TestExecutionServiceImpl extends ServiceImpl<TestExecutionMapper, TestExecution> implements TestExecutionService {

    @Autowired
    TestExecutionMapper testExecutionMapper;
    @Autowired
    private TaskTestCaseService taskTestCaseService;
    @Autowired
    private TestTaskService testTaskService;
    @Autowired
    private UserService userService;
    @Autowired
    private TestCaseService testCaseService;
    @Autowired
    @Lazy
    private IAutoTestCaseExecuteService autoTestCaseExecuteService;

    public void autoExetionTask(long executionTaskId, String userName, List<AutoExecutionRecordVo> autoRecordList) {
        if (CollectionUtils.isEmpty(autoRecordList)) {
            throw new CustomException("???????????????????????????????????????");
        }
        String executionVersion = "v1.0";
        List<TestExecution> executionList = new ArrayList<>();
        List<TaskTestCase> taskTestCaseList = new ArrayList<>();
        for (AutoExecutionRecordVo testCase : autoRecordList) {
            TestExecution testExecution = new TestExecution();
            long caseId = (long) testCase.getCaseId();
            // ??????????????????
            testExecution.setExecutionTime(LocalDateTime.now());
            String exeResult;
//            String bugKey = null;
            if ("SUCCESS".equals(testCase.getRecordResult())) {
                exeResult = TestExecutionStatusEnum.PASS.getDescription();
            } else {
                exeResult = TestExecutionStatusEnum.FAIL.getDescription();
//                Issue bug = this.createBugByDefende(executionTaskId, testCase.getCaseId() + "", testCase.getCaseName());
//                bugKey = Objects.isNull(bug) ? null : bug.getKey();
            }
            // ??????????????????
            testExecution.setExecutionTime(LocalDateTime.now());
            testExecution.setCaseKey(caseId + "");
            testExecution.setExecutionResult(exeResult);
            testExecution.setExecutionVersion(executionVersion);
            testExecution.setExecutionUser(userName);
            testExecution.setIsNew(1);
            testExecution.setExeRemark("???????????????");
            testExecution.setCaseId(caseId);
            // ??????api???????????????
            testExecution.setAssertRecordList(CollectionUtils.isNotEmpty(testCase.getList()) ? JSON.toJSONString(testCase.getList()) : null);
            testExecution.setActualResult(StringUtils.isBlank(testCase.getActualResult()) ? "" : testCase.getActualResult().substring(0, testCase.getActualResult().length() > 4500 ? 4500 : testCase.getActualResult().length()));
            testExecution.setTestExecutionType(TestExecutionTypeEnum.AUTO.getKey());
            testExecution.setResponseTime(testCase.getResponseTime());
//            testExecution.setRealInParam(StringUtils.isBlank(testCase.getRealInParam()) ? "" : testCase.getRealInParam().substring(0, testCase.getRealInParam().length() > 1000 ? 1000 : testCase.getRealInParam().length()));
            testExecution.setEnv(testCase.getEnv());
            testExecution.setUrl(StringUtils.isBlank(testCase.getRequestUrl()) ? "" : testCase.getRequestUrl().substring(0, testCase.getRequestUrl().length() > 1000 ? 1000 : testCase.getRequestUrl().length()));
            testExecution.setRequestType(testCase.getRequestType());
            testExecution.setReqHeader(StringUtils.isBlank(testCase.getHeader()) ? "" : testCase.getHeader().substring(0, testCase.getHeader().length() > 1000 ? 1000 : testCase.getHeader().length()));
            testExecution.setReqBody(StringUtils.isBlank(testCase.getRequestBody()) ? "" : testCase.getRequestBody().substring(0, testCase.getRequestBody().length() > 1000 ? 1000 : testCase.getRequestBody().length()));
            testExecution.setApplicationName(testCase.getApplicationName());
            testExecution.setExecutionTaskId(executionTaskId);
//            testExecution.setRelationBug(bugKey);
            executionList.add(testExecution);
            // update user
            TaskTestCase taskTestCase = new TaskTestCase();
            taskTestCase.setCasename(testCase.getCaseName());
            taskTestCase.setTaskId(executionTaskId);
            taskTestCase.setTestCaseId(caseId);
            taskTestCase.setCaseKey(caseId + "");
            taskTestCase.setExecutionUser(userName);
            taskTestCase.setExecutionResult(exeResult);
            taskTestCase.setExecutionTime(LocalDateTime.now());
            taskTestCase.setExecutionVersion(executionVersion);
            taskTestCase.setExeRemark("???????????????");

            taskTestCase.setCreateUser(userName);
            taskTestCase.setUpdateUser(userName);
            taskTestCase.setCreateTime(LocalDateTime.now());
            taskTestCase.setUpdateTime(LocalDateTime.now());
            // ??????????????????
            taskTestCase.setApplicationName(testCase.getApplicationName());
            taskTestCase.setCasename(testCase.getCaseName());
            taskTestCase.setTestExecutionType(TestExecutionTypeEnum.AUTO.getKey());
//            taskTestCase.setRelationBug(bugKey);

            taskTestCaseList.add(taskTestCase);
        }

        super.saveBatch(executionList);
        taskTestCaseService.saveBatch(taskTestCaseList);
        this.updateCount(executionTaskId, userName);
    }

    /**
     * ????????????bug
     *
     * @param singleExecute
     * @param userName
     */
    @Override
    public String createBugByInfo(SingleExecuteVo singleExecute, String userName) {
        TestTask testTask = testTaskService.getById(singleExecute.getExecutionTaskId());
        // ????????????????????????
        this.checkTemplateTask(testTask);
        Issue bug = this.createBugByDefende(testTask, userName, singleExecute);
        singleExecute.setRelationBug(bug.getKey());
        singleExecute.setExecutionVersion("v1.0");
        // ??????????????????
        if (TestExecutionTypeEnum.ARTIFICIAL.getKey().equals(testTask.getTestExecutionType())) {
            this.exeTestCaseFail(singleExecute, userName, bug);
        }
        // ??????????????????
        else {
            this.exeTestCaseFailAuto(singleExecute, userName, bug);
        }
        return bug.getKey();
    }

    /**
     * ??????jiraID?????????????????????
     *
     * @param taskId
     * @param userName
     * @return
     */
    @Override
    public DemandVO queryDemandInfo(Long taskId, String userName) {
        DemandVO demandVO = new DemandVO();
        // ?????? ??????????????????
//        JSONObject jsonObject = jiraApiClient.getJiraGroup(2000, null);
//        if (Objects.nonNull(jsonObject)) {
//            Integer groupCount = Integer.parseInt(jsonObject.get("total").toString());
//            if (groupCount > 0) {
//                List<JiraGroupDTO> groupList = jsonObject.getJSONArray("groups").toJavaList(JiraGroupDTO.class);
//                demandVO.setBugDeptList(groupList);
//            }
//        }
        // ?????? ????????????
        TestTask testTask = testTaskService.getById(taskId);
//        List<String> moduleList = JiraUtils.getComponentsAllowedValues(testTask.getRelationRequirement().split("-")[0], "??????");
//        if (CollectionUtils.isNotEmpty(moduleList)) {
//            demandVO.setModuleList(moduleList);
//        }

        return demandVO;
    }

    /**
     * ????????????bug???????????????????????????
     *
     * @param taskId
     * @return
     */
    @Override
    public List<TestExecution> getBugTestExecutionByTaskId(Long taskId) {
        QueryWrapper<TestExecution> infoQueryWrapper = new QueryWrapper<>();
        infoQueryWrapper
                .eq("execution_task_id", taskId)
                .isNotNull("relation_bug");
        return super.list(infoQueryWrapper);
    }

    @Override
    public void markTestCaseFail(SingleExecuteVo singleExecute, String userName) {
        if (StringUtils.isBlank(singleExecute.getCaseKey())) {
            throw new CustomException("????????????????????????caseKey?????????");
        }
        TestTask task = testTaskService.getById(singleExecute.getExecutionTaskId());
        // ????????????????????????
        this.checkTemplateTask(task);
        if (TestTaskStatusEnum.COMPLETE.getTestsTaskStatus().equals(task.getStatus())) {
            throw new CustomException("??????????????????????????????????????????????????????????????????????????????????????????????????????<??????>??????");
        } else if (TestTaskStatusEnum.LAUNCH.getTestsTaskStatus().equals(task.getStatus())) {
            throw new CustomException("?????????????????????????????????????????????????????????");
        }
        // ????????????????????????
//        Issue issue;
//        try {
//            issue = JiraUtils.getJiraIssueByIssueKey(singleExecute.getRelationBug());
//        } catch (Exception e) {
//            throw new CustomException("??????id?????????");
//        }
//        if (Objects.isNull(issue) || !TestCenterConstants.ISSUE_TYPE_BUG.equals(Long.parseLong(issue.getIssueType().getId()))) {
//            throw new CustomException("??????id?????????");
//        }
        // ??????????????????
        if (TestExecutionTypeEnum.ARTIFICIAL.getKey().equals(task.getTestExecutionType())) {
            this.exeTestCaseFail(singleExecute, userName, null);
        }
        // ??????????????????
        else {
            this.exeTestCaseFailAuto(singleExecute, userName, null);
        }


    }

    @Override
    public String batchExecuteTestCase(BatchExecuteVo batchExecute, String userName) {
        TestTask task = testTaskService.getById(batchExecute.getExecutionTaskId());
        if (Objects.isNull(task)) {
            throw new CustomException("??????????????????????????????");
        }
        // ????????????????????????
        this.checkTemplateTask(task);

        if (TestTaskStatusEnum.COMPLETE.getTestsTaskStatus().equals(task.getStatus())) {
            throw new CustomException("??????????????????????????????????????????????????????????????????????????????????????????????????????<??????>??????");
        }
        if (TestTaskStatusEnum.LAUNCH.getTestsTaskStatus().equals(task.getStatus())) {
            throw new CustomException("?????????????????????????????????????????????????????????");
        }
        // ????????????????????????
        if (TestExecutionTypeEnum.ARTIFICIAL.getKey().equals(task.getTestExecutionType())) {
            return this.batchExeArtificialInfo(batchExecute, userName);
            // ????????????????????????
        } else {
            return this.batchExeAutoInfo(batchExecute, userName);
        }
    }


    @Override
    public RestResultVo getCaseStatusNumber(Long taskId, String userName) {
        TestTask task = testTaskService.getById(taskId);
        if (task == null) {
            return RestResultVo.fromErrorMessage("??????????????????????????????");
        }

        String relationRequirement = task.getRelationRequirement();
        ExecutionSummaryVo summaryVo = new ExecutionSummaryVo();
//        try {
//            Issue issue = JiraUtils.getJiraIssueByIssueKey(relationRequirement);
//            String summary = issue.getSummary();
//            summaryVo.setRequirementSummary(relationRequirement + " " + summary);
//        } catch (Exception e) {
//            summaryVo.setRequirementSummary(relationRequirement);
//        }
        summaryVo.setRequirementSummary(relationRequirement);
        summaryVo.setNotRunCount(task.getNotRunCount());
        summaryVo.setRunCount(task.getRunCount());
        summaryVo.setRunFailCount(task.getRunFailCount());
        summaryVo.setRunSkipCount(task.getRunSkipCount());
        summaryVo.setRunSuccessCount(task.getRunSuccessCount());
        summaryVo.setTaskName(task.getTaskName());
        summaryVo.setTaskStatusStr(TestTaskStatusEnum.getStatusNameByStatus(task.getStatus()));
        // ??????????????????/?????????/???????????????????????????
        if (TestTaskStatusEnum.RUNNING.getTestsTaskStatus().equals(task.getStatus())
                && (userName.equals(task.getCreateUser()) || userName.equals(task.getTestTaskMaster()) || task.getTestTaskExecutors().contains(userName))) {
            summaryVo.setComButtonFlag(true);
        } else {
            summaryVo.setComButtonFlag(false);
        }
        return RestResultVo.fromData(summaryVo);
    }

    @Override
    public RestResultVo queryTestCaseByPage(Long taskId, Page page) {
        QueryWrapper<TaskTestCase> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(Objects.nonNull(taskId), "task_id", taskId);
        Page<TaskTestCase> taskTestCaseResult = taskTestCaseService.page(page, queryWrapper);
        List<TaskTestCase> taskTestCaseList = taskTestCaseResult.getRecords();
        // ????????????????????????
        Map<String, String> levelMap = DicUtil.dicListToMapEnName(QualityDefendeConstants.DATA_DICTIONARY_TEST_LEVEL_TYPE);
        taskTestCaseList.stream().parallel().forEach(taskTest -> {
            // ?????????
            taskTest.setTestCaseLevel(levelMap.get(taskTest.getTestCaseLevel()));
            // ????????????
            taskTest.setExecutionResult(TestTaskCaseStatusEnum.getTestsTaskStatusName(taskTest.getExecutionResult()));
        });
        taskTestCaseResult.setRecords(taskTestCaseList);
        return RestResultVo.fromData(taskTestCaseResult);
    }

    /**
     * ??????????????????????????????
     *
     * @param executionTaskId
     * @param userName
     */
    @Override
    public void updateCount(Long executionTaskId, String userName) {
        // 1.???????????????????????????
        QueryWrapper<TaskTestCase> taskTestQueryWrapper = new QueryWrapper<>();
        taskTestQueryWrapper.eq("task_id", executionTaskId);
        Integer caseCount = taskTestCaseService.count(taskTestQueryWrapper);
        // 2.??????????????????
        taskTestQueryWrapper.clear();
        taskTestQueryWrapper.eq("task_id", executionTaskId);
        taskTestQueryWrapper.eq("execution_result", TestTaskCaseStatusEnum.NOT_STARTED.getTestsTaskStatus());
        Integer notRunCount = taskTestCaseService.count(taskTestQueryWrapper);
        // 3.?????????????????????????????????
        taskTestQueryWrapper.clear();
        taskTestQueryWrapper.eq("task_id", executionTaskId);
        taskTestQueryWrapper.eq("execution_result", TestTaskCaseStatusEnum.PASS.getTestsTaskStatus());
        Integer runSuccessCount = taskTestCaseService.count(taskTestQueryWrapper);
        // 4.????????????????????????????????????
        taskTestQueryWrapper.clear();
        taskTestQueryWrapper.eq("task_id", executionTaskId);
        taskTestQueryWrapper.eq("execution_result", TestTaskCaseStatusEnum.FAIL.getTestsTaskStatus());
        Integer runFailCount = taskTestCaseService.count(taskTestQueryWrapper);
        // 5.?????????????????????????????????
        taskTestQueryWrapper.clear();
        taskTestQueryWrapper.eq("task_id", executionTaskId);
        taskTestQueryWrapper.eq("execution_result", TestTaskCaseStatusEnum.SKIP.getTestsTaskStatus());
        Integer skipCount = taskTestCaseService.count(taskTestQueryWrapper);

        TestTask testTask = testTaskService.getById(executionTaskId);
        int runCount = runSuccessCount + runFailCount + skipCount;
        testTask.setRunCount(runCount);
        testTask.setNotRunCount(notRunCount);
        testTask.setRunSuccessCount(runSuccessCount);
        testTask.setRunFailCount(runFailCount);
        testTask.setRunSkipCount(skipCount);
        testTask.setTestcaseCount(caseCount);
        // ???????????????????????????
        if (StringUtils.isNotBlank(userName)) {
            testTask.setUpdateTime(LocalDateTime.now());
            testTask.setUpdateUser(userName);
            if (StringUtils.isBlank(testTask.getTestTaskExecutors())) {
                testTask.setTestTaskExecutors(userName);
            } else if (!testTask.getTestTaskExecutors().contains(userName)) {
                testTask.setTestTaskExecutors(testTask.getTestTaskExecutors() + "," + userName);
            }
        }
        // ????????????????????????????????????????????????
        if (runCount > 0) {
            testTask.setStatus(TestTaskStatusEnum.RUNNING.getTestsTaskStatus());
            // ???jira???????????????jira??????????????????
            // ??????jira???????????????????????????????????????????????????????????????
//            this.changeJiraStatus(testTask.getRelationRequirement());
        } else {
            if (!TestTaskStatusEnum.NOT_STARTED.getTestsTaskStatus().equals(testTask.getStatus())
                    && !TestTaskStatusEnum.SUBMITED.getTestsTaskStatus().equals(testTask.getStatus())) {
                testTask.setStatus(TestTaskStatusEnum.NOT_STARTED.getTestsTaskStatus());
            }
        }
        testTaskService.updateById(testTask);
    }

    /**
     * ?????????????????????????????????
     *
     * @return
     */
    @Override
    public boolean updateTestExecutionUserInfo() {
        List<TestExecution> testExecutionList = this.list();
        testExecutionList.stream().forEach(testExecution -> {
            String executionUserUid = testExecution.getExecutionUser();
            User executionUser = userService.getUserInfoByUid(executionUserUid);
            testExecution.setExecutionUser(executionUser.getUserName());
        });
        return this.updateBatchById(testExecutionList);
    }

    /**
     * ?????????????????????????????????
     *
     * @param taskId
     * @return
     */
    @Override
    public List<TestExecution> getTestExecutionByNew(Long taskId) {
        return this.getTestExecutionByCondition(taskId, null, null);
    }

    /**
     * ???????????????????????????????????????
     *
     * @param taskId
     * @param startDateStr
     * @param endDateStr
     * @return
     */
    @Override
    public List<TestExecution> getTestExecutionByDate(Long taskId, String startDateStr, String endDateStr) {
        return this.getTestExecutionByCondition(taskId, startDateStr, endDateStr);
    }

    /**
     * ????????????????????????
     *
     * @param testExecution
     * @return
     */
    @Override
    public List<TestExecution> getTestExecutionHistory(TestExecution testExecution) {
        Long taskId = testExecution.getExecutionTaskId();
        String caseKey = testExecution.getCaseKey();
        QueryWrapper<TestExecution> infoQueryWrapper = new QueryWrapper<>();
        infoQueryWrapper
                .eq("execution_task_id", taskId)
                .eq("case_key", caseKey);
        infoQueryWrapper.orderByDesc("id");
        List<TestExecution> resList = super.list(infoQueryWrapper);
        if (CollectionUtils.isNotEmpty(resList)) {
            resList.stream().forEach(execution -> {
                User user = userService.getUserInfoByUserName(execution.getExecutionUser());
                execution.setExecutionUser(Objects.isNull(user) ? "" : user.getNickName());
                TestCase testCase = testCaseService.getById(execution.getCaseId());
                execution.setTestCaseVesion(Objects.isNull(testCase) ? "" : testCase.getVersion());
                execution.setExecutionResult(TestTaskCaseStatusEnum.getTestsTaskStatusName(execution.getExecutionResult()));
            });
        }
        return resList;
    }

//    /**
//     * ????????????????????????
//     *
//     * @param batchExecute
//     * @param userName
//     * @return
//     */
//    @Override
//    public TestResultVo checkTestCaseVersion(BatchExecuteVo batchExecute, String userName) {
//        if (CollectionUtils.isEmpty(batchExecute.getCaseKeyList()) || Objects.isNull(batchExecute.getExecutionTaskId())) {
//            return TestResultVo.builder().flag(false).msgRes(TestCenterConstants.RES_MSG_PARAMS_EMPTY).build();
//        }
//        QueryWrapper<TaskTestCase> taskTestQueryWrapper = new QueryWrapper<>();
//        taskTestQueryWrapper.eq("task_id", batchExecute.getExecutionTaskId());
//        taskTestQueryWrapper.in("case_key", batchExecute.getCaseKeyList());
//        List<TaskTestCase> taskTestList = taskTestCaseService.list(taskTestQueryWrapper);
//        if (CollectionUtils.isEmpty(taskTestList)) {
//            return TestResultVo.builder().flag(true).msgRes(TestCenterConstants.RES_MSG_SUCCESS).build();
//        }
//        QueryWrapper<TestCase> testCaseQueryWrapper = new QueryWrapper<>();
//        testCaseQueryWrapper.eq("delete_flag", false);
//        testCaseQueryWrapper.eq("flag", true);
//        testCaseQueryWrapper.in("case_key", batchExecute.getCaseKeyList());
//        List<TestCase> caseList = testCaseService.list(testCaseQueryWrapper);
//        if (CollectionUtils.isEmpty(caseList)) {
//            return TestResultVo.builder().flag(false).msgRes(TestCenterConstants.RES_MSG_SEARCH_EMPTY).build();
//        }
//        Map<String, String> caseKeyMap = caseList.stream().collect(Collectors.toMap(TestCase::getCaseKey, TestCase::getVersion));
//
//        StringBuffer sb = new StringBuffer();
//        taskTestList.stream().parallel().forEach(taskTest -> {
//            String caseVersion = caseKeyMap.get(taskTest.getCaseKey());
//            if (StringUtils.isNotBlank(caseVersion) && !caseVersion.equals(taskTest.getVersion())) {
//                sb.append("???????????????" + taskTest.getCasename() + "????????????" + taskTest.getVersion() + "??????????????????????????????" + caseVersion + "\n");
//            }
//        });
//        return TestResultVo.builder().flag(true).msgRes(sb.toString()).data(sb.toString()).build();
//    }

    /**
     * ?????????????????????????????????????????????
     *
     * @param taskId
     * @param startDateStr
     * @param endDateStr
     * @return
     */
    private List<TestExecution> getTestExecutionByCondition(Long taskId, String startDateStr, String endDateStr) {
        QueryWrapper<TestExecution> infoQueryWrapper = new QueryWrapper<>();
        infoQueryWrapper
                .eq("execution_task_id", taskId)
                .eq("is_new", 1)
        ;
        infoQueryWrapper.orderByDesc("id");
        if (StringUtils.isNotBlank(startDateStr) && StringUtils.isNotBlank(endDateStr)) {
            infoQueryWrapper.between("execution_time", startDateStr, endDateStr);
        }
        return super.list(infoQueryWrapper);
    }

    /**
     * ?????????????????????????????????????????????
     *
     * @param taskId
     * @param caseId
     * @return
     */
    private TestExecution getTestExecutionByTaskIdAndCaseId(Long taskId, Long caseId) {
        QueryWrapper<TestExecution> infoQueryWrapper = new QueryWrapper<>();
        infoQueryWrapper
                .eq("execution_task_id", taskId)
                .eq("case_id", caseId)
                .eq("is_new", 1)
        ;
        infoQueryWrapper.orderByDesc("id");
        return super.getOne(infoQueryWrapper);
    }

    /**
     * ????????????????????????
     *
     * @param batchExecute
     * @param userName
     */
    private String batchExeArtificialInfo(BatchExecuteVo batchExecute, String userName) {
        List<String> caseKeyList = batchExecute.getCaseKeyList();
        if (CollectionUtils.isEmpty(caseKeyList)) {
            throw new CustomException("????????????????????????caseKey?????????");
        }
        String executionResult = batchExecute.getExecutionResult();
        String executionVersion = batchExecute.getExecutionVersion();
        long executionTaskId = batchExecute.getExecutionTaskId();
        List<TaskTestCase> taskTestCaseList = new ArrayList<>();
        List<TestExecution> testExecutionList = new ArrayList<>();
        for (String caseKey : caseKeyList) {
            TestCase testCase = testCaseService.getTestCaseByCaseKey(caseKey);
            TestExecution testExecution = new TestExecution();
            // ???????????? case id ??? flag
            testExecution.setIsNew(0);
            testExecution.setCaseKey(caseKey);
            testExecution.setExecutionTaskId(executionTaskId);
            this.updateFlagByCaseKey(testExecution);
            // ??????????????????
            testExecution.setExecutionTime(LocalDateTime.now());
            testExecution.setExecutionResult(executionResult);
            testExecution.setExecutionVersion(executionVersion);
            testExecution.setExecutionUser(userName);
            testExecution.setIsNew(1);
            testExecution.setExeRemark(batchExecute.getExeRemark());
            testExecution.setCaseId(testCase.getId());
            testExecutionList.add(testExecution);
            // update user
            QueryWrapper<TaskTestCase> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("task_id", executionTaskId);
            queryWrapper.eq("case_key", caseKey);
            TaskTestCase taskTestCase = taskTestCaseService.getOne(queryWrapper);
            taskTestCase.setTaskId(executionTaskId);
            taskTestCase.setTestCaseId(testCase.getId());
            taskTestCase.setCaseKey(caseKey);
            taskTestCase.setExecutionUser(userName);
            taskTestCase.setExecutionResult(executionResult);
            taskTestCase.setExecutionTime(LocalDateTime.now());
            taskTestCase.setExecutionVersion(executionVersion);
            taskTestCase.setExeRemark(batchExecute.getExeRemark());
            // ??????????????????
            taskTestCase.setCasename(testCase.getCasename());
            // ??????caseKey???taskId????????????
            taskTestCaseList.add(taskTestCase);
        }
        super.saveBatch(testExecutionList);
        taskTestCaseService.updateBatchById(taskTestCaseList);
        this.updateCount(executionTaskId, userName);
        return "????????????";
    }


    /**
     * ????????????????????????
     *
     * @param batchExecute
     * @param userName
     */
    private String batchExeAutoInfo(BatchExecuteVo batchExecute, String userName) {
        List<String> caseKeyList = batchExecute.getCaseKeyList();
        if (CollectionUtils.isEmpty(caseKeyList)) {
            throw new CustomException("????????????ID?????????");
        }
        RunCaseByIdListDto runCaseByIdListDto = new RunCaseByIdListDto();
        runCaseByIdListDto.setUserCode(userName);
        runCaseByIdListDto.setCaseIdList(caseKeyList.stream().map(caseId -> Integer.parseInt(caseId)).collect(Collectors.toList()));
        runCaseByIdListDto.setEnv(batchExecute.getEnv());
        List<AutoExecutionRecordVo> exeResList = autoTestCaseExecuteService.runCaseByIdList(runCaseByIdListDto);
        Map<Long, AutoExecutionRecordVo> exeResMap = exeResList.stream().collect(Collectors.toMap(entity -> (long) entity.getCaseId(), Function.identity()));
        String executionVersion = batchExecute.getExecutionVersion();
        long executionTaskId = batchExecute.getExecutionTaskId();
        //????????????
        int successCount = 0;
        //????????????
        int failCount = 0;

        List<TaskTestCase> taskTestCaseList = new ArrayList<>();
        List<TestExecution> testExecutionList = new ArrayList<>();

        for (String caseId : caseKeyList) {
            AutoExecutionRecordVo testCase = exeResMap.get(Long.parseLong(caseId));
            if (Objects.isNull(testCase)) {
                log.info("taskId=={}????????????caseId=={}????????????", batchExecute.getExecutionTaskId(), caseId);
                continue;
            }
            TestExecution testExecution = new TestExecution();
            // ???????????? case id ??? flag
            testExecution.setIsNew(0);
            testExecution.setCaseId(Long.parseLong(caseId));
            testExecution.setExecutionTaskId(executionTaskId);
            this.updateFlagByCaseId(testExecution);
            // ??????????????????
            testExecution.setExecutionTime(LocalDateTime.now());
            String exeResult;
            if ("SUCCESS".equals(testCase.getRecordResult())) {
                exeResult = TestExecutionStatusEnum.PASS.getDescription();
                successCount++;
            } else {
                exeResult = TestExecutionStatusEnum.FAIL.getDescription();
                failCount++;
            }
            testExecution.setCaseKey(caseId);
            testExecution.setExecutionResult(exeResult);
            testExecution.setExecutionVersion(executionVersion);
            testExecution.setExecutionUser(userName);
            testExecution.setIsNew(1);
            testExecution.setExeRemark(batchExecute.getExeRemark());
            // ??????api???????????????
            testExecution.setAssertRecordList(CollectionUtils.isNotEmpty(testCase.getList()) ? JSON.toJSONString(testCase.getList()) : null);
            testExecution.setActualResult(StringUtils.isBlank(testCase.getActualResult()) ? "" : testCase.getActualResult().substring(0, testCase.getActualResult().length() > 4500 ? 4500 : testCase.getActualResult().length()));
            testExecution.setTestExecutionType(TestExecutionTypeEnum.AUTO.getKey());
            testExecution.setResponseTime(testCase.getResponseTime());
//            testExecution.setRealInParam(StringUtils.isBlank(testCase.getRealInParam()) ? "" : testCase.getRealInParam().substring(0, testCase.getRealInParam().length() > 1000 ? 1000 : testCase.getRealInParam().length()));
            testExecution.setEnv(batchExecute.getEnv());
            testExecution.setUrl(StringUtils.isBlank(testCase.getRequestUrl()) ? "" : testCase.getRequestUrl().substring(0, testCase.getRequestUrl().length() > 1000 ? 1000 : testCase.getRequestUrl().length()));
            testExecution.setRequestType(testCase.getRequestType());
            testExecution.setReqHeader(StringUtils.isBlank(testCase.getHeader()) ? "" : testCase.getHeader().substring(0, testCase.getHeader().length() > 1000 ? 1000 : testCase.getHeader().length()));
            testExecution.setReqBody(StringUtils.isBlank(testCase.getRequestBody()) ? "" : testCase.getRequestBody().substring(0, testCase.getRequestBody().length() > 1000 ? 1000 : testCase.getRequestBody().length()));
            testExecution.setApplicationName(testCase.getApplicationName());
            testExecutionList.add(testExecution);

            // update user
            QueryWrapper<TaskTestCase> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("task_id", executionTaskId);
            queryWrapper.eq("test_case_id", Long.parseLong(caseId));
            TaskTestCase taskTestCase = taskTestCaseService.getOne(queryWrapper);
            taskTestCase.setTaskId(executionTaskId);
            taskTestCase.setTestCaseId(Long.parseLong(caseId));
            taskTestCase.setCaseKey(caseId);
            taskTestCase.setExecutionUser(userName);
            taskTestCase.setExecutionResult(exeResult);
            taskTestCase.setExecutionTime(LocalDateTime.now());
            taskTestCase.setExecutionVersion(executionVersion);
            taskTestCase.setExeRemark(batchExecute.getExeRemark());
            // ??????????????????
            taskTestCase.setCasename(testCase.getCaseName());
            taskTestCase.setApplicationName(testCase.getApplicationName());
            taskTestCase.setTestExecutionType(TestExecutionTypeEnum.AUTO.getKey());
            taskTestCaseList.add(taskTestCase);
            // ??????caseKey???taskId????????????
        }
        super.saveBatch(testExecutionList);
        taskTestCaseService.updateBatchById(taskTestCaseList);

        this.updateCount(executionTaskId, userName);
        return "????????????????????????" + successCount + "???????????????" + failCount + "???";
    }

    /**
     * ??????????????????????????????
     *
     * @param singleExecute
     * @param userName
     * @param issue
     */
    private void exeTestCaseFail(SingleExecuteVo singleExecute,
                                 String userName,
                                 Issue issue) {
        TestExecution testExecution = new TestExecution();
        // ???????????? case id ??? flag
        testExecution.setIsNew(0);
        testExecution.setExecutionTaskId(singleExecute.getExecutionTaskId());
        testExecution.setCaseKey(singleExecute.getCaseKey());
        this.updateFlagByCaseKey(testExecution);
        // caseId????????????
        TestCase testCase = testCaseService.getTestCaseByCaseKey(singleExecute.getCaseKey());
        Long caseId = Objects.isNull(testCase) ? null : testCase.getId();
        testExecution.setCaseId(caseId);
        testExecution.setExecutionTime(LocalDateTime.now());
        testExecution.setExecutionResult(TestExecutionStatusEnum.FAIL.getDescription());
        testExecution.setExecutionVersion(singleExecute.getExecutionVersion());
        testExecution.setExeRemark(singleExecute.getExeRemark());
        testExecution.setExecutionUser(userName);
        testExecution.setIsNew(1);
        testExecution.setRelationBug(singleExecute.getRelationBug());
//        // ??????????????????id ???????????????????????? by lh
//        testExecution.setBugStatusId(Long.valueOf(issue.getStatus().getId()));
//        // ??????????????????id ???????????????????????? by lh
//        testExecution.setBugLevelId(Long.valueOf(issue.getPriority().getId()));
        super.save(testExecution);

        // ?????? case ?????????
        TaskTestCase taskTestCase = new TaskTestCase();
        taskTestCase.setTaskId(singleExecute.getExecutionTaskId());
        taskTestCase.setTestCaseId(caseId);
        taskTestCase.setCaseKey(singleExecute.getCaseKey());
        taskTestCase.setExecutionUser(userName);
        taskTestCase.setExecutionResult(TestExecutionStatusEnum.FAIL.getDescription());
        taskTestCase.setExecutionTime(LocalDateTime.now());
        taskTestCase.setExecutionVersion(singleExecute.getExecutionVersion());
        taskTestCase.setRelationBug(singleExecute.getRelationBug());
        taskTestCase.setExeRemark(singleExecute.getExeRemark());
        // ??????????????????
        taskTestCase.setCasename(testCase.getCasename());
        // ??????caseKey???taskId????????????
        QueryWrapper<TaskTestCase> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("task_id", singleExecute.getExecutionTaskId());
        queryWrapper.eq("case_key", singleExecute.getCaseKey());
        taskTestCaseService.update(taskTestCase, queryWrapper);
        // ??????????????????????????????
        this.updateCount(singleExecute.getExecutionTaskId(), userName);
    }

    /**
     * ????????????????????????????????????
     *
     * @param singleExecute
     * @param userName
     * @param issue
     */
    private void exeTestCaseFailAuto(SingleExecuteVo singleExecute,
                                     String userName,
                                     Issue issue) {
        // ??????????????????????????????????????????
        TestExecution oldTestExecution = this.getTestExecutionByTaskIdAndCaseId(singleExecute.getExecutionTaskId(), Long.parseLong(singleExecute.getCaseKey()));
        oldTestExecution.setIsNew(0);
        super.updateById(oldTestExecution);
        // ??????????????????????????????
        TestExecution testExecution = new TestExecution();
        BeanUtils.copyProperties(oldTestExecution, testExecution);
        testExecution.setExecutionTime(LocalDateTime.now());
        testExecution.setExecutionResult(TestExecutionStatusEnum.FAIL.getDescription());
        testExecution.setExecutionVersion(singleExecute.getExecutionVersion());
        testExecution.setExeRemark(singleExecute.getExeRemark());
        testExecution.setExecutionUser(userName);
        testExecution.setIsNew(1);
        testExecution.setRelationBug(singleExecute.getRelationBug());
        // ??????????????????id ???????????????????????? by lh
        testExecution.setBugStatusId(Long.valueOf(issue.getStatus().getId()));
        // ??????????????????id ???????????????????????? by lh
        testExecution.setBugLevelId(Long.valueOf(issue.getPriority().getId()));
        super.save(testExecution);

        // ?????? case ?????????
        TaskTestCase taskTestCase = new TaskTestCase();
        taskTestCase.setTaskId(singleExecute.getExecutionTaskId());
        taskTestCase.setTestCaseId(Long.parseLong(singleExecute.getCaseKey()));
        taskTestCase.setCaseKey(singleExecute.getCaseKey());
        taskTestCase.setExecutionUser(userName);
        taskTestCase.setExecutionResult(TestExecutionStatusEnum.FAIL.getDescription());
        taskTestCase.setExecutionTime(LocalDateTime.now());
        taskTestCase.setExecutionVersion(singleExecute.getExecutionVersion());
        taskTestCase.setRelationBug(singleExecute.getRelationBug());
        taskTestCase.setExeRemark(singleExecute.getExeRemark());
        // ??????????????????
        taskTestCase.setTestExecutionType(TestExecutionTypeEnum.AUTO.getKey());
        // ??????caseKey???taskId????????????
        QueryWrapper<TaskTestCase> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("task_id", singleExecute.getExecutionTaskId());
        queryWrapper.eq("test_case_id", Long.parseLong(singleExecute.getCaseKey()));
        taskTestCaseService.update(taskTestCase, queryWrapper);
        // ??????????????????????????????
        this.updateCount(singleExecute.getExecutionTaskId(), userName);
    }

    private void updateFlagByCaseList(TestExecution testExecution) {
        if (CollectionUtils.isEmpty(testExecution.getTestCaseIdList())
                && CollectionUtils.isEmpty(testExecution.getTestCaseKeyList())) {
            return;
        }
        testExecutionMapper.updateFlagByCaseList(testExecution);
    }

    private Integer updateFlagByCaseKey(TestExecution testExecution) {
        return testExecutionMapper.updateFlagByCaseKey(testExecution);
    }

    private Integer updateFlagByCaseId(TestExecution testExecution) {
        return testExecutionMapper.updateFlagByCaseId(testExecution);
    }

    /**
     * ????????????bug
     *
     * @param testTask
     * @param userName
     * @param singleExecuteVo
     * @return
     */
    private Issue createBugByDefende(TestTask testTask, String userName, SingleExecuteVo singleExecuteVo) {
        throw new CustomException("??????????????????bug?????????????????????bug?????????");
//        //1.????????????????????????
//        if (Objects.isNull(testTask)) {
//            throw new CustomException("??????????????????????????????");
//        }
//        //2.?????????????????????
//        Issue issue = JiraUtils.getJiraIssueByIssueKey(testTask.getRelationRequirement());
//        if (Objects.isNull(issue)) {
//            throw new CustomException("????????????????????????");
//        }
//        JiraIssue defect = new JiraIssue();
//        // bug???????????????
//        defect.setProject(issue.getProject().getKey());
//        // ?????????
//        defect.setAssignee(singleExecuteVo.getBugUserStr());
//        // ?????????
////        defect.setReporter(userName);
//        defect.setCustomfield_11292(userName);
//
////----------------------------------------------------------------------------------------------------
//        // bug??????
//        defect.setSummary(singleExecuteVo.getBugSummaryStr());
//        // ??????
//        defect.setDescription(singleExecuteVo.getBugSummaryStr());
//        // bug????????????
//        defect.setDuedate(StringUtils.isBlank(singleExecuteVo.getBugTimeStr()) ? TimeUtil.getDateTimeStrByDay(null, JiraUtils.DEFAULT_DUE_DAYS) : singleExecuteVo.getBugTimeStr());
//        // ?????????
//        defect.setPriority(singleExecuteVo.getBugLevelStr());
//        //??????bug????????????
//        List<String> depts = Arrays.asList(singleExecuteVo.getBugDeptStr());
//        defect.setCustomfield_10212(depts);
//        //??????bug????????????
//        List<String> centers = new ArrayList<>();
//        centers.add(singleExecuteVo.getBugCenterStr());
//        defect.setCustomfield_10222(centers);
//        //????????????
//        List<String> components = new ArrayList<>();
//        components.add(singleExecuteVo.getBugModuleStr());
//        defect.setComponents(components);
//        //???????????????
//        List<String> forms = new ArrayList<>();
//        forms.add(singleExecuteVo.getBugEndTypeStr());
//        defect.setCustomfield_10508(forms);
//        // ??????????????????
//        List<String> types = new ArrayList<>();
//        types.add(singleExecuteVo.getBugTypeStr());
//        defect.setCustomfield_10113(types);
//
////----------------------------------------------------------------------------------------------------
//
//        try {
//            return JiraUtils.createBugByDefectBuss(defect);
//        } catch (JiraException e) {
//            log.error("createBugByDefende??????bug???????????????issue === {}", testTask.getRelationRequirement(), e);
//            throw new CustomException(e.getMessage());
//        }
    }

    /**
     * ??????jira??????
     *
     * @param jiraId
     */
//    public void changeJiraStatus(String jiraId) {
//        Issue issue = JiraUtils.getJiraIssueByIssueKey(jiraId);
//        if (issue != null) {
//            try {
//                log.info("changeJiraStatus??????jiraId == {} ????????? == {}", jiraId, issue.getStatus().getName());
//                if ("?????????".equals(issue.getStatus().getName())) {
//                    try {
//                        issue.transition().execute("??????");
//                        log.info("changeJiraStatus??????jiraId == {} ?????????", jiraId);
//                    } catch (JiraException e) {
//                        issue.transition().execute("????????????");
//                        log.info("changeJiraStatus??????jiraId == {} ???????????????", jiraId);
//                    }
//                }
//            } catch (JiraException e) {
//                log.error("changeJiraStatus??????jiraId == {} ????????? == {}??????????????????", jiraId, issue.getStatus().getName(), e);
//            }
//        }
//    }

    /**
     * ????????????????????????
     *
     * @param task
     */
    private void checkTemplateTask(TestTask task) {
        if (task.getTemplateFlag()) {
            throw new CustomException("???????????????????????????????????????");
        }
    }
}
