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
public class TestResultVo {

    /**
     * 是否成功
     */
    private Boolean flag;

    /**
     * 返回消息内容
     */
    private String msgRes;

    /**
     * 返回结果
     */
    private Object data;

}
