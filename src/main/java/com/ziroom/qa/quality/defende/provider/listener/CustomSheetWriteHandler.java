package com.ziroom.qa.quality.defende.provider.listener;

import com.alibaba.excel.write.handler.SheetWriteHandler;
import com.alibaba.excel.write.metadata.holder.WriteSheetHolder;
import com.alibaba.excel.write.metadata.holder.WriteWorkbookHolder;
import com.ziroom.qa.quality.defende.provider.constant.QualityDefendeConstants;
import com.ziroom.qa.quality.defende.provider.entity.DataDictionary;
import com.ziroom.qa.quality.defende.provider.service.DataDictionaryService;
import org.apache.poi.ss.usermodel.DataValidation;
import org.apache.poi.ss.usermodel.DataValidationConstraint;
import org.apache.poi.ss.usermodel.DataValidationHelper;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 自定义Excel格式
 */
@Service
public class CustomSheetWriteHandler implements SheetWriteHandler {

    @Autowired
    private DataDictionaryService dataDictionaryService;

    @Override
    public void beforeSheetCreate(WriteWorkbookHolder writeWorkbookHolder, WriteSheetHolder writeSheetHolder) {

    }

    @Override
    public void afterSheetCreate(WriteWorkbookHolder writeWorkbookHolder, WriteSheetHolder writeSheetHolder) {
        //定义一个map key是需要添加下拉框的列的index value是下拉框数据
        Map<Integer, List<String>> mapDropDown = new HashMap<>(1);

        // 测试级别字典列表
        List<DataDictionary> levelList = dataDictionaryService.queryDataDictionaryListByType(QualityDefendeConstants.DATA_DICTIONARY_TEST_LEVEL_TYPE);
        List<String> levelNameList = levelList.stream().map(DataDictionary::getName).collect(Collectors.toList());

        // 测试策略（类型）字典列表
        List<DataDictionary> typeList = dataDictionaryService.queryDataDictionaryListByType(QualityDefendeConstants.DATA_DICTIONARY_STRATEGY_TYPE);
        List<String> typeNameList = typeList.stream().map(DataDictionary::getName).collect(Collectors.toList());

        // 测试所属平台（所属端）字典列表
        List<DataDictionary> applicationList = dataDictionaryService.queryDataDictionaryListByType(QualityDefendeConstants.DATA_DICTIONARY_APPLICATION_TYPE);
        List<String> applicationNameList = applicationList.stream().map(DataDictionary::getName).collect(Collectors.toList());

        //定义下拉框中的数据 并添加到map中 这里的key是写死的
        mapDropDown.put(1, levelNameList);
        mapDropDown.put(2, typeNameList);
        mapDropDown.put(3, applicationNameList);

        //获取工作簿
        Sheet sheet = writeSheetHolder.getSheet();
        //开始设置下拉框
        DataValidationHelper helper = sheet.getDataValidationHelper();
        //设置下拉框
        for (Map.Entry<Integer, List<String>> entry : mapDropDown.entrySet()) {
            /*起始行、终止行、起始列、终止列  起始行为1即表示表头不设置**/
            CellRangeAddressList addressList = new CellRangeAddressList(1, 65535, entry.getKey(), entry.getKey());
            /*设置下拉框数据**/
            String[] strings = new String[entry.getValue().size()];
            DataValidationConstraint constraint = helper.createExplicitListConstraint(entry.getValue().toArray(strings));
            DataValidation dataValidation = helper.createValidation(constraint, addressList);
            sheet.addValidationData(dataValidation);
        }


    }
}
