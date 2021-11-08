package com.ziroom.qa.quality.defende.provider.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.ziroom.qa.quality.defende.provider.vo.ApplicationModuleTree;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class TestApplication {
    private Long id;

    private String ehrTreePath;

    private String applicationType;

    private String applicationName;

    private String applicationHostname;

    private String createUser;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm", timezone = "GMT+8")
    private LocalDateTime createTime;

    private String updateUser;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm", timezone = "GMT+8")
    private LocalDateTime updateTime;

    @TableField(exist = false)
    private List<ApplicationModuleTree> applicationModuleTreeList;

    private String ehrGroup;

    @TableField(exist = false)
    private String updateUserStr;

}
