package com.ziroom.qa.quality.defende.provider.execTask.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ziroom.qa.quality.defende.provider.caseRepository.entity.TestCase;
import com.ziroom.qa.quality.defende.provider.caseRepository.service.TestCaseService;
import com.ziroom.qa.quality.defende.provider.constant.TestCenterConstants;
import com.ziroom.qa.quality.defende.provider.constant.enums.*;
import com.ziroom.qa.quality.defende.provider.entity.TopicTaskRel;
import com.ziroom.qa.quality.defende.provider.entity.User;
import com.ziroom.qa.quality.defende.provider.execTask.entity.TaskTestCase;
import com.ziroom.qa.quality.defende.provider.execTask.entity.TestExecution;
import com.ziroom.qa.quality.defende.provider.execTask.entity.TestTask;
import com.ziroom.qa.quality.defende.provider.execTask.entity.dto.ArkAppDTO;
import com.ziroom.qa.quality.defende.provider.execTask.entity.dto.ComplateAndEmailDTO;
import com.ziroom.qa.quality.defende.provider.execTask.entity.vo.ComplateAndEmailVO;
import com.ziroom.qa.quality.defende.provider.execTask.mapper.TestTaskMapper;
import com.ziroom.qa.quality.defende.provider.execTask.service.TaskTestCaseService;
import com.ziroom.qa.quality.defende.provider.execTask.service.TestExecutionService;
import com.ziroom.qa.quality.defende.provider.execTask.service.TestTaskService;
import com.ziroom.qa.quality.defende.provider.outinterface.client.service.EhrService;
import com.ziroom.qa.quality.defende.provider.outinterface.client.service.MatrixService;
import com.ziroom.qa.quality.defende.provider.result.CustomException;
import com.ziroom.qa.quality.defende.provider.result.RestResultVo;
import com.ziroom.qa.quality.defende.provider.service.SendMailService;
import com.ziroom.qa.quality.defende.provider.service.TopicTaskRelService;
import com.ziroom.qa.quality.defende.provider.service.UserService;
import com.ziroom.qa.quality.defende.provider.vo.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.mail.MessagingException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Slf4j
@Service
public class TestTaskServiceImpl extends ServiceImpl<TestTaskMapper, TestTask> implements TestTaskService {

    private final static String RUN_FLAG = "run";

    @Autowired
    private EhrService ehrService;
    @Autowired
    private TaskTestCaseService taskTestCaseService;
    @Autowired
    private TestExecutionService testExecutionService;
    @Autowired
    private UserService userService;
    @Autowired
    private TestCaseService testCaseService;
    @Autowired
    private TopicTaskRelService topicTaskRelService;
    @Autowired
    private MatrixService matrixService;
    @Autowired
    private SendMailService sendMailService;

    @Value("${quality.testTaskUrl}")
    private String testTaskUrl;

