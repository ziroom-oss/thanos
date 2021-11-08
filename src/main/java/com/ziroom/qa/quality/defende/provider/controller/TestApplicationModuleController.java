package com.ziroom.qa.quality.defende.provider.controller;

import com.alibaba.fastjson.JSON;
import com.ziroom.qa.quality.defende.provider.entity.TestApplicationModule;
import com.ziroom.qa.quality.defende.provider.service.TestApplicationModuleService;
import com.ziroom.qa.quality.defende.provider.vo.ApplicationModuleTree;
import com.ziroom.qa.quality.defende.provider.result.RestResultVo;
import com.ziroom.qa.quality.defende.provider.vo.ShiftModuleVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;


@Slf4j
@Api(value = "所属模块", tags = {"所属模块"})
@RequestMapping("/applicationModule")
@RestController
public class TestApplicationModuleController {

    @Autowired
    private TestApplicationModuleService testApplicationModuleService;


    @ApiOperation("追加根节点模块")
    @PostMapping("/appendRootApplicationModule")
    public RestResultVo<Long> appendRootApplicationModule(@RequestHeader String userName, @RequestBody TestApplicationModule testApplicationModule) {
        testApplicationModule.setLevel(1);
        testApplicationModule = testApplicationModuleService.saveTestApplicationModule(testApplicationModule, userName);
        return RestResultVo.fromData(testApplicationModule.getApplicationId());
    }

    @ApiOperation("追加孩子节点模块")
    @PostMapping("/appendChildApplicationModule")
    public RestResultVo<ApplicationModuleTree> appendChildApplicationModule(@RequestHeader String userName, @RequestBody TestApplicationModule testApplicationModule) {
        RestResultVo restResultVo;
        try {
            ApplicationModuleTree tree = testApplicationModuleService.appendChildApplicationModule(testApplicationModule, userName);
            restResultVo = RestResultVo.fromData(tree);
        } catch (Exception e) {
            log.error("/applicationModule/appendChildApplicationModule userName=={} 追加孩子节点模块失败!", userName, e);
            restResultVo = RestResultVo.fromErrorMessage(e.getMessage());
        }
        return restResultVo;
    }

    @ApiOperation("修改所属模块")
    @PostMapping("/updateApplicationModuleNameById")
    public RestResultVo updateApplicationModuleNameById(@RequestHeader String userName, @RequestBody TestApplicationModule testApplicationModule) {
        boolean result = testApplicationModuleService.updateApplicationModuleNameById(testApplicationModule, userName);
        if (result) {
            return RestResultVo.fromSuccess("所属模块修改成功");
        } else {
            return RestResultVo.fromErrorMessage("所属模块修改失败");
        }
    }

    @ApiOperation("删除所属模块")
    @GetMapping("/deleteTestApplicationModuleById")
    public RestResultVo deleteTestApplicationModuleById(@RequestParam("id") Long id) {
        return testApplicationModuleService.deleteTestApplicationModuleById(id);
    }

    @ApiOperation("获取应用关联的模块树")
    @GetMapping("/queryApplicationModuleTreeByApplicationId")
    public RestResultVo<List<ApplicationModuleTree>> queryApplicationModuleTreeByApplicationId(@RequestParam("applicationId") Long applicationId) {
        return RestResultVo.fromData(testApplicationModuleService.queryApplicationModuleTreeByApplicationId(applicationId));
    }

    @ApiOperation("模块转移")
    @PostMapping("/shiftModule")
    public RestResultVo shiftModule(@RequestHeader String userName,
                                    @RequestBody ShiftModuleVo shiftModuleVo) {
        testApplicationModuleService.shiftModule(userName,shiftModuleVo);
        return RestResultVo.fromSuccess("转移成功");
    }

}
