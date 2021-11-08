package com.ziroom.qa.quality.defende.provider.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class TestTopic {

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
    private String topicName;

    /**
     * jiraID
     */
    private String relationRequirement;

    /**
     * 备注
     */
    private String remark;

    /**
     * 负责人
     */
    private String topicMaster;

    /**
     * 上线时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm", timezone = "GMT+8")
    private LocalDateTime upTime;

    /**
     * 给前端解析的上线时间
     */
    @TableField(exist = false)
    private String upTimeStr;

    /**
     * 参与人员
     */
    private String topicParticipant;

    /**
     * 负责人 表现值
     */
    @TableField(exist = false)
    private String topicMasterStr;

    /**
     * 参与人 表现值
     */
    @TableField(exist = false)
    private String topicParticipantStr;

    /**
     * 需要关联的任务id集合
     */
    @TableField(exist = false)
    private List<Long> taskIdList;

    /**
     * 项目进度
     */
    @TableField(exist = false)
    private String topicProgress;

    /**
     * 最新的日报id
     */
    @TableField(exist = false)
    public Long newDailyId;
}
