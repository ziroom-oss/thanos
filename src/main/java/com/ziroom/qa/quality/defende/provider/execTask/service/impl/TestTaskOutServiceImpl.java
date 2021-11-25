package com.ziroom.qa.quality.defende.provider.execTask.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.ziroom.qa.quality.defende.provider.constant.TestCenterConstants;
import com.ziroom.qa.quality.defende.provider.constant.enums.*;
import com.ziroom.qa.quality.defende.provider.execTask.entity.TaskTestCase;
import com.ziroom.qa.quality.defende.provider.execTask.entity.TestExecution;
import com.ziroom.qa.quality.defende.provider.execTask.entity.TestTask;
import com.ziroom.qa.quality.defende.provider.execTask.service.TaskTestCaseService;
import com.ziroom.qa.quality.defende.provider.execTask.service.TestExecutionService;
import com.ziroom.qa.quality.defende.provider.execTask.service.TestTaskOutService;
import com.ziroom.qa.quality.defende.provider.execTask.service.TestTaskService;
import com.ziroom.qa.quality.defende.provider.outinterface.client.service.MatrixService;
import com.ziroom.qa.quality.defende.provider.outinterface.client.service.OmegaService;
import com.ziroom.qa.quality.defende.provider.outinterface.client.service.WeChatService;
import com.ziroom.qa.quality.defende.provider.result.CustomException;
import com.ziroom.qa.quality.defende.provider.service.SendMailService;
import com.ziroom.qa.quality.defende.provider.service.TestStatisticService;
import com.ziroom.qa.quality.defende.provider.util.MyUtil;
import com.ziroom.qa.quality.defende.provider.util.TimeUtil;
import com.ziroom.qa.quality.defende.provider.vo.MatrixUserDetail;
import com.ziroom.qa.quality.defende.provider.vo.TestReportBugListVo;
import com.ziroom.qa.quality.defende.provider.vo.TimeVo;
import com.ziroom.qa.quality.defende.provider.vo.outvo.OutReqVo;
import com.ziroom.qa.quality.defende.provider.vo.outvo.OutResVo;
import com.ziroom.qa.quality.defende.provider.vo.outvo.SendTaskMsgVo;
import com.ziroom.qa.quality.defende.provider.vo.statistics.TaskTopVo;
import lombok.extern.slf4j.Slf4j;
import net.rcarz.jiraclient.Issue;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class TestTaskOutServiceImpl implements TestTaskOutService {

    @Autowired
    private TestTaskService testTaskService;
    @Autowired
    private MatrixService matrixService;
    @Autowired
    private TestExecutionService testExecutionService;
    @Autowired
    private SendMailService sendMailService;
    @Autowired
    private WeChatService weChatService;
    @Autowired
    private TestStatisticService testStatisticService;
    @Autowired
    private TaskTestCaseService taskTestCaseService;
    @Autowired
    private OmegaService omegaService;

    @Value("${quality.testTaskUrl}")
    private String testTaskUrl;

    /**
     * 创建并获取测试执行信息
     *
     * @param outReqVo
     * @return
     */
    @Override
    public OutResVo outCreateOrGetTestTask(OutReqVo outReqVo) {
        // 发送消息标识，默认是发送（只有新增的时候才会发送）
        boolean sendMsgFlag = false;
        // 1.校验入参
        this.checkParamInfo(outReqVo, true);
        // 2.根据终端和jiraid查询测试执行任务信息
        TestTask testTask = this.getByParantJiraIdAndEndType(outReqVo);
        String checkTag = outReqVo.getCheckTag();
        // 判断新增
        if (Objects.isNull(testTask)) {
            sendMsgFlag = true;
            testTask = new TestTask();
            testTask.setCreateUser(outReqVo.getDevUserName());
            testTask.setCreateTime(LocalDateTime.now());
            testTask.setTaskType(TestTaskTypeEnum.FUNCTIONAL_TESTING.getTestsTaskType());
            testTask.setStatus(TestTaskStatusEnum.NOT_STARTED.getTestsTaskStatus());
            testTask.setUpdateUser(outReqVo.getDevUserName());
            testTask.setUpdateTime(LocalDateTime.now());
            // jira信息
            testTask.setParentJiraId(outReqVo.getJiraId());
            testTask.setRelationRequirement(outReqVo.getJiraId());
            // 终端类型
            testTask.setEndType(outReqVo.getEndType());
            // 负责人和执行人
            testTask.setTestTaskMaster(outReqVo.getTestUserName());
            testTask.setTestTaskExecutors(outReqVo.getTestUserName());
            // 状态和类型
            testTask.setTaskName(outReqVo.getTaskName());

            testTask.setAppId(outReqVo.getAppId());
            testTask.setBranchName(outReqVo.getBranchName());
            testTask.setCheckTag(checkTag);
            // 矩阵平台获取组织机构信息
            try {
                MatrixUserDetail detail = matrixService.getUserDetailByEmailPre(Arrays.asList(outReqVo.getDevUserName())).get(outReqVo.getDevUserName());
                testTask.setEhrTreePath(detail.getTreePath().substring(7).replace("-", ","));
                testTask.setEhrGroup(detail.getDeptName());
            } catch (Exception e) {
                log.error("TestTaskOutServiceImpl.outCreateOrGetTestTask 获取ehr信息失败，但是不影响使用！", e);
            }
            testTaskService.save(testTask);
        } else {
            List<String> branchList = new ArrayList<>();
            if (StringUtils.isNotBlank(testTask.getBranchName())) {
                branchList = Arrays.asList(testTask.getBranchName());
            }
            if (CollectionUtils.isNotEmpty(branchList) && !branchList.contains(outReqVo.getBranchName())) {
                branchList = new ArrayList<>(branchList);
                branchList.add(outReqVo.getBranchName());
                testTask.setBranchName(StringUtils.join(branchList.toArray(), ","));
                testTask.setUpdateUser(outReqVo.getDevUserName());
                testTask.setUpdateTime(LocalDateTime.now());
                testTask.setCheckTag(checkTag);
                testTaskService.updateById(testTask);
            }
        }
        OutResVo outResVo = OutResVo.builder().jiraId(outReqVo.getJiraId()).testTaskUrl(testTaskUrl + "&testExecutionType=1&taskId=" + testTask.getId()).testTask(testTask).build();
        // 如果是模板测试执行，则需要关联测试执行的用例
        if (StringUtils.isNotBlank(checkTag)) {
            outReqVo.setTestCaseResult(this.createTestCaseByTask(outReqVo.getAppId(), testTask));
        }
        // 发送测试执行创建完成的消息
        this.sendTestTaskInfo(outResVo.getTestTaskUrl(), outReqVo, sendMsgFlag);
        return outResVo;
    }

    /**
     * 获取测试执行报告信息
     *
     * @param outReqVo
     * @return
     */
    @Override
    public OutResVo getTestTaskDetailInfo(OutReqVo outReqVo) {
        // 1.校验入参
        this.checkParamInfo(outReqVo, false);
        // 2.根据终端和jiraid查询测试执行任务信息
        TestTask testTask = this.getByParantJiraIdAndEndType(outReqVo);
        if (Objects.isNull(testTask)) {
            throw new CustomException("查询该测试执行信息不存在！");
        }
        // 3.查询所有bug编号列表
        List<String> bugIdList = this.getBugListByTaskId(testTask.getId());
        List<TestReportBugListVo> bugList = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(bugIdList)) {
            bugIdList.stream().forEach(bugId -> {
                TestReportBugListVo bugVo = new TestReportBugListVo();
                try {
//                    Issue issue = JiraUtils.getJiraIssueByIssueKey(bugId);
//                    // 放入bug状态信息
//                    bugVo.setBugStatus(Objects.isNull(issue.getStatus().getId()) ? "" : BugStatusEnum.getStatusValueByKey(Long.valueOf(issue.getStatus().getId())));
//                    bugVo.setBugStatusId(Long.valueOf(issue.getStatus().getId()));
//                    // 放入bug优先级信息
//                    bugVo.setPriority(Objects.isNull(issue.getPriority().getId()) ? "" : BugLevelEnum.getLevelValueByKey(Long.valueOf(issue.getPriority().getId())));
//                    bugVo.setPriorityId(Long.valueOf(issue.getPriority().getId()));
                    // 放入bug终端类型信息
//                    Object obj = issue.getField("customfield_10508");
//                    bugVo.setBugEndType(Objects.isNull(obj) ? "" : JSON.parseObject(obj.toString()).getString("id"));
//                    bugVo.setBugEndTypeStr(Objects.isNull(obj) ? "" : JSON.parseObject(obj.toString()).getString("value"));
//                    bugVo.setDescription(issue.getSummary());
                } catch (Exception e) {
                    log.error("调用jira信息失败", e);
                }
                bugVo.setIssueKey(bugId);
                bugList.add(bugVo);
            });
        }
        return OutResVo.builder().jiraId(outReqVo.getJiraId()).testTaskUrl(testTaskUrl + "&testExecutionType=1&taskId=" + testTask.getId()).testTask(testTask).bugList(bugList).build();
    }

    /**
     * 提测完成（方舟系统回调接口）
     *
     * @param outReqVo
     * @return
     */
    @Override
    public void submitTestTaskByInfo(OutReqVo outReqVo) {
        // 1.校验入参
        this.checkParamInfo(outReqVo, false);
        // 2.根据终端和jiraid查询测试执行任务信息
        TestTask testTask = this.getByParantJiraIdAndEndType(outReqVo);
        if (Objects.isNull(testTask)) {
            throw new CustomException("查询该测试执行信息不存在！");
        }
        if (StringUtils.isBlank(outReqVo.getDevUserName())) {
            throw new CustomException("提测人员邮箱前缀不能为空！");
        }
        if (TestTaskStatusEnum.NOT_STARTED.getTestsTaskStatus().equals(testTask.getStatus())) {
            testTask.setStatus(TestTaskStatusEnum.SUBMITED.getTestsTaskStatus());
            testTask.setUpdateUser(outReqVo.getDevUserName());
            testTask.setUpdateTime(LocalDateTime.now());
            testTaskService.updateById(testTask);
        } else {
            throw new CustomException("只有未开始的测试执行才能提测,当前状态:" + TestTaskStatusEnum.getStatusNameByStatus(testTask.getStatus()));
        }
    }

    /**
     * 上线完成（方舟系统回调接口）
     *
     * @param outReqVo
     */
    @Override
    public void launchTestTaskByInfo(OutReqVo outReqVo) {
        // 1.校验入参
        this.checkParamInfo(outReqVo, false);
        // 2.根据终端和jiraid查询测试执行任务信息
        TestTask testTask = this.getByParantJiraIdAndEndType(outReqVo);
        if (Objects.isNull(testTask)) {
            throw new CustomException("查询该测试执行信息不存在！");
        }
        if (StringUtils.isBlank(outReqVo.getDevUserName())) {
            throw new CustomException("提测人员邮箱前缀不能为空！");
        }
        if (TestTaskStatusEnum.COMPLETE.getTestsTaskStatus().equals(testTask.getStatus())) {
            testTask.setStatus(TestTaskStatusEnum.LAUNCH.getTestsTaskStatus());
            testTask.setUpdateUser(outReqVo.getDevUserName());
            testTask.setUpdateTime(LocalDateTime.now());
            testTaskService.updateById(testTask);
        } else {
            throw new CustomException("只有完成的测试执行才能上线,当前状态:" + TestTaskStatusEnum.getStatusNameByStatus(testTask.getStatus()));
        }
    }

    /**
     * 创建自动的测试执行（zhujj 需要调用的接口）
     *
     * @param outReqVo
     */
    @Override
    public OutResVo createAutoTestExecution(OutReqVo outReqVo) {
        // 1.校验数据
        if (StringUtils.isBlank(outReqVo.getJiraId())) {
            throw new CustomException("JIRAID不能为空！");
        }
        if (CollectionUtils.isEmpty(outReqVo.getAutoRecordList())) {
            throw new CustomException("测试执行用例结果信息为空！");
        }
        if (StringUtils.isBlank(outReqVo.getTestUserName())) {
            throw new CustomException("测试执行人员邮箱前缀不能为空！");
        }
        if (StringUtils.isBlank(outReqVo.getTaskName())) {
            throw new CustomException("测试执行名称不能为空！");
        }
        // 2.创建执行任务
        TestTask testTask = this.createTaskInfo(outReqVo);
        // 3.批量保存测试执行结果
        long start = System.currentTimeMillis();
        testExecutionService.autoExetionTask(testTask.getId(), outReqVo.getTestUserName(), outReqVo.getAutoRecordList());
        long end = System.currentTimeMillis();
        log.info("createAutoTestExecution保存自动化测试执行时间为：" + (end - start) + " ms");
        OutResVo outResVo = OutResVo.builder().testTaskUrl(testTaskUrl + "&testExecutionType=2&taskId=" + testTask.getId()).build();
        return outResVo;
    }

    /**
     * 自动测试执行任务的用例数（相同应用）
     *
     * @param dateTimeStr
     */
    @Override
    @Async
    public void sendAutoTaskMsg(String dateTimeStr) {
        //1. 组织数据查询用例数
        TimeVo timeVo = TimeUtil.getDateTimeByStr(dateTimeStr, 1);
        // 自动用例执行次数
        List<TaskTopVo> taskCountList = testStatisticService.getAutoTaskTop(null, null);
        // 测试成功率
        List<TaskTopVo> taskRateList = testStatisticService.getTaskExeRate(null, null, TestExecutionTypeEnum.AUTO.getKey());
        // 自动用例数
        List<TaskTopVo> autoCaseTopList = testStatisticService.getAutoCaseTop(null, null);
        // 自动用例昨日新增数
        List<TaskTopVo> createList = testStatisticService.getAutoCaseTop(timeVo.getStart(), timeVo.getEnd());

        //2. 拼装应用对应的组名
        // 自动用例数
        List<TaskTopVo> autoCaseGroupList = this.getGroupListByAppList(autoCaseTopList);
        // 测试成功率
        List<TaskTopVo> taskRateGroupList = this.getGroupListByAppList(taskRateList);
        // 自动用例昨日新增数
        List<TaskTopVo> createGroupList = this.getGroupListByAppList(createList);
        // 自动用例执行次数
        List<TaskTopVo> taskCountGroupList = this.getGroupListByAppList(taskCountList);

        String autoStr = this.getStrByTaskTopVoList(autoCaseGroupList, false);
        String taskStr = this.getStrByTaskTopVoList(taskCountGroupList, false);
        String createStr = this.getStrByTaskTopVoList(createGroupList, false);
        String taskRateStr = this.getStrByTaskTopVoList(taskRateGroupList, true);
        //3. 发送企业微信消息
        weChatService.sendAutoCountMsg(SendTaskMsgVo.builder().exeCaseStr(autoStr).taskCountStr(taskStr).creCaseStr(createStr).taskRateStr(taskRateStr).build());
    }

    /**
     * jira创建测试执行
     *
     * @param issueMsg
     */
    @Override
    public void jiraCreateTestExe(String issueMsg) {
        if (StringUtils.isBlank(issueMsg)) {
            log.info("创建jira任务同步生成测试执行信息为空！！");
            return;
        }
        JSONObject result = JSONObject.parseObject(issueMsg);
        // 获取经办人
        String assignee = result.getJSONObject("issue").getJSONObject("fields").getJSONObject("assignee").get("name").toString();
        // jira标题
        String summary = result.getJSONObject("issue").getJSONObject("fields").get("summary").toString();
        // 获取jiraid
        String issueKey = result.getJSONObject("issue").get("key").toString();
        // 获取项目测试负责人
        String projectKey = issueKey.split("-")[0];
        String testLeader = JiraProjectTestLeaderEnum.getTestUserByProjectKey(projectKey);
        if (StringUtils.isBlank(testLeader)) {
            testLeader = assignee;
        }
        List<TestTask> testTaskList = testTaskService.findByIssueKey(issueKey);
        if (CollectionUtils.isNotEmpty(testTaskList)) {
            log.info("该jira任务已经存在，jiraid==={}", issueKey);
            return;
        }
        // 创建测试执行
        TestTask testTask = this.saveRequestInfo4Jira(testLeader, issueKey, summary);
        // 创建发送消息给对应的测试主管
        String url = testTaskUrl + "&testExecutionType=1&taskId=" + testTask.getId();
        String jiraUrl = "xxxx.xxx.xxx/" + "brower/" + issueKey;
        try {
            sendMailService.sendJiraTestTaskMail(testLeader, url, jiraUrl, summary);
        } catch (MessagingException e) {
            log.error("创建jira任务同步生成测试执行信息失败，但是不影响使用！", e);
        }
    }

    /**
     * 保存jira过来的测试执行任务
     *
     * @param testUser
     * @param issueKey
     * @param summary
     */
    private TestTask saveRequestInfo4Jira(String testUser, String issueKey, String summary) {
        TestTask testTask = new TestTask();
        testTask.setCreateUser(testUser);
        testTask.setCreateTime(LocalDateTime.now());
        testTask.setTaskType(TestTaskTypeEnum.FUNCTIONAL_TESTING.getTestsTaskType());
        testTask.setStatus(TestTaskStatusEnum.NOT_STARTED.getTestsTaskStatus());
        testTask.setUpdateUser(testUser);
        testTask.setUpdateTime(LocalDateTime.now());
        // jira信息
        testTask.setParentJiraId(issueKey);
        testTask.setRelationRequirement(issueKey);
        // 终端类型
        testTask.setEndType("Service");
        // 负责人和执行人
        testTask.setTestTaskMaster(testUser);
        testTask.setTestTaskExecutors(testUser);
        // 状态和类型
        testTask.setTaskName(summary);
        // 矩阵平台获取组织机构信息
        try {
            MatrixUserDetail detail = matrixService.getUserDetailByEmailPre(Arrays.asList(testUser)).get(testUser);
            testTask.setEhrTreePath(detail.getTreePath().substring(7).replace("-", ","));
            testTask.setEhrGroup(detail.getDeptName());
        } catch (Exception e) {
            log.error("获取ehr信息失败，但是不影响使用！", e);
        }
        testTaskService.save(testTask);
        return testTask;
    }

    /**
     * 获取当前用例内容
     *
     * @param list
     * @param flag 成功率内容标记
     * @return
     */
    private String getStrByTaskTopVoList(List<TaskTopVo> list, boolean flag) {
        StringBuilder sbcreate = new StringBuilder();
        list.stream().forEach(group -> {
            if (flag) {
                sbcreate.append(group.getGroupName() + "(" + group.getSucRateStr() + ")\n");
            } else {
                sbcreate.append(group.getGroupName() + "(" + group.getAppCount() + ")\n");
            }
        });
        return StringUtils.isBlank(sbcreate.toString()) ? "无\n" : sbcreate.toString();
    }

    /**
     * 根据应用集合组装成小组的集合
     *
     * @param taskTopVoList
     * @return
     */
    private List<TaskTopVo> getGroupListByAppList(List<TaskTopVo> taskTopVoList) {
        if (CollectionUtils.isEmpty(taskTopVoList)) {
            return new ArrayList<>();
        }
        taskTopVoList.stream().forEach(taskTopVo -> {
            String groupName = omegaService.getGroupNameByAppName(taskTopVo.getApplicationName());
            if (StringUtils.isNotBlank(groupName)) {
                taskTopVo.setGroupName(groupName);
            } else {
                taskTopVo.setGroupName("无匹配部门");
            }
            if (TestTaskCaseStatusEnum.PASS.getTestsTaskStatus().equals(taskTopVo.getExeRes())) {
                taskTopVo.setSuccCount(taskTopVo.getAppCount());
            }
        });
        log.info("getGroupListByAppList匹配部门：{}", JSON.toJSONString(taskTopVoList));
        Map<String, List<TaskTopVo>> groupMap = taskTopVoList.stream().collect(Collectors.groupingBy(TaskTopVo::getGroupName));
        List<TaskTopVo> groupList = new ArrayList<>();
        groupMap.keySet().forEach(groupKey -> {
            List<TaskTopVo> appList = groupMap.get(groupKey);
            // 总数
            int appCount = appList.stream().mapToInt(TaskTopVo::getAppCount).sum();
            // 成功数
            int succCount = appList.stream().mapToInt(TaskTopVo::getSuccCount).sum();
            TaskTopVo vo = new TaskTopVo();
            vo.setGroupName(groupKey);
            vo.setAppCount(appCount);
            vo.setSuccCount(succCount);
            BigDecimal succRate = BigDecimal.valueOf(vo.getSuccCount())
                    .divide(BigDecimal.valueOf(vo.getAppCount()), 4, RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(100));
            vo.setSucRateStr(succRate.doubleValue() + "%");
            groupList.add(vo);
        });
        // 倒序
        groupList.sort(Comparator.comparing(TaskTopVo::getAppCount).reversed());
        return groupList;
    }


    /**
     * 获取测试执行信息
     *
     * @param outReqVo
     * @return
     */
    private TestTask getByParantJiraIdAndEndType(OutReqVo outReqVo) {
        QueryWrapper<TestTask> queryWrapper = new QueryWrapper();
        queryWrapper.eq("end_type", outReqVo.getEndType());
        queryWrapper.eq("parent_jira_id", outReqVo.getJiraId());
        TestTask testTask = testTaskService.getOne(queryWrapper);
        return testTask;
    }

    /**
     * 获取bug编号集合
     *
     * @param taskId
     * @return
     */
    private List<String> getBugListByTaskId(Long taskId) {
        // 查询历史bug信息
        QueryWrapper<TestExecution> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("execution_task_id", taskId);
        queryWrapper.isNotNull("relation_bug");
        queryWrapper.eq("test_execution_type", TestExecutionTypeEnum.ARTIFICIAL.getKey());
        List<TestExecution> exeList = testExecutionService.list(queryWrapper);
        if (CollectionUtils.isEmpty(exeList)) {
            return null;
        }
        return exeList.stream().distinct().map(TestExecution::getRelationBug).collect(Collectors.toList());
    }

    /**
     * 校验测试执行信息
     *
     * @param outReqVo
     * @param checkFlag
     */
    private void checkParamInfo(OutReqVo outReqVo, boolean checkFlag) {

        if (checkFlag) {
            if (StringUtils.isBlank(outReqVo.getTaskName())) {
                throw new CustomException("测试执行名称不能为空！");
            }
            if (StringUtils.isBlank(outReqVo.getAppId())) {
                throw new CustomException("移动端APPID不能为空！");
            }
            if (StringUtils.isBlank(outReqVo.getBranchName())) {
                throw new CustomException("分支名称不能为空！");
            }
            if (StringUtils.isBlank(outReqVo.getDevUserName())) {
                throw new CustomException("开发人员邮箱前缀不能为空！");
            }
            if (StringUtils.isBlank(outReqVo.getTestUserName())) {
                throw new CustomException("测试人员邮箱前缀不能为空！");
            }
        }

        if (StringUtils.isBlank(outReqVo.getEndType())) {
            throw new CustomException("终端类型不能为空！");
        }
        if (StringUtils.isBlank(outReqVo.getJiraId())) {
            throw new CustomException("JIRAID不能为空！");
        }
    }


    /**
     * 发送测试执行创建完成的消息
     *
     * @param testTaskUrl
     * @param outReqVo
     * @param sendMsgFlag
     */
    private void sendTestTaskInfo(String testTaskUrl, OutReqVo outReqVo, boolean sendMsgFlag) {
        if (!sendMsgFlag) {
            log.info("发送测试执行创建完成的消息,测试执行名称：{}，jiraid：{}，已经创建过了！！！", outReqVo.getTaskName(), outReqVo.getJiraId());
            return;
        }
        String jiraUrl = "xxxx.xxx.xxx/" + "brower/" + outReqVo.getJiraId();
        Issue issue;
        String jiraDesc = "jira描述信息";
//        try {
//            issue = JiraUtils.getJiraIssueByIssueKey(outReqVo.getJiraId());
//            jiraDesc = issue.getSummary();
//        } catch (Exception e) {
//            log.error("sendTestTaskInfo获取jira明细信息失败，但是不影响使用！", e);
//        }

        List<String> userList = Arrays.asList(outReqVo.getDevUserName(), outReqVo.getTestUserName()).stream().distinct().collect(Collectors.toList());
        Map<String, MatrixUserDetail> userMap = matrixService.getUserDetailByEmailPre(userList);

        // 组合发送消息和邮件的对象
        SendTaskMsgVo sendTaskMsgVo = SendTaskMsgVo.builder()
                .taskName(outReqVo.getTaskName())
                .branchName(outReqVo.getBranchName())
                .devUserName(outReqVo.getDevUserName())
                .testUserName(outReqVo.getTestUserName())
                .endType(outReqVo.getEndType())
                .jiraId(outReqVo.getJiraId())
                .jiraUrl(jiraUrl)
                .jiraDesc(jiraDesc)
                .testTaskUrl(testTaskUrl)
                .devUserNameStr(userMap.get(outReqVo.getDevUserName()).getUserName())
                .testUserNameStr(userMap.get(outReqVo.getTestUserName()).getUserName())
                .testCaseResult(outReqVo.getTestCaseResult())
                .build();
        try {
            // 发送邮件
            sendMailService.sendTestTaskMail(sendTaskMsgVo);
            // 企业微信
            weChatService.sendTestTaskMsg(sendTaskMsgVo);
        } catch (MessagingException e) {
            log.error("sendTestTaskInfo 发送邮件/企业微信失败，但是不影响使用！", e);
        }
    }

    /**
     * 创建测试执行任务
     *
     * @param outReqVo
     * @return
     */
    private TestTask createTaskInfo(OutReqVo outReqVo) {
        TestTask testTask = new TestTask();
        testTask.setTaskName(outReqVo.getTaskName());
        testTask.setTestTaskMaster(outReqVo.getTestUserName());
        testTask.setTestTaskExecutors(outReqVo.getTestUserName());
        testTask.setRelationRequirement(outReqVo.getJiraId());
        testTask.setTaskType(TestTaskTypeEnum.FUNCTIONAL_TESTING.getTestsTaskType());
        testTask.setStatus(TestTaskStatusEnum.NOT_STARTED.getTestsTaskStatus());
        testTask.setTestExecutionType(TestExecutionTypeEnum.AUTO.getKey());
        testTaskService.saveOrUpdateTestTask(testTask, outReqVo.getTestUserName());
        return testTask;
    }

    /**
     * 给测试执行增加用例
     *
     * @param appId
     * @param testTask
     * @return
     */
    private String createTestCaseByTask(String appId, TestTask testTask) {
        //1 查询appid关联的模板集合
        QueryWrapper<TestTask> taskQueryWrapper = new QueryWrapper<>();
        taskQueryWrapper.eq("template_flag", true);
        taskQueryWrapper.like("app_id", appId);
        List<TestTask> taskList = testTaskService.list(taskQueryWrapper);
        if (CollectionUtils.isEmpty(taskList)) {
            log.info("createTestCaseByTask appid对应测试执行模板为空！appId=={}", appId);
            return TestCenterConstants.TESTEXE_TEMPLATE_NO_EXIST;
        }
        //2 查询模板对应的测试用例
        QueryWrapper<TaskTestCase> tcQueryWrapper = new QueryWrapper<>();
        tcQueryWrapper.eq("task_id", testTask.getId());
        List<TaskTestCase> thisTestList = taskTestCaseService.list(tcQueryWrapper);
        // 排除当前测试执行的用例集合
        tcQueryWrapper = new QueryWrapper<>();
        List<Long> taskIdList = taskList.stream().map(TestTask::getId).collect(Collectors.toList());
        tcQueryWrapper.in("task_id", taskIdList);
        tcQueryWrapper.notIn(CollectionUtils.isNotEmpty(thisTestList), "case_key", thisTestList.stream().map(TaskTestCase::getCaseKey).collect(Collectors.toList()));

        List<TaskTestCase> testList = taskTestCaseService.list(tcQueryWrapper);
        if (CollectionUtils.isEmpty(testList)) {
            log.info("createTestCaseByTask taskIdList对应测试用例为空！taskIdList=={}", taskIdList);
            return TestCenterConstants.TESTEXE_TEMPLATE_NO_CASE;
        }
        // 用例去重有可能多个模板的用例有交叉的地方
        testList = testList.stream().filter(MyUtil.distinctByKey(TaskTestCase::getCaseKey)).collect(Collectors.toList());
        // 批量初始化数据并保存用例
        testList.stream().forEach(taskTestCase -> {
            taskTestCase.setTaskId(testTask.getId());
            taskTestCase.setCreateUser(testTask.getCreateUser());
            taskTestCase.setUpdateUser(testTask.getUpdateUser());
            taskTestCase.setCreateTime(LocalDateTime.now());
            taskTestCase.setUpdateTime(LocalDateTime.now());

            taskTestCase.setId(null);
            taskTestCase.setExecutionTime(null);
            taskTestCase.setExeRemark(null);
            taskTestCase.setExecutionResult(TestTaskStatusEnum.NOT_STARTED.getTestsTaskStatus());
            taskTestCase.setExecutionUser(null);
            taskTestCase.setExecutionVersion(null);
            taskTestCase.setRelationBug(null);
        });
        taskTestCaseService.saveBatch(testList);
        testExecutionService.updateCount(testTask.getId(), null);
        return TestCenterConstants.RES_MSG_SUCCESS;
    }


//    public static void main(String[] args) {
//        List<TaskTestCase> testList = new ArrayList<>();
//        for(int i=0;i<10;i++){
//            TaskTestCase tc = new TaskTestCase();
//            if(i%2==0){
//                tc.setCaseKey("1"+i);
//            }else{
//                tc.setCaseKey("11111");
//            }
//            tc.setCasename("name"+i);
//            tc.setTestCaseId(Long.parseLong("1"+i));
//            testList.add(tc);
//        }
//        System.out.println(JSON.toJSONString(testList));
//        testList = testList.stream().filter(distinctByKey(TaskTestCase::getCaseKey)).collect(Collectors.toList());
//        System.out.println(JSON.toJSONString(testList));
//    }

}
