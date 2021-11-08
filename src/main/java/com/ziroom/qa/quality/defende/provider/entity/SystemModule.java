package com.ziroom.qa.quality.defende.provider.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class SystemModule {
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 模块类型：功能测试相关的模块，接口测试相关的模块
     */
    private String systemModuleType;

    private String systemDomainName;

    private String moduleName;

    private String comment;

    private String createUser;

    private String updateUser;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm", timezone = "GMT+8")
    private LocalDateTime createTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm", timezone = "GMT+8")
    private LocalDateTime updateTime;


}
