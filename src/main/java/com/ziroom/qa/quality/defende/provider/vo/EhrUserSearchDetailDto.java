package com.ziroom.qa.quality.defende.provider.vo;

import lombok.Data;

/**
 * 和矩阵平台同步
 */
@Data
public class EhrUserSearchDetailDto {
    /**
     * 部门编号
     */
    private String deptName;
    /**
     * 部门名称
     */
    private String deptCode;
    /**
     * 员工名称
     */
    private String userName;
    /**
     * 员工编号
     */
    private String empCode;
    /**
     * 员工邮箱前缀
     */
    private String userCode;
    /**
     * 员工类型
     */
    private String userType;
    /**
     * 员工手机号
     */
    private String phone;
}
