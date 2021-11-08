package com.ziroom.qa.quality.defende.provider.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ziroom.qa.quality.defende.provider.entity.TestConfigSetting;

public interface TestConfigSettingService extends IService<TestConfigSetting> {
    Page<TestConfigSetting> queryTestConfigSettingByPage(Page<TestConfigSetting> page, TestConfigSetting testConfigSetting);
}