    /**
     * @param testTask
     * @param userName
     */
    @Override
    @Transactional
    public long saveOrUpdateTestTask(TestTask testTask, String userName) {
        //1 ????????????
        //1.1 ????????????????????????????????????
        this.validateTestTaskName(testTask.getTaskName(), testTask.getId());
        //2. ??????jiraid
//        TestResultVo jiraRes = JiraUtils.validateJiraInfo(testTask.getRelationRequirement());
//        if (!jiraRes.getFlag()) {
//            throw new CustomException(jiraRes.getMsgRes());
//        }
        String jiraId = testTask.getRelationRequirement();
        //3. ??????ehr???????????????
        String ehrTreePath = testTask.getEhrTreePath();
        if (StringUtils.isNotBlank(ehrTreePath)) {
            String deptId = ehrTreePath.substring(ehrTreePath.lastIndexOf(",") + 1);
            EhrDeptInfo ehrDeptInfo = ehrService.getDeptInfoFromEhrByDeptId(deptId);
            testTask.setEhrGroup(ehrDeptInfo.getDescrShort());
        } else {
            try {
                MatrixUserDetail detail = matrixService.getUserDetailByEmailPre(Arrays.asList(userName)).get(userName);
                testTask.setEhrTreePath(detail.getTreePath().substring(7).replace("-", ","));
                testTask.setEhrGroup(detail.getDeptName());
            } catch (Exception e) {
                log.error("saveOrUpdateTestTask ??????ehr???????????????????????????????????????", e);
            }
        }
        if (Objects.isNull(testTask.getId())) {
            testTask.setCreateUser(userName);
            testTask.setCreateTime(LocalDateTime.now());
            //????????????????????????????????????
            if (StringUtils.isBlank(testTask.getStatus())) {
                testTask.setStatus(TestTaskCaseStatusEnum.NOT_STARTED.getTestsTaskStatus());
            }
        } else {
            TestTask oldTestTask = super.getById(testTask.getId());
            if (TestTaskStatusEnum.COMPLETE.getTestsTaskStatus().equals(oldTestTask.getStatus())) {
                throw new CustomException("??????????????????????????????????????????????????????????????????????????????<??????>??????");
            } else if (TestTaskStatusEnum.LAUNCH.getTestsTaskStatus().equals(oldTestTask.getStatus())) {
                throw new CustomException("?????????????????????????????????????????????????????????");
            }
            if (!testTask.getTestExecutionType().equals(oldTestTask.getTestExecutionType())
                    && TestTaskStatusEnum.NOT_STARTED.getTestsTaskStatus().equals(oldTestTask.getStatus())) {
                taskTestCaseService.batchDeleteTaskTestCase(TaskTestCaseVo.builder().taskId(oldTestTask.getId()).build());
            }
        }
        testTask.setRelationRequirement(jiraId);
        // ???????????????parentJiraId
        if (StringUtils.isBlank(testTask.getParentJiraId())) {
            testTask.setParentJiraId(jiraId);
        }
        //3. ????????????????????????
        String testTaskMaster = testTask.getTestTaskMaster();
        String testTaskExecutors = testTask.getTestTaskExecutors();
        if (StringUtils.isBlank(testTaskMaster)) {
            testTask.setTestTaskMaster(userName);
        }
        if (StringUtils.isBlank(testTaskExecutors)) {
            testTask.setTestTaskExecutors(userName);
        }
        testTask.setUpdateUser(userName);
        testTask.setUpdateTime(LocalDateTime.now());
        if (StringUtils.isBlank(testTask.getEndType())) {
            testTask.setEndType("Service");
        }
        return this.saveTaskInfo(testTask);
    }

    @Override
    public void validateTestTaskName(String taskName, Long taskId) {
        if (StringUtils.isNotBlank(taskName)) {
            QueryWrapper<TestTask> testTaskQueryWrapper = new QueryWrapper<>();
            testTaskQueryWrapper.eq("task_name", taskName);
            testTaskQueryWrapper.notIn(Objects.nonNull(taskId), "id", taskId);
            List<TestTask> testTaskList = super.list(testTaskQueryWrapper);
            if (CollectionUtils.isNotEmpty(testTaskList)) {
                throw new CustomException("?????????????????????????????????:" + taskName + "???????????????");
            }
        } else {
            throw new CustomException("?????????????????????????????????");
        }
    }


