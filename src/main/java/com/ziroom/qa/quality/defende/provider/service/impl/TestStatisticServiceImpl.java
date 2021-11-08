package com.ziroom.qa.quality.defende.provider.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.ziroom.qa.quality.defende.provider.constant.QualityDefendeConstants;
import com.ziroom.qa.quality.defende.provider.constant.TestCenterConstants;
import com.ziroom.qa.quality.defende.provider.entity.DataDictionary;
import com.ziroom.qa.quality.defende.provider.mapper.TestStatisticMapper;
import com.ziroom.qa.quality.defende.provider.result.CustomException;
import com.ziroom.qa.quality.defende.provider.service.DataDictionaryService;
import com.ziroom.qa.quality.defende.provider.outinterface.client.service.EhrService;
import com.ziroom.qa.quality.defende.provider.outinterface.client.service.MatrixService;
import com.ziroom.qa.quality.defende.provider.service.TestStatisticService;
import com.ziroom.qa.quality.defende.provider.util.TimeUtil;
import com.ziroom.qa.quality.defende.provider.vo.EhrDeptNewInfo;
import com.ziroom.qa.quality.defende.provider.vo.EhrUserDetail;
import com.ziroom.qa.quality.defende.provider.vo.TestResultVo;
import com.ziroom.qa.quality.defende.provider.vo.TimeVo;
import com.ziroom.qa.quality.defende.provider.vo.statistics.*;
import com.ziroom.qa.quality.defende.provider.vo.statistics.table.ChartFormatUtil;
import com.ziroom.qa.quality.defende.provider.vo.statistics.table.TestCaseTopVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Slf4j
@Service
public class TestStatisticServiceImpl implements TestStatisticService {

    @Autowired
    private TestStatisticMapper testStatisticMapper;
    @Autowired
    private EhrService ehrService;
    @Autowired
    private MatrixService matrixService;
    @Autowired
    private DataDictionaryService dataDictionaryService;

    private static final String TEST_CASE_NAME = "用例新增";

    private static final String TASK_CASE_NAME = "测试执行";

    private static final String TEST_CASE_TITAL_APP = "测试用例统计(应用组织结构)";

    private static final String TEST_CASE_TITAL_PEOPLE = "测试用例统计(人员组织结构)";

    private static final String TEST_CASE_TITAL_DETAIL = "测试用例统计(人员明细)";

    /**
     * 获取case和任务的所有用例统计
     *
     * @param vo
     * @return
     */
    @Override
    public TestResultVo getTestCaseAndTaskStatistics(StatisticSearchVo vo) {
        vo.setStatisticType(StatisticSearchVo.STATISTIC_BUSINESS);
        return this.getStatisticInfo(vo);
    }

    /**
     * 获取case和任务的所有用例统计 by people
     *
     * @param vo
     * @return
     */
    @Override
    public TestResultVo getTestCaseAndTaskStatisticsByPeople(StatisticSearchVo vo) {
        vo.setStatisticType(StatisticSearchVo.STATISTIC_DEPARTMENT);
        return this.getStatisticInfo(vo);
    }

    /**
     * 统计部门下人员创建用例和用例执行的数量
     *
     * @param vo
     * @return
     */
    @Override
    public TestResultVo getUserTestCaseStatisticsByDeptCode(StatisticSearchVo vo) {
        return this.getUserStatisticInfo(vo);
    }

    /**
     * 用力热度排行
     *
     * @param searchVo
     * @return
     */
    @Override
    public TestResultVo getTestCaseTop(StatisticSearchVo searchVo) {
        int topValue = this.getTopValue();
        TimeVo timeVo = TimeUtil.parseDateStr(searchVo.getDateTimeStr());
        List<TestCaseTopVo> userCaseList = testStatisticMapper.getTestCaseTop(timeVo.getStart(), timeVo.getEnd(), topValue);
        if (CollectionUtils.isEmpty(userCaseList)) {
            throw new CustomException("没有查询到用例信息！");
        }
        JSONObject kvMapObj = new JSONObject(new LinkedHashMap<>());
        kvMapObj.put("number", "序号");
        kvMapObj.put("casename", "用例名称");
        kvMapObj.put("createTime", "创建时间");
        kvMapObj.put("createUser", "创建人");
        kvMapObj.put("tcCount", "关联次数");
        return TestResultVo.builder().flag(true).data(ChartFormatUtil.tableChart(userCaseList, kvMapObj)).build();
    }

