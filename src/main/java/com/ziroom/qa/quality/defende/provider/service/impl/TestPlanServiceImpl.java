package com.ziroom.qa.quality.defende.provider.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ziroom.qa.quality.defende.provider.constant.QualityDefendeConstants;
import com.ziroom.qa.quality.defende.provider.constant.TestCenterConstants;
import com.ziroom.qa.quality.defende.provider.constant.enums.TestPlanStatusEnum;
import com.ziroom.qa.quality.defende.provider.entity.DataDictionary;
import com.ziroom.qa.quality.defende.provider.entity.TestPlan;
import com.ziroom.qa.quality.defende.provider.mapper.TestPlanMapper;
import com.ziroom.qa.quality.defende.provider.outinterface.client.service.MatrixService;
import com.ziroom.qa.quality.defende.provider.service.DataDictionaryService;
import com.ziroom.qa.quality.defende.provider.service.TestPlanService;
import com.ziroom.qa.quality.defende.provider.util.MD5Util;
import com.ziroom.qa.quality.defende.provider.util.TimeUtil;
import com.ziroom.qa.quality.defende.provider.vo.MatrixUserDetail;
import com.ziroom.qa.quality.defende.provider.vo.Pagination;
import com.ziroom.qa.quality.defende.provider.vo.TestResultVo;
import com.ziroom.qa.quality.defende.provider.vo.TimeVo;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author liangh4
 * @since 2021-04-26
 */
@Service
public class TestPlanServiceImpl extends ServiceImpl<TestPlanMapper, TestPlan> implements TestPlanService {

    @Autowired
    private TestPlanMapper testPlanMapper;

    @Autowired
    private DataDictionaryService dataDictionaryService;

    @Autowired
    private MatrixService matrixService;

    /**
     * 分页查询用例计划
     *
     * @param pagination
     * @return
     */
    @Override
    public Page<TestPlan> queryTestPlanByPage(Pagination<TestPlan> pagination) {
        //1. 格式化查询参数
        pagination.getSearchObj().setLatestFlag(true);
        pagination.getSearchObj().setDeleteFlag(false);
        QueryWrapper<TestPlan> queryWrapper = this.getTestPlanQueryWrapper(pagination.getSearchObj());
        //2. 分页查询
        Page<TestPlan> testPlanRes = this.page(pagination.getPage(), queryWrapper);
        if (testPlanRes != null && CollectionUtils.isNotEmpty(testPlanRes.getRecords())) {
            List<TestPlan> list = testPlanRes.getRecords();
            this.assableTestPlanInfo(list, null);
            testPlanRes.setRecords(list);
        }
        return testPlanRes;
    }

