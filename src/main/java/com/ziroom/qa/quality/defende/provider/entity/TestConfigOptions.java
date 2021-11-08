package com.ziroom.qa.quality.defende.provider.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;

@Slf4j
@Data
public class TestConfigOptions {
    private Long id;

    private String optionVal;

    private Boolean defaultSelected;

    private String bgColor;

    private String color;

    private String descrition;

    private Long testConfigId;

    private String createUser;

    private LocalDateTime createTime;

    private String updateUser;

    private LocalDateTime updateTime;

    @TableField(exist = false)
    private Boolean editStatus = false;

}
