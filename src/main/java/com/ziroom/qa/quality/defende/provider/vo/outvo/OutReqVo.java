package com.ziroom.qa.quality.defende.provider.vo.outvo;

import com.ziroom.qa.quality.defende.provider.execTask.entity.vo.AutoExecutionRecordVo;
import com.ziroom.qa.quality.defende.provider.vo.telot.ExecutionRecordEntity;
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
public class OutReqVo {

    /**
     * jiraid
     */
    private String jiraId;

    /**
     * 测试执行名称
     */
    private String taskName;

    /**
     * 分支名称
     */
    private String branchName;

    /**
     * 终端类型：
     * service
     * FE
     * Android
     * IOS
     */
    private String endType;

    /**
     * appid
     */
    private String appId;

    /**
     * 开发人员邮箱前缀 ps:liangh4
     */
    private String devUserName;

    /**
     * 测试人员邮箱前缀 ps:liangh4
     */
    private String testUserName;

    /**
     * 自动测试执行结果回调
     */
    private List<AutoExecutionRecordVo> autoRecordList;

    /**
     * 必验项标识，如果是必验项（统计使用）
     * 1.代表方舟平台release的标识
     */
    private String checkTag;

    /**
     * 测试执行返回结果
     */
    private String testCaseResult;

}
