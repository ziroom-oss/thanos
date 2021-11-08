package com.ziroom.qa.quality.defende.provider.execTask.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ziroom.qa.quality.defende.provider.execTask.entity.TestTask;
import com.ziroom.qa.quality.defende.provider.execTask.entity.dto.ArkAppDTO;
import com.ziroom.qa.quality.defende.provider.execTask.entity.vo.ComplateAndEmailVO;
import com.ziroom.qa.quality.defende.provider.result.RestResultVo;
import com.ziroom.qa.quality.defende.provider.vo.TestResultVo;

import java.util.List;


public interface TestTaskService extends IService<TestTask> {

    long saveOrUpdateTestTask(TestTask testTask, String userName);

    void validateTestTaskName(String taskName, Long taskId);

    TestResultVo batchDeleteTestTask(List<Long> idList, String userName);

    RestResultVo getTestReportCaseListByTaskId(Page page, Long taskId, String type);

    RestResultVo getTestReportBugListByTaskId(Page page, Long taskId);

    boolean updateTestTaskUserInfo();

    /**
     * 分页查询
     *
     * @param page
     * @param testTask
     * @param userName
     * @return
     */
    Page<TestTask> queryTestTaskByPage(Page<TestTask> page, TestTask testTask, String userName);

    TestTask getTestTaskDetailById(Long id);

    TestResultVo completeTaskStatus(List<Long> idList, String userName);

    TestResultVo restartTaskStatus(List<Long> idList, String userName);

    /**
     * 获取appid信息列表
     * @return
     */
    List<ArkAppDTO> getAppInfoList();

    List<TestTask> findByIssueKey(String issueKey);

    void completeTaskAndEmail(ComplateAndEmailVO complateAndEmailVO, String userName);
}
