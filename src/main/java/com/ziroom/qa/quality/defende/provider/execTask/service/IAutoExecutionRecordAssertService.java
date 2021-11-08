package com.ziroom.qa.quality.defende.provider.execTask.service;

import com.ziroom.qa.quality.defende.provider.execTask.entity.AutoExecutionRecord;
import com.ziroom.qa.quality.defende.provider.execTask.entity.AutoExecutionRecordAssert;
import com.baomidou.mybatisplus.extension.service.IService;
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
public interface IAutoExecutionRecordAssertService extends IService<AutoExecutionRecordAssert> {

    List<AutoExecutionRecordAssert> assertAndSave(AutoExecutionRecord executeRecord, FormatResultVo formatResultVo);
}