    @Transactional
    @Override
    public TestResultVo batchDeleteTestTask(List<Long> idList, String userName) {
        if (CollectionUtils.isEmpty(idList)) {
            return TestResultVo.builder().flag(false).msgRes(TestCenterConstants.RES_MSG_PARAMS_EMPTY).build();
        }
        List<TestTask> taskList = super.listByIds(idList);
        if (CollectionUtils.isEmpty(taskList)) {
            throw new CustomException("???????????????????????????");
        }
        // ????????????????????????????????????????????????case??????????????????????????????
        if (UserRoleEnum.ROLE_SUPER_ADMIN.getName().equals(userService.getUserInfoByUserName(userName).getRole())) {
            QueryWrapper<TaskTestCase> taskTestCaseQueryWrapper = new QueryWrapper<>();
            taskTestCaseQueryWrapper.in("task_id", idList);
            taskTestCaseService.remove(taskTestCaseQueryWrapper);
            QueryWrapper<TestExecution> executionQueryWrapper = new QueryWrapper<>();
            executionQueryWrapper.in("execution_task_id", idList);
            testExecutionService.remove(executionQueryWrapper);
            super.removeByIds(idList);
            return TestResultVo.builder().flag(true).msgRes("??????????????????????????????????????????").build();
        }


        taskList.stream().forEach(task -> {
            if (TestTaskStatusEnum.COMPLETE.getTestsTaskStatus().equals(task.getStatus())) {
                throw new CustomException("???????????????????????????????????????????????????");
            } else if (TestTaskStatusEnum.LAUNCH.getTestsTaskStatus().equals(task.getStatus())) {
                throw new CustomException("???????????????????????????????????????????????????");
            } else if (TestTaskStatusEnum.SUBMITED.getTestsTaskStatus().equals(task.getStatus())) {
                throw new CustomException("???????????????????????????????????????????????????");
            } else if (!userName.equals(task.getCreateUser()) && !userName.equals(task.getTestTaskMaster())) {
                throw new CustomException("?????????????????????????????????????????????");
            }
        });
        //1. ??????????????????????????????
        List relList = topicTaskRelService.findByTaskIds(idList);
        if (CollectionUtils.isNotEmpty(relList)) {
            return TestResultVo.builder().flag(false).msgRes(TestCenterConstants.RES_MSG_RELATION).build();
        }
        //2. ?????????????????????????????????
        QueryWrapper<TaskTestCase> taskTestCaseQueryWrapper = new QueryWrapper<>();
        taskTestCaseQueryWrapper.in("task_id", idList);
        taskTestCaseService.remove(taskTestCaseQueryWrapper);
        //3. ????????????????????????
        super.removeByIds(idList);
        return TestResultVo.builder().flag(true).msgRes(TestCenterConstants.RES_MSG_SUCCESS).build();
    }


    @Override
    public RestResultVo<Page<TaskTestCase>> getTestReportCaseListByTaskId(Page page, Long taskId, String type) {
        RestResultVo result = RestResultVo.fromData(null);
        if (Objects.isNull(taskId) || taskId <= 0) {
            return result;
        }

        String valueOne = null;
        String valueTwo = null;
        if (TestTaskCaseStatusEnum.NOT_STARTED.getTestsTaskStatus().equals(type)) {
            valueOne = TestTaskCaseStatusEnum.NOT_STARTED.getTestsTaskStatus();
        } else if (TestTaskCaseStatusEnum.SKIP.getTestsTaskStatus().equals(type)) {
            valueOne = TestTaskCaseStatusEnum.SKIP.getTestsTaskStatus();
        } else if (RUN_FLAG.equals(type)) {
            valueOne = TestTaskCaseStatusEnum.PASS.getTestsTaskStatus();
            valueTwo = TestTaskCaseStatusEnum.FAIL.getTestsTaskStatus();
        }
        Page<TaskTestCase> resultPage;
        List<String> values = new ArrayList<>();
        if (StringUtils.isNotBlank(valueOne)) {
            values.add(valueOne);
        }
        if (StringUtils.isNotBlank(valueTwo)) {
            values.add(valueTwo);
        }
        if (CollectionUtils.isEmpty(values)) {
            return result;
        }
        QueryWrapper<TaskTestCase> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("task_id", taskId);
        queryWrapper.in("execution_result", values);

        resultPage = taskTestCaseService.page(page, queryWrapper);

        List<TaskTestCase> taskTestCaseList = resultPage.getRecords();
        taskTestCaseList.stream().parallel().forEach(testCase -> {
            TestCase testCaseOne = testCaseService.getTestCaseById(testCase.getTestCaseId());
            testCase.setTestCaseLevel(testCaseOne.getTestCaseLevelStr());
            testCase.setType(testCaseOne.getTypeStr());
        });

        return RestResultVo.fromData(resultPage);
    }

