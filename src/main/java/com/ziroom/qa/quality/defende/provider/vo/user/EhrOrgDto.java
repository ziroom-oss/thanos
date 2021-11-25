package com.ziroom.qa.quality.defende.provider.vo.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EhrOrgDto {
    /**
     * 主键
     */
    private Integer id;
    /**
     * 部门编码
     */
    private String deptCode;
    /**
     * 部门名称
     */
    private String deptName;

    /**
     * 是否是最后一层节点，默认不是。
     */
    private boolean last = false;

    /**
     * 业务线编码
     */
    private String treePath;


    /**
     * ehr编码
     */
    private String ehrCode;

    /**
     * 父编码
     */
    private String parentCode;

    /**
     * 父部门名称
     */
    private String parentName;

    /**
     * 子部门的集合
     */
    private List<EhrOrgDto> childDept;

    /**
     * 001:普通员工 002-劳务派遣 003-实习生A 004-实习生B 005-临时工
     */
    private String userType;

    private boolean isUser = false;

    private String empCode;

    private String jobName;

}
