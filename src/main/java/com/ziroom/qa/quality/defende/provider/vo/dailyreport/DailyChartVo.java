package com.ziroom.qa.quality.defende.provider.vo.dailyreport;

import lombok.Data;

@Data
public class DailyChartVo {

//    /**
//     * 图表类型
//     */
//    private String chartType;

    /**
     * 图表名
     */
    private String chartKey;

    /**
     * 图表值
     */
    private Integer chartValue;
}