    @Override
    public RestResultVo getTestReportBugListByTaskId(Page page, Long taskId) {

        QueryWrapper<TestExecution> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("execution_task_id", taskId);
        queryWrapper.isNotNull("relation_bug");
        queryWrapper.orderByDesc("id");

        Page<TestExecution> executeVoPage = testExecutionService.page(page, queryWrapper);
        List<TestExecution> executionList = executeVoPage.getRecords();

        Page<TestReportBugListVo> reportBugListVoPage = new Page<>();
        List<TestReportBugListVo> reportBugListVoList = new ArrayList<>();

        executionList.stream().parallel().forEach(oneExecution -> {

            TestReportBugListVo oneReport = new TestReportBugListVo();
            String issueKey = oneExecution.getRelationBug();

            try {
//                Issue issue = JiraUtils.getJiraIssueByIssueKey(issueKey);
//                String summary = issue.getSummary();
//                oneReport.setIssueKey(issueKey);
//                oneReport.setDescription(summary);
//                // ??????bug????????????
//                oneReport.setBugStatus(Objects.isNull(issue.getStatus().getId()) ? "" : BugStatusEnum.getStatusValueByKey(Long.valueOf(issue.getStatus().getId())));
//                // ??????bug???????????????
//                oneReport.setPriority(Objects.isNull(issue.getPriority().getId()) ? "" : BugLevelEnum.getLevelValueByKey(Long.valueOf(issue.getPriority().getId())));
//                oneReport.setExecutionVersion(oneExecution.getExecutionVersion());
//                List<EhrUserDetail> userList = matrixService.getEhrUserDetailLikeUserName(oneExecution.getExecutionUser());
//                oneReport.setExecutorName(CollectionUtils.isNotEmpty(userList) ? userList.get(0).getName() : "");
//                Object obj = issue.getField("customfield_10508");
//                oneReport.setBugEndType(Objects.isNull(obj) ? "" : JSON.parseObject(obj.toString()).getString("id"));
//                oneReport.setBugEndTypeStr(Objects.isNull(obj) ? "" : JSON.parseObject(obj.toString()).getString("value"));
            } catch (Exception e) {
                oneReport.setDescription("??????jira bug???????????????????????? jira issue key");
                log.error("????????? issueKey ??????{}", issueKey);
            }
            reportBugListVoList.add(oneReport);
        });
        reportBugListVoPage.setRecords(reportBugListVoList);
        reportBugListVoPage.setTotal(executeVoPage.getTotal());

        return RestResultVo.fromData(reportBugListVoPage);
    }

    /**
     * ????????????????????????
     *
     * @return
     */
    @Override
    public boolean updateTestTaskUserInfo() {
        List<TestTask> testTaskList = this.list();
        testTaskList.stream().forEach(testTask -> {
            String createUserUid = testTask.getCreateUser();
            if (StringUtils.isNotBlank(createUserUid)) {
                User createUser = userService.getUserInfoByUid(createUserUid);
                testTask.setCreateUser(createUser.getUserName());
            }
            String updateUserUid = testTask.getUpdateUser();
            if (StringUtils.isNotBlank(updateUserUid)) {
                User updateUser = userService.getUserInfoByUid(updateUserUid);
                testTask.setUpdateUser(updateUser.getUserName());
            }
        });
        return this.updateBatchById(testTaskList);
    }

