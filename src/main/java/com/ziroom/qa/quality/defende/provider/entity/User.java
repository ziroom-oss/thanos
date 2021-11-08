package com.ziroom.qa.quality.defende.provider.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class User {
    /**
     * 主键自增
     */
    private Long id;

    /**
     * 员工编号
     */
    private String uid;

    /**
     * OA账号，邮箱前缀
     */
    private String userName;

    /**
     * 中文名称
     */
    private String nickName;

    /**
     * 组织结构全路径
     */
    private String treePath;

    /**
     * 用户所在组
     */
    private String ehrGroup;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

    /**
     * 角色
     */
    private String role;

    /**
     * 人员类型
     * 外包人员：2正式；3外包
     */
    private int userType;

    /**
     * 密码 md5 加密
     */
    private String password;

}
