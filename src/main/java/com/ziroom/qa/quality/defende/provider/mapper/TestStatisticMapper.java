package com.ziroom.qa.quality.defende.provider.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ziroom.qa.quality.defende.provider.vo.statistics.TaskTopVo;
import com.ziroom.qa.quality.defende.provider.vo.statistics.TestCaseStatisticVo;
import com.ziroom.qa.quality.defende.provider.vo.statistics.table.TestCaseTopVo;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@Mapper
public interface TestStatisticMapper extends BaseMapper {

    /**
     * 根据时间和部门信息获取测试用例统计信息
     *
     * @param likeDeptCode
     * @param startTime
     * @param endTime
     * @return
     */
    List<TestCaseStatisticVo> getTestCaseStatistics(String likeDeptCode, LocalDateTime startTime, LocalDateTime endTime);

    /**
     * 根据时间和部门信息获取测试执行任务中测试用例统计信息
     *
     * @param likeDeptCode
     * @param startTime
     * @param endTime
     * @return
     */
    List<TestCaseStatisticVo> getTaskTestCaseStatistics(String likeDeptCode, LocalDateTime startTime, LocalDateTime endTime);

    /**
     * 根据时间和部门信息获取测试用例统计信息 by people
     *
     * @param likeDeptCode
     * @param startTime
     * @param endTime
     * @return
     */
    List<TestCaseStatisticVo> getTestCaseStatisticsByPeople(String likeDeptCode, LocalDateTime startTime, LocalDateTime endTime);

    /**
     * 根据时间和部门信息获取测试执行任务中测试用例统计信息 by people
     *
     * @param likeDeptCode
     * @param startTime
     * @param endTime
     * @return
     */
    List<TestCaseStatisticVo> getTaskTestCaseStatisticsByPeople(String likeDeptCode, LocalDateTime startTime, LocalDateTime endTime);

    /**
     * 根据时间和部门查询用例创建总数（以人为维度）
     *
     * @param likeDeptCode
     * @param startTime
     * @param endTime
     * @return
     */
    List<TestCaseStatisticVo> getTestStatisticsDetail(String likeDeptCode, LocalDateTime startTime, LocalDateTime endTime);

    /**
     * 根据时间和部门查询测试执行总数（以人为维度）
     *
     * @param likeDeptCode
     * @param startTime
     * @param endTime
     * @return
     */
    List<TestCaseStatisticVo> getTestExecutionStatisticsDetail(String likeDeptCode, LocalDateTime startTime, LocalDateTime endTime);

    /**
     * 查询用力热度排行
     *
     * @param startTime
     * @param endTime
     * @return
     * @Param topValue
     */
    List<TestCaseTopVo> getTestCaseTop(LocalDateTime startTime, LocalDateTime endTime, int topValue);


    /**
     * 自动测试执行任务的执行次数（相同应用）
     *
     * @param startTime
     * @param endTime
     * @param topValue
     * @return
     */
    List<TaskTopVo> getAutoTaskTop(LocalDateTime startTime, LocalDateTime endTime, int topValue);


    /**
     * 自动测试执行任务的用例数（相同应用）
     *
     * @param startTime
     * @param endTime
     * @param topValue
     * @return
     */
    List<TaskTopVo> getAutoCaseTop(LocalDateTime startTime, LocalDateTime endTime, int topValue);

    /**
     * 应用下的测试执行（成功、失败、跳过、未执行）数量
     *
     * @param startTime
     * @param endTime
     * @param exeType
     * @return
     */
    List<TaskTopVo> getTaskExeRate(LocalDateTime startTime, LocalDateTime endTime, Integer exeType);


}
