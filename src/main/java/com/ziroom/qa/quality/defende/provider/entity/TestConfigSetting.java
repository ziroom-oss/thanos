package com.ziroom.qa.quality.defende.provider.entity;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;

@Slf4j
@Data
public class TestConfigSetting {
    private Long id;

    private String name;

    private Boolean systemConfig;

    private String createUser;

    private LocalDateTime createTime;

    private String updateUser;

    private LocalDateTime updateTime;

}
