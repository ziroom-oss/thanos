package com.ziroom.qa.quality.defende.provider.vo.statistics;

import lombok.Data;

@Data
public class TaskTopVo {

    private String applicationName;
    /**
     * 数量统计
     */
    private int appCount;


    private Long taskId;
    private String taskName;

    /**
     * 执行结果（成功，失败，未执行，跳过）
     */
    private String exeRes;

    /**
     * 成功率字符串
     */
    private String sucRateStr;

    /**
     * 应用所在组名
     */
    private String groupName;

    /**
     * 成功数
     */
    private int succCount;

}
