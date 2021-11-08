package com.ziroom.qa.quality.defende.provider.vo.testcase;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TestCaseDownVo {

    /**
     * 测试用例名称
     */
    @ColumnWidth(50)
    @ExcelProperty(value = "用例名称*")
    private String casename;

    /**
     * 优先级
     */
    @ColumnWidth(20)
    @ExcelProperty(value = "优先级*")
    private String testCaseLevel;

    /**
     * 测试用例类型
     */
    @ColumnWidth(20)
    @ExcelProperty(value = "用例类型*")
    private String type;

    /**
     * 所属平台（所属端）
     */
    @ColumnWidth(20)
    @ExcelProperty(value = "所属平台*")
    private String belongPlatform;

    /**
     * 前置条件
     */
    @ColumnWidth(30)
    @ExcelProperty(value = "前置条件")
    private String preCondition;

    /**
     * 执行步骤
     */
    @ColumnWidth(50)
    @ExcelProperty(value = "执行步骤*")
    private String step;

    /**
     * 期望结果
     */
    @ColumnWidth(50)
    @ExcelProperty(value = "期望结果*")
    private String expectedResults;

//    /**
//     * 所属项目（是一个跨平台的项目）
//     */
//    @ColumnWidth(20)
//    @ExcelProperty(value = "所属项目")
//    private String belongTopic;

    /**
     * 测试用例所属应用
     */
    @ColumnWidth(20)
    @ExcelProperty(value = "所属应用")
    private String belongToSystem;

    /**
     * 关联所属模块的ID ： test_application_module 的id字段
     */
    @ColumnWidth(20)
    @ExcelProperty(value = "所属模块")
    private String belongToModule;

}
