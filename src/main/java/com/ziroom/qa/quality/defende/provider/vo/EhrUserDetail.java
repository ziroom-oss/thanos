package com.ziroom.qa.quality.defende.provider.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * {
 * "graduateSchool":"巢湖学院",
 * "empType":"001",
 * "jobIndicator":"P",
 * "cityCode":"11",
 * "deptManagerEmpId":"20023831",
 * "superEmpCode":"60009209",
 * "jobCode":"100039",
 * "deptCodeNew":"104642",
 * "effdt":"20210108",
 * "highestEducation":"本科（统招）",
 * "orgLevel":"4",
 * "levelCode":"33",
 * "cityCodeNew":"110000",
 * "jobCodeNew":"100317",
 * "contrastJobCode":"100308",
 * "jobSeries":"FUNC",
 * "adCode":"hanf6",
 * "centerCode":"100363",
 * "setId":"101",
 * "hlTreepath":"100001,101877,100363,104642,104651",
 * "email":"hanf6@ziroom.com",
 * "group":"维修研发组",
 * "groupCode":"104651",
 * "centerManagerEmpId":"60007349",
 * "companyCode":"104",
 * "center":"自如家服平台",
 * "sex":"M",
 * "groupBusType":"",
 * "photo":"http://ehrstatic.ziroom.com/60026925.jpg",
 * "levelName":"T4",
 * "dept":"业务研发部",
 * "deptType":"",
 * "centerType":"",
 * "centerCodeNew":"100363",
 * "descr":"Java工程师",
 * "emplid":"60026925",
 * "graduateDate":"2017-07-03",
 * "phone":"15001022106",
 * "lastModifyTime":"20210206164640",
 * "name":"韩飞",
 * "postCode":"10015824",
 * "gradeCode":"06",
 * "groupCodeNew":"104651",
 * "userType":"ZR",
 * "deptCode":"104642",
 * "treePath":"100001,101877,100363,104642,104651"
 * }
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EhrUserDetail {
    private String jobIndicator;
    private String empType;
    private String jobCode;
    private String highestEducation;
    private String effdt;
    private String jobSeries;
    private String adCode;
    private String setId;
    private String group;
    private String groupCode;
    private String companyCode;
    private String centerManagerEmpId;
    private String groupBusType;
    private String emplid;
    private String graduateDate;
    private String descr;
    private String centerCodeNew;
    private String entryTime;
    private String phone;
    private String superPostCode;
    private String name;
    private String postCode;
    private String userType;
    private String gradeCode;
    private String groupCodeNew;
    private String emplRcd;
    private String deptCode;
    private String graduateSchool;
    private String groupType;
    private String cityCode;
    private String deptManagerEmpId;
    private String superEmpCode;
    private String orgLevel;
    private String deptCodeNew;
    private String levelCode;
    private String cityCodeNew;
    private String jobCodeNew;
    private String contrastJobCode;
    private String centerCode;
    private String hlTreepath;
    private String email;
    private String sex;
    private String center;
    private String photo;
    private String levelName;
    private String dept;
    private String deptType;
    private String centerType;
    private String lastModifyTime;
    private String treePath;

}