    /**
     * 自动测试执行任务的执行次数（相同应用）
     *
     * @param startTime
     * @param endTime
     * @return
     */
    @Override
    public List<TaskTopVo> getAutoTaskTop(LocalDateTime startTime, LocalDateTime endTime) {
//        int topValue = this.getTopValue();
        List<TaskTopVo> taskTopVos = testStatisticMapper.getAutoTaskTop(startTime, endTime, 1000);
//        if (CollectionUtils.isEmpty(taskTopVos)) {
//            throw new CustomException("(自动测试执行任务的执行次数)没有查询到测试执行信息！");
//        }
        return taskTopVos;
    }

    /**
     * 自动测试执行任务的用例数（相同应用）
     *
     * @param startTime
     * @param endTime
     * @return
     */
    @Override
    public List<TaskTopVo> getAutoCaseTop(LocalDateTime startTime, LocalDateTime endTime) {
//        int topValue = this.getTopValue();
        List<TaskTopVo> taskTopVos = testStatisticMapper.getAutoCaseTop(startTime, endTime, 1000);
//        if (CollectionUtils.isEmpty(taskTopVos)) {
//            throw new CustomException("(自动测试执行任务的用例数)没有查询到测试执行信息！");
//        }
        return taskTopVos;
    }

    /**
     * 应用下的测试执行case（成功、失败、跳过、未执行）数量
     *
     * @param startTime
     * @param endTime
     * @param exeType
     * @return
     */
    @Override
    public List<TaskTopVo> getTaskExeRate(LocalDateTime startTime, LocalDateTime endTime, Integer exeType) {
        List<TaskTopVo> taskTopVos = testStatisticMapper.getTaskExeRate(startTime, endTime, exeType);
//        if (CollectionUtils.isEmpty(taskTopVos)) {
//            throw new CustomException("没有查询应用下的测试执行case！");
//        }
        return taskTopVos;
    }

    /**
     * 获取排行数据top n
     *
     * @return
     */
    private int getTopValue() {
        int topValue = 10;
        List<DataDictionary> dicList = dataDictionaryService.queryDataDictionaryListByType(QualityDefendeConstants.DATA_DICTIONARY_TOP_TYPE);
        if (CollectionUtils.isNotEmpty(dicList)) {
            String dicValue = dicList.get(0).getDicValue();
            topValue = Integer.parseInt(dicValue);
        }
        return topValue;
    }

