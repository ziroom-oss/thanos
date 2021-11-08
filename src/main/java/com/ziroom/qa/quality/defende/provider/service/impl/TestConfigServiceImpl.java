package com.ziroom.qa.quality.defende.provider.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.collect.Lists;
import com.ziroom.qa.quality.defende.provider.constant.enums.FieldTypeEnum;
import com.ziroom.qa.quality.defende.provider.entity.TestConfig;
import com.ziroom.qa.quality.defende.provider.entity.TestConfigOptions;
import com.ziroom.qa.quality.defende.provider.mapper.TestConfigMapper;
import com.ziroom.qa.quality.defende.provider.service.TestConfigOptionsService;
import com.ziroom.qa.quality.defende.provider.service.TestConfigService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
public class TestConfigServiceImpl extends ServiceImpl<TestConfigMapper, TestConfig> implements TestConfigService {

    @Autowired
    private TestConfigOptionsService testConfigOptionsService;

    public Boolean initTestCofig(TestConfig testConfig,String userName){
        if (testConfig == null){
            testConfig = new TestConfig();
        }
        if(Objects.isNull(testConfig.getId()) || testConfig.getId() == 0){
            testConfig.setPool("testcase");
            testConfig.setCreateUser(userName);
            testConfig.setCreateTime(LocalDateTime.now());
        }
        testConfig.setUpdateUser(userName);
        testConfig.setUpdateTime(LocalDateTime.now());
        testConfig.setFieldTypeLable(FieldTypeEnum.getFieldTypeLableByKey(testConfig.getFieldType()));
        return queryTestConfigByPoolAndName(testConfig);
    }

    @Override
    public Boolean saveOrUpdateTestConfig(TestConfig testConfig, String userName) {
        boolean result = initTestCofig(testConfig, userName);
        if(result){
            // 保持逻辑
            List<TestConfigOptions> testConfigOptionsList = testConfig.getTestConfigOptionsList();
            result = this.save(testConfig);
            if(result){
                testConfigOptionsList.stream().forEach(testConfigOptions -> {
                    formatTestConfigOptions(testConfig.getId(),testConfigOptions,userName);
                });
                return testConfigOptionsService.saveBatch(testConfigOptionsList);
            }else {
                return false;
            }
        }else {
            // 更新逻辑
            if(Objects.nonNull(testConfig.getId())){
                result = this.updateById(testConfig);
                if(result){
                    List<TestConfigOptions> testConfigOptionsList = testConfig.getTestConfigOptionsList();
                    testConfigOptionsList.stream().forEach(testConfigOptions -> {
                        formatTestConfigOptions(testConfig.getId(),testConfigOptions,userName);
                    });
                    testConfigOptionsService.saveOrUpdateBatch(testConfigOptionsList);
                }

                return this.updateById(testConfig);
            }else {
                return false;
            }
        }
    }

    public TestConfigOptions formatTestConfigOptions(Long configId,TestConfigOptions testConfigOptions,String userName){
        Optional.ofNullable(testConfigOptions).ifPresent(options ->{
            if (Objects.isNull(testConfigOptions.getId())){
                LocalDateTime createTime = Optional.ofNullable(options.getCreateTime()).orElse(LocalDateTime.now());
                String createUser = Optional.ofNullable(options.getCreateUser()).orElse(userName);
                options.setCreateUser(createUser);
                options.setCreateTime(createTime);
                options.setTestConfigId(configId);
                options.setUpdateUser(userName);
                options.setUpdateTime(LocalDateTime.now());
            }
        });

        return testConfigOptions;
    }

    /**
     * 批量保存或添加
     * @param testConfigList
     * @param userName
     */
    @Override
    public void batchSaveOrUpdateTestConfig(List<TestConfig> testConfigList, String userName) {
        List<TestConfig> addTestConfigs = Lists.newArrayList();
        List<TestConfig> updateTestConfigs = Lists.newArrayList();
        for (TestConfig testConfig : testConfigList) {
            boolean result = initTestCofig(testConfig, userName);
            if(result){
                addTestConfigs.add(testConfig);
            }else{
                if(Objects.nonNull(testConfig.getId())){
                    updateTestConfigs.add(testConfig);
                }else{
                    log.info("属性名称重复不可以重复插入：{}",testConfig);
                }
            }
        }
        if(CollectionUtils.isNotEmpty(addTestConfigs)){
            this.saveBatch(addTestConfigs);
        }
        if(CollectionUtils.isNotEmpty(updateTestConfigs)){
            this.updateBatchById(updateTestConfigs);
        }
    }

    /**
     * 分页查询
     * @param page
     * @param testConfig
     * @return
     */
    @Override
    public Page<TestConfig> queryTestConfigByPage(Page<TestConfig> page, TestConfig testConfig) {
        QueryWrapper<TestConfig> testConfigQueryWrapper = new QueryWrapper<>();
        testConfigQueryWrapper
                .like(StringUtils.isNotBlank(testConfig.getName()),"name",testConfig.getName())
                .like(StringUtils.isNotBlank(testConfig.getLable()),"lable",testConfig.getLable());
        testConfigQueryWrapper.orderByDesc("id");
        return this.page(page,testConfigQueryWrapper);
    }

    /**
     * 根据ID查询
     * @param id
     * @return
     */
    @Override
    public TestConfig queryTestConfigById(Long id) {
        TestConfig testConfig = this.getById(id);
        List<TestConfigOptions> testConfigOptionsList = testConfigOptionsService.queryTestConfigOptionsByConfigId(id);
        if(CollectionUtils.isNotEmpty(testConfigOptionsList)){
            testConfig.setTestConfigOptionsList(testConfigOptionsList);
        }
        return testConfig;
    }

    @Override
    public Boolean deleteTestConfigById(Long id) {
        boolean flag = true;
        try{
            List<TestConfigOptions> testConfigOptionsList = testConfigOptionsService.queryTestConfigOptionsByConfigId(id);
            List<Long> idList = testConfigOptionsList.stream().map(TestConfigOptions::getId).collect(Collectors.toList());
            testConfigOptionsService.removeByIds(idList);
            this.removeById(id);
        } catch (Exception e){
            e.printStackTrace();
            flag = false;
        }
        return flag;
    }

    /**
     * 根据name和pool查询唯一
     * @param testConfig
     * @return
     */
    public boolean queryTestConfigByPoolAndName(TestConfig testConfig){
        QueryWrapper<TestConfig> testConfigQueryWrapper = new QueryWrapper<>();
        testConfigQueryWrapper
                .eq(StringUtils.isNotBlank(testConfig.getPool()),"pool",testConfig.getPool())
                .eq(StringUtils.isNotBlank(testConfig.getName()),"name",testConfig.getName());
        List<TestConfig> testConfigList = this.list(testConfigQueryWrapper);
        return CollectionUtils.isEmpty(testConfigList);
    }
}