    /**
     * ????????????
     *
     * @param page
     * @param testTask
     * @param userName
     * @return
     */
    @Override
    public Page<TestTask> queryTestTaskByPage(Page<TestTask> page, TestTask testTask, String userName) {
        //1 ??????????????????
        QueryWrapper queryWrapper = this.getTestTaskQueryWrapper(testTask);
        //2 ????????????
        if (Objects.nonNull(testTask.getTestTopicId())) {
            List<TopicTaskRel> relList = topicTaskRelService.findByTopicIds(Arrays.asList(testTask.getTestTopicId()));
            if (CollectionUtils.isEmpty(relList)) {
                return new Page<>();
            } else {
                List<Long> taskList = relList.stream().map(TopicTaskRel::getTaskId).collect(Collectors.toList());
                queryWrapper.in("id", taskList);
            }
        }
        page = super.page(page, queryWrapper);
        if (Objects.nonNull(page) && CollectionUtils.isNotEmpty(page.getRecords())) {
            List<String> emailPreList = this.getEmailPreByList(page.getRecords());
            Map<String, MatrixUserDetail> userMap = matrixService.getUserDetailByEmailPre(emailPreList);
            page.getRecords().stream().forEach(task -> {
                // ??????????????????
                task.setTestExecutionTypeStr(TestExecutionTypeEnum.getTestExeMethodMap().get(task.getTestExecutionType()));
                task.setTestTaskMasterStr(Optional.ofNullable(userMap)
                        .map(item -> item.get(task.getTestTaskMaster()))
                        .map(detail -> detail.getUserName())
                        .orElse(""));
                task.setCreateUserStr(Optional.ofNullable(userMap)
                        .map(item -> item.get(task.getCreateUser()))
                        .map(detail -> detail.getUserName())
                        .orElse(""));
                task.setStatusStr(TestTaskStatusEnum.getStatusNameByStatus(task.getStatus()));
                task.setTaskTypeStr(TestTaskTypeEnum.getTypeNameByType(task.getTaskType()));


                if (StringUtils.isNotBlank(task.getTestTaskExecutors())) {
                    String executors = "";
                    for (String emailPre : task.getTestTaskExecutors().split(",")) {
                        String myUserName = Optional.ofNullable(userMap)
                                .map(item -> item.get(emailPre))
                                .map(detail -> detail.getUserName())
                                .orElse("");
                        if (StringUtils.isNotBlank(myUserName)) {
                            executors += myUserName + " ";
                        }
                    }
                    List<TopicTaskRel> taskRels = topicTaskRelService.findByTaskIds(Arrays.asList(task.getId()));
                    task.setTestTopicId(CollectionUtils.isEmpty(taskRels) ? null : taskRels.get(0).getTopicId());
                    task.setTestTaskExecutorsStr(executors);
                }
                // ??????????????????
                if (!userName.equals(task.getCreateUser()) && !userName.equals(task.getTestTaskMaster()) && !userName.contains(task.getTestTaskExecutors())) {
                    task.setDelButtonFlag(false);
                    task.setEditButtonFlag(false);
                    task.setRestartButtonFlag(false);
                }
                // ??????????????????????????????
                if (TestTaskStatusEnum.COMPLETE.getTestsTaskStatus().equals(task.getStatus())) {
                    task.setCompleteButtonFlag(false);
                    task.setDelButtonFlag(false);
                    task.setRunButtonFlag(false);
                    task.setEditButtonFlag(false);
                } else if (TestTaskStatusEnum.LAUNCH.getTestsTaskStatus().equals(task.getStatus())) {
                    task.setCompleteButtonFlag(false);
                    task.setDelButtonFlag(false);
                    task.setRunButtonFlag(false);
                    task.setEditButtonFlag(false);
                    task.setRestartButtonFlag(false);
                } else if (TestTaskStatusEnum.NOT_STARTED.getTestsTaskStatus().equals(task.getStatus())
                        || TestTaskStatusEnum.SUBMITED.getTestsTaskStatus().equals(task.getStatus())) {
                    task.setCompleteButtonFlag(false);
                    task.setRestartButtonFlag(false);
                } else {
                    task.setDelButtonFlag(false);
                    task.setRestartButtonFlag(false);
                }
                // ???????????????????????????????????????
                if (!task.isDelButtonFlag() && UserRoleEnum.ROLE_SUPER_ADMIN.getName().equals(userService.getUserInfoByUserName(userName).getRole())) {
                    task.setDelButtonFlag(true);
                    task.setEditButtonFlag(true);
                }
                if (task.getTemplateFlag()) {
                    task.setCompleteButtonFlag(false);
                    task.setRestartButtonFlag(false);
                }
            });
        }
        return page;
    }


