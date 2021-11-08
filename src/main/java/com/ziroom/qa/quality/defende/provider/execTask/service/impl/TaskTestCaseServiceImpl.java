package com.ziroom.qa.quality.defende.provider.execTask.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ziroom.qa.quality.defende.provider.caseRepository.entity.TestCase;
import com.ziroom.qa.quality.defende.provider.caseRepository.entity.dto.SingleApiCaseListDto;
import com.ziroom.qa.quality.defende.provider.caseRepository.entity.vo.SingleApiCaseListVO;
import com.ziroom.qa.quality.defende.provider.caseRepository.service.IAutoSingleApiCaseService;
import com.ziroom.qa.quality.defende.provider.caseRepository.service.TestCaseService;
import com.ziroom.qa.quality.defende.provider.constant.enums.TestCaseStatusEnum;
import com.ziroom.qa.quality.defende.provider.constant.enums.TestExecutionTypeEnum;
import com.ziroom.qa.quality.defende.provider.constant.enums.TestTaskCaseStatusEnum;
import com.ziroom.qa.quality.defende.provider.constant.enums.UserRoleEnum;
import com.ziroom.qa.quality.defende.provider.entity.User;
import com.ziroom.qa.quality.defende.provider.execTask.entity.TaskTestCase;
import com.ziroom.qa.quality.defende.provider.execTask.entity.TestTask;
import com.ziroom.qa.quality.defende.provider.execTask.mapper.TaskTestCaseMapper;
import com.ziroom.qa.quality.defende.provider.execTask.service.TaskTestCaseService;
import com.ziroom.qa.quality.defende.provider.execTask.service.TestExecutionService;
import com.ziroom.qa.quality.defende.provider.execTask.service.TestTaskService;
import com.ziroom.qa.quality.defende.provider.result.CustomException;
import com.ziroom.qa.quality.defende.provider.outinterface.client.service.MatrixService;
import com.ziroom.qa.quality.defende.provider.service.UserService;
import com.ziroom.qa.quality.defende.provider.util.JiraUtils;
import com.ziroom.qa.quality.defende.provider.vo.MatrixUserDetail;
import com.ziroom.qa.quality.defende.provider.vo.Pagination;
import com.ziroom.qa.quality.defende.provider.vo.TaskTestCaseVo;
import com.ziroom.qa.quality.defende.provider.vo.TestReportStatusVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class TaskTestCaseServiceImpl extends ServiceImpl<TaskTestCaseMapper, TaskTestCase> implements TaskTestCaseService {

    @Autowired
    private TaskTestCaseMapper taskTestCaseMapper;
    @Autowired
    private TestTaskService testTaskService;
    @Autowired
    private TestCaseService testCaseService;
    @Autowired
    private UserService userService;
    @Autowired
    private TestExecutionService testExecutionService;
    @Autowired
    private MatrixService matrixService;
    @Autowired
    private IAutoSingleApiCaseService iAutoSingleApiCaseService;


    @Override
    public List<TaskTestCase> getTaskTestCaseByTaskId(Long taskId) {
        QueryWrapper<TaskTestCase> taskTestCaseQueryWrapper = new QueryWrapper<>();
        taskTestCaseQueryWrapper.eq(Objects.nonNull(taskId), "task_id", taskId);
        taskTestCaseQueryWrapper.orderByDesc("id");
        return this.list(taskTestCaseQueryWrapper);
    }

    /**
     * 根据testCaseId查询存在的id
     *
     * @param testCaseList
     * @return
     */
    @Override
    public List<Long> findIdsByIdList(List<Long> testCaseList) {
        QueryWrapper<TaskTestCase> taskTestCaseQueryWrapper = new QueryWrapper<>();
        taskTestCaseQueryWrapper.in("test_case_id", testCaseList);
        List<TaskTestCase> list = taskTestCaseMapper.selectList(taskTestCaseQueryWrapper);
        if (CollectionUtils.isNotEmpty(list)) {
            return list.stream().map(p -> p.getTestCaseId()).collect(Collectors.toList());
        }
        return null;
    }

    @Override
    public void batchDeleteTaskTestCase(TaskTestCaseVo taskTestCaseVo) {
        boolean templateFlag = testTaskService.getById(taskTestCaseVo.getTaskId()).getTemplateFlag();
        // 1.首先查询是否可以被删除
        QueryWrapper<TaskTestCase> queryWrapper = new QueryWrapper<>();
        // 超级管理员可以删除测试执行任务的case（删除有异常的用例）
        if (!(StringUtils.isNotBlank(taskTestCaseVo.getUserName())
                && UserRoleEnum.ROLE_SUPER_ADMIN.getName().equals(userService.getUserInfoByUserName(taskTestCaseVo.getUserName()).getRole()))
                || !templateFlag
        ) {
            queryWrapper.eq("task_id", taskTestCaseVo.getTaskId());
            queryWrapper.in(CollectionUtils.isNotEmpty(taskTestCaseVo.getTaskTestCaseIdList()), "id", taskTestCaseVo.getTaskTestCaseIdList());
            queryWrapper.notIn("execution_result", TestTaskCaseStatusEnum.NOT_STARTED.getTestsTaskStatus());
            int notStartCount = this.count(queryWrapper);
            if (notStartCount > 0) {
                throw new CustomException("删除失败，没有未执行的测试用例！");
            }
        }
        // 2.删除
        queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("task_id", taskTestCaseVo.getTaskId());
        queryWrapper.in("id", taskTestCaseVo.getTaskTestCaseIdList());
        boolean deleteResult = this.remove(queryWrapper);
        // 3.成功之后需要更新任务的case数量
        if (deleteResult) {
            testExecutionService.updateCount(taskTestCaseVo.getTaskId(), taskTestCaseVo.getUserName());
        } else {
            throw new CustomException("删除失败！");
        }
    }

    @Override
    public void batchSaveOrUpdateTaskTestCase(TaskTestCaseVo taskTestCaseVo, String userName) {
        Long taskId = taskTestCaseVo.getTaskId();
        TestTask testTask = testTaskService.getById(taskId);
        if (Objects.isNull(testTask)) {
            throw new CustomException("测试执行任务不存在！");
        }
        List<TaskTestCase> taskTestCaseList;
        int count = 0;
        //1. 搜集所有的新的测试执行集合
        List<Long> newTaskTestCaseIdList = taskTestCaseVo.getTaskTestCaseIdList();
        if (TestExecutionTypeEnum.ARTIFICIAL.getKey().equals(testTask.getTestExecutionType())) {
            List<TestCase> testCaseList = testCaseService.listByIds(newTaskTestCaseIdList);
            //2. 将TestCase转换为TaskTestCase
            taskTestCaseList = this.convertTestCaseToTaskTestCase(testCaseList, taskId, userName);
            count = taskTestCaseList.size();
        } else {
            SingleApiCaseListDto singleApiCaseListDto = new SingleApiCaseListDto();
            singleApiCaseListDto.setListIn(newTaskTestCaseIdList);
            Page<SingleApiCaseListVO> pageVo = iAutoSingleApiCaseService.findSingleApiListTableData(1, 1000, singleApiCaseListDto);
            List<SingleApiCaseListVO> caseList = Optional.ofNullable(pageVo).map(res -> res.getRecords()).orElse(new ArrayList<>());
            //3. 将api测试用例转换为TaskTestCase
            taskTestCaseList = this.convertApiCaseToTaskTestCase(caseList, taskId, userName);
            count = taskTestCaseList.size();
        }
        //4. 保存最新修改的测试用例集合
        if (count > 0) {
            super.saveBatch(taskTestCaseList);
            testExecutionService.updateCount(taskTestCaseVo.getTaskId(), userName);
        }
    }


    /**
     * 数据清洗-创建人、更新人、执行人
     *
     * @return
     */
    @Override
    public boolean updateTaskTestCaseUserInfo() {
        List<TaskTestCase> taskTestCaseList = this.list();
        taskTestCaseList.stream().forEach(taskTestCase -> {
            String createUserUid = taskTestCase.getCreateUser();
            if (StringUtils.isNotBlank(createUserUid)) {
                User createUser = userService.getUserInfoByUid(createUserUid);
                taskTestCase.setCreateUser(createUser.getUserName());
            }
            String updateUserUid = taskTestCase.getUpdateUser();
            if (StringUtils.isNotBlank(updateUserUid)) {
                User updateUser = userService.getUserInfoByUid(updateUserUid);
                taskTestCase.setUpdateUser(updateUser.getUserName());
            }
            String executionUserUid = taskTestCase.getExecutionUser();
            if (StringUtils.isNotBlank(executionUserUid)) {
                User executionUser = userService.getUserInfoByUid(executionUserUid);
                taskTestCase.setExecutionUser(executionUser.getUserName());
            }
        });
        return this.updateBatchById(taskTestCaseList);
    }

    /**
     * 获取测试 task 对应的测试结果
     *
     * @param taskId
     * @return
     */
    @Override
    public TestReportStatusVo getTestReportStatusByTaskId(Long taskId) {
        TestReportStatusVo statusVo = new TestReportStatusVo();
        if (Objects.isNull(taskId)) {
            return statusVo;
        }
        QueryWrapper<TaskTestCase> taskTestQueryWrapper = new QueryWrapper<>();
        taskTestQueryWrapper.eq("task_id", taskId);
        List<TaskTestCase> taskList = this.list(taskTestQueryWrapper);
        if (CollectionUtils.isEmpty(taskList)) {
            return statusVo;
        }
        //总数
        int totalCount = taskList.size();
        //未执行数量
        int noStartedCount = 0;
        for (TaskTestCase ttc : taskList) {
            if (TestTaskCaseStatusEnum.NOT_STARTED.getTestsTaskStatus().equals(ttc.getExecutionResult())) {
                noStartedCount++;
            }
        }
        if (noStartedCount > 0) {
            statusVo.setStatus("进行中");
            DecimalFormat df = new DecimalFormat("0.00");
            df.setRoundingMode(RoundingMode.HALF_UP);
            statusVo.setProgress(df.format(((double) (totalCount - noStartedCount) / (double) totalCount) * 100) + "%");
        } else {
            //失败数量
            int failCount = 0;
            for (TaskTestCase ttc : taskList) {
                if (TestTaskCaseStatusEnum.FAIL.getTestsTaskStatus().equals(ttc.getExecutionResult())) {
                    failCount++;
                }
            }
            if (failCount <= 0) {
                statusVo.setStatus("通过");
                statusVo.setProgress("100%");
            } else {
                statusVo.setStatus("未通过");
                DecimalFormat df = new DecimalFormat("0.00");
                df.setRoundingMode(RoundingMode.HALF_UP);
                statusVo.setProgress(df.format(((double) (totalCount - failCount) / (double) totalCount) * 100) + "%");
            }
        }
        return statusVo;
    }

    /**
     * 分页查询已经关联的测试用例
     *
     * @param pagination
     * @return
     */
    @Override
    public Page<TaskTestCase> queryRelTestCaseByPage(Pagination<TaskTestCase> pagination) {
        // 1. 初始化查询条件
        Page<TaskTestCase> taskPage = pagination.getPage();
        TaskTestCase queryInfo = pagination.getSearchObj();
        if (Objects.isNull(queryInfo.getTestExecutionType())) {
            queryInfo.setTestExecutionType(1);
        }
        // 2. 查询关联的测试用例集合
        QueryWrapper<TaskTestCase> queryWrapper = this.getTaskTestCaseQueryWapper(queryInfo);
        queryWrapper.orderByAsc("test_case_id");
        Page<TaskTestCase> newtaskPage = this.page(taskPage, queryWrapper);
        if (CollectionUtils.isEmpty(newtaskPage.getRecords())) {
            return newtaskPage;
        }
        // 测试级别字典列表
//        Map<String, String> levelMap = DicUtil.dicListToMapEnName(QualityDefendeConstants.DATA_DICTIONARY_TEST_LEVEL_TYPE);
        List<String> emailPreList = this.getEmailPreByList(newtaskPage.getRecords());
        Map<String, MatrixUserDetail> userMap = matrixService.getUserDetailByEmailPre(emailPreList);
        newtaskPage.getRecords().stream().parallel().forEach(taskTest -> {
            // 优先级
//            taskTest.setTestCaseLevel(levelMap.get(taskTest.getTestCaseLevel()));
            // 执行结果
            taskTest.setExecutionResult(TestTaskCaseStatusEnum.getTestsTaskStatusName(taskTest.getExecutionResult()));
            // 执行创建人
            taskTest.setCreateUser(Optional.ofNullable(userMap)
                    .map(item -> item.get(taskTest.getCreateUser()))
                    .map(detail -> detail.getUserName())
                    .orElse(""));
            // 执行人
            taskTest.setExecutionUser(Optional.ofNullable(userMap)
                    .map(item -> item.get(taskTest.getExecutionUser()))
                    .map(detail -> detail.getUserName())
                    .orElse(""));
            // bug连接
            if (StringUtils.isNotBlank(taskTest.getRelationBug())) {
                taskTest.setRelationBugUrl(JiraUtils.getJiraUrl(taskTest.getRelationBug()));
            }
        });
        return newtaskPage;
    }

    /**
     * 查询未关联自动测试用例执行分页信息
     *
     * @param pagination
     * @return
     */
    @Override
    public Page queryUnRelApiCaseByPage(Pagination<SingleApiCaseListDto> pagination) {
        SingleApiCaseListDto singleApiCaseListDto = pagination.getSearchObj();
        List<TaskTestCase> taskTestCaseList = this.getTestCaseListByTask(singleApiCaseListDto.getTaskId(), TestExecutionTypeEnum.AUTO.getKey());
        if (CollectionUtils.isNotEmpty(taskTestCaseList)) {
            List<Long> testCaseIdList = taskTestCaseList.stream().map(TaskTestCase::getTestCaseId).collect(Collectors.toList());
            singleApiCaseListDto.setListNotIn(testCaseIdList);
        }

        int nowPage = (int) pagination.getPage().getCurrent();
        int pageSize = (int) pagination.getPage().getSize();

        Page<SingleApiCaseListVO> pageVo = iAutoSingleApiCaseService.findSingleApiListTableData(nowPage, pageSize, singleApiCaseListDto);
        if (Objects.nonNull(pageVo)) {
            List<SingleApiCaseListVO> newList = pageVo.getRecords();
            if (CollectionUtils.isEmpty(newList)) {
                return null;
            }
            List<String> emailPreList = newList.stream().distinct().map(SingleApiCaseListVO::getUpdateUserCode).collect(Collectors.toList());
            Map<String, MatrixUserDetail> userMap = matrixService.getUserDetailByEmailPre(emailPreList);
            newList.stream().forEach(mydtoV2 -> {
                mydtoV2.setUpdateUserCode(Optional.ofNullable(userMap)
                        .map(item -> item.get(mydtoV2.getUpdateUserCode()))
                        .map(detail -> detail.getUserName())
                        .orElse(""));
            });
            return pageVo;
        }
        return null;
    }

    /**
     * 查询未关联人工测试用例执行分页信息
     *
     * @param pagination
     * @return
     */
    @Override
    public Page<TestCase> queryUnRelTestCaseByPage(Pagination<TestCase> pagination) {
        TestCase testCase = pagination.getSearchObj();
        List<TaskTestCase> taskTestCaseList = this.getTestCaseListByTask(testCase.getTaskId(), TestExecutionTypeEnum.ARTIFICIAL.getKey());
        List<Long> testCaseIdList = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(taskTestCaseList)) {
            testCaseIdList = taskTestCaseList.stream().map(TaskTestCase::getTestCaseId).collect(Collectors.toList());
        }
        // 设置用例是未删除的
        testCase.setDeleteFlag(false);
        testCase.setFlag(true);
        testCase.setTestCaseStatus(TestCaseStatusEnum.APPROVED.getTestCaseStatus());

        QueryWrapper<TestCase> testCaseQueryWrapper = testCaseService.getTestCaseQueryWrapper(testCase);
        testCaseQueryWrapper.notIn(CollectionUtils.isNotEmpty(testCaseIdList), "id", testCaseIdList);
        Page<TestCase> page = testCaseService.page(pagination.getPage(), testCaseQueryWrapper);
        if (Objects.nonNull(page) && CollectionUtils.isNotEmpty(page.getRecords())) {
            testCaseService.assableTestInfo(page.getRecords(), null);
        }
        return page;
    }

    /**
     * 获取测试用例详情信息（已关联的）
     *
     * @param id
     * @return
     */
    @Override
    public TaskTestCase getCaseDetailById(Long id) {
        TaskTestCase taskTestCase = super.getById(id);
        if (Objects.isNull(taskTestCase)) {
            throw new CustomException("该测试用例不存在！");
        }
        // 人工测试执行
        if (TestExecutionTypeEnum.ARTIFICIAL.getKey().equals(taskTestCase.getTestExecutionType())) {
            TaskTestCase oldTaskCase = taskTestCase;
            TestCase testCase = testCaseService.getTestCaseById(taskTestCase.getTestCaseId());

            taskTestCase.setCasename(testCase.getCasename());
            taskTestCase.setPreCondition(testCase.getPreCondition());
            taskTestCase.setStep(testCase.getStep());
            taskTestCase.setExpectedResults(testCase.getExpectedResults());
            taskTestCase.setRelationRequirement(testCase.getRelationRequirement());

            taskTestCase.setBelongPlatformStr(testCase.getBelongPlatformStr());
            taskTestCase.setBelongToSystemStr(testCase.getBelongToSystemStr());
            taskTestCase.setBelongToModuleStr(testCase.getBelongToModuleStr());

            taskTestCase.setTestCaseLevelStr(testCase.getTestCaseLevelStr());
            taskTestCase.setTypeStr(testCase.getTypeStr());

            taskTestCase.setExecutionResult(oldTaskCase.getExecutionResult());
            taskTestCase.setExecutionVersion(oldTaskCase.getExecutionVersion());
            taskTestCase.setExeRemark(oldTaskCase.getExeRemark());
            taskTestCase.setRelationBug(oldTaskCase.getRelationBug());

            // 自动测试执行
        } else {
            SingleApiCaseListVO singleApiCaseListVO = iAutoSingleApiCaseService.getSingleApiCaseById(taskTestCase.getTestCaseId().intValue());
            if (Objects.isNull(singleApiCaseListVO)) {
                throw new CustomException("/getSingleApiCaseById 获取api接口信息异常或用例不存在！");
            }
            // 保存api接口的字段
            taskTestCase.setAssertRecordList(JSON.toJSONString(singleApiCaseListVO.getAssertList()));
            taskTestCase.setTestExecutionType(TestExecutionTypeEnum.AUTO.getKey());
            taskTestCase.setRealInParam(singleApiCaseListVO.getRequestParam());
            // TODO bapplehaha
            taskTestCase.setRequestUri(singleApiCaseListVO.getRequestUri());
            taskTestCase.setRequestType(singleApiCaseListVO.getRequestType());
            taskTestCase.setReqHeader(singleApiCaseListVO.getHeader());
            taskTestCase.setReqBody(singleApiCaseListVO.getRequestBody());
            taskTestCase.setApplicationName(singleApiCaseListVO.getApplicationName());
            taskTestCase.setCasename(singleApiCaseListVO.getCaseName());
            taskTestCase.setProtocolType(singleApiCaseListVO.getProtocolType());
            Map<String, MatrixUserDetail> userMap = matrixService.getUserDetailByEmailPre(Arrays.asList(taskTestCase.getUpdateUser()));
            taskTestCase.setUpdateUser(Optional.ofNullable(userMap)
                    .map(item -> item.get(taskTestCase.getUpdateUser()))
                    .map(detail -> detail.getUserName())
                    .orElse(""));
        }
        return taskTestCase;
    }

    /**
     * 获取用户邮箱前缀的集合
     *
     * @param taskList
     * @return
     */
    private List<String> getEmailPreByList(List<TaskTestCase> taskList) {
        List<String> emailPreList = new ArrayList<>();
        taskList.stream().forEach(task -> {
            if (StringUtils.isNotBlank(task.getCreateUser())) {
                emailPreList.add(task.getCreateUser());
            }
            if (StringUtils.isNotBlank(task.getExecutionUser())) {
                emailPreList.add(task.getExecutionUser());
            }
        });
        if (CollectionUtils.isEmpty(emailPreList)) {
            return emailPreList;
        }
        return emailPreList.stream().distinct().collect(Collectors.toList());
    }

    /**
     * 组装查询信息
     *
     * @param queryInfo
     * @return
     */
    private QueryWrapper<TaskTestCase> getTaskTestCaseQueryWapper(TaskTestCase queryInfo) {
        QueryWrapper<TaskTestCase> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("task_id", queryInfo.getTaskId());
        queryWrapper.eq("test_execution_type", queryInfo.getTestExecutionType());
        queryWrapper.eq(StringUtils.isNotBlank(queryInfo.getCreateUser()), "create_user", queryInfo.getCreateUser());
        queryWrapper.eq(StringUtils.isNotBlank(queryInfo.getExecutionUser()), "execution_user", queryInfo.getExecutionUser());
        queryWrapper.eq(StringUtils.isNotBlank(queryInfo.getExecutionResult()), "execution_result", queryInfo.getExecutionResult());
        queryWrapper.like(StringUtils.isNotBlank(queryInfo.getCasename()), "casename", queryInfo.getCasename());
        return queryWrapper;
    }

    /**
     * 查询测试执行任务下的测试用例集合
     *
     * @param taskId
     * @param exeType
     * @return
     */
    private List<TaskTestCase> getTestCaseListByTask(Long taskId, Integer exeType) {
        QueryWrapper<TaskTestCase> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("task_id", taskId);
        queryWrapper.eq(Objects.nonNull(exeType), "test_execution_type", exeType);
        List<TaskTestCase> taskTestCaseList = super.list(queryWrapper);
        return taskTestCaseList;
    }

    /**
     * 转化普通测试用例信息
     *
     * @param testCaseList
     * @param taskId
     * @param userName
     * @return
     */
    private List<TaskTestCase> convertTestCaseToTaskTestCase(List<TestCase> testCaseList, Long taskId, String userName) {
        List<TaskTestCase> taskTestCaseList = new ArrayList<>();
        for (TestCase testCase : testCaseList) {
            TaskTestCase taskTestCase = new TaskTestCase();
            taskTestCase.setCasename(testCase.getCasename());
            taskTestCase.setTestCaseId(testCase.getId());
            taskTestCase.setCaseKey(testCase.getCaseKey());
            taskTestCase.setTaskId(taskId);
            taskTestCase.setCreateUser(userName);
            taskTestCase.setUpdateUser(userName);
            taskTestCase.setCreateTime(LocalDateTime.now());
            taskTestCase.setUpdateTime(LocalDateTime.now());
            taskTestCase.setExecutionResult(TestTaskCaseStatusEnum.NOT_STARTED.getTestsTaskStatus());
            taskTestCaseList.add(taskTestCase);
        }
        return taskTestCaseList;
    }

    /**
     * 转化api测试用例信息
     *
     * @param caseList
     * @param taskId
     * @param userName
     * @return
     */
    private List<TaskTestCase> convertApiCaseToTaskTestCase(List<SingleApiCaseListVO> caseList, Long taskId, String userName) {
        List<TaskTestCase> taskTestCaseList = new ArrayList<>();
        for (SingleApiCaseListVO testCase : caseList) {
            TaskTestCase taskTestCase = new TaskTestCase();
            taskTestCase.setTestCaseId((long) testCase.getId());
            taskTestCase.setCaseKey(testCase.getId() + "");
            taskTestCase.setCasename(testCase.getCaseName());
            taskTestCase.setTaskId(taskId);
            taskTestCase.setCreateUser(userName);
            taskTestCase.setUpdateUser(userName);
            taskTestCase.setCreateTime(LocalDateTime.now());
            taskTestCase.setUpdateTime(LocalDateTime.now());
            taskTestCase.setExecutionResult(TestTaskCaseStatusEnum.NOT_STARTED.getTestsTaskStatus());
            taskTestCase.setTestExecutionType(TestExecutionTypeEnum.AUTO.getKey());
            taskTestCaseList.add(taskTestCase);
        }
        return taskTestCaseList;
    }

}
