package com.ziroom.qa.quality.defende.provider.vo.statistics;

import lombok.Data;

@Data
public class StatisticSearchVo {

    /**
     * 业务类型（根据应用组织结构统计）
     */
    public static final String STATISTIC_BUSINESS = "business";

    /**
     * 部门类型（根据人员组织结构统计）
     */
    public static final String STATISTIC_DEPARTMENT = "department";

    /**
     * 开始时间-结束时间
     */
    private String dateTimeStr;
    /**
     * 父级部门code
     */
    private String parentDeptCode;

    /**
     * 统计类型
     */
    private String statisticType = STATISTIC_BUSINESS;

    /**
     * 用例数量排序 0 升序，1 降序
     * 执行数量排序 2 升序，3 降序
     */
    private Integer caseOrder;

    /**
     * 用例数[开始,结束],闭区间
     * ps：0,100
     */
    private String caseStaEnd;

    /**
     * 执行数[开始,结束],闭区间
     * ps：0,100
     */
    private String exeStaEnd;
}
