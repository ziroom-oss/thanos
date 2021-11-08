package com.ziroom.qa.quality.defende.provider.execTask.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ziroom.qa.quality.defende.provider.execTask.entity.AutoExecutionRecord;
import com.ziroom.qa.quality.defende.provider.execTask.entity.dto.LatestExecutionRecordDto;
import com.ziroom.qa.quality.defende.provider.execTask.entity.dto.RunByCaseDetailDto;
import com.ziroom.qa.quality.defende.provider.execTask.entity.vo.AutoExecutionRecordVo;
import com.ziroom.qa.quality.defende.provider.util.handler.FormatResultVo;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author mybatisPlusAutoGenerate
 * @since 2021-09-26
 */
public interface IAutoExecutionRecordService extends IService<AutoExecutionRecord> {

    AutoExecutionRecord saveExecuteRecord(RunByCaseDetailDto runByCaseDetailDto, FormatResultVo vo);

    List<AutoExecutionRecordVo> caseLatestDetail(LatestExecutionRecordDto dto);
}
