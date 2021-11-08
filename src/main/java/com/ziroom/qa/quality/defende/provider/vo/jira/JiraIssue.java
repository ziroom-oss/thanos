package com.ziroom.qa.quality.defende.provider.vo.jira;


import com.alibaba.fastjson.JSON;
import lombok.Data;
import net.rcarz.jiraclient.WorkLog;

import java.util.List;

/**
 * @Author: yinm5
 * @Description: Jira Issue包装类
 * @Date: 17:05 2019/2/20
 */
@Data
public class JiraIssue {

    /**
     * Issue标题
     */
    private String summary;

    /**
     * Issue ID号
     */
    private String key;
    /**
     * issue经办人，邮箱前缀
     */
    private String assignee;

    /**
     * issue所属模块
     */
    private List<String> components;

    /**
     * issue描述
     */
    private String description;

    /**
     * issue到期时间
     */
    private String duedate;

    /**
     * issue解决版本
     */
    private List<String> fixVersions;

    /**
     * issue类型
     */
    private String issuetype;

    /**
     * issue标签列表
     */
    private List<String> labels;

    /**
     * issue优先级
     */
    private String priority;

    /**
     * issue项目
     */
    private String project;

    /**
     * issue报告人
     */
    private String reporter;

    /**
     * 缺陷创建人
     */
    private String bugCreater;

    /**
     * Bug责任部门
     */
    private List<String> customfield_10212;

    /**
     * Bug所属中心
     */
    private List<String> customfield_10222;


    /**
     * 缺陷类型
     */
    private List<String> customfield_10113;

    /**
     * 需求发起部门
     */
    private List<String> customfield_10220;

    /**
     * 缺陷所属端
     */
    private List<String> customfield_10508;

    /**
     * 缺陷创建人
     */
    private String customfield_11292;

    /**
     * 为了获取存在的有效的Jira的字段，与Jira字段无关。
     */
    private List<String> storedIssueKeys;

    private List<WorkLog> workLogs = null;

    @Override
    public String toString() {
        return JSON.toJSONString(this);
    }

    public void appendLabel(String label) {
        this.labels.add(label);
    }
}
