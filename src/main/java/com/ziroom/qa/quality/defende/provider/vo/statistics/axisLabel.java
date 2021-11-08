package com.ziroom.qa.quality.defende.provider.vo.statistics;
import lombok.Data;

/**
 * @program: api-qabi-web
 * @description: 图表Y轴列表格式
 * @author: xuezhenlong
 * @create: 2021年6月1日16:17:22
 **/
@Data
public class axisLabel {
    //Y轴数据的单位
    private String formatter;

    public axisLabel(){

    }

    public axisLabel(String formatter){
        this.formatter = formatter;
    }

}
