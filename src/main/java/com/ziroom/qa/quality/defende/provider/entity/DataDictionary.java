package com.ziroom.qa.quality.defende.provider.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;


@Data
public class DataDictionary {
    private Long id;

    private String type;

    private String englishName;

    private String name;

    private String createUser;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm", timezone = "GMT+8")
    private LocalDateTime createTime;

    private String updateUser;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm", timezone = "GMT+8")
    private LocalDateTime updateTime;

    private String dicValue;

}
