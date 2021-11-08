package com.ziroom.qa.quality.defende.provider.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ziroom.qa.quality.defende.provider.entity.TestConfig;

import java.util.List;

public interface TestConfigService extends IService<TestConfig> {

    Boolean saveOrUpdateTestConfig(TestConfig testConfig, String userName);

    void batchSaveOrUpdateTestConfig(List<TestConfig> testConfigList, String userName);

    Page<TestConfig> queryTestConfigByPage(Page<TestConfig> page, TestConfig testConfig);

    TestConfig queryTestConfigById(Long id);

    Boolean deleteTestConfigById(Long id);
}
