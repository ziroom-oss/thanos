package com.ziroom.qa.quality.defende.provider.entity;

import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class TestApplicationModule {

    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    private Long applicationId;

    private String moduleName;

    @TableField(updateStrategy = FieldStrategy.IGNORED )
    private Long parentId;

    private String createUser;

    private LocalDateTime createTime;

    private String updateUser;

    private LocalDateTime updateTime;

    private int level;

    private String moduleTreePath;

}
