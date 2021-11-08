package com.ziroom.qa.quality.defende.provider.vo.statistics.table;

import lombok.Data;

@Data
public class TestCaseTopVo {

    private int number;

    private String casename;

    private Long id;

    private String createUser;

    private String createTime;

    private int tcCount;
}
