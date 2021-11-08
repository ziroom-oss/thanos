package com.ziroom.qa.quality.defende.provider.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ziroom.qa.quality.defende.provider.entity.TestConfigOptions;

import java.util.List;

public interface TestConfigOptionsService extends IService<TestConfigOptions> {
    List<TestConfigOptions> queryTestConfigOptionsByConfigId(Long id);

    Boolean deteleTestConfigOptionsById(Long id);
}
