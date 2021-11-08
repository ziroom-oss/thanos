package com.ziroom.qa.quality.defende.provider.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ziroom.qa.quality.defende.provider.entity.TestConfigSetting;
import com.ziroom.qa.quality.defende.provider.service.TestConfigSettingService;
import com.ziroom.qa.quality.defende.provider.vo.Pagination;
import com.ziroom.qa.quality.defende.provider.result.RestResultVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Api(value="用例属性配置集",tags={"用例属性配置集"})
@Slf4j
@RequestMapping("/testConfigSetting")
@RestController
public class TestConfigSettingController {

    @Autowired
    private TestConfigSettingService testConfigSettingService;


    @ApiOperation("用例属性配置集分页查询")
    @PostMapping(value = "/queryTestConfigSettingByPage",produces = { "application/json;charset=UTF-8" })
    public RestResultVo<Page<TestConfigSetting>> queryTestConfigSettingByPage(@RequestBody Pagination<TestConfigSetting> pagination){
        TestConfigSetting testConfigSetting = pagination.getSearchObj();
        if(null == testConfigSetting){
            testConfigSetting = new TestConfigSetting();
        }
        Page<TestConfigSetting> TestConfigSettingResult = testConfigSettingService.queryTestConfigSettingByPage(pagination.getPage(),testConfigSetting);
        return RestResultVo.fromData(TestConfigSettingResult);
    }
}
