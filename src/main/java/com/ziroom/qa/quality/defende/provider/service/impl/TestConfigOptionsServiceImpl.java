package com.ziroom.qa.quality.defende.provider.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ziroom.qa.quality.defende.provider.entity.TestConfigOptions;
import com.ziroom.qa.quality.defende.provider.mapper.TestConfigOptionsMapper;
import com.ziroom.qa.quality.defende.provider.service.TestConfigOptionsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Slf4j
@Service
public class TestConfigOptionsServiceImpl extends ServiceImpl<TestConfigOptionsMapper,TestConfigOptions> implements TestConfigOptionsService {

    @Override
    public List<TestConfigOptions> queryTestConfigOptionsByConfigId(Long testConfigId) {
        QueryWrapper<TestConfigOptions> optionQueryWrapper = new QueryWrapper<>();
        optionQueryWrapper.eq(Objects.nonNull(testConfigId),"test_config_id",testConfigId);
        return this.list(optionQueryWrapper);
    }

    @Override
    public Boolean deteleTestConfigOptionsById(Long id) {
        return this.removeById(id);
    }
}
