package com.ziroom.qa.quality.defende.provider.constant.enums;

import java.util.HashMap;
import java.util.Map;

public enum JiraProjectTestLeaderEnum {
//    NEW-数据中台中心需求池
//    NEW-企业信息平台中心需求池
//    NEW-用户体验增长中心需求池
    SJZT("SJZT", "wangqf1"),
    QYXX("QYXX", "zouzh"),
    YHZZ("YHZZ","zhangn11");

    private String projectKey;
    private String testUser;

    JiraProjectTestLeaderEnum(String projectKey, String testUser) {
        this.projectKey = projectKey;
        this.testUser = testUser;
    }

    private static final Map<String, JiraProjectTestLeaderEnum> MAP = new HashMap();

    static {
        for (JiraProjectTestLeaderEnum item : JiraProjectTestLeaderEnum.values()) {
            MAP.put(item.projectKey, item);
        }
    }

    public static String getTestUserByProjectKey(String key) {
        return MAP.get(key) == null ? "" : MAP.get(key).getTestUser();
    }

    public String getProjectKey() {
        return projectKey;
    }

    public void setProjectKey(String projectKey) {
        this.projectKey = projectKey;
    }

    public String getTestUser() {
        return testUser;
    }

    public void setTestUser(String testUser) {
        this.testUser = testUser;
    }
}