    /**
     * 获取人维度统计报表信息
     *
     * @param vo
     * @return
     */
    private TestResultVo getUserStatisticInfo(StatisticSearchVo vo) {
        //1. 判断入参
        if (Objects.isNull(vo)
                || StringUtils.isBlank(vo.getParentDeptCode())
                || StringUtils.isBlank(vo.getDateTimeStr())) {
            return TestResultVo.builder().flag(false).msgRes(TestCenterConstants.RES_MSG_PARAMS_EMPTY).build();
        }
        //2. 获取部门下的人员名单
        List<EhrUserDetail> userList = matrixService.getInternetPlatEhrUserListFromMatrix(vo.getParentDeptCode());
        if (CollectionUtils.isEmpty(userList)) {
            return TestResultVo.builder().flag(false).msgRes(TestCenterConstants.RES_MSG_SEARCH_EMPTY).build();
        }
        //3. 统计所有人的用例信息
        // 拼装用户统计数据
        List<UserStatisticsDetail> userResList = new ArrayList<>();
        userList.stream().forEach(user -> {
            UserStatisticsDetail detail = new UserStatisticsDetail();
            detail.setUserName(user.getName());
            detail.setEmailPre(user.getAdCode());
            detail.setUserType(user.getUserType());
            detail.setEmplId(user.getEmplid());
            userResList.add(detail);
        });
        //4. 查询报表数据
        TimeVo timeVo = TimeUtil.parseDateStr(vo.getDateTimeStr());
        // 用例创建数
        List<TestCaseStatisticVo> userCaseList = testStatisticMapper.getTestStatisticsDetail("%" + vo.getParentDeptCode() + "%", timeVo.getStart(), timeVo.getEnd());
        // 用例执行数
        List<TestCaseStatisticVo> userExecutionList = testStatisticMapper.getTestExecutionStatisticsDetail("%" + vo.getParentDeptCode() + "%", timeVo.getStart(), timeVo.getEnd());

        TestCaseStatisticResultVo resultVo = new TestCaseStatisticResultVo();
        // 报表标题
        resultVo.setTitle(TEST_CASE_TITAL_DETAIL);
        //5. 拼装报表数据
        List<ChartSeriesDto> chartSeriesDtoList = new ArrayList<>();
        this.initReportData(resultVo,
                userResList.stream().map(UserStatisticsDetail::getUserName).collect(Collectors.toList()),
                userResList.stream().map(UserStatisticsDetail::getTestCaseCount).collect(Collectors.toList()),
                chartSeriesDtoList);
        // 拼装测试用例总数报表
        if (CollectionUtils.isNotEmpty(userCaseList)) {
            userResList.stream().forEach(user -> {
                TestCaseStatisticVo statisticVo = userCaseList.stream().filter(userCase -> user.getEmailPre().equals(userCase.getEmailPre())).findFirst().orElse(null);
                if (Objects.nonNull(statisticVo)) {
                    user.setTestCaseCount(statisticVo.getTestCount());
                }
            });
        }
        // 拼装测试执行总数报表
        if (CollectionUtils.isNotEmpty(userExecutionList)) {
            userResList.stream().forEach(user -> {
                TestCaseStatisticVo statisticVo = userExecutionList.stream().filter(userExe -> user.getEmailPre().equals(userExe.getEmailPre())).findFirst().orElse(null);
                if (Objects.nonNull(statisticVo)) {
                    user.setTestExecutionCount(statisticVo.getTestCount());
                }
            });
        }
        // *根据用例数量排序
        Integer caseOrder = vo.getCaseOrder();
        if (Objects.nonNull(caseOrder)) {
            switch (caseOrder) {
                case 0:
                    userResList.sort(Comparator.comparing(UserStatisticsDetail::getTestCaseCount));
                    break;
                case 1:
                    userResList.sort(Comparator.comparing(UserStatisticsDetail::getTestCaseCount).reversed());
                    break;
                case 2:
                    userResList.sort(Comparator.comparing(UserStatisticsDetail::getTestExecutionCount));
                    break;
                case 3:
                    userResList.sort(Comparator.comparing(UserStatisticsDetail::getTestExecutionCount).reversed());
                    break;
                default:
                    break;
            }
        }
        // *根据用例数数量过滤
        List<UserStatisticsDetail> newUserList = this.analysisTestCount(vo, userResList);

        chartSeriesDtoList.forEach(tc -> {
            if (TEST_CASE_NAME.equals(tc.getName())) {
                tc.setData(newUserList.stream().map(UserStatisticsDetail::getTestCaseCount).collect(Collectors.toList()));
            }
            if (TASK_CASE_NAME.equals(tc.getName())) {
                tc.setData(newUserList.stream().map(UserStatisticsDetail::getTestExecutionCount).collect(Collectors.toList()));
            }
        });
        resultVo.setSeriesList(chartSeriesDtoList);
        resultVo.setSubtext("总人数为：" + newUserList.size());
        // 排序后重新设置x轴数据
        resultVo.setXAxis(newUserList.stream().map(UserStatisticsDetail::getUserName).collect(Collectors.toList()));
        return TestResultVo.builder().flag(true).data(resultVo).build();
    }

