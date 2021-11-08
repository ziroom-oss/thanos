package com.ziroom.qa.quality.defende.provider.controller;

import com.ziroom.qa.quality.defende.provider.entity.TestConfig;
import com.ziroom.qa.quality.defende.provider.entity.TestConfigOptions;
import com.ziroom.qa.quality.defende.provider.service.TestConfigOptionsService;
import com.ziroom.qa.quality.defende.provider.service.TestConfigService;
import com.ziroom.qa.quality.defende.provider.result.RestResultVo;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Api(value="配置中心-用例属性-选项值",tags={"配置中心-用例属性-选项值"})
@Slf4j
@RequestMapping("/testConfigOptions")
@RestController
public class TestConfigOptionsController {

    @Autowired
    private TestConfigOptionsService testConfigOptionsService;

    @Autowired
    private TestConfigService testConfigService;

    @GetMapping("/deteleTestConfigOptionsById")
    public RestResultVo<TestConfig> deteleTestConfigOptionsById(@RequestParam("id") Long id){
        //1. 查询属性值对象
        TestConfigOptions testConfigOptions = testConfigOptionsService.getById(id);

        //2. 删除此属性值
        testConfigOptionsService.deteleTestConfigOptionsById(id);

        //3. 查询返回值渲染列表
        TestConfig testConfig = testConfigService.queryTestConfigById(testConfigOptions.getTestConfigId());
        return RestResultVo.fromData(testConfig);
    }

}
