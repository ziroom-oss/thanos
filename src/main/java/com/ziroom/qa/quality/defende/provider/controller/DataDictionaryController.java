package com.ziroom.qa.quality.defende.provider.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.ziroom.qa.quality.defende.provider.constant.QualityDefendeConstants;
import com.ziroom.qa.quality.defende.provider.entity.DataDictionary;
import com.ziroom.qa.quality.defende.provider.result.RestResultVo;
import com.ziroom.qa.quality.defende.provider.service.DataDictionaryService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(value = "数据字典", tags = {"数据字典"})
@RestController
@RequestMapping("/dataDictionary")
public class DataDictionaryController {

    @Autowired
    private DataDictionaryService dataDictionaryService;


    /**
     * 获取所有的测试用例等级
     *
     * @return
     */
    @GetMapping("/getTestCaseLevel")
    public RestResultVo<List<DataDictionary>> getTestCaseLevel() {
        QueryWrapper<DataDictionary> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("type", QualityDefendeConstants.DATA_DICTIONARY_TYPE_CASE_LEVEL);
        return RestResultVo.fromData(dataDictionaryService.list(queryWrapper));
    }

    @ApiOperation("查询应用类型列表")
    @GetMapping("/queryTestApplicationTypeList")
    public RestResultVo<List<DataDictionary>> queryTestApplicationTypeList() {
        return RestResultVo.fromData(dataDictionaryService.queryDataDictionaryListByType(QualityDefendeConstants.DATA_DICTIONARY_APPLICATION_TYPE));
    }


    @ApiOperation("查询测试环境列表")
    @GetMapping("/queryTestEnvironmentTypeList")
    public RestResultVo<List<DataDictionary>> queryTestEnvironmentTypeList() {
        return RestResultVo.fromData(dataDictionaryService.queryDataDictionaryListByType(QualityDefendeConstants.DATA_DICTIONARY_ENVIRONMENT_TYPE));
    }

    @ApiOperation("查询测试策略列表")
    @GetMapping("/queryTestStrategyTypeList")
    public RestResultVo<List<DataDictionary>> queryTestStrategyTypeList() {
        return RestResultVo.fromData(dataDictionaryService.queryDataDictionaryListByType(QualityDefendeConstants.DATA_DICTIONARY_STRATEGY_TYPE));
    }

    @ApiOperation("查询测试级别列表")
    @GetMapping("/queryTestLevelTypeList")
    public RestResultVo<List<DataDictionary>> queryTestLevelTypeList() {
        return RestResultVo.fromData(dataDictionaryService.queryDataDictionaryListByType(QualityDefendeConstants.DATA_DICTIONARY_TEST_LEVEL_TYPE));
    }

    @ApiOperation("查询用例属性类型")
    @GetMapping("/queryFieldTypeList")
    public RestResultVo<List<DataDictionary>> queryFieldTypeList() {
        return RestResultVo.fromData(dataDictionaryService.queryDataDictionaryListByType(QualityDefendeConstants.DATA_DICTIONARY_TEST_FIELD_TYPE));
    }

    @ApiOperation("查询用例变更类型列表")
    @GetMapping("/queryTestChangeTypeList")
    public RestResultVo<List<DataDictionary>> queryTestChangeTypeList() {
        return RestResultVo.fromData(dataDictionaryService.queryDataDictionaryListByType(QualityDefendeConstants.DATA_DICTIONARY_CHANGE_TYPE));
    }


    @ApiOperation("保存或更新数据字典")
    @PostMapping("/saveOrUpdateDataDictionary")
    public RestResultVo saveOrUpdateDataDictionary(@RequestBody DataDictionary dataDictionary) {
        return RestResultVo.fromData(dataDictionaryService.saveOrUpdateDataDictionary(dataDictionary));
    }

    @ApiOperation("查询终端类型")
    @GetMapping("/queryEndTypeList")
    public RestResultVo<List<DataDictionary>> queryEndTypeList() {
        return RestResultVo.fromData(dataDictionaryService.queryDataDictionaryListByType(QualityDefendeConstants.DATA_DICTIONARY_TEST_END_TYPE));
    }


//    /**
//     * 保存测试用例登录
//     * @param dataDictionary
//     * @return
//     */
//    @PostMapping("/saveTestCaseLevelToDataDictionary")
//    public RestResult saveTestCaseLevelToDataDictionary(@RequestBody DataDictionary dataDictionary){
//        dataDictionary.setType(QualityDefendeConstants.DATA_DICTIONARY_TYPE_CASE_LEVEL);
//        dataDictionary.setEnglishName("level");
//        dataDictionary.setName();
//
//    }

}
