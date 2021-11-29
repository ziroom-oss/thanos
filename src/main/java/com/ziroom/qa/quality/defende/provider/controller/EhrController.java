package com.ziroom.qa.quality.defende.provider.controller;

import com.alibaba.fastjson.JSONObject;
import com.ziroom.qa.quality.defende.provider.result.RestResultVo;
import com.ziroom.qa.quality.defende.provider.vo.user.EhrOrgDto;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Api(value = "EHR组织结构信息接口", tags = {"EHR组织结构信息接口"})
@Slf4j
@RestController
@RequestMapping("/ehr")
public class EhrController {

    /**
     * 组织结构的格式，如果要接入自己的组织结构可以自行添加（这个只是一个样例）
     *
     * @return
     */
    @GetMapping("/getEhrInfo")
    public RestResultVo<Object> getEhrInfo() {
        String ehrOrgStr = "{\n" +
                "    \"deptName\":\"总部\",\n" +
                "    \"last\":false,\n" +
                "    \"user\":false,\n" +
                "    \"deptCode\":\"100001\",\n" +
                "    \"treePath\":\"100001\",\n" +
                "    \"childDept\":[\n" +
                "        {\n" +
                "            \"deptName\":\"事业部\",\n" +
                "            \"parentName\":\"总部\",\n" +
                "            \"last\":false,\n" +
                "            \"user\":false,\n" +
                "            \"parentCode\":\"100001\",\n" +
                "            \"deptCode\":\"100011\",\n" +
                "            \"treePath\":\"100001-100011\",\n" +
                "            \"childDept\":[\n" +
                "                {\n" +
                "                    \"deptName\":\"xx事业群\",\n" +
                "                    \"parentName\":\"事业部\",\n" +
                "                    \"last\":false,\n" +
                "                    \"parentCode\":\"100011\",\n" +
                "                    \"user\":false,\n" +
                "                    \"deptCode\":\"100111\",\n" +
                "                    \"treePath\":\"100001-100011-100111\",\n" +
                "                    \"childDept\":[\n" +
                "                        {\n" +
                "                            \"deptName\":\"xx运管中心\",\n" +
                "                            \"parentName\":\"xx事业群\",\n" +
                "                            \"last\":false,\n" +
                "                            \"parentCode\":\"100111\",\n" +
                "                            \"user\":false,\n" +
                "                            \"deptCode\":\"101111\",\n" +
                "                            \"treePath\":\"100001-100011-100111-101111\",\n" +
                "                            \"childDept\":[\n" +
                "                                {\n" +
                "                                    \"deptName\":\"xx管理部\",\n" +
                "                                    \"parentName\":\"xx运管中心\",\n" +
                "                                    \"last\":true,\n" +
                "                                    \"parentCode\":\"101111\",\n" +
                "                                    \"user\":false,\n" +
                "                                    \"deptCode\":\"111111\",\n" +
                "                                    \"treePath\":\"100001-100011-100111-101111-111111\",\n" +
                "                                    \"childDept\":[\n" +
                "\n" +
                "                                    ]\n" +
                "                                }\n" +
                "                            ]\n" +
                "                        }\n" +
                "                    ]\n" +
                "                }\n" +
                "            ]\n" +
                "        }\n" +
                "    ]\n" +
                "}";
        return RestResultVo.fromData(JSONObject.parseObject(ehrOrgStr, EhrOrgDto.class));
    }

}