    /**
     * 修改用例计划
     *
     * @param testPlan
     * @return
     */
    @Override
    public TestResultVo updateTestPlan(TestPlan testPlan) {
        //1. 校验
        //1.1 校验状态是否正确，待审核的用例计划不能编辑
        if (testPlan == null || TestPlanStatusEnum.PENDING.getTestPlanStatus().equals(testPlan.getTestPlanStatus())) {
            return TestResultVo.builder().flag(false).msgRes(TestCenterConstants.RES_MSG_STATUS_NO_RIGHT).build();
        }
        //1.2 校验测试计划名称是否通过
        TestResultVo resultVo = this.checkNewPlanName(testPlan.getId(), testPlan.getPlanName());

        boolean flag = false;
        if (resultVo.getFlag()) {
            //1.3 验证测试用例jiraid是否正确
//            TestResultVo jiraRes = JiraUtils.validateJiraInfo(testPlan.getRelationRequirement());
//            if (!jiraRes.getFlag()) {
//                return TestResultVo.builder().flag(false).msgRes(jiraRes.getMsgRes()).build();
//            }
//            testPlan.setRelationRequirement(jiraRes.getData().toString());
            //2.更新被锁定的执行计划（审核通过会被锁定）
            if (testPlan.getLockFlag()) {
                //2.1 更新原用例计划为非最新版本
                TestPlan testPlanOld = new TestPlan();
                BeanUtils.copyProperties(testPlan, testPlanOld);
                testPlanOld.setLatestFlag(false);
                flag = this.updateById(testPlanOld);
                if (flag) {
                    //2.2 复制一个新的用例计划,去保存，旧的用例计划会变成历史
                    TestPlan newTestPlan = new TestPlan();
                    BeanUtils.copyProperties(testPlan, newTestPlan);
                    newTestPlan.setTestPlanVersion(String.format("%.1f", Double.parseDouble(newTestPlan.getTestPlanVersion()) + 0.1));
                    newTestPlan.setLockFlag(false);
                    newTestPlan.setLatestFlag(true);
                    newTestPlan.setTestPlanStatus(TestPlanStatusEnum.PENDING.getTestPlanStatus());
                    newTestPlan.setId(null);
                    flag = this.save(newTestPlan);
                }
                //3.更新未锁定的执行计划（审核通过会被锁定）
            } else {
                String version = testPlan.getTestPlanVersion();
                testPlan.setTestPlanVersion(StringUtils.isNotBlank(version) ? String.format("%.1f", Double.parseDouble(version) + 0.1) : QualityDefendeConstants.TEST_CASE_DEFAULT_VERSION);
                testPlan.setTestPlanStatus(TestPlanStatusEnum.PENDING.getTestPlanStatus());
                testPlan.setLatestFlag(true);
                flag = this.updateById(testPlan);
            }
            return TestResultVo.builder().flag(flag).msgRes(flag ? TestCenterConstants.RES_MSG_SUCCESS : TestCenterConstants.RES_MSG_ERROR).build();

        } else {
            return resultVo;
        }
    }

    /**
     * 新增用例计划
     *
     * @param testPlan
     * @return
     */
    @Override
    public TestResultVo insertTestPlan(TestPlan testPlan) {
        //1.先校验测试计划是否正确
        TestResultVo resultVo = this.checkNewPlanName(null, testPlan.getPlanName());
        if (resultVo.getFlag()) {
            //2.增加测试计划的key（关联历史使用）
            testPlan.setTestPlanKey(MD5Util.MD5Encode(testPlan.toString()));
            testPlan.setTestPlanVersion(QualityDefendeConstants.TEST_CASE_DEFAULT_VERSION);
            testPlan.setLatestFlag(true);
            testPlan.setDeleteFlag(false);
            testPlan.setLockFlag(false);
            //提交状态true 待审核
            if (testPlan.isSubmitFlag()) {
                testPlan.setTestPlanStatus(TestPlanStatusEnum.PENDING.getTestPlanStatus());
                //提交状态false 待提交
            } else {
                testPlan.setTestPlanStatus(TestPlanStatusEnum.UNSUBMIT.getTestPlanStatus());
            }

            //3.保存用例计划
            boolean flag = this.save(testPlan);
            return TestResultVo.builder().flag(flag).msgRes(flag ? TestCenterConstants.RES_MSG_SUCCESS : TestCenterConstants.RES_MSG_ERROR).build();
        }
        return resultVo;

    }

