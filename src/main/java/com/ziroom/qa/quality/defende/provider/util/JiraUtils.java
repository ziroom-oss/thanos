package com.ziroom.qa.quality.defende.provider.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.ziroom.qa.quality.defende.provider.constant.TestCenterConstants;
import com.ziroom.qa.quality.defende.provider.properties.JiraProperties;
import com.ziroom.qa.quality.defende.provider.vo.TestResultVo;
import com.ziroom.qa.quality.defende.provider.vo.jira.JiraIssue;
import lombok.extern.slf4j.Slf4j;
import net.rcarz.jiraclient.Field;
import net.rcarz.jiraclient.Issue;
import net.rcarz.jiraclient.JiraClient;
import net.rcarz.jiraclient.JiraException;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Slf4j
@Component
public class JiraUtils {

    public static final int DEFAULT_DUE_DAYS = 3;
    private static final String DEFAULT_LABEL = "报告分析";
    private static String bugType = "缺陷";

    private static JiraClient jiraClient;

    private static String validateFlag;

    private static JiraProperties jiraProperties;

    private static Pattern pattern = Pattern.compile("/(.*?)/");

    @Value("${jiraIssue.validateFlag}")
    public void setValidateFlag(String flag) {
        validateFlag = flag;
    }

    @Autowired
    public JiraUtils(JiraClient jiraClient) {
        JiraUtils.jiraClient = jiraClient;
    }

    @Autowired
    public void setJiraProperties(JiraProperties properties) {
        jiraProperties = properties;
    }

    public static Issue getJiraIssueByIssueKey(String issueKey) {
        Issue issue = null;
        try {
            long start = System.currentTimeMillis();
            issue = jiraClient.getIssue(issueKey);
            long end = System.currentTimeMillis();
            log.info("getJiraIssueByIssueKey issueKey === {}", issueKey);
            log.info("getJiraIssueByIssueKey 响应毫秒数1 === {}", end - start);
        } catch (JiraException e) {
            log.error("获取jira信息失败：jiraid==={}", issueKey, e);
        }
        return issue;
    }

    /**
     * 创建业务bug列表
     *
     * @param defect
     * @return
     * @throws JiraException
     */
    public static Issue createBugByDefectBuss(JiraIssue defect) throws JiraException {
        defect = buildDefaultJiraDefectByBuss(defect);
        Issue issue = createIssue(defect);
        return issue;
    }

    /**
     * 创建默认JiraIssue类，填充Jira缺陷默认以及必填字段(业务方创建的bug)
     *
     * @param defect
     * @return JiraIssue
     */
    public static JiraIssue buildDefaultJiraDefectByBuss(JiraIssue defect) {
        defect.setIssuetype(bugType);


        ArrayList<String> labels = new ArrayList<>();
        labels.add(DEFAULT_LABEL);
        defect.setLabels(labels);
        return defect;
    }

    /**
     * 内部创建方法，与调用类隔离
     *
     * @param issue JiraIssue包装类
     * @return net.rcarz.jiraclient.Issue
     * @throws JiraException
     */
    private static Issue createIssue(JiraIssue issue) throws JiraException {
        JSONObject json = (JSONObject) JSON.toJSON(issue);

        Issue.FluentCreate issueForCreating = jiraClient.createIssue(json.getString(Field.PROJECT), json.getString(Field.ISSUE_TYPE));
        for (Map.Entry<String, Object> entry : json.entrySet()) {
            if (entry.getKey().equals(Field.PROJECT) || entry.getKey().equals(Field.ISSUE_TYPE) || entry.getValue() == null) {
                continue;
            }
            issueForCreating.field(entry.getKey(), entry.getValue());
        }
        return issueForCreating.execute();
    }

    /**
     * 获取模块集合
     *
     * @param project
     * @param issueType
     * @return
     */
    public static List<String> getComponentsAllowedValues(String project, String issueType) {
        try {
            List<net.rcarz.jiraclient.Component> list = jiraClient.getComponentsAllowedValues(project, issueType);
            if (CollectionUtils.isEmpty(list)) {
                return null;
            }
            List<String> resList = list.stream().map(net.rcarz.jiraclient.Component::getName).collect(Collectors.toList());
            return resList;
        } catch (JiraException e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * 校验并验证jiraid
     *
     * @param issueKey
     * @return
     */
    public static TestResultVo validateJiraInfo(String issueKey) {
        if (StringUtils.isBlank(issueKey)) {
            return TestResultVo.builder().flag(false).msgRes(TestCenterConstants.RES_MSG_PARAMS_EMPTY).build();
        }
        issueKey = assableJiraId(issueKey);
        try {
            // 测试环境直接通过
            if ("false".equals(validateFlag)) {
                return TestResultVo.builder().flag(true).data(issueKey).build();
            }
            Issue issue = getJiraIssueByIssueKey(issueKey);
            if (Objects.isNull(issue)) {
                return TestResultVo.builder().flag(false).msgRes(TestCenterConstants.RES_MSG_JIRA_NOEXIST).build();
            }
        } catch (Exception e) {
            log.error("validateJiraInfo--jiraIssueService.getJiraIssueByIssueKey() issueKey=={}", issueKey, e);
            return TestResultVo.builder().flag(false).msgRes(TestCenterConstants.RES_MSG_JIRA_ERROR).build();
        }
        return TestResultVo.builder().flag(true).data(issueKey).build();
    }

    /**
     * 过滤jiraid信息
     *
     * @param issueKey
     * @return
     */
    public static String assableJiraId(String issueKey) {
        if (StringUtils.isBlank(issueKey)) {
            return "";
        }
        issueKey = issueKey.replace("https://", "")
                .replace("http://", "")
                .replace("jira.ziroom.com/browse", "")
                .replace("tjira.ziroom.com/browse", "");

        Matcher matcher = pattern.matcher(issueKey);
        while (matcher.find()) {
            issueKey = matcher.group(1);
        }
        return issueKey.replace("/", "");
    }

    /**
     * 获取jira的地址
     *
     * @param issueKey
     * @return
     */
    public static String getJiraUrl(String issueKey) {
        return jiraProperties.getBaseurl() + "browse/" + issueKey;
    }

}
