package com.ziroom.qa.quality.defende.provider.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ziroom.qa.quality.defende.provider.entity.TestConfigSetting;
import com.ziroom.qa.quality.defende.provider.mapper.TestConfigSettingMapper;
import com.ziroom.qa.quality.defende.provider.service.TestConfigSettingService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class TestConfigSettingServiceImpl extends ServiceImpl<TestConfigSettingMapper, TestConfigSetting> implements TestConfigSettingService {
    @Override
    public Page<TestConfigSetting> queryTestConfigSettingByPage(Page<TestConfigSetting> page, TestConfigSetting testConfigSetting) {
        QueryWrapper<TestConfigSetting> testConfigSettingQueryWrapper = new QueryWrapper<>();
        testConfigSettingQueryWrapper
                .like(StringUtils.isNotBlank(testConfigSetting.getName()),"name",testConfigSetting.getName());
        return this.page(page,testConfigSettingQueryWrapper);
    }
}
