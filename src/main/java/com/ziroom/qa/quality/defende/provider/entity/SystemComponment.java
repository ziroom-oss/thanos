package com.ziroom.qa.quality.defende.provider.entity;

import lombok.Data;

import java.time.LocalDateTime;
@Data
public class SystemComponment {
    private Long id;

    private String name;

    private String type;

    private LocalDateTime createTime;

    private String createUser;

    private LocalDateTime updateTime;

    private String updateUser;

}
