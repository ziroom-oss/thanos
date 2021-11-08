package com.ziroom.qa.quality.defende.provider.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ziroom.qa.quality.defende.provider.constant.QualityDefendeConstants;
import com.ziroom.qa.quality.defende.provider.entity.SystemModule;
import com.ziroom.qa.quality.defende.provider.entity.User;
import com.ziroom.qa.quality.defende.provider.mapper.SystemModuleMapper;
import com.ziroom.qa.quality.defende.provider.service.SystemModuleService;
import com.ziroom.qa.quality.defende.provider.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
public class SystemModuleServiceImpl extends ServiceImpl<SystemModuleMapper, SystemModule> implements SystemModuleService {

    @Autowired
    private SystemModuleMapper systemModuleMapper;
    @Autowired
    private UserService userService;


    @Override
    public SystemModule saveSystemModule(String systemDomainName, String moduleName) {
        SystemModule systemModule = new SystemModule();
        systemModule.setSystemDomainName(systemDomainName);
        systemModule.setModuleName(moduleName);
        return saveSystemModule(systemModule);
    }

    @Override
    public SystemModule saveSystemModule(SystemModule systemModule) {
        //1. 根据系统域名和所属模块查询是否存在如果存在则不插入
        SystemModule existSystemModule = queryOnlySystemModuleByDomainNameAndModuleName(systemModule.getSystemDomainName(), systemModule.getModuleName());
        if(null == existSystemModule){
            systemModule.setSystemModuleType(QualityDefendeConstants.SYSTEM_MODULE_TYPE_FUNCTION_TESTING);
            systemModule.setUpdateTime(LocalDateTime.now());
            systemModule.setCreateTime(LocalDateTime.now());
            systemModuleMapper.insert(systemModule);
            return systemModuleMapper.selectById(systemModule.getId());
        }else {
            return existSystemModule;
        }
    }

    /**
     * 根据系统域名和模块名称查询
     * @param systemDomainName
     * @param moduleName
     * @return
     */
    @Override
    public SystemModule queryOnlySystemModuleByDomainNameAndModuleName(String systemDomainName, String moduleName) {
        QueryWrapper<SystemModule> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("system_module_type", QualityDefendeConstants.SYSTEM_MODULE_TYPE_FUNCTION_TESTING);
        queryWrapper.eq("system_domain_name",systemDomainName);
        queryWrapper.eq("module_name",moduleName);
        return systemModuleMapper.selectOne(queryWrapper);
    }

    /**
     * 数据清洗-更新用户信息
     * @return
     */
    @Override
    public boolean updateSystemModuleUserInfo() {
        List<SystemModule> systemModuleList = this.list();
        systemModuleList.stream().forEach(systemModule -> {
            if(null != systemModule){
                String createUserUid = systemModule.getCreateUser();
                if(StringUtils.isNotBlank(createUserUid)){
                    User createUser = userService.getUserInfoByUid(createUserUid);
                    systemModule.setCreateUser(createUser.getUserName());
                }
                String updateUserUid = systemModule.getUpdateUser();
                if(StringUtils.isNotBlank(updateUserUid)){
                    User updateUser = userService.getUserInfoByUid(updateUserUid);
                    systemModule.setUpdateUser(updateUser.getUserName());
                }
            }
        });
        boolean result = this.updateBatchById(systemModuleList);
        return result;
    }
}
