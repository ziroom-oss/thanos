package com.ziroom.qa.quality.defende.provider.vo.statistics;
import lombok.Data;


/**
 * @program: api-qabi-web
 * @description: 图表Y轴列表格式
 * @author: xuezhenlong
 * @create: 2021年6月1日16:17:22
 **/
@Data
public class ChartyAxisDto {
    //y轴坐标单位计量
    private String name;
    //y轴数据显示的单位
    private axisLabel axisLabel;
    //控制Y轴显示的位置
    private String position;


    public ChartyAxisDto() {

    }
    public ChartyAxisDto(String name, axisLabel axisLabel, String position) {
        this.name = name;
        this.axisLabel = axisLabel;
        this.position = position;

    }

}
