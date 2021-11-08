package com.ziroom.qa.quality.defende.provider.service;

import com.ziroom.qa.quality.defende.provider.vo.TestResultVo;
import com.ziroom.qa.quality.defende.provider.vo.statistics.StatisticSearchVo;
import com.ziroom.qa.quality.defende.provider.vo.statistics.TaskTopVo;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 测试用例统计service
 */
public interface TestStatisticService {

    /**
     * 获取case和任务的所有用例统计
     *
     * @param vo
     * @return
     */
    TestResultVo getTestCaseAndTaskStatistics(StatisticSearchVo vo);

    /**
     * 获取case和任务的所有用例统计 by people
     *
     * @param vo
     * @return
     */
    TestResultVo getTestCaseAndTaskStatisticsByPeople(StatisticSearchVo vo);

    /**
     * 统计部门下人员创建用例和用例执行的数量
     *
     * @param vo
     * @return
     */
    TestResultVo getUserTestCaseStatisticsByDeptCode(StatisticSearchVo vo);

    /**
     * 用力热度排行
     *
     * @param searchVo
     * @return
     */
    TestResultVo getTestCaseTop(StatisticSearchVo searchVo);

    /**
     * 自动测试执行任务的执行次数（相同应用）
     *
     * @param startTime
     * @param endTime
     * @return
     */
    List<TaskTopVo> getAutoTaskTop(LocalDateTime startTime,LocalDateTime endTime);

    /**
     * 自动测试执行任务的用例数（相同应用）
     *
     * @param startTime
     * @param endTime
     * @return
     */
    List<TaskTopVo> getAutoCaseTop(LocalDateTime startTime,LocalDateTime endTime);

    /**
     * 应用下的测试执行case（成功、失败、跳过、未执行）数量
     *
     * @param startTime
     * @param endTime
     * @param exeType
     * @return
     */
    List<TaskTopVo> getTaskExeRate(LocalDateTime startTime,LocalDateTime endTime,Integer exeType);
}