    /**
     * 过滤并分析查询条件
     * 根据用例数数量过滤
     *
     * @param vo
     * @param userResList
     */
    private List<UserStatisticsDetail> analysisTestCount(StatisticSearchVo vo, List<UserStatisticsDetail> userResList) {
        //测试用例数查询条件
        String caseStaEnd = vo.getCaseStaEnd();
        //测试执行数查询条件
        String exeStaEnd = vo.getExeStaEnd();
        String[] caseStaEnds = StringUtils.isBlank(caseStaEnd) ? null : caseStaEnd.split(",");
        String[] exeStaEnds = StringUtils.isBlank(exeStaEnd) ? null : exeStaEnd.split(",");
        if (Objects.isNull(caseStaEnds) && Objects.isNull(exeStaEnds)) {
            return userResList;
        }
        int caseStartCount = 0;
        int caseEndCount = 0;
        int exeStartCount = 0;
        int exeEndCount = 0;

        if (Objects.nonNull(caseStaEnds)) {
            caseStartCount = Integer.parseInt(caseStaEnds[0]);
            caseEndCount = Integer.parseInt(caseStaEnds[1]);
        }
        if (Objects.nonNull(exeStaEnds)) {
            exeStartCount = Integer.parseInt(exeStaEnds[0]);
            exeEndCount = Integer.parseInt(exeStaEnds[1]);
        }

        List<UserStatisticsDetail> resList = new ArrayList<>();

        for (UserStatisticsDetail userDetail : userResList) {

            boolean caseFlag = false;
            boolean exeFlag = false;
            if (Objects.isNull(caseStaEnds)) {
                caseFlag = true;
            } else if (caseStartCount <= userDetail.getTestCaseCount()
                    && caseEndCount >= userDetail.getTestCaseCount()) {
                caseFlag = true;
            }

            if (Objects.isNull(exeStaEnds)) {
                exeFlag = true;
            } else if (exeStartCount <= userDetail.getTestExecutionCount()
                    && exeEndCount >= userDetail.getTestExecutionCount()) {
                exeFlag = true;
            }
            if (caseFlag && exeFlag) {
                resList.add(userDetail);
            }
        }
        return resList;
    }

