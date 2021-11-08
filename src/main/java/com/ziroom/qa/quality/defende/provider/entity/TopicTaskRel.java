package com.ziroom.qa.quality.defende.provider.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class TopicTaskRel {

    /**
     * 自增主键
     */
    private Long id;

    /**
     * 创建人 自如工号
     */
    private String createUser;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm", timezone = "GMT+8")
    private LocalDateTime createTime;

    /**
     * 修改人 自如工号
     */
    private String updateUser;

    /**
     * 修改时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm", timezone = "GMT+8")
    private LocalDateTime updateTime;

    /**
     * 项目名称
     */
    @TableField(exist = false)
    private String topicName;

    /**
     * task名称
     */
    @TableField(exist = false)
    private String testTaskName;

    /**
     * 关联标识 true 已关联，false 未关联
     */
    @TableField(exist = false)
    private boolean relFlag = false;

    /**
     * 项目名称
     */
    private Long topicId;

    /**
     * task名称
     */
    private Long taskId;

}
