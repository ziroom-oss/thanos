package com.ziroom.qa.quality.defende.provider.execTask.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ziroom.qa.quality.defende.provider.caseRepository.entity.TestCase;
import com.ziroom.qa.quality.defende.provider.caseRepository.entity.dto.SingleApiCaseListDto;
import com.ziroom.qa.quality.defende.provider.execTask.entity.TaskTestCase;
import com.ziroom.qa.quality.defende.provider.vo.Pagination;
import com.ziroom.qa.quality.defende.provider.vo.TaskTestCaseVo;
import com.ziroom.qa.quality.defende.provider.vo.TestReportStatusVo;

import java.util.List;

public interface TaskTestCaseService extends IService<TaskTestCase> {

    List<TaskTestCase> getTaskTestCaseByTaskId(Long taskId);

    /**
     * 根据testCaseId查询存在的id
     *
     * @param testCaseList
     * @return 存在的testCaseId
     */
    List<Long> findIdsByIdList(List<Long> testCaseList);

    void batchDeleteTaskTestCase(TaskTestCaseVo taskTestCaseVo);

    void batchSaveOrUpdateTaskTestCase(TaskTestCaseVo taskTestCaseVo, String userName);

    boolean updateTaskTestCaseUserInfo();

    /**
     * 获取测试 task 对应的测试结果
     *
     * @param taskId
     * @return
     */
    TestReportStatusVo getTestReportStatusByTaskId(Long taskId);

    /**
     * 分页查询已经关联的测试用例
     *
     * @param pagination
     * @return
     */
    Page<TaskTestCase> queryRelTestCaseByPage(Pagination<TaskTestCase> pagination);

    /**
     * 查询未关联自动测试用例执行分页信息
     *
     * @param pagination
     * @return
     */
    Page queryUnRelApiCaseByPage(Pagination<SingleApiCaseListDto> pagination);

    /**
     * 查询未关联人工测试用例执行分页信息
     *
     * @param pagination
     * @return
     */
    Page<TestCase> queryUnRelTestCaseByPage(Pagination<TestCase> pagination);

    /**
     * 获取测试用例详情信息（已关联的）
     *
     * @param id
     * @return
     */
    TaskTestCase getCaseDetailById(Long id);
}