    /**
     * 获取统计报表信息
     *
     * @param vo
     * @return
     */
    private TestResultVo getStatisticInfo(StatisticSearchVo vo) {
        //1. 判断入参
        if (Objects.isNull(vo)
                || StringUtils.isBlank(vo.getParentDeptCode())
                || StringUtils.isBlank(vo.getDateTimeStr())) {
            return TestResultVo.builder().flag(false).msgRes(TestCenterConstants.RES_MSG_PARAMS_EMPTY).build();
        }
        //2. 根据父部门获取其下子部门信息
        List<EhrDeptNewInfo> childDeptList = ehrService.getChildEhrDeptList(vo.getParentDeptCode());
        if (CollectionUtils.isEmpty(childDeptList)) {
            return TestResultVo.builder().flag(false).msgRes(TestCenterConstants.RES_MSG_CHILDDEPT_NOEXIST).build();
        }
        //3. 获取测试用例统计数量和测试执行任务统计数量集合
        // 塞入所属部门名称
        TestCaseStatisticResultVo resultVo = new TestCaseStatisticResultVo();
        TimeVo timeVo = TimeUtil.parseDateStr(vo.getDateTimeStr());
        List<TestCaseStatisticVo> testList = null;
        List<TestCaseStatisticVo> taskList = null;
        AtomicInteger caseCount = new AtomicInteger();
        AtomicInteger executionCount = new AtomicInteger();
        if (StatisticSearchVo.STATISTIC_BUSINESS.equals(vo.getStatisticType())) {
            testList = testStatisticMapper.getTestCaseStatistics("%" + vo.getParentDeptCode() + "%", timeVo.getStart(), timeVo.getEnd());
            taskList = testStatisticMapper.getTaskTestCaseStatistics("%" + vo.getParentDeptCode() + "%", timeVo.getStart(), timeVo.getEnd());
            resultVo.setTitle(TEST_CASE_TITAL_APP);
        } else if (StatisticSearchVo.STATISTIC_DEPARTMENT.equals(vo.getStatisticType())) {
            testList = testStatisticMapper.getTestCaseStatisticsByPeople("%" + vo.getParentDeptCode() + "%", timeVo.getStart(), timeVo.getEnd());
            taskList = testStatisticMapper.getTaskTestCaseStatisticsByPeople("%" + vo.getParentDeptCode() + "%", timeVo.getStart(), timeVo.getEnd());
            resultVo.setTitle(TEST_CASE_TITAL_PEOPLE);
        }
        //4. 拼装报表数据
        List<ChartSeriesDto> chartSeriesDtoList = new ArrayList<>();
        this.initReportData(resultVo,
                childDeptList.stream().map(EhrDeptNewInfo::getName).collect(Collectors.toList()),
                childDeptList.stream().map(EhrDeptNewInfo::getCount).collect(Collectors.toList()),
                chartSeriesDtoList);
        // 拼装测试用例统计数据
        if (CollectionUtils.isNotEmpty(testList)) {
            List<EhrDeptNewInfo> testCountList = childDeptList;
            List<TestCaseStatisticVo> finalTestList = testList;
            testCountList.stream().forEach(test -> {
                int count = finalTestList.stream().filter(testCase -> testCase.getTreePath().indexOf(test.getCode()) > -1).mapToInt(TestCaseStatisticVo::getTestCount).sum();
                test.setCount(count);
            });

            chartSeriesDtoList.forEach(tc -> {
                if (TEST_CASE_NAME.equals(tc.getName())) {
                    tc.setData(testCountList.stream().map(EhrDeptNewInfo::getCount).collect(Collectors.toList()));
                    caseCount.set(testCountList.stream().mapToInt(EhrDeptNewInfo::getCount).sum());
                }
            });
        }
        // 拼装测试执行任务统计数据
        if (CollectionUtils.isNotEmpty(taskList)) {
            List<EhrDeptNewInfo> taskCountList = childDeptList;
            List<TestCaseStatisticVo> finalTaskList = taskList;
            taskCountList.forEach(task -> {
                int count = finalTaskList.stream().filter(testCase -> testCase.getTreePath().indexOf(task.getCode()) > -1).mapToInt(TestCaseStatisticVo::getTestCount).sum();
                task.setCount(count);
            });

            chartSeriesDtoList.forEach(tc -> {
                if (TASK_CASE_NAME.equals(tc.getName())) {
                    tc.setData(taskCountList.stream().map(EhrDeptNewInfo::getCount).collect(Collectors.toList()));
                    executionCount.set(taskCountList.stream().mapToInt(EhrDeptNewInfo::getCount).sum());
                }
            });

        }
        // 数量放在子主体
        resultVo.setSubtext("用例新增：" + caseCount.get() + "个，测试执行：" + executionCount.get() + "个");
        resultVo.setSeriesList(chartSeriesDtoList);
        //4.2 组织数据返回给前端
        return TestResultVo.builder().flag(true).data(resultVo).build();
    }

    /**
     * 组装报表初始化数据
     *
     * @param resultVo
     * @param xAxis
     * @param countList
     * @param chartSeriesDtoList
     */
    private void initReportData(TestCaseStatisticResultVo resultVo, List<String> xAxis, List<Integer> countList, List<ChartSeriesDto> chartSeriesDtoList) {
        // 塞入默认的x轴数据
        resultVo.setXAxis(xAxis);
        // 给结果塞入默认值
        resultVo.setLegendList(Arrays.asList(new String[]{TEST_CASE_NAME, TASK_CASE_NAME}));
        List<ChartyAxisDto> yAxisList = new ArrayList<>();
        ChartyAxisDto yAxis = new ChartyAxisDto("数量", new axisLabel("个"), "left");
        yAxisList.add(yAxis);
        resultVo.setYAxisList(yAxisList);

        //初始化seriesList数据
        ChartSeriesDto testCasedto = new ChartSeriesDto();
        testCasedto.setName(TEST_CASE_NAME);
        testCasedto.setData(countList);
        testCasedto.setYAxisIndex(0);
        chartSeriesDtoList.add(testCasedto);

        ChartSeriesDto taskCasedto = new ChartSeriesDto();
        taskCasedto.setName(TASK_CASE_NAME);
        taskCasedto.setData(countList);
        taskCasedto.setYAxisIndex(0);
        chartSeriesDtoList.add(taskCasedto);
    }


}
