package com.ziroom.qa.quality.defende.provider.caseRepository.service.impl;

import com.alibaba.excel.EasyExcel;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.collect.Lists;
import com.ziroom.qa.quality.defende.provider.caseRepository.entity.TestCase;
import com.ziroom.qa.quality.defende.provider.caseRepository.mapper.TestCaseMapper;
import com.ziroom.qa.quality.defende.provider.caseRepository.service.TestCaseService;
import com.ziroom.qa.quality.defende.provider.constant.QualityDefendeConstants;
import com.ziroom.qa.quality.defende.provider.constant.TestCenterConstants;
import com.ziroom.qa.quality.defende.provider.constant.enums.TestCaseStatusEnum;
import com.ziroom.qa.quality.defende.provider.entity.TestApplication;
import com.ziroom.qa.quality.defende.provider.entity.TestApplicationModule;
import com.ziroom.qa.quality.defende.provider.entity.User;
import com.ziroom.qa.quality.defende.provider.execTask.entity.TaskTestCase;
import com.ziroom.qa.quality.defende.provider.execTask.service.TaskTestCaseService;
import com.ziroom.qa.quality.defende.provider.listener.CustomSheetWriteHandler;
import com.ziroom.qa.quality.defende.provider.outinterface.client.service.MatrixService;
import com.ziroom.qa.quality.defende.provider.result.CustomException;
import com.ziroom.qa.quality.defende.provider.result.RestResultVo;
import com.ziroom.qa.quality.defende.provider.service.TestApplicationModuleService;
import com.ziroom.qa.quality.defende.provider.service.TestApplicationService;
import com.ziroom.qa.quality.defende.provider.service.UserService;
import com.ziroom.qa.quality.defende.provider.util.DicUtil;
import com.ziroom.qa.quality.defende.provider.util.JiraUtils;
import com.ziroom.qa.quality.defende.provider.util.TimeUtil;
import com.ziroom.qa.quality.defende.provider.util.XmindUtil;
import com.ziroom.qa.quality.defende.provider.util.xmind.pojo.Attached;
import com.ziroom.qa.quality.defende.provider.vo.MatrixUserDetail;
import com.ziroom.qa.quality.defende.provider.vo.TestCaseVo;
import com.ziroom.qa.quality.defende.provider.vo.TestResultVo;
import com.ziroom.qa.quality.defende.provider.vo.TimeVo;
import com.ziroom.qa.quality.defende.provider.vo.testcase.TestCaseDownVo;
import com.ziroom.qa.quality.defende.provider.vo.testcase.TestCaseUploadVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Slf4j
@Service
public class TestCaseServiceImpl extends ServiceImpl<TestCaseMapper, TestCase> implements TestCaseService {

    @Autowired
    private TestCaseMapper testCaseMapper;
    @Autowired
    private TaskTestCaseService taskTestCaseService;
    @Autowired
    private UserService userService;
    @Autowired
    private CustomSheetWriteHandler customSheetWriteHandler;
    @Autowired
    private TestApplicationService testApplicationService;
    @Autowired
    private TestApplicationModuleService testApplicationModuleService;
    @Autowired
    private MatrixService matrixService;

    private static Pattern CHINESE_PATTERN = Pattern.compile("[\u4e00-\u9fa5]");

    private static final String TC_REGEX = "tc-";
    private static final String PC_REGEX = "pc:";
    private static final String RC_REGEX = "rc:";

    @Override
    public QueryWrapper<TestCase> getTestCaseQueryWrapper(TestCase testCase) {
        //1. 格式化查询对象
        QueryWrapper<TestCase> testCaseQueryWrapper = new QueryWrapper<>();
        testCaseQueryWrapper.eq(null != testCase.getFlag(), "flag", testCase.getFlag())
                // 如果是更新操作,则不校验当前id liangh4
                .notIn(null != testCase.getId(), "id", testCase.getId())
                .eq(null != testCase.getCaseLock(), "case_lock", testCase.getCaseLock())
                .eq(StringUtils.isNotBlank(testCase.getCaseKey()), "case_key", testCase.getCaseKey())

                .eq(null != testCase.getDeleteFlag(), "delete_flag", testCase.getDeleteFlag())
                .eq(StringUtils.isNotBlank(testCase.getTestCaseStatus()), "test_case_status", testCase.getTestCaseStatus())

                .like(StringUtils.isNotBlank(testCase.getPreCondition()), "pre_condition", testCase.getPreCondition())
                .like(StringUtils.isNotBlank(testCase.getStep()), "step", testCase.getStep())
                .like(StringUtils.isNotBlank(testCase.getExpectedResults()), "expected_results", testCase.getExpectedResults())

                .like(StringUtils.isNotBlank(testCase.getRemarks()), "remarks", testCase.getRemarks())
//                .like(StringUtils.isNotBlank(testCase.getEhrTreePath()),"ehr_tree_path",testCase.getEhrTreePath())

                .eq(null != testCase.getApprovedTime(), "approved_time", testCase.getApprovedTime())
                .like(StringUtils.isNotBlank(testCase.getApproveUser()), "approve_user", testCase.getApproveUser())
                .like(StringUtils.isNotBlank(testCase.getCreateUser()), "create_user", testCase.getCreateUser())
                .like(StringUtils.isNotBlank(testCase.getUpdateUser()), "update_user", testCase.getUpdateUser())
                .like(StringUtils.isNotBlank(testCase.getSourceFileKey()), "source_file_key", testCase.getSourceFileKey())
                .like(StringUtils.isNotBlank(testCase.getSourceFileUrl()), "source_file_url", testCase.getSourceFileUrl())
                .like(StringUtils.isNotBlank(testCase.getCasename()), "casename", testCase.getCasename())
                .eq(StringUtils.isNotBlank(testCase.getVersion()), "version", testCase.getVersion())

                // 所需查询条件
                .eq(StringUtils.isNotBlank(testCase.getTestCaseLevel()), "test_case_level", testCase.getTestCaseLevel())
                .eq(StringUtils.isNotBlank(testCase.getRelationRequirement()), "relation_requirement", testCase.getRelationRequirement())
                .eq(Objects.nonNull(testCase.getBelongToSystem()), "belong_to_system", testCase.getBelongToSystem())
                // ehr查询到所属应用

                .eq(Objects.nonNull(testCase.getBelongToModule()), "belong_to_module", testCase.getBelongToModule())
                .eq(StringUtils.isNotBlank(testCase.getType()), "type", testCase.getType())
                .eq(StringUtils.isNotBlank(testCase.getBelongPlatform()), "belong_platform", testCase.getBelongPlatform())
                .eq(!Objects.isNull(testCase.getBelongTopic()), "belong_topic", testCase.getBelongTopic())
        ;
        //2. 设置创建时间
        if (StringUtils.isNotBlank(testCase.getCreateTimeVal())) {
            TimeVo timeVo = TimeUtil.parseTimeStr(testCase.getCreateTimeVal());
            testCaseQueryWrapper.between("create_time", timeVo.getStart(), timeVo.getEnd());
        }

        //3. 设置更新时间
        if (StringUtils.isNotBlank(testCase.getUpdateTimeVal())) {
            TimeVo timeVo = TimeUtil.parseTimeStr(testCase.getUpdateTimeVal());
            testCaseQueryWrapper.between("update_time", timeVo.getStart(), timeVo.getEnd());
        }
        //4. 查询ehr信息
        if (StringUtils.isNotBlank(testCase.getEhrTreePath())
                && Objects.isNull(testCase.getBelongToSystem())) {
            QueryWrapper<TestApplication> queryWrapper = new QueryWrapper<>();
            queryWrapper.like("ehr_tree_path", testCase.getEhrTreePath());
            List<TestApplication> appList = testApplicationService.list(queryWrapper);
            List<String> appIdList = appList.stream().map(application -> application.getId() + "").collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(appIdList)) {
                testCaseQueryWrapper.in("belong_to_system", appIdList);
            } else {
                // 查询不到的情况
                testCaseQueryWrapper.eq("1", "2");
            }
        }

