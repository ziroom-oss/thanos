package com.ziroom.qa.quality.defende.provider.execTask.service;

import com.ziroom.qa.quality.defende.provider.vo.outvo.OutReqVo;
import com.ziroom.qa.quality.defende.provider.vo.outvo.OutResVo;

public interface TestTaskOutService {

    /**
     * 创建并获取测试执行信息
     *
     * @param outReqVo
     * @return
     */
    OutResVo outCreateOrGetTestTask(OutReqVo outReqVo);

    /**
     * 获取测试执行报告信息
     *
     * @param outReqVo
     * @return
     */
    OutResVo getTestTaskDetailInfo(OutReqVo outReqVo);

    /**
     * 提测完成（方舟系统回调接口）
     *
     * @param outReqVo
     */
    void submitTestTaskByInfo(OutReqVo outReqVo);

    /**
     * 上线完成（方舟系统回调接口）
     *
     * @param outReqVo
     */
    void launchTestTaskByInfo(OutReqVo outReqVo);

    /**
     * 创建自动的测试执行（zhujj 需要调用的接口）
     *
     * @param outReqVo
     */
    OutResVo createAutoTestExecution(OutReqVo outReqVo);

    /**
     * 自动测试执行任务的用例数（相同应用）
     *
     * @param dateTimeStr
     */
    void sendAutoTaskMsg(String dateTimeStr);

    /**
     * jira创建测试执行
     *
     * @param issueMsg
     */
    void jiraCreateTestExe(String issueMsg);
}
