package com.ziroom.qa.quality.defende.provider.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ziroom.qa.quality.defende.provider.config.OperateLogAnnotation;
import com.ziroom.qa.quality.defende.provider.constant.OperateLogModuleConstants;
import com.ziroom.qa.quality.defende.provider.constant.OperateLogTypeConstants;
import com.ziroom.qa.quality.defende.provider.entity.TestApplication;
import com.ziroom.qa.quality.defende.provider.service.TestApplicationService;
import com.ziroom.qa.quality.defende.provider.vo.ApplicationModuleTree;
import com.ziroom.qa.quality.defende.provider.vo.Pagination;
import com.ziroom.qa.quality.defende.provider.result.RestResultVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Api(value = "所属应用", tags = {"所属应用"})
@RequestMapping("/applicatioin")
@RestController
@Slf4j
public class TestApplicationController {

    @Autowired
    private TestApplicationService testApplicationService;

    @ApiOperation("所属应用分页查询")
    @PostMapping(value = "/queryTestApplicationByPage", produces = {"application/json;charset=UTF-8"})
    public RestResultVo<Page<TestApplication>> queryTestApplicationByPage(@RequestBody Pagination<TestApplication> pagination) {
        return RestResultVo.fromData(testApplicationService.queryTestApplicationByPage(pagination));
    }

    @OperateLogAnnotation(moduleName = OperateLogModuleConstants.APPLICATION, option = OperateLogTypeConstants.DETAIL)
    @ApiOperation("获取所属应用详情")
    @GetMapping(value = "/queryTestApplicationDetailById", produces = {"application/json;charset=UTF-8"})
    public RestResultVo<TestApplication> queryTestApplicationDetailById(@RequestParam("id") Long id) {
        TestApplication testApplication = testApplicationService.queryTestApplicationDetailById(id);
        return RestResultVo.fromData(testApplication);
    }

    @OperateLogAnnotation(moduleName = OperateLogModuleConstants.APPLICATION, option = OperateLogTypeConstants.SAVEORUPDATE)
    @ApiOperation("所属应用保存或更新")
    @PostMapping("/saveOrUpdateTestApplication")
    public RestResultVo saveOrUpdateTestApplication(@RequestBody TestApplication testApplication, HttpServletRequest request) {
        String userName = request.getHeader("userName");
        return testApplicationService.saveOrUpdateTestApplication(testApplication, userName);
    }

    @ApiOperation("验证所属用例名称为")
    @GetMapping("/validateTestApplicationName")
    public RestResultVo validateTestApplicationName(@RequestParam("applicationName") String applicationName, @RequestParam(name = "id", required = false) Long id) {
        boolean result = testApplicationService.validateTestApplicationName(applicationName, id);
        if (result) {
            return RestResultVo.fromSuccess(null);
        } else {
            return RestResultVo.fromErrorMessage("所属应用名称已存在");
        }

    }

    @ApiOperation("查询所有应用信息")
    @GetMapping("/findAllApplication")
    public RestResultVo findAllApplication(@RequestHeader String userName) {
        RestResultVo restResultVo;
        try {
            List<TestApplication> list = testApplicationService.list();
            restResultVo = RestResultVo.fromData(list);
        } catch (Exception e) {
            log.error("/applicatioin/findAllApplication queryVo=={} 查询所有应用信息!", userName, e);
            restResultVo = RestResultVo.fromErrorMessage(e.getMessage());
        }
        return restResultVo;
    }

    @ApiOperation("查询所有应用信息by组织结构")
    @GetMapping("/findAllApplicationByEhrTreePath")
    public RestResultVo findAllApplicationByEhrTreePath(@RequestHeader String userName,
                                                        @RequestParam(value = "ehrTreePath", required = false) String ehrTreePath) {
        RestResultVo restResultVo;
        try {
            List<TestApplication> list = testApplicationService.listByEhrTreePath(ehrTreePath);
            restResultVo = RestResultVo.fromData(list);
        } catch (Exception e) {
            log.error("/applicatioin/findAllApplicationByEhrTreePath queryVo=={} 查询所有应用信息by组织结构!", userName, e);
            restResultVo = RestResultVo.fromErrorMessage(e.getMessage());
        }
        return restResultVo;
    }


    @ApiOperation("根据ID删除应用")
    @GetMapping("/deleteTestApplicationById")
    public RestResultVo<String> deleteTestApplicationById(Long id) {
        TestApplication application = testApplicationService.getById(id);
        String message = testApplicationService.deleteTestApplicationById(id);
        if (message.contains("删除应用成功")) {
            return RestResultVo.fromData("[" + application.getApplicationName() + "]应用删除成功！");
        } else {
            return RestResultVo.fromErrorMessage("[" + application.getApplicationName() + "]下尚有关联用例删除失败," + message);
        }
    }

    @ApiOperation("查询所有应用下包含的模块信息（包含应用信息树状结构）")
    @GetMapping("/queryApplicationAndModule")
    public RestResultVo queryApplicationAndModule(@RequestHeader String userName,
                                                  @RequestParam(value = "treePath", required = false) String treePath) {

        RestResultVo restResultVo;
        try {
            List<ApplicationModuleTree> list = testApplicationService.queryApplicationAndModule(userName, treePath);
            restResultVo = RestResultVo.fromData(list);
        } catch (Exception e) {
            log.error("/applicatioin/queryApplicationAndModule queryVo=={} 查询所有应用下包含的模块信息（包含应用信息树状结构）失败!", userName, e);
            restResultVo = RestResultVo.fromErrorMessage(e.getMessage());
        }
        return restResultVo;
    }

    @ApiOperation("查询登陆人已经收藏的所有应用下包含的模块信息（包含应用信息树状结构）")
    @GetMapping("/queryMyApplicationAndModule")
    public RestResultVo queryMyApplicationAndModule(@RequestHeader String userName,
                                                    @RequestParam(value = "treePath", required = false) String treePath) {

        RestResultVo restResultVo;
        try {
            List<ApplicationModuleTree> list = testApplicationService.queryMyApplicationAndModule(userName, treePath);
            restResultVo = RestResultVo.fromData(list);
        } catch (Exception e) {
            log.error("/applicatioin/queryMyApplicationAndModule queryVo=={} 查询登陆人已经收藏的所有应用下包含的模块信息（包含应用信息树状结构）失败!", userName, e);
            restResultVo = RestResultVo.fromErrorMessage(e.getMessage());
        }
        return restResultVo;
    }

}