    /**
     * 批量删除用例计划
     *
     * @param uid
     * @param idList
     * @return flag:true 通过 false 不通过
     * msgRes: 原因
     */
    @Override
    public TestResultVo deleteTestPlan(String uid, List<Long> idList) {
        //1.判断需要删除的用例计划id集合
        if (CollectionUtils.isEmpty(idList)) {
            return TestResultVo.builder().flag(false).msgRes(TestCenterConstants.RES_MSG_PARAMS_EMPTY).build();
        }
        //2.判断删除的id集合在数据库中是否存在
        List<TestPlan> testPlanList = testPlanMapper.selectBatchIds(idList);
        if (CollectionUtils.isEmpty(testPlanList)) {
            return TestResultVo.builder().flag(false).msgRes(TestCenterConstants.RES_MSG_SEARCH_EMPTY).build();
        }
        //3.遍历测试计划删除
        List<TestPlan> newTestPlanList = new ArrayList<>();
        for (TestPlan testPlan : testPlanList) {
            if (TestPlanStatusEnum.APPROVED.getTestPlanStatus().equals(testPlan.getTestPlanStatus())) {
                return TestResultVo.builder().flag(false).msgRes(TestCenterConstants.RES_MSG_APPROVED_PASS_NODEL).build();
            }
            testPlan.setDeleteFlag(true);
            testPlan.setUpdateUser(uid);
            testPlan.setUpdateTime(LocalDateTime.now());
            newTestPlanList.add(testPlan);
        }
        //4.判断审核参数组装后没有需要删除的则直接返回失败
        if (CollectionUtils.isEmpty(newTestPlanList)) {
            return TestResultVo.builder().flag(false).msgRes(TestCenterConstants.RES_MSG_APPROVED_EMPTY).build();
        }
        //5.更新删除结果
        boolean updateFlag = this.updateBatchById(newTestPlanList);
        return TestResultVo.builder().flag(updateFlag).msgRes(updateFlag ? TestCenterConstants.RES_MSG_SUCCESS : TestCenterConstants.RES_MSG_ERROR).build();
    }

    /**
     * 校验用例计划名称是否重复
     *
     * @param id
     * @param planName
     * @return flag:true 通过 false 不通过
     * msgRes: 原因
     */
    @Override
    public TestResultVo checkNewPlanName(Long id, String planName) {
        if (StringUtils.isBlank(planName) || StringUtils.isBlank(planName.trim())) {
            return TestResultVo.builder().flag(false).msgRes(TestCenterConstants.RES_MSG_PARAMS_EMPTY).build();
        }
        QueryWrapper<TestPlan> queryWrapper = new QueryWrapper<>();
        if (!Objects.isNull(id)) {
            queryWrapper.notIn("id", id);
        }
        queryWrapper.eq("latest_flag", 1);
        queryWrapper.eq("plan_name", planName);
        List<TestPlan> list = testPlanMapper.selectList(queryWrapper);
        if (CollectionUtils.isEmpty(list)) {
            return TestResultVo.builder().flag(true).msgRes(TestCenterConstants.RES_MSG_SUCCESS).build();
        }
        return TestResultVo.builder().flag(false).msgRes(TestCenterConstants.RES_MSG_REPPEAT).build();
    }

