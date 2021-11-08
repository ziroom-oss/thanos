package com.ziroom.qa.quality.defende.provider.execTask.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ziroom.qa.quality.defende.provider.caseRepository.entity.AutoSingleApiCase;
import com.ziroom.qa.quality.defende.provider.caseRepository.service.IAutoSingleApiCaseService;
import com.ziroom.qa.quality.defende.provider.constant.enums.ExecutionResultEnum;
import com.ziroom.qa.quality.defende.provider.execTask.entity.AutoExecutionRecord;
import com.ziroom.qa.quality.defende.provider.execTask.entity.AutoExecutionRecordAssert;
import com.ziroom.qa.quality.defende.provider.execTask.entity.dto.LatestExecutionRecordDto;
import com.ziroom.qa.quality.defende.provider.execTask.entity.dto.RunByCaseDetailDto;
import com.ziroom.qa.quality.defende.provider.execTask.entity.vo.AutoExecutionRecordVo;
import com.ziroom.qa.quality.defende.provider.execTask.mapper.AutoExecutionRecordMapper;
import com.ziroom.qa.quality.defende.provider.execTask.service.IAutoExecutionRecordAssertService;
import com.ziroom.qa.quality.defende.provider.execTask.service.IAutoExecutionRecordService;
import com.ziroom.qa.quality.defende.provider.util.handler.FormatResultVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author mybatisPlusAutoGenerate
 * @since 2021-09-26
 */
@Service
public class AutoExecutionRecordServiceImpl extends ServiceImpl<AutoExecutionRecordMapper, AutoExecutionRecord> implements IAutoExecutionRecordService {
    @Autowired
    private IAutoExecutionRecordAssertService autoExecutionRecordAssertService;
    @Autowired
    private IAutoSingleApiCaseService autoSingleApiCaseService;
    @Override
    public AutoExecutionRecord saveExecuteRecord(RunByCaseDetailDto runByCaseDetailDto, FormatResultVo vo) {
        AutoExecutionRecord autoExecutionRecord = new AutoExecutionRecord();
        BeanUtils.copyProperties(runByCaseDetailDto,autoExecutionRecord);
        BeanUtils.copyProperties(vo,autoExecutionRecord);
        autoExecutionRecord.setApiId(runByCaseDetailDto.getApiId());
        autoExecutionRecord.setCaseId(runByCaseDetailDto.getId());
        autoExecutionRecord.setDeleted(false);
        autoExecutionRecord.setRecordName("");
        String actualResult = autoExecutionRecord.getActualResult();
        if(actualResult!=null&&actualResult.length()>5120){
            actualResult = actualResult.substring(0,5120);
            autoExecutionRecord.setActualResult(actualResult);
        }
        autoExecutionRecord.setRecordResult(ExecutionResultEnum.FAILURE.toString());
        this.save(autoExecutionRecord);
        return autoExecutionRecord;
    }

    @Override
    public List<AutoExecutionRecordVo> caseLatestDetail(LatestExecutionRecordDto dto) {
        Integer lastSize = dto.getLastSize();
        List<Integer> caseIdList = dto.getCaseIdList();
        List<AutoExecutionRecordVo> listRes = new ArrayList<>();
        caseIdList.forEach(caseId ->{
            AutoSingleApiCase apiCase = autoSingleApiCaseService.getById(caseId);
            QueryWrapper<AutoExecutionRecord> queryWrapper =new QueryWrapper<>();
            queryWrapper.eq("case_id",caseId)
                    .eq("deleted",0)
                    .last("limit " +lastSize);
            List<AutoExecutionRecord> list = this.list(queryWrapper);
            list.forEach(record ->{
                AutoExecutionRecordVo vo = new AutoExecutionRecordVo();
                BeanUtils.copyProperties(record,vo);
                vo.setCaseName(apiCase.getCaseName());
                QueryWrapper<AutoExecutionRecordAssert> queryWrapperAssert = new QueryWrapper<>();
                queryWrapper.eq("record_id",record.getId());
                List<AutoExecutionRecordAssert> listAssert = autoExecutionRecordAssertService.list(queryWrapperAssert);
                vo.setList(listAssert);
                listRes.add(vo);
            });
        });
        return listRes;
    }
}
