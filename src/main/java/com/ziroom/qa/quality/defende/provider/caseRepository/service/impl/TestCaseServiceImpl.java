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
        //1. ?????????????????????
        QueryWrapper<TestCase> testCaseQueryWrapper = new QueryWrapper<>();
        testCaseQueryWrapper.eq(null != testCase.getFlag(), "flag", testCase.getFlag())
                // ?????????????????????,??????????????????id liangh4
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

                // ??????????????????
                .eq(StringUtils.isNotBlank(testCase.getTestCaseLevel()), "test_case_level", testCase.getTestCaseLevel())
                .eq(StringUtils.isNotBlank(testCase.getRelationRequirement()), "relation_requirement", testCase.getRelationRequirement())
                .eq(Objects.nonNull(testCase.getBelongToSystem()), "belong_to_system", testCase.getBelongToSystem())
                // ehr?????????????????????

                .eq(Objects.nonNull(testCase.getBelongToModule()), "belong_to_module", testCase.getBelongToModule())
                .eq(StringUtils.isNotBlank(testCase.getType()), "type", testCase.getType())
                .eq(StringUtils.isNotBlank(testCase.getBelongPlatform()), "belong_platform", testCase.getBelongPlatform())
                .eq(!Objects.isNull(testCase.getBelongTopic()), "belong_topic", testCase.getBelongTopic())
        ;
        //2. ??????????????????
        if (StringUtils.isNotBlank(testCase.getCreateTimeVal())) {
            TimeVo timeVo = TimeUtil.parseTimeStr(testCase.getCreateTimeVal());
            testCaseQueryWrapper.between("create_time", timeVo.getStart(), timeVo.getEnd());
        }

        //3. ??????????????????
        if (StringUtils.isNotBlank(testCase.getUpdateTimeVal())) {
            TimeVo timeVo = TimeUtil.parseTimeStr(testCase.getUpdateTimeVal());
            testCaseQueryWrapper.between("update_time", timeVo.getStart(), timeVo.getEnd());
        }
        //4. ??????ehr??????
        if (StringUtils.isNotBlank(testCase.getEhrTreePath())
                && Objects.isNull(testCase.getBelongToSystem())) {
            QueryWrapper<TestApplication> queryWrapper = new QueryWrapper<>();
            queryWrapper.like("ehr_tree_path", testCase.getEhrTreePath());
            List<TestApplication> appList = testApplicationService.list(queryWrapper);
            List<String> appIdList = appList.stream().map(application -> application.getId() + "").collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(appIdList)) {
                testCaseQueryWrapper.in("belong_to_system", appIdList);
            } else {
                // ?????????????????????
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
     * ??????????????????????????????????????????
     *
     * @param belongToSystem
     * @return
     */
    @Override
    public RestResultVo validateBelongToSystem(String belongToSystem) {
//        log.info("???????????? belongToSystem???{}", belongToSystem);
//        RestResultVo<List<OmegaApplicationEnv>> list = ciFeignClient.queryEnvByLevelAndDomain(belongToSystem, "prod");
//        log.info("????????????Omega ????????????????????????{}", JSON.toJSONString(list));
//        if (list.getData().isEmpty()) {
//            return RestResultVo.fromErrorMessage("Omega??????????????????????????????????????????[" + belongToSystem + "]");
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
     * ????????????????????????
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
                // ??????????????????
                if (StringUtils.isNotBlank(changeType)) {
                    testCase.setChangeType(changeType);
                }
                newTestCaseList.add(testCase);
            } else {
                testCase.setTestCaseStatus(TestCaseStatusEnum.REJECTED.getTestCaseStatus());
                testCase.setCaseLock(false);
            }
            // TODO ????????????????????????????????????caseKey ?????????????????????????????????CaseKey
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
        //1. ??????????????????
        if (null == testCase) {
            testCase = new TestCase();
        }
        testCase.setDeleteFlag(false);
        testCase.setFlag(true);
        testCase.setTestCaseStatus(TestCaseStatusEnum.APPROVED.getTestCaseStatus());
        //2. ?????????????????????
        QueryWrapper<TestCase> testCaseQueryWrapper = this.getTestCaseQueryWrapper(testCase);

        //3. ?????????????????????????????????????????????
        Long taskId = testCase.getTaskId();
        if (null != taskId) {
            List<TaskTestCase> testTestCaseList = taskTestCaseService.getTaskTestCaseByTaskId(taskId);
            if (CollectionUtils.isNotEmpty(testTestCaseList)) {
                List<String> caseKeyList = testTestCaseList.stream().map(TaskTestCase::getCaseKey).collect(Collectors.toList());
                testCaseQueryWrapper.notIn("case_key", caseKeyList);
            }
        }
        testCaseQueryWrapper.orderByDesc("id");

        //4. ????????????
        return this.list(testCaseQueryWrapper);
    }

    /**
     * ??????????????????
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
        // ????????????
        // ????????????????????????,???????????????????????????true,????????????,
        // ????????????????????????false
        Boolean caseLock = testCase.getCaseLock();
        try {
            Long caseId = testCase.getId();
            TestCase testCaseOld = this.getById(caseId);
            if (caseLock) {
                //1. ??????????????????????????????????????????????????????????????????
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
                //2. ???????????????,????????????????????????
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
                // TODO ????????????????????????????????????caseKey ?????????????????????????????????CaseKey
                if (StringUtils.isBlank(testCaseOld.getCaseKey())) {
                    testCase.setCaseKey(UUID.randomUUID().toString().replace("-", ""));
                }
                flag = this.updateById(testCase);
            }
        } catch (Exception e) {
            log.error("??????????????????,????????????testCase.getId:{}", testCase.getId(), e);
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
        //1. ?????????????????????
        QueryWrapper<TestCase> testCaseQueryWrapper = this.getTestCaseQueryWrapper(testCase);
        testCaseQueryWrapper.orderByDesc("id");
        //2. ?????????
        List<TestCase> testCaseList = this.list(testCaseQueryWrapper);
        List<Long> idList = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(testCaseList)) {
            idList = testCaseList.stream().map(TestCase::getId).collect(Collectors.toList());
        }
        return idList;
    }

    @Override
    public Page<TestCase> queryTestCaseByPage(Page<TestCase> page, TestCase testCase) {
        //1. ?????????????????????
        QueryWrapper<TestCase> testCaseQueryWrapper = this.getTestCaseQueryWrapper(testCase);
        testCaseQueryWrapper.orderByDesc("id");

        //2. ????????????
        Page<TestCase> testCasePageResult = this.page(page, testCaseQueryWrapper);
        if (Objects.nonNull(testCasePageResult) && CollectionUtils.isNotEmpty(testCasePageResult.getRecords())) {
            List<TestCase> caseList = testCasePageResult.getRecords();
            this.assableTestInfo(caseList, null);
            testCasePageResult.setRecords(caseList);
        }
        return testCasePageResult;
    }

    /**
     * ??????ID????????????????????????
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
     * ??????????????????????????????????????????
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
     * ??????????????????????????????????????????
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
     * ?????????????????????????????????
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
     * ??????xmind??????
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
     * ??????????????????
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
                        TestCase testCase = parseTestCase(child, testCaseUploadVo);//????????????????????????
                        testCase.setBelongPlatform(testCaseUploadVo.getBelongPlatform());
                        testCase.setType(testCaseUploadVo.getType());
                        testCase.setCreateUser(testCaseUploadVo.getUserName());
                        testCase.setUpdateUser(testCaseUploadVo.getUserName());
                        // ????????????????????????????????????
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
     * ???Xmind???????????????????????????
     *
     * @param child
     * @param testCaseUploadVo
     * @return
     */
    public TestCase parseTestCase(Attached child, TestCaseUploadVo testCaseUploadVo) throws Exception {
        //1. ????????????&????????????
        String[] caseNameAndLevelArray = child.getTitle().split(":");
        String level = null;
        try {
            level = caseNameAndLevelArray[0].split("-")[1].toUpperCase();
        } catch (Exception e) {
            throw new CustomException(child.getTitle() + "\n !!!??????????????????????????????!!!");
        }
        String casename = null;
        try {
            casename = caseNameAndLevelArray[1];
        } catch (Exception e) {
            log.error("xmind????????????????????????????????????????????????", e);
            throw new CustomException(child.getTitle() + "\n !!!???????????????????????????!!!");
        }

        //2. ??????????????????&????????????
        AtomicReference<String> preCondition = new AtomicReference<>("");
        AtomicReference<String> expectedResults = new AtomicReference<>("");
        List<Attached> leafChildren = child.getParent().getAttachedChildren();
        // ?????????????????????????????????for????????????
        for (Attached leafChild : leafChildren) {
            String leafChildText = leafChild.getTitle();
            if (isAvailable(leafChildText, PC_REGEX)) {
                String pre = null;
                try {
                    pre = leafChildText.split("pc:")[1].trim();
                } catch (Exception e) {
                    log.error("xmind??????????????????????????????????????????????????????", e);
                    throw new CustomException(leafChildText + "\n !!!???????????????????????????!!!");
                }
                preCondition.set(pre);
            }
            if (isAvailable(leafChildText, RC_REGEX)) {
                String expectedResult = null;
                try {
                    expectedResult = leafChildText.split("rc:")[1].trim();
                } catch (Exception e) {
                    log.error("xmind??????????????????????????????????????????????????????", e);
                    throw new CustomException(leafChildText + "\n !!!???????????????????????????!!!");
                }
                expectedResults.set(expectedResult);
            }
        }

        //3. ??????????????????
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

        //4. ?????????????????????
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
     * ????????????????????????????????????
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
     * ????????????????????????????????????
     */
    public String batchValidateUploadFileTestCaseNameListRepeat(List<TestCase> testCaseList) {
        if (CollectionUtils.isNotEmpty(testCaseList)) {
            testCaseList.stream().forEach(testCase -> {
                if (StringUtils.isBlank(testCase.getCasename())) {
                    throw new CustomException("??????????????????????????????????????????????????????????????????");
                }
            });
            String startName = "??????????????????:";
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
     * Xmind??????????????????
     *
     * @param rootTopic
     * @param testCaseUploadVo
     */
    @Override
    public String uploadTestCase4Xmind(Attached rootTopic, TestCaseUploadVo testCaseUploadVo) throws Exception {
        //1. ??????????????????
        StringBuilder sb = new StringBuilder();
        List<TestCase> testCaseList = Lists.newArrayList();
        testCaseList = getXmindChildren(rootTopic.getAttachedChildren(), testCaseUploadVo, testCaseList, sb);

        //2. ??????Xmind????????????????????????????????????
        String result = this.batchValidateUploadFileTestCaseNameListRepeat(testCaseList);
        if (StringUtils.isNotBlank(result)) {
            return result;
        } else {
            //3. ??????????????????
            if (StringUtils.isNotBlank(sb.toString())) {
                throw new CustomException(sb.toString());
            } else {
                if (CollectionUtils.isNotEmpty(testCaseList)) {
                    this.batchSaveTestCase(testCaseList, testCaseUploadVo.isUploadFlag());
                    return "";
                } else {
                    return "??????????????????";
                }
            }
        }
    }

    /**
     * ??????????????????????????????
     *
     * @param testCaseList
     * @param uploadFlag   ????????????????????????false???????????????????????????
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
            //????????????????????? liangh4
            testCaseQueryWrapper.eq("flag", true);
            List<TestCase> oldTestCaseList = testCaseMapper.selectList(testCaseQueryWrapper);
            // list???map
            Map<String, TestCase> maps = testCaseList.stream().collect(Collectors.toMap(TestCase::getCasename, Function.identity()));
            this.updateUserTestCase(oldTestCaseList, maps);
            // ??????????????????
            super.updateBatchById(oldTestCaseList);
            // ??????????????????
            super.saveBatch(maps.values());

        }

    }

    /**
     * ??????????????????????????????
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
                //1. ??????????????????????????????????????????????????????????????????
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
                //2. ???????????????,????????????????????????
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
                // TODO ????????????????????????????????????caseKey ?????????????????????????????????CaseKey
                if (StringUtils.isBlank(testCaseOld.getCaseKey())) {
                    testCase.setCaseKey(UUID.randomUUID().toString().replace("-", ""));
                }
            }
        });
    }


    /**
     * ??????????????????
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
        //????????????????????? liangh4
        testCaseQueryWrapper.eq("flag", true);
//        //???????????????????????????????????????????????????
//        testCaseQueryWrapper.eq(Objects.nonNull(moduleId), "belong_to_module", moduleId);
        //??????????????????????????????id liangh4
        if (!Objects.isNull(id)) {
            testCaseQueryWrapper.notIn("id", id);
        }

        List<TestCase> testCaseList = testCaseMapper.selectList(testCaseQueryWrapper);
        if (!testCaseList.isEmpty()) {
            return RestResultVo.fromErrorMessage("\n?????????????????????[" + casename + "] ?????????");
        } else {
            return RestResultVo.fromSuccess(null);
        }
    }

    /**
     * ??????????????????????????????
     *
     * @param testCase
     * @return
     */
    private RestResultVo<TestCase> checkAndFormatTestCase(TestCase testCase) {

        //1. ??????????????????
        //1.1. ??????????????????????????????????????????(???????????????,???????????????)
        RestResultVo restResultVo = RestResultVo.fromData(null);
        if (Objects.isNull(testCase.getBelongToSystem())
                || Objects.isNull(testCase.getBelongToModule())) {
            restResultVo = RestResultVo.fromErrorMessage("????????????/??????????????????");
        }
        if (restResultVo.isSuccess()) {
            //1.2. ??????????????????????????????
            // ????????????????????????????????????????????????false???????????????????????????????????????
            if (testCase.isUploadFlag()) {
                restResultVo = this.validateName(testCase.getCasename(), testCase.getId(), testCase.getBelongToModule());
            }
            if (restResultVo.isSuccess()) {
                //1.3. ????????????????????????????????????
//                restResult = this.validateTestCaseRequiredField(testCase,restResult);
                //1.4. ??????????????????jiraid????????????
//                TestResultVo jiraResult = JiraUtils.validateJiraInfo(testCase.getRelationRequirement());
//                if (jiraResult.getFlag()) {
//                    testCase.setRelationRequirement(jiraResult.getData().toString());
//                } else {
//                    restResultVo = RestResultVo.fromErrorMessage(jiraResult.getMsgRes());
//                }
                //2. ?????????????????????
                if (restResultVo.isSuccess()) {
                    if (!testCase.isPeopSaveFlag()) {
                        //2.1. ????????????????????????
                        testCase = this.formatTestCasePlatform(testCase);
                        if (StringUtils.isBlank(testCase.getBelongPlatform())) {
                            return RestResultVo.fromErrorMessage("?????????????????????");
                        }
                        //2.2. ??????????????????
                        testCase = this.formatTestCaseLevel(testCase);
                        if (StringUtils.isBlank(testCase.getTestCaseLevel())) {
                            return RestResultVo.fromErrorMessage("?????????????????????");
                        }
                        //2.3. ??????????????????
                        testCase = this.formatTestCaseType(testCase);
                        if (StringUtils.isBlank(testCase.getType())) {
                            return RestResultVo.fromErrorMessage("?????????????????????");
                        }
                    }
                    if (Objects.isNull(testCase.getId())) {
                        //2.4. ??????????????????
                        testCase = settingTestCaseDefaultAttribute(testCase);
                    }

                    restResultVo.setData(testCase);
                }
            }
        }
        return restResultVo;
    }

    /**
     * ??????????????????????????????
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
     * ??????????????????????????????????????????
     *
     * @param testCase
     * @return
     */
    private TestCase formatTestCasePlatform(TestCase testCase) {
        String testCaseBelongPlatform = testCase.getBelongPlatform();
        if (StringUtils.isNotBlank(testCaseBelongPlatform)) {
            // ?????????????????????????????????????????????
            Map<String, String> applicationMap = DicUtil.dicListToMapName(QualityDefendeConstants.DATA_DICTIONARY_APPLICATION_TYPE);
            testCase.setBelongPlatform(applicationMap.get(testCaseBelongPlatform));
        }
        return testCase;
    }

    /**
     * ???????????????????????????
     *
     * @param testCase
     * @return
     */
    private TestCase formatTestCaseLevel(TestCase testCase) {
        String testCaseLevel = testCase.getTestCaseLevel();
        String level = "P3";
        if (StringUtils.isNotBlank(testCaseLevel) && isChinese(testCaseLevel)) {
            // ????????????????????????
            Map<String, String> levelMap = DicUtil.dicListToMapName(QualityDefendeConstants.DATA_DICTIONARY_TEST_LEVEL_TYPE);
            // ??????map??????????????????
            testCase.setTestCaseLevel(levelMap.get(testCaseLevel));
        } else if (StringUtils.isNotBlank(testCaseLevel)) {
            testCase.setTestCaseLevel(testCaseLevel.toUpperCase());
        } else {
            testCase.setTestCaseLevel(level);
        }
        return testCase;
    }

    /**
     * ???????????????????????????
     *
     * @param testCase
     * @return
     */
    private TestCase formatTestCaseType(TestCase testCase) {
        String testCaseType = testCase.getType();
        if (StringUtils.isNotBlank(testCaseType) && isChinese(testCaseType)) {
            // ????????????????????????????????????
            Map<String, String> typeMap = DicUtil.dicListToMapName(QualityDefendeConstants.DATA_DICTIONARY_STRATEGY_TYPE);
            testCase.setType(typeMap.get(testCaseType));
        }
        return testCase;
    }


    /**
     * ?????????????????????
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
     * ???????????????????????????
     *
     * @param caseList
     * @param testCase
     */
    @Override
    public void assableTestInfo(List<TestCase> caseList, TestCase testCase) {
        if (CollectionUtils.isEmpty(caseList) && Objects.isNull(testCase)) {
            return;
        }
        // ????????????????????????
        Map<String, String> levelMap = DicUtil.dicListToMapEnName(QualityDefendeConstants.DATA_DICTIONARY_TEST_LEVEL_TYPE);
        // ????????????????????????????????????
        Map<String, String> typeMap = DicUtil.dicListToMapEnName(QualityDefendeConstants.DATA_DICTIONARY_STRATEGY_TYPE);
        // ?????????????????????????????????????????????
        Map<String, String> applicationMap = DicUtil.dicListToMapEnName(QualityDefendeConstants.DATA_DICTIONARY_APPLICATION_TYPE);
        // 1.???list??????
        if (CollectionUtils.isNotEmpty(caseList)) {
            List<String> userList = caseList.stream().distinct().map(TestCase::getCreateUser).collect(Collectors.toList());
            Map<String, MatrixUserDetail> userMap = matrixService.getUserDetailByEmailPre(userList);
            caseList.forEach(testcase -> {
                // ????????????????????????
                this.assableTestCase(testcase, levelMap, typeMap, applicationMap);
                String userName = Optional.ofNullable(userMap)
                        .map(item -> item.get(testcase.getCreateUser()))
                        .map(detail -> detail.getUserName())
                        .orElse("");
                testcase.setCreateUserStr(userName);
            });
        }
        // 2.???????????????
        if (!Objects.isNull(testCase)) {
            // ????????????????????????
            this.assableTestCase(testCase, levelMap, typeMap, applicationMap);
        }
    }

    /**
     * ??????caseKey????????????????????????
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
     * ??????????????????????????????
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
        // ??????????????????
        testCase.setTestCaseLevelStr(levelMap.get(testCase.getTestCaseLevel()));
        // ??????????????????
        testCase.setTypeStr(typeMap.get(testCase.getType()));
        // ??????????????????
        testCase.setBelongPlatformStr(applicationMap.get(testCase.getBelongPlatform()));
        // ????????????????????????
        if (Objects.nonNull(testCase.getBelongToSystem())) {
            TestApplication ta = null;
            try {
                ta = testApplicationService.getById(testCase.getBelongToSystem());
                if (!Objects.isNull(ta)) {
                    testCase.setBelongToSystemStr(ta.getApplicationName());
                }
            } catch (NumberFormatException e) {
                log.info("testApplicationService ???????????????{}", testCase.getBelongToSystem());
            }
        }
        // ????????????????????????
        if (Objects.nonNull(testCase.getBelongToModule())) {
            try {
                TestApplicationModule tam = testApplicationModuleService.getById(testCase.getBelongToModule());
                if (!Objects.isNull(tam)) {
                    testCase.setBelongToModuleStr(tam.getModuleName());
                    testCase.setModuleTreePath(tam.getModuleTreePath());
                }
            } catch (NumberFormatException e) {
                log.info("testApplicationModuleService ???????????????{}", testCase.getBelongToModule());
            }
        }
        // ????????????????????????
//        if(!Objects.isNull(testCase.getBelongTopic())){
//            TestTopic topic = testTopicService.getById(testCase.getBelongTopic());
//            if(!Objects.isNull(topic)){
//                testCase.setBelongTopicStr(topic.getTopicName());
//            }
//        }
        // ?????????????????????
        testCase.setTestCaseStatusStr(TestCaseStatusEnum.getTestCaseName(testCase.getTestCaseStatus()));
    }

    /**
     * ????????????????????????
     *
     * @param testCaseList
     * @return
     */
    private List<TestCaseDownVo> exportTestCaseToExcelData(List<TestCase> testCaseList) {
        List<TestCaseDownVo> testCaseDownList = new ArrayList<>();
        // ????????????????????????
        Map<String, String> levelMap = DicUtil.dicListToMapEnName(QualityDefendeConstants.DATA_DICTIONARY_TEST_LEVEL_TYPE);
        // ????????????????????????????????????
        Map<String, String> typeMap = DicUtil.dicListToMapEnName(QualityDefendeConstants.DATA_DICTIONARY_STRATEGY_TYPE);
        // ?????????????????????????????????????????????
        Map<String, String> applicationMap = DicUtil.dicListToMapEnName(QualityDefendeConstants.DATA_DICTIONARY_APPLICATION_TYPE);
        // ???????????????????????????????????????
        for (TestCase testCase : testCaseList) {
            TestCaseDownVo downVo = TestCaseDownVo.builder()
                    .casename(testCase.getCasename())
                    .testCaseLevel(testCase.getPreCondition())
                    .step(testCase.getStep())
                    .expectedResults(testCase.getExpectedResults())
                    .build();
            //1. ?????????????????????????????????
            downVo.setBelongPlatform(applicationMap.get(testCase.getBelongPlatform()));
            //2. ???????????????????????????
            downVo.setTestCaseLevel(levelMap.get(testCase.getTestCaseLevel()));
            //3. ????????????????????????
            if (Objects.nonNull(testCase.getBelongToSystem())) {
                TestApplication ta = testApplicationService.getById(testCase.getBelongToSystem());
                if (!Objects.isNull(ta)) {
                    downVo.setBelongToSystem(ta.getApplicationName());
                }
            }
            //4. ????????????????????????
            if (Objects.nonNull(testCase.getBelongToModule())) {
                TestApplicationModule tam = testApplicationModuleService.getById(testCase.getBelongToModule());
                if (!Objects.isNull(tam)) {
                    downVo.setBelongToModule(tam.getModuleName());
                }
            }
            //5. ??????????????????
//            if(!Objects.isNull(testCase.getBelongTopic())){
//                TestTopic topic = testTopicService.getById(testCase.getBelongTopic());
//                if(!Objects.isNull(topic)){
//                    downVo.setBelongTopic(topic.getTopicName());
//                }
//            }
            //6. ????????????????????????
            downVo.setType(typeMap.get(testCase.getType()));

            testCaseDownList.add(downVo);
        }
        return testCaseDownList;
    }

    /**
     * ??????Excel
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
     * ??????Excel
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
