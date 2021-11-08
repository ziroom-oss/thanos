package com.ziroom.qa.quality.defende.provider.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class TestMessageRead {

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
     * 消息主体外键
     */
    private Long msgId;

    /**
     * 消息状态 1: 已读 2: 未读
     */
    private Integer msgStatus;
}
