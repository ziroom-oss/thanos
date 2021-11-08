package com.ziroom.qa.quality.defende.provider.execTask.service;

import com.ziroom.qa.quality.defende.provider.execTask.entity.dto.RunByCaseDetailDto;
import com.ziroom.qa.quality.defende.provider.execTask.entity.dto.RunCaseByIdListDto;
import com.ziroom.qa.quality.defende.provider.execTask.entity.vo.AutoExecutionRecordVo;

import java.util.List;

/**
 * @author zhujj5
 * @Title: 自动化用例执行服务
 * @Description:
 * @date 2021/9/24 5:35 下午
 */
public interface IAutoTestCaseExecuteService {

    List<AutoExecutionRecordVo> runCaseByIdList(RunCaseByIdListDto dto);

    AutoExecutionRecordVo runCaseByDetail(RunByCaseDetailDto runByCaseDetailDto);

    void runAllCaseByAppName(String qua, String appName, String branch);

}
