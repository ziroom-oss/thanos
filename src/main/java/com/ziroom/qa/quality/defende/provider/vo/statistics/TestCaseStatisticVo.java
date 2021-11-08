package com.ziroom.qa.quality.defende.provider.vo.statistics;

import lombok.Data;

/**
 * 测试用例统计报表信息
 */
@Data
public class TestCaseStatisticVo {

    /**
     * 测试用例数量
     */
    private int testCount;

    /**
     * 部门treePath
     */
    private String treePath;

    /**
     * 邮箱前缀
     */
    private String emailPre;
}
