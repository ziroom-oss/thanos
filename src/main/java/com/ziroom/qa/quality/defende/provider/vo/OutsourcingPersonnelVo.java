package com.ziroom.qa.quality.defende.provider.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OutsourcingPersonnelVo {

    /**
     * id
     */
    private int id;

    /**
     * 员工姓名
     */
    private String employeeName;

    /**
     * 手机号码
     */
    private String mobile;

    /**
     * 入职时间
     */
    private String hireDate;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 邮箱前缀
     */
    private String emailPre;

    /**
     * 挂靠部门/业务线
     */
    private String deptName;

    /**
     * 结构路径
     */
    private String EhrTreePath;

    /**
     * 所属部门编号
     */
    private String deptCode;

    /**
     * 外包公司名称
     */
    private String outsourcingCompany;

    /**
     * 自如负责人
     */
    private String leaderName;

    /**
     * 自如负责人手机号
     */
    private String leaderMobile;

    /**
     * 出生年月
     */
    private String birthday;

    /**
     * 删除标记: 0删除|1未删除
     */
    private Integer isDel;

    /**
     * 创建时间
     */
    private String createTime;

    /**
     * 创建人邮箱前缀
     */
    private String createUserEmailPre;

    /**
     * 创建人姓名
     */
    private String createUserName;

    /**
     * 更新时间
     */
    private String updateTime;

    /**
     * 更新人邮箱前缀
     */
    private String updateUserEmailPre;

    /**
     * 更新者姓名
     */
    private String updateUserName;


}
