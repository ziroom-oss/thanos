package com.ziroom.qa.quality.defende.provider.controller;

import com.ziroom.qa.quality.defende.provider.result.RestResultVo;
import com.ziroom.qa.quality.defende.provider.util.JiraUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import net.rcarz.jiraclient.Issue;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;

@Api(value = "验证JIRA信息", tags = {"验证JIRA信息"})
@Slf4j
@RestController
@RequestMapping("jiraIssue")
public class JiraIssueController {

    @Value("${jiraIssue.validateFlag}")
    private String validateFlag;


    @ApiOperation("根据Jira Issue Key验证关联需求是否有效")
    @GetMapping("/validateIssueKey")
    public RestResultVo<String> validateIssueKey(@RequestParam(value = "issueKey", required = false) String issueKey) {
        try {
            if ("false".equals(validateFlag)) {
                return RestResultVo.fromSuccess("Jira key 有效");
            }
            if (StringUtils.isNotBlank(issueKey)) {
                Issue issue = JiraUtils.getJiraIssueByIssueKey(issueKey);
                if (Objects.nonNull(issue)) {
                    return RestResultVo.fromSuccess(null);
                } else {
                    return RestResultVo.fromErrorMessage("Jira key 无效");
                }
            } else {
                return RestResultVo.fromErrorMessage("请输入需求来源");
            }
        } catch (Exception e) {
            log.error("根据Jira Issue Key验证关联需求是否有效 issueKey==={} , error ==", issueKey, e);
            return RestResultVo.fromErrorMessage("JiraId无效,请重新输入");
        }
    }
}