    @Override
    public TestTask getTestTaskDetailById(Long id) {
        TestTask task = super.getById(id);
        if (Objects.isNull(task)) {
            return null;
        }
        List<TopicTaskRel> taskRels = topicTaskRelService.findByTaskIds(Arrays.asList(task.getId()));
        task.setTestTopicId(CollectionUtils.isEmpty(taskRels) ? null : taskRels.get(0).getTopicId());
        return task;
    }

    @Override
    public TestResultVo completeTaskStatus(List<Long> idList, String userName) {
        if (CollectionUtils.isEmpty(idList) || StringUtils.isBlank(userName)) {
            return TestResultVo.builder().flag(false).msgRes(TestCenterConstants.RES_MSG_PARAMS_EMPTY).build();
        }
        List<TestTask> list = super.listByIds(idList);
        if (CollectionUtils.isEmpty(list)) {
            return TestResultVo.builder().flag(false).msgRes(TestCenterConstants.RES_MSG_APPROVED_EMPTY).build();
        }
        list.stream().forEach(testTask -> {
            if (TestTaskStatusEnum.RUNNING.getTestsTaskStatus().equals(testTask.getStatus())) {
                testTask.setStatus(TestTaskStatusEnum.COMPLETE.getTestsTaskStatus());
            } else {
                throw new CustomException("???????????????????????????????????????????????????????????????????????????");
            }
        });
        super.updateBatchById(list);
        return TestResultVo.builder().flag(true).msgRes(TestCenterConstants.RES_MSG_SUCCESS).build();
    }

    @Override
    public TestResultVo restartTaskStatus(List<Long> idList, String userName) {
        if (CollectionUtils.isEmpty(idList) || StringUtils.isBlank(userName)) {
            return TestResultVo.builder().flag(false).msgRes(TestCenterConstants.RES_MSG_PARAMS_EMPTY).build();
        }
        List<TestTask> list = super.listByIds(idList);
        if (CollectionUtils.isEmpty(list)) {
            return TestResultVo.builder().flag(false).msgRes(TestCenterConstants.RES_MSG_APPROVED_EMPTY).build();
        }
        list.stream().forEach(testTask -> {
            if (TestTaskStatusEnum.COMPLETE.getTestsTaskStatus().equals(testTask.getStatus())
                    && (userName.equals(testTask.getCreateUser()) || userName.equals(testTask.getTestTaskMaster()))) {
                testTask.setStatus(TestTaskStatusEnum.RUNNING.getTestsTaskStatus());
            } else {
                throw new CustomException("???????????????????????????????????????????????????????????????????????????");
            }
        });
        super.updateBatchById(list);
        return TestResultVo.builder().flag(true).msgRes(TestCenterConstants.RES_MSG_SUCCESS).build();
    }

    /**
     * ??????appid????????????
     *
     * @return
     */
    @Override
    public List<ArkAppDTO> getAppInfoList() {
//        RestResultVo<List<ArkAppDTO>> res = arkApiClient.getAllApp();
//        if (Objects.nonNull(res)) {
//            return res.getData();
//        }
        return new ArrayList<>();
    }

    @Override
    public List<TestTask> findByIssueKey(String issueKey) {
        if (StringUtils.isBlank(issueKey)) {
            return null;
        }
        QueryWrapper<TestTask> testTaskQueryWrapper = new QueryWrapper<>();
        testTaskQueryWrapper.eq("relation_requirement", issueKey);
        return super.list(testTaskQueryWrapper);
    }

