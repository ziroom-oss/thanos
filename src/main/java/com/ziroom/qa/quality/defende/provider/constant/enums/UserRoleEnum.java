package com.ziroom.qa.quality.defende.provider.constant.enums;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public enum UserRoleEnum {

    /**
     * 测试执行任务类型
     */
    ROLE_USER("user", "普通用户"),
    ROLE_ADMIN("admin", "管理员"),
    ROLE_SUPER_ADMIN("superAdmin", "超级管理员");


    private String name;
    private String chineseName;

    private static final Map<String, UserRoleEnum> MAP = new HashMap<>();

    static {
        for (UserRoleEnum item : UserRoleEnum.values()) {
            MAP.put(item.name, item);
        }
    }

    UserRoleEnum(String name, String chineseName) {
        this.name = name;
        this.chineseName = chineseName;

    }

    public String getName() {
        return name;
    }

    public static List<String> getUserRoleList() {
        return new ArrayList<>(MAP.keySet());
    }

    public String getChineseName() {
        return chineseName;
    }

}