    /**
     * 获取未审核的测试计划id集合
     *
     * @param testPlan 查询条件
     * @return
     */
    @Override
    public List<Long> queryIdsByUncheck(TestPlan testPlan) {
        //1. 格式化查询参数
        testPlan.setLatestFlag(true);
        testPlan.setDeleteFlag(false);

        QueryWrapper<TestPlan> queryWrapper = this.getTestPlanQueryWrapper(testPlan);
        //2. 去查询
        List<TestPlan> testPlanList = this.list(queryWrapper);
        List<Long> idList = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(testPlanList)) {
            idList = testPlanList.stream().map(TestPlan::getId).collect(Collectors.toList());
        }
        return idList;
    }

    /**
     * 根据key获取用例计划历史
     *
     * @param planKey
     * @return
     */
    @Override
    public List<TestPlan> queryTestPlanHistoryByPlanKey(String planKey) {
        if (StringUtils.isBlank(planKey)) {
            return null;
        }
        // 配置查询条件
        QueryWrapper<TestPlan> testPlanQueryWrapper = this.getTestPlanQueryWrapper(TestPlan.builder().testPlanKey(planKey).lockFlag(true).latestFlag(false).build());
        List<TestPlan> list = testPlanMapper.selectList(testPlanQueryWrapper);
        this.assableTestPlanInfo(list, null);
        return list;
    }

    /**
     * 批量审核
     *
     * @param userName 用户id
     * @param approve  审核状态
     * @param remark   备注
     * @param idList   批量审核id集合
     * @return
     */
    @Override
    public TestResultVo batchApprovedTestPlan(String userName, Integer approve, String remark, List<Long> idList) {
        //1.判断需要审核的用例计划id集合
        if (CollectionUtils.isEmpty(idList)) {
            return TestResultVo.builder().flag(false).msgRes(TestCenterConstants.RES_MSG_PARAMS_EMPTY).build();
        }
        //2.判断审核的id集合在数据库中是否存在
        List<TestPlan> testPlanList = testPlanMapper.selectBatchIds(idList);
        if (CollectionUtils.isEmpty(testPlanList)) {
            return TestResultVo.builder().flag(false).msgRes(TestCenterConstants.RES_MSG_SEARCH_EMPTY).build();
        }
        //3.遍历测试计划更新审核状态
        List<TestPlan> newTestPlanList = new ArrayList<>();
        for (TestPlan testPlan : testPlanList) {
            if (TestPlanStatusEnum.APPROVED.getTestPlanStatus().equals(testPlan.getTestPlanStatus())
                    || TestPlanStatusEnum.REJECTED.getTestPlanStatus().equals(testPlan.getTestPlanStatus())
                    || TestPlanStatusEnum.UNSUBMIT.getTestPlanStatus().equals(testPlan.getTestPlanStatus())) {
                continue;
            }
            //3.1 审核通过
            if (approve.equals(TestPlanStatusEnum.APPROVED.getTestPlanStatus())) {
                String version = testPlan.getTestPlanVersion();
                if (StringUtils.isBlank(version)) {
                    version = QualityDefendeConstants.TEST_CASE_DEFAULT_VERSION;
                }
                double versionVal = Double.parseDouble(version);
                if (versionVal < 1) {
                    version = QualityDefendeConstants.TEST_CASE_PASS_DEFAULT_VERSION;
                } else {
                    version = String.format("%.1f", versionVal + 1);
                }
                testPlan.setTestPlanVersion(version);
                testPlan.setTestPlanStatus(TestPlanStatusEnum.APPROVED.getTestPlanStatus());
                testPlan.setLockFlag(true);
                newTestPlanList.add(testPlan);
                //3.2 审核拒绝
            } else {
                testPlan.setTestPlanStatus(TestPlanStatusEnum.REJECTED.getTestPlanStatus());
                testPlan.setLockFlag(false);
            }
            testPlan.setRemark(remark);
            testPlan.setApprovedTime(LocalDateTime.now());
            testPlan.setApproveUser(userName);
            testPlan.setUpdateTime(LocalDateTime.now());
            testPlan.setUpdateUser(userName);
            newTestPlanList.add(testPlan);
        }
        //4.判断审核参数组装后没有需要审核的则直接返回失败
        if (CollectionUtils.isEmpty(newTestPlanList)) {
            return TestResultVo.builder().flag(false).msgRes(TestCenterConstants.RES_MSG_APPROVED_EMPTY).build();
        }
        //5.更新审核结果
        boolean updateFlag = this.updateBatchById(newTestPlanList);
        return TestResultVo.builder().flag(updateFlag).msgRes(updateFlag ? TestCenterConstants.RES_MSG_SUCCESS : TestCenterConstants.RES_MSG_ERROR).build();
    }

    /**
     * 提交保存（更改执行计划的状态为待审核）
     *
     * @param userName 用户id
     * @param idList   测试计划的id集合
     * @return
     */
    @Override
    public TestResultVo submitTestPlanList(String userName, List<Long> idList) {
        //1.判断入参
        if (CollectionUtils.isEmpty(idList)) {
            return TestResultVo.builder().flag(false).msgRes(TestCenterConstants.RES_MSG_PARAMS_EMPTY).build();
        }
        //2.判断id集合在数据库中是否存在
        List<TestPlan> testPlanList = testPlanMapper.selectBatchIds(idList);
        if (CollectionUtils.isEmpty(testPlanList)) {
            return TestResultVo.builder().flag(false).msgRes(TestCenterConstants.RES_MSG_SEARCH_EMPTY).build();
        }
        //3.遍历测试计划更新待审核状态
        List<TestPlan> newTestPlanList = new ArrayList<>();
        for (TestPlan testPlan : testPlanList) {
            if (TestPlanStatusEnum.APPROVED.getTestPlanStatus().equals(testPlan.getTestPlanStatus())
                    || TestPlanStatusEnum.REJECTED.getTestPlanStatus().equals(testPlan.getTestPlanStatus())
                    || TestPlanStatusEnum.PENDING.getTestPlanStatus().equals(testPlan.getTestPlanStatus())) {
                continue;
            }
            testPlan.setTestPlanStatus(TestPlanStatusEnum.PENDING.getTestPlanStatus());
            testPlan.setUpdateTime(LocalDateTime.now());
            testPlan.setUpdateUser(userName);
            newTestPlanList.add(testPlan);
        }
        //4.判断更新数据是否为空
        if (CollectionUtils.isEmpty(newTestPlanList)) {
            return TestResultVo.builder().flag(false).msgRes(TestCenterConstants.RES_MSG_APPROVED_EMPTY).build();
        }
        //5.更新审核结果
        boolean updateFlag = this.updateBatchById(newTestPlanList);
        return TestResultVo.builder().flag(updateFlag).msgRes(updateFlag ? TestCenterConstants.RES_MSG_SUCCESS : TestCenterConstants.RES_MSG_ERROR).build();
    }

    /**
     * 根据id获取用例计划
     *
     * @param id
     * @return
     */
    @Override
    public TestPlan getTestPlanById(Long id) {
        if (Objects.isNull(id)) {
            return null;
        }
        TestPlan testPlan = this.getById(id);
        if (Objects.isNull(testPlan)) {
            return null;
        }
        this.assableTestPlanInfo(null, testPlan);
        return testPlan;
    }

    /**
     * 创建查询条件
     *
     * @param testPlan
     * @return
     */
    private QueryWrapper<TestPlan> getTestPlanQueryWrapper(TestPlan testPlan) {
        //1. 格式化查询对象
        QueryWrapper<TestPlan> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(!Objects.isNull(testPlan.getLatestFlag()), "latest_flag", testPlan.getLatestFlag())
                .notIn(!Objects.isNull(testPlan.getId()), "id", testPlan.getId())
                .eq(!Objects.isNull(testPlan.getLockFlag()), "lock_flag", testPlan.getLockFlag())
                .eq(!Objects.isNull(testPlan.getDeleteFlag()), "delete_flag", testPlan.getDeleteFlag())
                .eq(!Objects.isNull(testPlan.getTestPlanStatus()), "test_plan_status", testPlan.getTestPlanStatus())
                .eq(StringUtils.isNotBlank(testPlan.getTestPlanKey()), "test_plan_key", testPlan.getTestPlanKey())
                .eq(StringUtils.isNotBlank(testPlan.getTestPlanVersion()), "version", testPlan.getTestPlanVersion())
                .eq(StringUtils.isNotBlank(testPlan.getRelationRequirement()), "relation_requirement", testPlan.getRelationRequirement())
                .eq(StringUtils.isNotBlank(testPlan.getTestPlanMaster()), "test_plan_master", testPlan.getTestPlanMaster())
                .eq(StringUtils.isNotBlank(testPlan.getCreateUser()), "create_user", testPlan.getCreateUser())
                .eq(StringUtils.isNotBlank(testPlan.getUpdateUser()), "update_user", testPlan.getUpdateUser())
                .eq(StringUtils.isNotBlank(testPlan.getApproveUser()), "approve_user", testPlan.getApproveUser())
                .eq(StringUtils.isNotBlank(testPlan.getTestStrategy()), "test_strategy", testPlan.getTestStrategy())
                .eq(StringUtils.isNotBlank(testPlan.getTestEnvironment()), "test_environment", testPlan.getTestEnvironment())
                .like(StringUtils.isNotBlank(testPlan.getPlanName()), "plan_name", testPlan.getPlanName())
        ;
        //2. 设置创建时间
        if (StringUtils.isNotBlank(testPlan.getTestPlanStartVal())) {
            TimeVo timeVo = TimeUtil.parseTimeStr(testPlan.getTestPlanStartVal());
            queryWrapper.between("test_plan_start_time", timeVo.getStart(), timeVo.getEnd());
        }

        //3. 设置更新时间
        if (StringUtils.isNotBlank(testPlan.getTestPlanEndVal())) {
            TimeVo timeVo = TimeUtil.parseTimeStr(testPlan.getTestPlanEndVal());
            queryWrapper.between("test_plan_end_time", timeVo.getStart(), timeVo.getEnd());
        }
        queryWrapper.orderByDesc("id");
        return queryWrapper;
    }

    /**
     * 组织参数给列表页面
     *
     * @param planList
     * @param testPlan
     */
    private void assableTestPlanInfo(List<TestPlan> planList, TestPlan testPlan) {
        if (CollectionUtils.isEmpty(planList) && Objects.isNull(testPlan)) {
            return;
        }
        // 测试环境字典列表
        List<DataDictionary> environmentList = dataDictionaryService.queryDataDictionaryListByType(QualityDefendeConstants.DATA_DICTIONARY_ENVIRONMENT_TYPE);
        // 测试策略字典列表
        List<DataDictionary> strategyList = dataDictionaryService.queryDataDictionaryListByType(QualityDefendeConstants.DATA_DICTIONARY_STRATEGY_TYPE);

        // 1.给list赋值
        if (CollectionUtils.isNotEmpty(planList)) {
            List<String> userList = planList.stream().distinct().map(TestPlan::getTestPlanMaster).collect(Collectors.toList());
            Map<String, MatrixUserDetail> userMap = matrixService.getUserDetailByEmailPre(userList);
            planList.forEach(plan -> {
                // 设置测试环境字符
                this.assableTestPlan(plan, environmentList, strategyList);
                String userName = Optional.ofNullable(userMap)
                        .map(item -> item.get(plan.getTestPlanMaster()))
                        .map(detail -> detail.getUserName())
                        .orElse("");
                plan.setTestPlanMasterStr(userName);
            });
        }
        // 2.给对象赋值
        if (!Objects.isNull(testPlan)) {
            // 设置测试环境字符
            this.assableTestPlan(testPlan, environmentList, strategyList);
            Map<String, MatrixUserDetail> userMap = matrixService.getUserDetailByEmailPre(Arrays.asList(testPlan.getTestPlanMaster()));
            String userName = Optional.ofNullable(userMap)
                    .map(item -> item.get(testPlan.getTestPlanMaster()))
                    .map(detail -> detail.getUserName())
                    .orElse("");
            testPlan.setTestPlanMasterStr(userName);
        }
    }

    /**
     * 给测试计划塞入表现值
     *
     * @param testPlan
     * @param environmentList
     * @param strategyList
     */
    private void assableTestPlan(TestPlan testPlan, List<DataDictionary> environmentList, List<DataDictionary> strategyList) {
        // 设置测试环境字符
        if (CollectionUtils.isNotEmpty(environmentList)) {
            for (DataDictionary dic : environmentList) {
                if (dic.getEnglishName().equals(testPlan.getTestEnvironment())) {
                    testPlan.setTestEnvironmentStr(dic.getName());
                    break;
                }
            }
        }
        // 设置测试策略字符
        if (CollectionUtils.isNotEmpty(strategyList)) {
            for (DataDictionary dic : strategyList) {
                if (dic.getEnglishName().equals(testPlan.getTestStrategy())) {
                    testPlan.setTestStrategyStr(dic.getName());
                    break;
                }
            }
        }
        // 设置状态字符串
        testPlan.setTestPlanStatusStr(TestPlanStatusEnum.getTestPlanName(testPlan.getTestPlanStatus()));
//        DateTimeFormatter dtf3 = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
//        testPlan.setTestPlanTime(testPlan.getTestPlanStartTime().format(dtf3)+","+testPlan.getTestPlanEndTime().format(dtf3));
    }
}
