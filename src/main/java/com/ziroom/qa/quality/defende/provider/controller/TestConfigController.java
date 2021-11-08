package com.ziroom.qa.quality.defende.provider.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ziroom.qa.quality.defende.provider.entity.TestConfig;
import com.ziroom.qa.quality.defende.provider.service.TestConfigService;
import com.ziroom.qa.quality.defende.provider.vo.Pagination;
import com.ziroom.qa.quality.defende.provider.result.RestResultVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Api(value="配置中心-用例属性",tags={"配置中心-用例属性"})
@Slf4j
@RequestMapping("/testConfig")
@RestController
public class TestConfigController {

    @Autowired
    private TestConfigService testConfigService;

    @ApiOperation("保存配置")
    @PostMapping("/saveOrUpdateTestConfig")
    public RestResultVo<Boolean> saveOrUpdateTestConfig(@RequestBody TestConfig testConfig, HttpServletRequest request){
        String userName = request.getHeader("userName");
        Boolean result = testConfigService.saveOrUpdateTestConfig(testConfig, userName);
        return RestResultVo.fromData(result);
    }

    @ApiOperation("查询")
    @GetMapping("/queryTestConfig")
    public RestResultVo<List<TestConfig>> queryTestConfig(){
        List<TestConfig> testConfigList = testConfigService.list();
        return RestResultVo.fromData(testConfigList);
    }

    @ApiOperation("用例属性分页查询")
    @PostMapping(value = "/queryTestConfigByPage",produces = { "application/json;charset=UTF-8" })
    public RestResultVo<Page<TestConfig>> queryTestConfigByPage(@RequestBody Pagination<TestConfig> pagination){
        TestConfig testConfig = pagination.getSearchObj();
        if(null == testConfig){
            testConfig = new TestConfig();
        }
        Page<TestConfig> result = testConfigService.queryTestConfigByPage(pagination.getPage(),testConfig);
        return RestResultVo.fromData(result);
    }

    @ApiOperation("根据ID查询")
    @GetMapping("/queryTestConfigById")
    public RestResultVo<TestConfig> queryTestConfigById(@RequestParam("id") Long id){
        TestConfig testConfig = testConfigService.queryTestConfigById(id);
        return RestResultVo.fromData(testConfig);
    }

    @ApiOperation("根据ID删除")
    @GetMapping("/deleteTestConfigById")
    public RestResultVo<Boolean> deleteTestConfigById (@RequestParam("id") Long id){
        Boolean result = testConfigService.deleteTestConfigById(id);
        return RestResultVo.fromData(result);
    }

}
