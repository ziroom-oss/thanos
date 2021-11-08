package com.ziroom.qa.quality.defende.provider.vo;

import lombok.Data;
import java.util.List;

@Data
public class TestCaseVo {

    private List<Long> idList;
    private String approve;
    private String remark;
    private String changeType;
    private String userName;
}
