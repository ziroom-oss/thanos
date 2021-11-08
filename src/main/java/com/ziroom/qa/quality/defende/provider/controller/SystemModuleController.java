package com.ziroom.qa.quality.defende.provider.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.ziroom.qa.quality.defende.provider.constant.QualityDefendeConstants;
import com.ziroom.qa.quality.defende.provider.entity.SystemModule;
import com.ziroom.qa.quality.defende.provider.service.SystemModuleService;
import com.ziroom.qa.quality.defende.provider.service.UserService;
import com.ziroom.qa.quality.defende.provider.result.RestResultVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 此类已修改完成从HEADER 获取UID
 */
@Api(value = "测试用例所属模块", tags = {"测试用例所属模块"})
@RestController
@RequestMapping("systemModule")
public class SystemModuleController {

    @Autowired
    private SystemModuleService systemModuleService;
    @Autowired
    private UserService userService;

    /**
     * 根据系统域名或模块名称查询
     *
     * @param systemDomainName
     * @param moduleName
     * @return
     */
    @GetMapping(value = "/querySystemModuleByDomainNameAndModuleName", produces = {"application/json;charset=UTF-8"})
    public RestResultVo<List<SystemModule>> querySystemModuleByDomainNameAndModuleName(@RequestParam(name = "systemDomainName", required = false) String systemDomainName, @RequestParam(name = "moduleName", required = false) String moduleName) {
        QueryWrapper<SystemModule> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("system_module_type", QualityDefendeConstants.SYSTEM_MODULE_TYPE_FUNCTION_TESTING);
        if (StringUtils.isNotBlank(systemDomainName)) {
            queryWrapper.like("system_domain_name", systemDomainName);
        }
        if (StringUtils.isNotBlank(moduleName)) {
            queryWrapper.like("module_name", moduleName);
        }
        return RestResultVo.fromData(systemModuleService.list(queryWrapper));
    }


    @GetMapping(value = "/queryOnlySystemModuleByDomainNameAndModuleName", produces = {"application/json;charset=UTF-8"})
    public RestResultVo<SystemModule> queryOnlySystemModuleByDomainNameAndModuleName(@RequestParam(name = "systemDomainName") String systemDomainName, @RequestParam(name = "moduleName") String moduleName) {
        return RestResultVo.fromData(systemModuleService.queryOnlySystemModuleByDomainNameAndModuleName(systemDomainName, moduleName));
    }


    /**
     * 保存所属域名
     *
     * @param systemModule
     * @return
     */
    @PostMapping(value = "/saveSystemModule", produces = {"application/json;charset=UTF-8"})
    public RestResultVo<SystemModule> saveSystemModule(@RequestBody SystemModule systemModule, HttpServletRequest request) {
        String userName = request.getHeader("userName");
        systemModule.setCreateUser(userName);
        systemModule.setUpdateUser(userName);
        return RestResultVo.fromData(systemModuleService.saveSystemModule(systemModule));
    }

    /**
     * 更新系统模块
     *
     * @param systemModule
     * @return
     */

    @PostMapping(value = "/updateSystemModule", produces = {"application/json;charset=UTF-8"})
    public RestResultVo updateSystemModule(@RequestBody SystemModule systemModule, HttpServletRequest request) {
        String userName = request.getHeader("userName");
        systemModule.setUpdateUser(userName);
        systemModule.setUpdateTime(LocalDateTime.now());
        boolean result = systemModuleService.updateById(systemModule);
        return RestResultVo.fromData(systemModule);
    }


    @ApiOperation("用户信息清洗-更新用户信息")
    @GetMapping("/updateSystemModuleUserInfo")
    public RestResultVo updateSystemModuleUserInfo() {
        boolean result = systemModuleService.updateSystemModuleUserInfo();
        return RestResultVo.fromData(result);
    }

}
