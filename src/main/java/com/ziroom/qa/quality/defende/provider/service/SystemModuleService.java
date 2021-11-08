package com.ziroom.qa.quality.defende.provider.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ziroom.qa.quality.defende.provider.entity.SystemModule;

public interface SystemModuleService extends IService<SystemModule> {

    SystemModule saveSystemModule(String systemDomainName, String moduleName);

    SystemModule saveSystemModule(SystemModule systemModule);

    SystemModule queryOnlySystemModuleByDomainNameAndModuleName(String systemDomainName, String moduleName);

    boolean updateSystemModuleUserInfo();
}
