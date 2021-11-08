package com.ziroom.qa.quality.defende.provider.vo.outvo;

import com.ziroom.qa.quality.defende.provider.execTask.entity.TestTask;
import com.ziroom.qa.quality.defende.provider.vo.TestReportBugListVo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 对外接口返回信息
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OutResVo {

    /**
     * jiraid
     */
    private String jiraId;

    /**
     * 测试执行连接地址
     */
    private String testTaskUrl;

    /**
     * 测试执行数据
     */
    private TestTask testTask;

    /**
     * 测试执行bug列表
     */
    private List<TestReportBugListVo> bugList;
}
