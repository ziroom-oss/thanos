package com.ziroom.qa.quality.defende.provider.vo.statistics;

import lombok.Data;

import java.util.List;

/**
 * @program: api-qabi-web
 * @description: 图表Series格式
 * @author: zhangw7
 * @create: 2021-01-24 15:26
 **/
@Data
public class ChartSeriesDto<T> {

    private String name;
    private String stack;
    private String type;
    private int yAxisIndex;
    private List<T> data;

    public ChartSeriesDto() {

    }

}
