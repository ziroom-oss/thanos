package com.ziroom.qa.quality.defende.provider.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Data
public class TestConfig {
    private Long id;

    private String lable;

    private String name;

    /**
     * 属性英文名称
     */
    private String fieldType;

    /**
     * 属性中文名称
     */
    private String fieldTypeLable;

    private String itemType;

    private Boolean required;

    /**
     * 是否为系统配置：这个属性应该在外层
     */
    private Boolean systemConfig;

    private String pool;

    private String createUser;

    private LocalDateTime createTime;

    private String updateUser;

    private LocalDateTime updateTime;


    @TableField(exist = false)
    private List<TestConfigOptions> testConfigOptionsList;

}
