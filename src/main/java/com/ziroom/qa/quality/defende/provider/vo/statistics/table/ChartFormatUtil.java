package com.ziroom.qa.quality.defende.provider.vo.statistics.table;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.JSONPath;

/**
 * @program: api-qabi-web
 * @description: 图表返回类型封装
 * @author: zhangw7
 * @create: 2021-01-24 15:18
 **/
//例：
//{
//        title: {text: '折线图堆叠'},
//        legend: {data: ['邮件营销', '联盟广告', '视频广告', '直接访问', '搜索引擎']},
//        xAxis: {data: ['周一', '周二', '周三', '周四', '周五', '周六', '周日']},
//        yAxis: {name:'单位'},
//        series: [
//        {
//        name: '邮件营销',
//        stack: '总量',
//        data: [120, 132, 101, 134, 90, 230, 210]
//        }
//        ]
//}
public class ChartFormatUtil {

    private static final String TABLELIST = "tableList";
    private static final String KVMAP = "kvMap";

    /**
     * @description: 封装列表返回值
     * @param: [tableList,
     * @param: kvMap table参数与列名的对应关系
     * @Author: zhangw7
     * @Date: 2021/1/24
     */
    public static JSONObject tableChart(Object tableObj, JSONObject kvObj) {
        JSONObject returnObject = new JSONObject();
        JSONPath.set(returnObject, TABLELIST, tableObj);
        JSONPath.set(returnObject, KVMAP,kvObj);
        return returnObject;
    }
}
