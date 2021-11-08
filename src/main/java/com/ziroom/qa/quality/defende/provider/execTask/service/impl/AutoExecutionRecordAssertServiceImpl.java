package com.ziroom.qa.quality.defende.provider.execTask.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.JSONPath;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ziroom.qa.quality.defende.provider.caseRepository.entity.AutoSingleAssert;
import com.ziroom.qa.quality.defende.provider.constant.enums.AssertTypeEnum;
import com.ziroom.qa.quality.defende.provider.constant.enums.ExecutionResultEnum;
import com.ziroom.qa.quality.defende.provider.execTask.entity.AutoExecutionRecord;
import com.ziroom.qa.quality.defende.provider.execTask.entity.AutoExecutionRecordAssert;
import com.ziroom.qa.quality.defende.provider.execTask.mapper.AutoExecutionRecordAssertMapper;
import com.ziroom.qa.quality.defende.provider.execTask.service.IAutoExecutionRecordAssertService;
import com.ziroom.qa.quality.defende.provider.util.handler.FormatResultVo;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

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
public class AutoExecutionRecordAssertServiceImpl extends ServiceImpl<AutoExecutionRecordAssertMapper, AutoExecutionRecordAssert> implements IAutoExecutionRecordAssertService {

    @Override
    public List<AutoExecutionRecordAssert> assertAndSave(AutoExecutionRecord executeRecord, FormatResultVo vo) {
        List<AutoSingleAssert> list = vo.getList();
        List<AutoExecutionRecordAssert> autoExecutionRecordAsserts = new ArrayList<>();
        if(CollectionUtils.isEmpty(list)){
            AutoExecutionRecordAssert recordAssert = new AutoExecutionRecordAssert();
            recordAssert.setAssertContent("【平台提示]断言为空！请添加断言");
            recordAssert.setAssertResult(ExecutionResultEnum.FAILURE.toString());
            autoExecutionRecordAsserts.add(recordAssert);
            return autoExecutionRecordAsserts;
        }
//      断言
        list.stream().forEach(autoSingleAssert ->{
            AutoExecutionRecordAssert recordAssert = new AutoExecutionRecordAssert();
            recordAssert.setAssertContent(autoSingleAssert.getAssertContent());
            Integer assertType = autoSingleAssert.getAssertType();
            recordAssert.setAssertType(assertType);
            recordAssert.setRecordId(executeRecord.getId());
            if(assertType == AssertTypeEnum.CONTAIN.getKey().intValue()){
                String actualResult = vo.getActualResult();
                String expectedResults = autoSingleAssert.getAssertContent();
                String recordResult;
                if(expectedResults!=null&&actualResult.contains(expectedResults.trim())){
                    recordResult = ExecutionResultEnum.SUCCESS.toString();
                }else{
                    recordResult = ExecutionResultEnum.FAILURE.toString();
                }
                recordAssert.setAssertResult(recordResult);
            }
            if(assertType == AssertTypeEnum.JSONPATH.getKey().intValue()){
                String actualResult = vo.getActualResult();
                String assertContent = autoSingleAssert.getAssertContent();
                JSONObject jsonObject = JSONObject.parseObject(assertContent);
                String key = jsonObject.getString("key");
                String value = String.valueOf(jsonObject.getString("value"));
                String recordResult;
                String read = String.valueOf(JSONPath.read(actualResult, key));
                if(read!=null&&read.contains(value.trim())){
                    recordResult = ExecutionResultEnum.SUCCESS.toString();
                }else{
                    recordResult = ExecutionResultEnum.FAILURE.toString();
                }
                recordAssert.setAssertResult(recordResult);
            }
            autoExecutionRecordAsserts.add(recordAssert);
        });
        //            保存断言记录
        if(executeRecord.getCaseId()==null)
        this.saveBatch(autoExecutionRecordAsserts);
        return autoExecutionRecordAsserts;
    }}