    @Override
    public void completeTaskAndEmail(ComplateAndEmailVO complateAndEmailVO, String userName) {
        if (StringUtils.isBlank(complateAndEmailVO.getSender())) {
            throw new CustomException("??????????????????????????????");
        }
        if (StringUtils.isBlank(complateAndEmailVO.getAddressee())) {
            throw new CustomException("??????????????????????????????");
        }
        if (StringUtils.isBlank(complateAndEmailVO.getCcUser())) {
            throw new CustomException("??????????????????????????????");
        }
        if (StringUtils.isBlank(complateAndEmailVO.getEmailContent())) {
            throw new CustomException("?????????????????????????????????");
        }

        if (Objects.isNull(complateAndEmailVO.getTaskId()) || StringUtils.isBlank(userName)) {
            throw new CustomException("??????????????????id???????????????");
        }
        TestTask testTask = super.getById(complateAndEmailVO.getTaskId());
        if (Objects.isNull(testTask)) {
            throw new CustomException("????????????????????????????????????");
        }
        // ????????????????????????
        if (complateAndEmailVO.isComplateAndEmailFlag()) {
            if (TestTaskStatusEnum.RUNNING.getTestsTaskStatus().equals(testTask.getStatus())) {
                testTask.setStatus(TestTaskStatusEnum.COMPLETE.getTestsTaskStatus());
                testTask.setUpdateUser(userName);
                testTask.setUpdateTime(LocalDateTime.now());
            } else {
                throw new CustomException("???????????????????????????????????????????????????????????????????????????");
            }
            super.updateById(testTask);
        }
        // ????????????????????????
        complateAndEmailVO.setTestTask(testTask);
        this.sendTaskReportEmail(complateAndEmailVO);
    }

    /**
     * ????????????????????????
     *
     * @param vo
     */
    private void sendTaskReportEmail(ComplateAndEmailVO vo) {
        ComplateAndEmailDTO complateAndEmailDTO = new ComplateAndEmailDTO();
        complateAndEmailDTO.setTaskName(vo.getTestTask().getTaskName());
        complateAndEmailDTO.setEmailContent(vo.getEmailContent());
        complateAndEmailDTO.setAddressee(vo.getAddressee());
        complateAndEmailDTO.setSender(vo.getSender());
        complateAndEmailDTO.setCcUser(vo.getCcUser());
        complateAndEmailDTO.setTestTaskUrl(testTaskUrl + "&testExecutionType=1&taskId=" + vo.getTaskId());

//        1.??????????????????????????????
        List<TaskTestCase> testCaseList = taskTestCaseService.getTaskTestCaseByTaskId(vo.getTaskId());
        complateAndEmailDTO.setCaseNameList(testCaseList.stream().map(TaskTestCase::getCasename).collect(Collectors.toList()));
//        2.????????????bug??????
        List<TestExecution> testExecutionList = testExecutionService.getBugTestExecutionByTaskId(vo.getTaskId());
        if (CollectionUtils.isNotEmpty(testExecutionList)) {
            complateAndEmailDTO.setBugCount(testExecutionList.size());
            AtomicInteger p1p2bugCount = new AtomicInteger();
            //?????????p1p2??????bug?????????bug???+1
            testExecutionList.stream().forEach(te -> {
                try {
//                    Issue issue = JiraUtils.getJiraIssueByIssueKey(te.getRelationBug());
//                    if (Objects.nonNull(issue)) {
//                        if (Long.valueOf(issue.getPriority().getId()) < BugLevelEnum.P2.getLevelId()) {
//                            p1p2bugCount.getAndIncrement();
//                        }
//                    }
                } catch (Exception e) {
                    log.error("??????jira??????????????????", e);
                }
            });
            complateAndEmailDTO.setP1p2BugCount(p1p2bugCount.get());
        }
//        ??????????????????: 21 ??????????????????: 80.95%???????????????: 19.05%???????????????: 0.00%
        vo.getTestTask().getTestcaseCount();
        vo.getTestTask().getRunSuccessCount();
        vo.getTestTask().getRunFailCount();
        vo.getTestTask().getRunSkipCount();
        // ?????????
        double successRate = BigDecimal.valueOf(vo.getTestTask().getRunSuccessCount())
                .divide(BigDecimal.valueOf(vo.getTestTask().getTestcaseCount()), 4, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100)).doubleValue();
        // ?????????
        double failRate = BigDecimal.valueOf(vo.getTestTask().getRunFailCount())
                .divide(BigDecimal.valueOf(vo.getTestTask().getTestcaseCount()), 4, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100)).doubleValue();
        // ?????????
        double skipRate = BigDecimal.valueOf(vo.getTestTask().getRunSkipCount())
                .divide(BigDecimal.valueOf(vo.getTestTask().getTestcaseCount()), 4, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100)).doubleValue();
        complateAndEmailDTO.setTestTaskDetail("??????????????????: " + vo.getTestTask().getTestcaseCount() + "???" +
                "???????????????: " + successRate + "%" +
                "???????????????: " + failRate + "%" +
                "???????????????: " + skipRate + "%");
