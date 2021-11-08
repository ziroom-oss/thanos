package com.ziroom.qa.quality.defende.provider.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * 给前端返回的信息集合
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TestMessageInfoVo {

    /**
     * userId 自如员工号
     */
    private String userId;

    /**
     * 未读消息数量
     */
    private int msgCount=0;

    /**
     * 消息主体
     */
    private List<TestMessageVo> testMessageVo = new ArrayList<>();
}
