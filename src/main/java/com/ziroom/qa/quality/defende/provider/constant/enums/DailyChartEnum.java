package com.ziroom.qa.quality.defende.provider.constant.enums;

public enum DailyChartEnum {

    /**
     * 测试执行任务类型
     */
    total("total","总进度图表"),
    accumulative("accumulative","累计比例图表"),
    daily("daily","当日比例图表")
    ;

    private String chartKey;
    private String chartName;

    DailyChartEnum(String chartKey, String chartName) {
        this.chartKey = chartKey;
        this.chartName = chartName;
    }

    public String getChartKey() {
        return chartKey;
    }

    public String getChartName() {
        return chartName;
    }

}
