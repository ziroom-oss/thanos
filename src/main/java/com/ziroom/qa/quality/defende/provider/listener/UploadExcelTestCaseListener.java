package com.ziroom.qa.quality.defende.provider.listener;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.google.common.collect.Maps;
import com.ziroom.qa.quality.defende.provider.caseRepository.entity.TestCase;
import com.ziroom.qa.quality.defende.provider.result.CustomException;
import com.ziroom.qa.quality.defende.provider.vo.testcase.TestCaseDownVo;
import com.ziroom.qa.quality.defende.provider.vo.testcase.TestCaseUploadVo;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Slf4j
public class UploadExcelTestCaseListener extends AnalysisEventListener<TestCaseDownVo> {

    /**
     * 每隔5条存储数据库，实际使用中可以3000条，然后清理list ，方便内存回收
     */
    private static final int BATCH_COUNT = 5;

    private TestCaseUploadVo testCaseUploadVo;

    List<TestCase> testCaseList = new ArrayList<>();
    Map<Integer, String> errorMap = Maps.newHashMap();

    public UploadExcelTestCaseListener(TestCaseUploadVo testCaseUploadVo) {
        this.testCaseUploadVo = testCaseUploadVo;
    }

    /**
     * 每条数据都会调用
     *
     * @param uploadVo
     * @param analysisContext
     */
    @SneakyThrows
    @Override
    public void invoke(TestCaseDownVo uploadVo, AnalysisContext analysisContext) {
        //1. 将上传的测试用例对象转换为DB对象
        TestCase testCase = new TestCase();
        BeanUtils.copyProperties(uploadVo, testCase);
        testCase.setUploadFlag(testCaseUploadVo.isUploadFlag());

        //2. 上传必填公共字段
        testCase = testCaseUploadVo.getTestCaseService().initBelongToAttr(testCase, testCaseUploadVo);

        //3. 验证测试用例必填属性
        testCaseUploadVo.setRestResultVo(testCaseUploadVo.getTestCaseService().formatTestCase(testCase));
        if (testCaseUploadVo.getRestResultVo().isSuccess()) {
            testCaseList.add(testCaseUploadVo.getRestResultVo().getData());
        } else {
            String errorMessage = testCaseUploadVo.getRestResultVo().getMessage();
            int rowIndex = analysisContext.readRowHolder().getRowIndex();
            errorMap.put(rowIndex, errorMessage);
        }
    }

    /**
     * 所有数据解析完会调用
     *
     * @param analysisContext
     */
    @Override
    public void doAfterAllAnalysed(AnalysisContext analysisContext) {
        //1. 批量验证上传文档测试用例名称是否重复
        String result = testCaseUploadVo.getTestCaseService().batchValidateUploadFileTestCaseNameListRepeat(testCaseList);
        if (StringUtils.isNotBlank(result)) {
            testCaseUploadVo.getRestResultVo().setSuccess(false);
            testCaseUploadVo.getRestResultVo().setMessage(result);
        } else {
            //2. 验证上传的文档和DB中的用例是否有异常
            StringBuilder sb = new StringBuilder();
            if (MapUtils.isNotEmpty(errorMap)) {
                Set<Integer> indexKeySet = errorMap.keySet();
                for (Integer indexKey : indexKeySet) {
                    sb.append("第").append(indexKey + 1).append("行").append("数据异常：").append(errorMap.get(indexKey)).append("\n");
                }
                throw new CustomException(sb.toString());
            } else {
                //3. 批量保存测试用例
                savaTestCaseExcelDdata(testCaseList, testCaseUploadVo.isUploadFlag());
            }
        }
    }

    /**
     * 批量保存测试用例
     *
     * @param testCaseList
     * @param uploadFlag
     */
    public void savaTestCaseExcelDdata(List<TestCase> testCaseList, boolean uploadFlag) {
        if (CollectionUtils.isNotEmpty(testCaseList)) {
            testCaseUploadVo.getTestCaseService().batchSaveTestCase(testCaseList, uploadFlag);
        }
    }


}
