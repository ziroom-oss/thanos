package com.ziroom.qa.quality.defende.provider.vo.statistics;

import lombok.Data;

import java.util.List;

@Data
public class TestCaseStatisticResultVo {

    /**
     * 统计报表标题
     */
    private String title;

    /**
     * 子标题
     */
    private String subtext;

    /**
     * 每个统计维度的名称
     */
    private List<String> legendList;

    /**
     * x轴列表
     */
    private List<String> xAxis;

    /**
     * y轴列表
     */
    private List<ChartyAxisDto> yAxisList;


    /**
     * 具体的内容series
     */
    private List<ChartSeriesDto> seriesList;

}
