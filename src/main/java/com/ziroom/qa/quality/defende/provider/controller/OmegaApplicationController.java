package com.ziroom.qa.quality.defende.provider.controller;

import com.ziroom.qa.quality.defende.provider.result.RestResultVo;
import com.ziroom.qa.quality.defende.provider.vo.OmegaApplicationEnv;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Api(value = "测试用例所属应用", tags = {"测试用例所属应用"})
@Slf4j
@RestController
@RequestMapping("/ci")
public class OmegaApplicationController {

    @ApiOperation("查询Omega线上应用域名")
    @GetMapping("/queryApplicationEnvByLevelAndDomain")
    public RestResultVo<List<OmegaApplicationEnv>> queryApplicationEnvByLevelAndDomain(@RequestParam(name = "belongToSystem", required = false) String belongToSystem) {
        return null;
    }
}
