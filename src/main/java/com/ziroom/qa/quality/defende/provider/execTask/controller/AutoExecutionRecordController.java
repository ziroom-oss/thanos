package com.ziroom.qa.quality.defende.provider.execTask.controller;


import com.ziroom.qa.quality.defende.provider.execTask.entity.dto.LatestExecutionRecordDto;
import com.ziroom.qa.quality.defende.provider.execTask.entity.dto.RunByCaseDetailDto;
import com.ziroom.qa.quality.defende.provider.execTask.entity.dto.RunCaseByIdListDto;
import com.ziroom.qa.quality.defende.provider.execTask.entity.vo.AutoExecutionRecordVo;
import com.ziroom.qa.quality.defende.provider.execTask.service.IAutoExecutionRecordService;
import com.ziroom.qa.quality.defende.provider.execTask.service.IAutoTestCaseExecuteService;
import com.ziroom.qa.quality.defende.provider.result.RestResultVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author mybatisPlusAutoGenerate
 * @since 2021-09-26
 */
@RestController
@RequestMapping("/execTask/auto-execution-record")
public class AutoExecutionRecordController {
    @Autowired
    private IAutoTestCaseExecuteService autoTestCaseExecuteService;
    @Autowired
    private IAutoExecutionRecordService LatestExecutionRecordDto;
    @RequestMapping("runCaseByDetail")
    public RestResultVo runByCaseDetailDto(@RequestHeader("userName")String userCode,@RequestBody RunByCaseDetailDto runByCaseDetailDto){
        runByCaseDetailDto.setUserCode(userCode);
        return RestResultVo.fromData(autoTestCaseExecuteService.runCaseByDetail(runByCaseDetailDto));
    }
    @RequestMapping("runCaseByIdList")
    public RestResultVo runByCaseDetailDto(@RequestHeader("userName")String userCode, @RequestBody RunCaseByIdListDto runCaseByIdListDto){
        runCaseByIdListDto.setUserCode(userCode);
        return RestResultVo.fromData(autoTestCaseExecuteService.runCaseByIdList(runCaseByIdListDto));
    }

    @PostMapping("latestExecutionRecord")
    public RestResultVo caseLatestDetail(@RequestBody LatestExecutionRecordDto dto){
        List<AutoExecutionRecordVo> list  = LatestExecutionRecordDto.caseLatestDetail(dto);
        return RestResultVo.fromData(list);
    }
}

