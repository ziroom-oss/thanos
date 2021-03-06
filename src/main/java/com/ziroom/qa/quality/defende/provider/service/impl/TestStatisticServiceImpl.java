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

    private static final String TEST_CASE_NAME = "????????????";

    private static final String TASK_CASE_NAME = "????????????";

    private static final String TEST_CASE_TITAL_APP = "??????????????????(??????????????????)";

    private static final String TEST_CASE_TITAL_PEOPLE = "??????????????????(??????????????????)";

    private static final String TEST_CASE_TITAL_DETAIL = "??????????????????(????????????)";

    /**
     * ??????case??????????????????????????????
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
     * ??????case?????????????????????????????? by people
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
     * ?????????????????????????????????????????????????????????
     *
     * @param vo
     * @return
     */
    @Override
    public TestResultVo getUserTestCaseStatisticsByDeptCode(StatisticSearchVo vo) {
        return this.getUserStatisticInfo(vo);
    }

    /**
     * ??????????????????
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
            throw new CustomException("??????????????????????????????");
        }
        JSONObject kvMapObj = new JSONObject(new LinkedHashMap<>());
        kvMapObj.put("number", "??????");
        kvMapObj.put("casename", "????????????");
        kvMapObj.put("createTime", "????????????");
        kvMapObj.put("createUser", "?????????");
        kvMapObj.put("tcCount", "????????????");
        return TestResultVo.builder().flag(true).data(ChartFormatUtil.tableChart(userCaseList, kvMapObj)).build();
    }

    /**
     * ?????????????????????????????????????????????????????????
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
//            throw new CustomException("(???????????????????????????????????????)????????????????????????????????????");
//        }
        return taskTopVos;
    }

    /**
     * ??????????????????????????????????????????????????????
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
//            throw new CustomException("(????????????????????????????????????)????????????????????????????????????");
//        }
        return taskTopVos;
    }

    /**
     * ????????????????????????case????????????????????????????????????????????????
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
//            throw new CustomException("????????????????????????????????????case???");
//        }
        return taskTopVos;
    }

    /**
     * ??????????????????top n
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
     * ?????????????????????????????????
     *
     * @param vo
     * @return
     */
    private TestResultVo getUserStatisticInfo(StatisticSearchVo vo) {
        //1. ????????????
        if (Objects.isNull(vo)
                || StringUtils.isBlank(vo.getParentDeptCode())
                || StringUtils.isBlank(vo.getDateTimeStr())) {
            return TestResultVo.builder().flag(false).msgRes(TestCenterConstants.RES_MSG_PARAMS_EMPTY).build();
        }
        //2. ??????????????????????????????
        List<EhrUserDetail> userList = matrixService.getInternetPlatEhrUserListFromMatrix(vo.getParentDeptCode());
        if (CollectionUtils.isEmpty(userList)) {
            return TestResultVo.builder().flag(false).msgRes(TestCenterConstants.RES_MSG_SEARCH_EMPTY).build();
        }
        //3. ??????????????????????????????
        // ????????????????????????
        List<UserStatisticsDetail> userResList = new ArrayList<>();
        userList.stream().forEach(user -> {
            UserStatisticsDetail detail = new UserStatisticsDetail();
            detail.setUserName(user.getName());
            detail.setEmailPre(user.getAdCode());
            detail.setUserType(user.getUserType());
            detail.setEmplId(user.getEmplid());
            userResList.add(detail);
        });
        //4. ??????????????????
        TimeVo timeVo = TimeUtil.parseDateStr(vo.getDateTimeStr());
        // ???????????????
        List<TestCaseStatisticVo> userCaseList = testStatisticMapper.getTestStatisticsDetail("%" + vo.getParentDeptCode() + "%", timeVo.getStart(), timeVo.getEnd());
        // ???????????????
        List<TestCaseStatisticVo> userExecutionList = testStatisticMapper.getTestExecutionStatisticsDetail("%" + vo.getParentDeptCode() + "%", timeVo.getStart(), timeVo.getEnd());

        TestCaseStatisticResultVo resultVo = new TestCaseStatisticResultVo();
        // ????????????
        resultVo.setTitle(TEST_CASE_TITAL_DETAIL);
        //5. ??????????????????
        List<ChartSeriesDto> chartSeriesDtoList = new ArrayList<>();
        this.initReportData(resultVo,
                userResList.stream().map(UserStatisticsDetail::getUserName).collect(Collectors.toList()),
                userResList.stream().map(UserStatisticsDetail::getTestCaseCount).collect(Collectors.toList()),
                chartSeriesDtoList);
        // ??????????????????????????????
        if (CollectionUtils.isNotEmpty(userCaseList)) {
            userResList.stream().forEach(user -> {
                TestCaseStatisticVo statisticVo = userCaseList.stream().filter(userCase -> user.getEmailPre().equals(userCase.getEmailPre())).findFirst().orElse(null);
                if (Objects.nonNull(statisticVo)) {
                    user.setTestCaseCount(statisticVo.getTestCount());
                }
            });
        }
        // ??????????????????????????????
        if (CollectionUtils.isNotEmpty(userExecutionList)) {
            userResList.stream().forEach(user -> {
                TestCaseStatisticVo statisticVo = userExecutionList.stream().filter(userExe -> user.getEmailPre().equals(userExe.getEmailPre())).findFirst().orElse(null);
                if (Objects.nonNull(statisticVo)) {
                    user.setTestExecutionCount(statisticVo.getTestCount());
                }
            });
        }
        // *????????????????????????
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
        // *???????????????????????????
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
        resultVo.setSubtext("???????????????" + newUserList.size());
        // ?????????????????????x?????????
        resultVo.setXAxis(newUserList.stream().map(UserStatisticsDetail::getUserName).collect(Collectors.toList()));
        return TestResultVo.builder().flag(true).data(resultVo).build();
    }

    /**
     * ???????????????????????????
     * ???????????????????????????
     *
     * @param vo
     * @param userResList
     */
    private List<UserStatisticsDetail> analysisTestCount(StatisticSearchVo vo, List<UserStatisticsDetail> userResList) {
        //???????????????????????????
        String caseStaEnd = vo.getCaseStaEnd();
        //???????????????????????????
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
     * ????????????????????????
     *
     * @param vo
     * @return
     */
    private TestResultVo getStatisticInfo(StatisticSearchVo vo) {
        //1. ????????????
        if (Objects.isNull(vo)
                || StringUtils.isBlank(vo.getParentDeptCode())
                || StringUtils.isBlank(vo.getDateTimeStr())) {
            return TestResultVo.builder().flag(false).msgRes(TestCenterConstants.RES_MSG_PARAMS_EMPTY).build();
        }
        //2. ??????????????????????????????????????????
        List<EhrDeptNewInfo> childDeptList = ehrService.getChildEhrDeptList(vo.getParentDeptCode());
        if (CollectionUtils.isEmpty(childDeptList)) {
            return TestResultVo.builder().flag(false).msgRes(TestCenterConstants.RES_MSG_CHILDDEPT_NOEXIST).build();
        }
        //3. ?????????????????????????????????????????????????????????????????????
        // ????????????????????????
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
        //4. ??????????????????
        List<ChartSeriesDto> chartSeriesDtoList = new ArrayList<>();
        this.initReportData(resultVo,
                childDeptList.stream().map(EhrDeptNewInfo::getName).collect(Collectors.toList()),
                childDeptList.stream().map(EhrDeptNewInfo::getCount).collect(Collectors.toList()),
                chartSeriesDtoList);
        // ??????????????????????????????
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
        // ????????????????????????????????????
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
        // ?????????????????????
        resultVo.setSubtext("???????????????" + caseCount.get() + "?????????????????????" + executionCount.get() + "???");
        resultVo.setSeriesList(chartSeriesDtoList);
        //4.2 ???????????????????????????
        return TestResultVo.builder().flag(true).data(resultVo).build();
    }

    /**
     * ???????????????????????????
     *
     * @param resultVo
     * @param xAxis
     * @param countList
     * @param chartSeriesDtoList
     */
    private void initReportData(TestCaseStatisticResultVo resultVo, List<String> xAxis, List<Integer> countList, List<ChartSeriesDto> chartSeriesDtoList) {
        // ???????????????x?????????
        resultVo.setXAxis(xAxis);
        // ????????????????????????
        resultVo.setLegendList(Arrays.asList(new String[]{TEST_CASE_NAME, TASK_CASE_NAME}));
        List<ChartyAxisDto> yAxisList = new ArrayList<>();
        ChartyAxisDto yAxis = new ChartyAxisDto("??????", new axisLabel("???"), "left");
        yAxisList.add(yAxis);
        resultVo.setYAxisList(yAxisList);

        //?????????seriesList??????
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