//        3.????????????????????????
        try {
            sendMailService.sendTaskReportEmail(complateAndEmailDTO);
            log.info("?????????????????????jiraId=={}???taskName == {}", vo.getTestTask().getRelationRequirement(), vo.getTestTask().getTaskName());
        } catch (MessagingException e) {
            log.error("??????????????????????????????jiraId=={}???taskName == {}", vo.getTestTask().getRelationRequirement(), vo.getTestTask().getTaskName(), e);
        }

    }

    /**
     * ?????????????????????????????????
     *
     * @param taskList
     * @return
     */
    private List<String> getEmailPreByList(List<TestTask> taskList) {
        List<String> emailPreList = new ArrayList<>();
        taskList.stream().forEach(task -> {
            if (StringUtils.isNotBlank(task.getTestTaskMaster())) {
                emailPreList.add(task.getTestTaskMaster());
            }
            if (StringUtils.isNotBlank(task.getCreateUser())) {
                emailPreList.add(task.getCreateUser());
            }
            if (StringUtils.isNotBlank(task.getTestTaskExecutors())) {
                String[] strs = task.getTestTaskExecutors().split(",");
                emailPreList.addAll(Arrays.asList(strs));
            }
        });
        if (CollectionUtils.isEmpty(emailPreList)) {
            return emailPreList;
        }
        return emailPreList.stream().distinct().collect(Collectors.toList());
    }


    private QueryWrapper<TestTask> getTestTaskQueryWrapper(TestTask testTask) {
        QueryWrapper<TestTask> testTaskQueryWrapper = new QueryWrapper<>();
        testTaskQueryWrapper
                .eq(Objects.nonNull(testTask.getTestExecutionType()), "test_execution_type", testTask.getTestExecutionType())
                .like(StringUtils.isNotBlank(testTask.getTaskName()), "task_name", testTask.getTaskName())
                .eq(StringUtils.isNotBlank(testTask.getStatus()), "status", testTask.getStatus())
                .eq(StringUtils.isNotBlank(testTask.getTaskType()), "task_type", testTask.getTaskType())
                .like(StringUtils.isNotBlank(testTask.getEhrTreePath()), "ehr_tree_path", testTask.getEhrTreePath())
                .eq(StringUtils.isNotBlank(testTask.getTestTaskMaster()), "test_task_master", testTask.getTestTaskMaster())
                .like(StringUtils.isNotBlank(testTask.getRelationRequirement()), "relation_requirement", testTask.getRelationRequirement())
                .eq(Objects.nonNull(testTask.getTemplateFlag()), "template_flag", testTask.getTemplateFlag())
        ;
        testTaskQueryWrapper.orderByDesc("id");
        return testTaskQueryWrapper;
    }

    /**
     * ??????????????????
     *
     * @param testTask
     * @return
     */
    private long saveTaskInfo(TestTask testTask) {
        boolean saveResult = this.saveOrUpdate(testTask);
        if (saveResult) {
            //???????????????????????????id
            topicTaskRelService.taskRelTopic(testTask.getTestTopicId(), testTask.getId(), testTask.getUpdateUser());
            return testTask.getId();
        } else {
            throw new CustomException("?????????????????????????????????");
        }
    }
}
