package com.ziroom.qa.quality.defende.provider.vo.dailyreport;

import com.ziroom.qa.quality.defende.provider.entity.DailyTestReportType;
import com.ziroom.qa.quality.defende.provider.entity.TestTopic;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class DailyReportVo {

    /**
     * 日报汇总信息
     */
    private DailyTestReportType dailyReportSum;

    /**
     * 项目信息
     */
    private TestTopic testTopic;

    /**
     * 日报名称
     */
    private String dailyReportName;

    /**
     * 风险名称
     */
    private String dailyRiskNames;

    /**
     * 用例图表信息
     * key:total 总进度图表
     * key:accumulative 累计比例图表
     * key:daily 当日比例图表
     * value：List<key=比例说明，value=数值>
     */
    private Map<String, List<DailyChartVo>> chartMap;

    /**
     * 测试执行任务信息集合
     */
    private List<DailyTestReportType> dailyTestTaskList;

    /**
     * 测试执行任务数
     */
    private int taskSize;

    private boolean jiraFlag = true;


}
