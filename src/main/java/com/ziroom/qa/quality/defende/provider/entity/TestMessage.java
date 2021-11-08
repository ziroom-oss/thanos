package com.ziroom.qa.quality.defende.provider.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class TestMessage {

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
     * 消息标题
     */
    private String msgTitle;

    /**
     * 变更内容
     */
    private String content;

}
