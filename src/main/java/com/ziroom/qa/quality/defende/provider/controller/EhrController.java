package com.ziroom.qa.quality.defende.provider.controller;

import com.ziroom.qa.quality.defende.provider.result.RestResultVo;
import com.ziroom.qa.quality.defende.provider.util.RedisUtil;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@Api(value = "EHR组织结构信息接口", tags = {"EHR组织结构信息接口"})
@Slf4j
@RestController
@RequestMapping("/ehr")
public class EhrController {

    @Resource
    private RedisUtil redisUtil;
    @Value("${ziroom.ehr.rediskey:bi-ehrDeptTree-Qa}")
    private String ehrTreeKey;

    /**
     * 此接口信息和矩阵平台同步，只要矩阵平台更新即可：
     * http://api-qabi-web.kp.ziroom.com/ehr/updateEhrDeptToRedis
     *
     * @return
     */
    @GetMapping("/getEhrInfo")
    public RestResultVo<Object> getEhrInfo() {
        return RestResultVo.fromData(redisUtil.get(ehrTreeKey));
    }

}
