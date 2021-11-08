package com.ziroom.qa.quality.defende.provider.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class SystemComponmentParams {
    private Long id;

    private Long componmentId;

    private String paramLable;

    private String paramVal;

    private String paramDesc;

    private LocalDateTime createTime;

    private String createUser;

    private LocalDateTime updateTime;

    private String updateUser;

}
