package com.ziroom.qa.quality.defende.provider.execTask.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ziroom.qa.quality.defende.provider.execTask.entity.TestExecution;
import com.ziroom.qa.quality.defende.provider.execTask.entity.vo.AutoExecutionRecordVo;
import com.ziroom.qa.quality.defende.provider.execTask.entity.vo.DemandVO;
import com.ziroom.qa.quality.defende.provider.result.RestResultVo;
import com.ziroom.qa.quality.defende.provider.vo.BatchExecuteVo;
import com.ziroom.qa.quality.defende.provider.vo.SingleExecuteVo;

import java.util.List;

public interface TestExecutionService extends IService<TestExecution> {

    void markTestCaseFail(SingleExecuteVo singleExecute, String userName);

    String batchExecuteTestCase(BatchExecuteVo batchExecute, String userName);

    RestResultVo getCaseStatusNumber(Long taskId, String userName);

    RestResultVo queryTestCaseByPage(Long taskId, Page page);

    boolean updateTestExecutionUserInfo();

    /**
     * 获取最新的测试执行记录
     *
     * @param taskId
     * @return
     */
    List<TestExecution> getTestExecutionByNew(Long taskId);

    /**
     * 获取对应日期的测试执行记录
     *
     * @param taskId
     * @param startDateStr
     * @param endDateStr
     * @return
     */
    List<TestExecution> getTestExecutionByDate(Long taskId, String startDateStr, String endDateStr);

    /**
     * 查询测试执行历史
     *
     * @param testExecution
     * @return
     */
    List<TestExecution> getTestExecutionHistory(TestExecution testExecution);

//    /**
//     * 校验测试执行版本
//     * @param batchExecute
//     * @param userName
//     * @return
//     */
//    TestResultVo checkTestCaseVersion(BatchExecuteVo batchExecute, String userName);

    /**
     * 更新测试（执行数据）
     *
     * @param executionTaskId
     * @param userName
     */
    void updateCount(Long executionTaskId, String userName);

    /**
     * 创建测试执行和测试执行case
     *
     * @param executionTaskId
     * @param userName
     * @param autoRecordList
     */
    void autoExetionTask(long executionTaskId, String userName, List<AutoExecutionRecordVo> autoRecordList);

    /**
     * 一键生成bug
     *
     * @param singleExecute
     * @param userName
     */
    String createBugByInfo(SingleExecuteVo singleExecute, String userName);

    /**
     * 根据jiraID查询所需的信息
     *
     * @param taskId
     * @param userName
     * @return
     */
    DemandVO queryDemandInfo(Long taskId, String userName);

    /**
     * 获取所有bug对应的测试执行记录
     *
     * @param taskId
     * @return
     */
    List<TestExecution> getBugTestExecutionByTaskId(Long taskId);
}
