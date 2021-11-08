package com.ziroom.qa.quality.defende.provider.vo;

import lombok.Data;

@Data
public class TestMessageVo {
    /**
     * 消息主体id
     */
    private Long testMessageId;

    /**
     * 是否读取 1，已读 2，未读
     */
    private Integer msgStatus=2;

    /**
     * 消息标题
     */
    private String msgTitle;

    /**
     * 变更内容
     */
    private String content;
}