        return testCaseQueryWrapper;
    }

    @Override
    public RestResultVo<TestCase> formatTestCase(TestCase testCase) {
        return this.checkAndFormatTestCase(testCase);
    }

    /**
     * 验证测试用例所属系统是否存在
     *
     * @param belongToSystem
     * @return
     */
    @Override
    public RestResultVo validateBelongToSystem(String belongToSystem) {
//        log.info("传入参数 belongToSystem：{}", belongToSystem);
//        RestResultVo<List<OmegaApplicationEnv>> list = ciFeignClient.queryEnvByLevelAndDomain(belongToSystem, "prod");
//        log.info("调动获取Omega 应用接口返回值：{}", JSON.toJSONString(list));
//        if (list.getData().isEmpty()) {
//            return RestResultVo.fromErrorMessage("Omega部署平台不存在正式环境域名：[" + belongToSystem + "]");
//        } else {
//            return RestResultVo.fromSuccess(null);
//        }
        return RestResultVo.fromSuccess(null);
    }

    @Override
    public RestResultVo validateTestcaseName(Long id, String casename, Long moduleId) {
        return this.validateName(casename, id, moduleId);
    }

    /**
     * 批量审批测试用例
     *
     * @param testCaseVo
     * @return
     */
    @Override
    public boolean batchApprovedTestCase(TestCaseVo testCaseVo) {
        String userName = testCaseVo.getUserName();
        String approve = testCaseVo.getApprove();
        String remark = testCaseVo.getRemark();
        String changeType = testCaseVo.getChangeType();
        List<Long> idList = testCaseVo.getIdList();

        List<TestCase> testCaseList = testCaseMapper.selectBatchIds(idList);
        List<TestCase> newTestCaseList = new ArrayList<>();
        if (CollectionUtils.isEmpty(testCaseList)) {
            return false;
        }
        for (TestCase testCase : testCaseList) {
            if (TestCaseStatusEnum.APPROVED.getTestCaseStatus().equals(testCase.getTestCaseStatus())
                    || TestCaseStatusEnum.REJECTED.getTestCaseStatus().equals(testCase.getTestCaseStatus())) {
                continue;
            }
            if (approve.equals(TestCaseStatusEnum.APPROVED.getTestCaseStatus())) {
                String version = testCase.getVersion();
                if (StringUtils.isBlank(version)) {
                    version = QualityDefendeConstants.TEST_CASE_DEFAULT_VERSION;
                }
                double versionVal = Double.parseDouble(version);
                if (versionVal < 1) {
                    version = QualityDefendeConstants.TEST_CASE_PASS_DEFAULT_VERSION;
                } else {
                    version = String.format("%.1f", versionVal + 1);
                }
                testCase.setVersion(version);
                testCase.setTestCaseStatus(TestCaseStatusEnum.APPROVED.getTestCaseStatus());
                testCase.setCaseLock(true);
                // 用例变更类型
                if (StringUtils.isNotBlank(changeType)) {
                    testCase.setChangeType(changeType);
                }
                newTestCaseList.add(testCase);
            } else {
                testCase.setTestCaseStatus(TestCaseStatusEnum.REJECTED.getTestCaseStatus());
                testCase.setCaseLock(false);
            }
            // TODO 补偿新增的时候没有更新上caseKey 测试用例审批通过时新增CaseKey
            if (StringUtils.isBlank(testCase.getCaseKey())) {
                testCase.setCaseKey(UUID.randomUUID().toString().replace("-", ""));
            }
            testCase.setRemarks(remark);
            testCase.setApprovedTime(LocalDateTime.now());
            testCase.setApproveUser(userName);
            newTestCaseList.add(testCase);
        }
        if (CollectionUtils.isEmpty(newTestCaseList)) {
            return false;
        }
        return this.updateBatchById(newTestCaseList);
    }

    @Override
    public TestResultVo batchDeleteTestCase(String userName, List<Long> idList) {
        if (CollectionUtils.isEmpty(idList)) {
            return TestResultVo.builder().flag(false).msgRes(TestCenterConstants.RES_MSG_PARAMS_EMPTY).build();
        }
        List<TestCase> testCaseList = testCaseMapper.selectBatchIds(idList);
        if (CollectionUtils.isEmpty(testCaseList)) {
            return TestResultVo.builder().flag(false).msgRes(TestCenterConstants.RES_MSG_SEARCH_EMPTY).build();
        }
        List<TestCase> newTestCaseList = new ArrayList<>();
        List<Long> relativeIds = taskTestCaseService.findIdsByIdList(idList);
        for (TestCase testCase : testCaseList) {
            if (CollectionUtils.isNotEmpty(relativeIds) && relativeIds.contains(testCase.getId())) {
                continue;
            }
            testCase.setDeleteFlag(true);
            testCase.setUpdateTime(LocalDateTime.now());
            testCase.setUpdateUser(userName);
            newTestCaseList.add(testCase);
        }
        if (CollectionUtils.isEmpty(newTestCaseList)) {
            return TestResultVo.builder().flag(false).msgRes(TestCenterConstants.RES_MSG_RELATION).build();
        }
        boolean updateFlag = updateBatchById(newTestCaseList);
        return TestResultVo.builder().flag(updateFlag).msgRes(updateFlag ? TestCenterConstants.RES_MSG_SUCCESS : TestCenterConstants.RES_MSG_ERROR).build();
    }

    @Override
    public void exportTestCaseExcel(List<Long> idList, HttpServletResponse response, String fileName, String sheetName) throws IOException {
        response = this.exportExcel(response, fileName);
        List<TestCaseDownVo> testCaseDownList = null;
        if (CollectionUtils.isNotEmpty(idList)) {
            List<TestCase> testCaseList = this.listByIds(idList);
            testCaseDownList = this.exportTestCaseToExcelData(testCaseList);
        }
        this.writeExcel(response, sheetName, testCaseDownList);
    }

    @Override
    public List<TestCase> queryTestCaseList4Task(TestCase testCase) {
        //1. 设置查询参数
        if (null == testCase) {
            testCase = new TestCase();
        }
        testCase.setDeleteFlag(false);
        testCase.setFlag(true);
        testCase.setTestCaseStatus(TestCaseStatusEnum.APPROVED.getTestCaseStatus());
        //2. 格式化查询参数
        QueryWrapper<TestCase> testCaseQueryWrapper = this.getTestCaseQueryWrapper(testCase);

        //3. 排除测试执行已经关联的测试用例
        Long taskId = testCase.getTaskId();
        if (null != taskId) {
            List<TaskTestCase> testTestCaseList = taskTestCaseService.getTaskTestCaseByTaskId(taskId);
            if (CollectionUtils.isNotEmpty(testTestCaseList)) {
                List<String> caseKeyList = testTestCaseList.stream().map(TaskTestCase::getCaseKey).collect(Collectors.toList());
                testCaseQueryWrapper.notIn("case_key", caseKeyList);
            }
        }
        testCaseQueryWrapper.orderByDesc("id");

        //4. 分页查询
        return this.list(testCaseQueryWrapper);
    }

    /**
     * 更新用例信息
     *
     * @param testCase
     * @return
     */
    @Override
    public TestResultVo update4TestCase(TestCase testCase) {
        RestResultVo<TestCase> restResultVo = this.checkAndFormatTestCase(testCase);
        if (!restResultVo.isSuccess()) {
            return TestResultVo.builder().flag(false).msgRes(restResultVo.getMessage()).build();
        }
        testCase = restResultVo.getData();
        boolean flag;
        // 锁定状态
        // 如果已经审核通过,则该用例为锁定状态true,需要留档,
        // 否则为未锁定状态false
        Boolean caseLock = testCase.getCaseLock();
        try {
            Long caseId = testCase.getId();
            TestCase testCaseOld = this.getById(caseId);
            if (caseLock) {
                //1. 复制一个对象，原测试用例作为历史版本不再更新
                testCaseOld.setFlag(false);
                flag = this.updateById(testCaseOld);
                if (flag) {
                    String version = testCase.getVersion();
                    testCase.setVersion(String.format("%.1f", Double.parseDouble(version) + 0.1));
                    String caseKey = testCaseOld.getCaseKey();
                    testCase.setFlag(true);
                    testCase.setDeleteFlag(false);
                    testCase.setUpdateTime(LocalDateTime.now());
                    testCase.setCaseLock(false);
                    testCase.setCaseKey(caseKey);
                    testCase.setTestCaseStatus(TestCaseStatusEnum.PENDING.getTestCaseStatus());
                    flag = this.save(testCase);
                }
            } else {
                //2. 未锁定状态,直接修改当前版本
                String version = testCase.getVersion();
                if (StringUtils.isBlank(version)) {
                    version = QualityDefendeConstants.TEST_CASE_DEFAULT_VERSION;
                } else {
                    version = String.format("%.1f", Double.parseDouble(version) + 0.1);
                }
                testCase.setDeleteFlag(false);
                testCase.setFlag(true);
                testCase.setVersion(version);
                testCase.setUpdateTime(LocalDateTime.now());
                testCase.setTestCaseStatus(TestCaseStatusEnum.PENDING.getTestCaseStatus());
                // TODO 补偿新增的时候没有更新上caseKey 测试用例审批通过时新增CaseKey
                if (StringUtils.isBlank(testCaseOld.getCaseKey())) {
                    testCase.setCaseKey(UUID.randomUUID().toString().replace("-", ""));
                }
                flag = this.updateById(testCase);
            }
        } catch (Exception e) {
            log.error("保存用例出错,错误信息testCase.getId:{}", testCase.getId(), e);
            flag = false;
        }
        return TestResultVo.builder().flag(flag).msgRes(flag ? TestCenterConstants.RES_MSG_SUCCESS : TestCenterConstants.RES_MSG_ERROR).build();
    }

    @Override
    public List<TestCase> queryTestCaseHistoryByCaseId(String caseKey) {
        if (StringUtils.isBlank(caseKey)) {
            return null;
        }
        QueryWrapper<TestCase> testCaseQueryWrapper = new QueryWrapper<>();
        testCaseQueryWrapper.eq("case_key", caseKey);
        testCaseQueryWrapper.eq("case_lock", true);
        testCaseQueryWrapper.eq("flag", false);
        testCaseQueryWrapper.orderByDesc("id");

        List<TestCase> caseList = testCaseMapper.selectList(testCaseQueryWrapper);
        this.assableTestInfo(caseList, null);
        return caseList;
    }

    @Override
    public List<Long> queryIdsByTestCase(TestCase testCase) {
        //1. 格式化查询参数
        QueryWrapper<TestCase> testCaseQueryWrapper = this.getTestCaseQueryWrapper(testCase);
        testCaseQueryWrapper.orderByDesc("id");
        //2. 去查询
        List<TestCase> testCaseList = this.list(testCaseQueryWrapper);
        List<Long> idList = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(testCaseList)) {
            idList = testCaseList.stream().map(TestCase::getId).collect(Collectors.toList());
        }
        return idList;
    }

    @Override
    public Page<TestCase> queryTestCaseByPage(Page<TestCase> page, TestCase testCase) {
        //1. 格式化查询参数
        QueryWrapper<TestCase> testCaseQueryWrapper = this.getTestCaseQueryWrapper(testCase);
        testCaseQueryWrapper.orderByDesc("id");

        //2. 分页查询
        Page<TestCase> testCasePageResult = this.page(page, testCaseQueryWrapper);
        if (Objects.nonNull(testCasePageResult) && CollectionUtils.isNotEmpty(testCasePageResult.getRecords())) {
            List<TestCase> caseList = testCasePageResult.getRecords();
            this.assableTestInfo(caseList, null);
            testCasePageResult.setRecords(caseList);
        }
        return testCasePageResult;
    }

    /**
     * 根据ID获取测试用例详情
     *
     * @param id
     * @return
     */
    @Override
    public TestCase getTestCaseById(Long id) {
        if (Objects.isNull(id)) {
            return null;
        }
        TestCase testCase = this.getById(id);
        this.assableTestInfo(null, testCase);
        return testCase;
    }

    /**
     * 验证所属模块是否关联测试用例
     *
     * @param belongToModuleList
     * @return
     */
    @Override
    public boolean validateApplicationModuleContainsTestCase(List<Long> belongToModuleList) {
        QueryWrapper<TestCase> testCaseQueryWrapper = new QueryWrapper<>();
        testCaseQueryWrapper.in("belong_to_module", belongToModuleList);
        testCaseQueryWrapper.eq("delete_flag", false);
        testCaseQueryWrapper.eq("flag", true);
        List<TestCase> testCaseList = this.list(testCaseQueryWrapper);
        return CollectionUtils.isNotEmpty(testCaseList);
    }

    /**
     * 根据所属模块查询测试用例列表
     *
     * @param belongToModule
     * @return
     */
    public List<TestCase> queryTestCaseListByBelongToModule(Long belongToModule) {
        QueryWrapper<TestCase> testCaseQueryWrapper = new QueryWrapper<>();
        testCaseQueryWrapper.eq("belong_to_module", belongToModule);
        testCaseQueryWrapper.eq("delete_flag", false);
        testCaseQueryWrapper.eq("flag", true);
        return this.list(testCaseQueryWrapper);
    }

    /**
     * 创建人、更新人、审批人
     *
     * @return
     */
    @Override
    public boolean updateTestCaseUserInfo() {
        List<TestCase> testCaseList = this.list();
        testCaseList.stream().forEach(testCase -> {
            String approveUserUid = testCase.getApproveUser();
            if (StringUtils.isNotBlank(approveUserUid)) {
                User approveUser = userService.getUserInfoByUid(approveUserUid);
                testCase.setApproveUser(approveUser.getUserName());
            }
            String createUserUid = testCase.getCreateUser();
            if (StringUtils.isNotBlank(createUserUid)) {
                User createUser = userService.getUserInfoByUid(createUserUid);
                testCase.setCreateUser(createUser.getUserName());
            }
            String updateUserUid = testCase.getUpdateUser();
            if (StringUtils.isNotBlank(updateUserUid)) {
                User updateUser = userService.getUserInfoByUid(updateUserUid);
                testCase.setUpdateUser(updateUser.getUserName());
            }
        });
        return this.updateBatchById(testCaseList);
    }

    private static boolean isAvailable(String str, String regex) {
        if (StringUtils.isEmpty(str) || StringUtils.isEmpty(regex)) {
            return false;
        }
//        Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
//        Matcher result = pattern.matcher(str);
//        return result.find();
        return str.startsWith(regex);
    }

    /**
     * 导出xmind信息
     *
     * @param idList
     * @param response
     * @param fileName
     */
    @Override
    public void exportTestCaseXmind(List<Long> idList, HttpServletResponse response, String fileName) {
        if (CollectionUtils.isEmpty(idList)) {
            XmindUtil.download(null, response, fileName);
        } else {
            List<TestCase> testCaseList = this.listByIds(idList);
            XmindUtil.download(testCaseList, response, fileName);
        }
    }

    /**
     * 解析测试用例
     *
     * @param allChildren
     * @param testCaseUploadVo
     * @param testCaseList
     * @return
     */
    public List<TestCase> getXmindChildren(List<Attached> allChildren, TestCaseUploadVo testCaseUploadVo, List<TestCase> testCaseList, StringBuilder sb) throws Exception {
        if (CollectionUtils.isNotEmpty(allChildren)) {
            for (Attached child : allChildren) {
                if (CollectionUtils.isNotEmpty(child.getAttachedChildren())) {
                    getXmindChildren(child.getAttachedChildren(), testCaseUploadVo, testCaseList, sb);
                } else {
                    String titleText = child.getTitle();
                    if (isAvailable(titleText, TC_REGEX)) {
                        TestCase testCase = parseTestCase(child, testCaseUploadVo);//解析每个测试用例
                        testCase.setBelongPlatform(testCaseUploadVo.getBelongPlatform());
                        testCase.setType(testCaseUploadVo.getType());
                        testCase.setCreateUser(testCaseUploadVo.getUserName());
                        testCase.setUpdateUser(testCaseUploadVo.getUserName());
                        // 是否校验用例名称重复标识
                        testCase.setUploadFlag(testCaseUploadVo.isUploadFlag());
                        RestResultVo<TestCase> testCaseResult = this.formatTestCase(testCase);
                        if (testCaseResult.isSuccess()) {
                            testCaseList.add(testCase);
                        } else {
                            sb.append(testCaseResult.getMessage()).append("\n");
                        }

                    }
                }
            }
        }
        return testCaseList;
    }

    /**
     * 将Xmind内容转换为测试用例
     *
     * @param child
     * @param testCaseUploadVo
     * @return
     */
    public TestCase parseTestCase(Attached child, TestCaseUploadVo testCaseUploadVo) throws Exception {
        //1. 用例等级&用例名称
        String[] caseNameAndLevelArray = child.getTitle().split(":");
        String level = null;
        try {
            level = caseNameAndLevelArray[0].split("-")[1].toUpperCase();
        } catch (Exception e) {
            throw new CustomException(child.getTitle() + "\n !!!用例优先级格式不正确!!!");
        }
        String casename = null;
        try {
            casename = caseNameAndLevelArray[1];
        } catch (Exception e) {
            log.error("xmind解析，测试用例名称解析失败！！！", e);
            throw new CustomException(child.getTitle() + "\n !!!用例名称输入不正确!!!");
        }

        //2. 用例前置条件&期望结果
        AtomicReference<String> preCondition = new AtomicReference<>("");
        AtomicReference<String> expectedResults = new AtomicReference<>("");
        List<Attached> leafChildren = child.getParent().getAttachedChildren();
        // 需要抛异常，所以要改成for循环形式
        for (Attached leafChild : leafChildren) {
            String leafChildText = leafChild.getTitle();
            if (isAvailable(leafChildText, PC_REGEX)) {
                String pre = null;
                try {
                    pre = leafChildText.split("pc:")[1].trim();
                } catch (Exception e) {
                    log.error("xmind解析，测试用例前置条件解析失败！！！", e);
                    throw new CustomException(leafChildText + "\n !!!前置条件输入不正确!!!");
                }
                preCondition.set(pre);
            }
            if (isAvailable(leafChildText, RC_REGEX)) {
                String expectedResult = null;
                try {
                    expectedResult = leafChildText.split("rc:")[1].trim();
                } catch (Exception e) {
                    log.error("xmind解析，测试用例执行结果解析失败！！！", e);
                    throw new CustomException(leafChildText + "\n !!!执行结果输入不正确!!!");
                }
                expectedResults.set(expectedResult);
            }
        }

        //3. 用例执行步骤
        List<String> list = Lists.newArrayList();
        Attached newChild = child.getParent();
        while (!newChild.isRoot()) {
            String title = newChild.getTitle();
            list.add(title);
            newChild = newChild.getParent();
        }
        Collections.reverse(list);
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < list.size(); i++) {
            sb.append(i + 1);
            sb.append(". ");
            sb.append(list.get(i));
            sb.append(" ");
            sb.append("\n");
        }
        String step = sb.toString().trim();

        //4. 格式化用例对象
        TestCase testCase = new TestCase();
        testCase.setCasename(casename);
        testCase.setTestCaseLevel(level);
        testCase.setStep(step);
        testCase.setExpectedResults(expectedResults.get());
        if (StringUtils.isNotBlank(preCondition.get())) {
            testCase.setPreCondition(preCondition.get());
        }
        testCase = initBelongToAttr(testCase, testCaseUploadVo);
        return testCase;
    }

    /**
     * 上传测试用例默认属性设置
     *
     * @param testCase
     * @param testCaseUploadVo
     * @return
     */
    public TestCase initBelongToAttr(TestCase testCase, TestCaseUploadVo testCaseUploadVo) {
        testCase.setRelationRequirement(testCaseUploadVo.getRelationRequirement());
        testCase.setBelongToSystem(testCaseUploadVo.getBelongToSystem());
        testCase.setBelongToModule(testCaseUploadVo.getBelongToModule());
        testCase.setCreateUser(testCaseUploadVo.getUserName());
        testCase.setUpdateUser(testCaseUploadVo.getUserName());
        testCase.setCreateTime(LocalDateTime.now());
        testCase.setUpdateTime(LocalDateTime.now());
        return testCase;
    }

    /**
     * 批量校验用例名称是否重复
     */
    public String batchValidateUploadFileTestCaseNameListRepeat(List<TestCase> testCaseList) {
        if (CollectionUtils.isNotEmpty(testCaseList)) {
            testCaseList.stream().forEach(testCase -> {
                if (StringUtils.isBlank(testCase.getCasename())) {
                    throw new CustomException("文档中有【用例名称】为空的项，请修改后上传！");
                }
            });
            String startName = "重复用例名称:";
            Map<String, List<TestCase>> caseNameMap = testCaseList.stream().collect(Collectors.groupingBy(TestCase::getCasename));
            StringBuilder sb = new StringBuilder(startName);
            for (String caseName : caseNameMap.keySet()) {
                List<TestCase> caseList = caseNameMap.get(caseName);
                if (caseList.size() > 1) {
                    sb.append(caseName + ",");
                }
            }
            if (!startName.equals(sb.toString())) {
                return sb.toString();
            } else {
                return "";
            }
        } else {
            return "";
        }
    }

    /**
     * Xmind测试用例上传
     *
     * @param rootTopic
     * @param testCaseUploadVo
     */
    @Override
    public String uploadTestCase4Xmind(Attached rootTopic, TestCaseUploadVo testCaseUploadVo) throws Exception {
        //1. 解析测试用例
        StringBuilder sb = new StringBuilder();
        List<TestCase> testCaseList = Lists.newArrayList();
        testCaseList = getXmindChildren(rootTopic.getAttachedChildren(), testCaseUploadVo, testCaseList, sb);

        //2. 验证Xmind文档自身用例名称是否重复
        String result = this.batchValidateUploadFileTestCaseNameListRepeat(testCaseList);
        if (StringUtils.isNotBlank(result)) {
            return result;
        } else {
            //3. 保存测试用例
            if (StringUtils.isNotBlank(sb.toString())) {
                throw new CustomException(sb.toString());
            } else {
                if (CollectionUtils.isNotEmpty(testCaseList)) {
                    this.batchSaveTestCase(testCaseList, testCaseUploadVo.isUploadFlag());
                    return "";
                } else {
                    return "解析结果为空";
                }
            }
        }
    }

    /**
     * 批量保存测试用例信息
     *
     * @param testCaseList
     * @param uploadFlag   上传标记，如果是false则更新同名测试用例
     */
    @Override
    public void batchSaveTestCase(List<TestCase> testCaseList, boolean uploadFlag) {
        if (CollectionUtils.isEmpty(testCaseList)) {
            return;
        }
        if (uploadFlag) {
            this.saveOrUpdateBatch(testCaseList);
        } else {
            List<String> caseNameList = testCaseList.stream().map(TestCase::getCasename).collect(Collectors.toList());
            QueryWrapper<TestCase> testCaseQueryWrapper = new QueryWrapper<>();
            testCaseQueryWrapper.eq("delete_flag", false);
            testCaseQueryWrapper.in("casename", caseNameList);
            //只校验最新版本 liangh4
            testCaseQueryWrapper.eq("flag", true);
            List<TestCase> oldTestCaseList = testCaseMapper.selectList(testCaseQueryWrapper);
            // list转map
            Map<String, TestCase> maps = testCaseList.stream().collect(Collectors.toMap(TestCase::getCasename, Function.identity()));
            this.updateUserTestCase(oldTestCaseList, maps);
            // 更新老的用例
            super.updateBatchById(oldTestCaseList);
            // 创建新的用例
            super.saveBatch(maps.values());

        }

    }

    /**
     * 更新老的用例和新用例
     *
     * @param oldTestCaseList
     * @param maps
     */
    private void updateUserTestCase(List<TestCase> oldTestCaseList, Map<String, TestCase> maps) {
        oldTestCaseList.stream().forEach(testCaseOld -> {
            TestCase testCase = maps.get(testCaseOld.getCasename());
            if (Objects.isNull(testCase)) {
                return;
            }
            testCaseOld.setFlag(false);
            if (testCaseOld.getCaseLock()) {
                //1. 复制一个对象，原测试用例作为历史版本不再更新
                String version = testCase.getVersion();
                testCase.setVersion(String.format("%.1f", Double.parseDouble(version) + 0.1));
                String caseKey = testCaseOld.getCaseKey();
                testCase.setFlag(true);
                testCase.setDeleteFlag(false);
                testCase.setUpdateTime(LocalDateTime.now());
                testCase.setCaseLock(false);
                testCase.setCaseKey(caseKey);
                testCase.setTestCaseStatus(TestCaseStatusEnum.PENDING.getTestCaseStatus());
            } else {
                //2. 未锁定状态,直接修改当前版本
                String version = testCaseOld.getVersion();
                if (StringUtils.isBlank(version)) {
                    version = QualityDefendeConstants.TEST_CASE_DEFAULT_VERSION;
                } else {
                    version = String.format("%.1f", Double.parseDouble(version) + 0.1);
                }
                testCase.setDeleteFlag(false);
                testCase.setFlag(true);
                testCase.setVersion(version);
                testCase.setUpdateTime(LocalDateTime.now());
                testCase.setTestCaseStatus(TestCaseStatusEnum.PENDING.getTestCaseStatus());
                // TODO 补偿新增的时候没有更新上caseKey 测试用例审批通过时新增CaseKey
                if (StringUtils.isBlank(testCaseOld.getCaseKey())) {
                    testCase.setCaseKey(UUID.randomUUID().toString().replace("-", ""));
                }
            }
        });
    }


    /**
     * 校验用例名称
     *
     * @param casename
     * @param id
     * @param moduleId
     * @return
     */
    private RestResultVo validateName(String casename, Long id, Long moduleId) {
        QueryWrapper<TestCase> testCaseQueryWrapper = new QueryWrapper<>();
        testCaseQueryWrapper.eq("delete_flag", false);
        testCaseQueryWrapper.eq("casename", casename);
        //只校验最新版本 liangh4
        testCaseQueryWrapper.eq("flag", true);
//        //加入判断是否是相同模块下的名称一致
//        testCaseQueryWrapper.eq(Objects.nonNull(moduleId), "belong_to_module", moduleId);
        //更新的操作不校验当前id liangh4
        if (!Objects.isNull(id)) {
            testCaseQueryWrapper.notIn("id", id);
        }

        List<TestCase> testCaseList = testCaseMapper.selectList(testCaseQueryWrapper);
        if (!testCaseList.isEmpty()) {
            return RestResultVo.fromErrorMessage("\n测试用例名称：[" + casename + "] 重复！");
        } else {
            return RestResultVo.fromSuccess(null);
        }
    }

    /**
     * 校验并格式化入参参数
     *
     * @param testCase
     * @return
     */
    private RestResultVo<TestCase> checkAndFormatTestCase(TestCase testCase) {

        //1. 验证测试用例
        //1.1. 验证测试用例所属系统是否存在(如果选择的,则需要校验)
        RestResultVo restResultVo = RestResultVo.fromData(null);
        if (Objects.isNull(testCase.getBelongToSystem())
                || Objects.isNull(testCase.getBelongToModule())) {
            restResultVo = RestResultVo.fromErrorMessage("所属应用/模块不存在！");
        }
        if (restResultVo.isSuccess()) {
            //1.2. 验证测试用例名称唯一
            // 上传判断是否需要校验名称，如果为false，则允许名称重复并覆盖用例
            if (testCase.isUploadFlag()) {
                restResultVo = this.validateName(testCase.getCasename(), testCase.getId(), testCase.getBelongToModule());
            }
            if (restResultVo.isSuccess()) {
                //1.3. 验证测试用例必填字段唯一
//                restResult = this.validateTestCaseRequiredField(testCase,restResult);
                //1.4. 验证测试用例jiraid是否正确
                TestResultVo jiraResult = JiraUtils.validateJiraInfo(testCase.getRelationRequirement());
                if (jiraResult.getFlag()) {
                    testCase.setRelationRequirement(jiraResult.getData().toString());
                } else {
                    restResultVo = RestResultVo.fromErrorMessage(jiraResult.getMsgRes());
                }
                //2. 格式化测试用例
                if (restResultVo.isSuccess()) {
                    if (!testCase.isPeopSaveFlag()) {
                        //2.1. 测试用例所属平台
                        testCase = this.formatTestCasePlatform(testCase);
                        if (StringUtils.isBlank(testCase.getBelongPlatform())) {
                            return RestResultVo.fromErrorMessage("所属平台为空！");
                        }
                        //2.2. 测试用例等级
                        testCase = this.formatTestCaseLevel(testCase);
                        if (StringUtils.isBlank(testCase.getTestCaseLevel())) {
                            return RestResultVo.fromErrorMessage("用例等级为空！");
                        }
                        //2.3. 测试用例类型
                        testCase = this.formatTestCaseType(testCase);
                        if (StringUtils.isBlank(testCase.getType())) {
                            return RestResultVo.fromErrorMessage("用例类型为空！");
                        }
                    }
                    if (Objects.isNull(testCase.getId())) {
                        //2.4. 系统默认字段
                        testCase = settingTestCaseDefaultAttribute(testCase);
                    }

                    restResultVo.setData(testCase);
                }
            }
        }
        return restResultVo;
    }

    /**
     * 设置测试用例默认属性
     *
     * @param testCase
     * @return
     */
    public TestCase settingTestCaseDefaultAttribute(TestCase testCase) {
        if (StringUtils.isBlank(testCase.getCaseKey())) {
            testCase.setCaseKey(UUID.randomUUID().toString().replace("-", ""));
        }
        testCase.setCreateTime(LocalDateTime.now());
        testCase.setUpdateTime(LocalDateTime.now());
        testCase.setVersion(QualityDefendeConstants.TEST_CASE_DEFAULT_VERSION);
        testCase.setFlag(true);
        testCase.setDeleteFlag(false);
        testCase.setCaseLock(false);
        testCase.setTestCaseStatus(TestCaseStatusEnum.PENDING.getTestCaseStatus());
        return testCase;
    }

    /**
     * 格式化测试用例所属平台（端）
     *
     * @param testCase
     * @return
     */
    private TestCase formatTestCasePlatform(TestCase testCase) {
        String testCaseBelongPlatform = testCase.getBelongPlatform();
        if (StringUtils.isNotBlank(testCaseBelongPlatform)) {
            // 测试所属平台（所属端）字典列表
            Map<String, String> applicationMap = DicUtil.dicListToMapName(QualityDefendeConstants.DATA_DICTIONARY_APPLICATION_TYPE);
            testCase.setBelongPlatform(applicationMap.get(testCaseBelongPlatform));
        }
        return testCase;
    }

    /**
     * 格式化测试用例等级
     *
     * @param testCase
     * @return
     */
    private TestCase formatTestCaseLevel(TestCase testCase) {
        String testCaseLevel = testCase.getTestCaseLevel();
        String level = "P3";
        if (StringUtils.isNotBlank(testCaseLevel) && isChinese(testCaseLevel)) {
            // 测试级别字典列表
            Map<String, String> levelMap = DicUtil.dicListToMapName(QualityDefendeConstants.DATA_DICTIONARY_TEST_LEVEL_TYPE);
            // 转成map用于匹配信息
            testCase.setTestCaseLevel(levelMap.get(testCaseLevel));
        } else if (StringUtils.isNotBlank(testCaseLevel)) {
            testCase.setTestCaseLevel(testCaseLevel.toUpperCase());
        } else {
            testCase.setTestCaseLevel(level);
        }
        return testCase;
    }

    /**
     * 格式化测试用例类型
     *
     * @param testCase
     * @return
     */
    private TestCase formatTestCaseType(TestCase testCase) {
        String testCaseType = testCase.getType();
        if (StringUtils.isNotBlank(testCaseType) && isChinese(testCaseType)) {
            // 测试策略（类型）字典列表
            Map<String, String> typeMap = DicUtil.dicListToMapName(QualityDefendeConstants.DATA_DICTIONARY_STRATEGY_TYPE);
            testCase.setType(typeMap.get(testCaseType));
        }
        return testCase;
    }


    /**
     * 判断是否为汉字
     *
     * @param testCaseLevel
     * @return
     */
    private boolean isChinese(String testCaseLevel) {
        for (int i = 0; i < testCaseLevel.length(); i = i + 1) {
            if (!CHINESE_PATTERN.matcher(
                    String.valueOf(testCaseLevel.charAt(i))).find()) {
                return false;
            }
        }
        return true;
    }

    /**
     * 组织参数给列表页面
     *
     * @param caseList
     * @param testCase
     */
    @Override
    public void assableTestInfo(List<TestCase> caseList, TestCase testCase) {
        if (CollectionUtils.isEmpty(caseList) && Objects.isNull(testCase)) {
            return;
        }
        // 测试级别字典列表
        Map<String, String> levelMap = DicUtil.dicListToMapEnName(QualityDefendeConstants.DATA_DICTIONARY_TEST_LEVEL_TYPE);
        // 测试策略（类型）字典列表
        Map<String, String> typeMap = DicUtil.dicListToMapEnName(QualityDefendeConstants.DATA_DICTIONARY_STRATEGY_TYPE);
        // 测试所属平台（所属端）字典列表
        Map<String, String> applicationMap = DicUtil.dicListToMapEnName(QualityDefendeConstants.DATA_DICTIONARY_APPLICATION_TYPE);
        // 1.给list赋值
        if (CollectionUtils.isNotEmpty(caseList)) {
            List<String> userList = caseList.stream().distinct().map(TestCase::getCreateUser).collect(Collectors.toList());
            Map<String, MatrixUserDetail> userMap = matrixService.getUserDetailByEmailPre(userList);
            caseList.forEach(testcase -> {
                // 设置测试环境字符
                this.assableTestCase(testcase, levelMap, typeMap, applicationMap);
                String userName = Optional.ofNullable(userMap)
                        .map(item -> item.get(testcase.getCreateUser()))
                        .map(detail -> detail.getUserName())
                        .orElse("");
                testcase.setCreateUserStr(userName);
            });
        }
        // 2.给对象赋值
        if (!Objects.isNull(testCase)) {
            // 设置测试环境字符
            this.assableTestCase(testCase, levelMap, typeMap, applicationMap);
        }
    }

    /**
     * 根据caseKey获取测试用例信息
     *
     * @param caseKey
     * @return
     */
    @Override
    public TestCase getTestCaseByCaseKey(String caseKey) {
        if (StringUtils.isBlank(caseKey)) {
            return null;
        }
        QueryWrapper<TestCase> testCaseQueryWrapper = new QueryWrapper<>();
        testCaseQueryWrapper.eq("delete_flag", false);
        testCaseQueryWrapper.eq("case_key", caseKey);
        testCaseQueryWrapper.eq("flag", true);
        return testCaseMapper.selectOne(testCaseQueryWrapper);
    }

    @Override
    public TestResultVo saveOrUpdateTestCase(TestCase testCase) {
        RestResultVo<TestCase> restResultVo = this.checkAndFormatTestCase(testCase);
        if (restResultVo.isSuccess()) {
            testCase = restResultVo.getData();
            boolean flag = super.saveOrUpdate(testCase);
            return TestResultVo.builder().flag(flag).msgRes(flag ? TestCenterConstants.RES_MSG_SUCCESS : TestCenterConstants.RES_MSG_ERROR).build();
        }
        return TestResultVo.builder().flag(false).msgRes(restResultVo.getMessage()).build();
    }

    /**
     * 给测试用例塞入表现值
     *
     * @param testCase
     * @param levelMap
     * @param typeMap
     * @param applicationMap
     */
    private void assableTestCase(TestCase testCase,
                                 Map<String, String> levelMap,
                                 Map<String, String> typeMap,
                                 Map<String, String> applicationMap) {
        // 设置测试等级
        testCase.setTestCaseLevelStr(levelMap.get(testCase.getTestCaseLevel()));
        // 设置测试类型
        testCase.setTypeStr(typeMap.get(testCase.getType()));
        // 设置测试平台
        testCase.setBelongPlatformStr(applicationMap.get(testCase.getBelongPlatform()));
        // 设置测试所属应用
        if (Objects.nonNull(testCase.getBelongToSystem())) {
            TestApplication ta = null;
            try {
                ta = testApplicationService.getById(testCase.getBelongToSystem());
                if (!Objects.isNull(ta)) {
                    testCase.setBelongToSystemStr(ta.getApplicationName());
                }
            } catch (NumberFormatException e) {
                log.info("testApplicationService 应用获取：{}", testCase.getBelongToSystem());
            }
        }
        // 设置测试所属模块
        if (Objects.nonNull(testCase.getBelongToModule())) {
            try {
                TestApplicationModule tam = testApplicationModuleService.getById(testCase.getBelongToModule());
                if (!Objects.isNull(tam)) {
                    testCase.setBelongToModuleStr(tam.getModuleName());
                    testCase.setModuleTreePath(tam.getModuleTreePath());
                }
            } catch (NumberFormatException e) {
                log.info("testApplicationModuleService 模块获取：{}", testCase.getBelongToModule());
            }
        }
        // 设置测试所属项目
//        if(!Objects.isNull(testCase.getBelongTopic())){
//            TestTopic topic = testTopicService.getById(testCase.getBelongTopic());
//            if(!Objects.isNull(topic)){
//                testCase.setBelongTopicStr(topic.getTopicName());
//            }
//        }
        // 设置状态字符串
        testCase.setTestCaseStatusStr(TestCaseStatusEnum.getTestCaseName(testCase.getTestCaseStatus()));
    }

    /**
     * 导出测试用例数据
     *
     * @param testCaseList
     * @return
     */
    private List<TestCaseDownVo> exportTestCaseToExcelData(List<TestCase> testCaseList) {
        List<TestCaseDownVo> testCaseDownList = new ArrayList<>();
        // 测试级别字典列表
        Map<String, String> levelMap = DicUtil.dicListToMapEnName(QualityDefendeConstants.DATA_DICTIONARY_TEST_LEVEL_TYPE);
        // 测试策略（类型）字典列表
        Map<String, String> typeMap = DicUtil.dicListToMapEnName(QualityDefendeConstants.DATA_DICTIONARY_STRATEGY_TYPE);
        // 测试所属平台（所属端）字典列表
        Map<String, String> applicationMap = DicUtil.dicListToMapEnName(QualityDefendeConstants.DATA_DICTIONARY_APPLICATION_TYPE);
        // 需要处理导出的测试用例信息
        for (TestCase testCase : testCaseList) {
            TestCaseDownVo downVo = TestCaseDownVo.builder()
                    .casename(testCase.getCasename())
                    .testCaseLevel(testCase.getPreCondition())
                    .step(testCase.getStep())
                    .expectedResults(testCase.getExpectedResults())
                    .build();
            //1. 处理所属平台（所属端）
            downVo.setBelongPlatform(applicationMap.get(testCase.getBelongPlatform()));
            //2. 处理测试用例优先级
            downVo.setTestCaseLevel(levelMap.get(testCase.getTestCaseLevel()));
            //3. 设置测试所属应用
            if (Objects.nonNull(testCase.getBelongToSystem())) {
                TestApplication ta = testApplicationService.getById(testCase.getBelongToSystem());
                if (!Objects.isNull(ta)) {
                    downVo.setBelongToSystem(ta.getApplicationName());
                }
            }
            //4. 设置测试所属模块
            if (Objects.nonNull(testCase.getBelongToModule())) {
                TestApplicationModule tam = testApplicationModuleService.getById(testCase.getBelongToModule());
                if (!Objects.isNull(tam)) {
                    downVo.setBelongToModule(tam.getModuleName());
                }
            }
            //5. 处理项目名称
//            if(!Objects.isNull(testCase.getBelongTopic())){
//                TestTopic topic = testTopicService.getById(testCase.getBelongTopic());
//                if(!Objects.isNull(topic)){
//                    downVo.setBelongTopic(topic.getTopicName());
//                }
//            }
            //6. 处理测试类型名称
            downVo.setType(typeMap.get(testCase.getType()));

            testCaseDownList.add(downVo);
        }
        return testCaseDownList;
    }

    /**
     * 导出Excel
     *
     * @param response
     * @param fileName
     * @return
     */
    private HttpServletResponse exportExcel(HttpServletResponse response, String fileName) {
        response.setContentType("application/vnd.ms-excel;charset=utf-8");
        response.setCharacterEncoding("utf-8");
        response.setHeader("Content-disposition", "attachment;filename=" + fileName + ".xlsx");
        return response;
    }


    /**
     * 下载Excel
     *
     * @param response
     * @param sheetName
     * @param testCaseDownVoList
     * @throws IOException
     */
    private void writeExcel(HttpServletResponse response, String sheetName, List<TestCaseDownVo> testCaseDownVoList) throws IOException {
        EasyExcel.write(response.getOutputStream(), TestCaseDownVo.class).sheet(sheetName).registerWriteHandler(customSheetWriteHandler).doWrite(testCaseDownVoList);
    }

}
