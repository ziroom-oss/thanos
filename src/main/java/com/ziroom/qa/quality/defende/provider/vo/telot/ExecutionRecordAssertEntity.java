package com.ziroom.qa.quality.defende.provider.vo.telot;

import lombok.Data;

@Data
public class ExecutionRecordAssertEntity {

    private Integer id;

    /**
     * 关联的record id
     */
    private Integer recordId;

    /**
     * 断言内容
     */
    private String assertContent;

    /**
     * 断言类型
     */
    private Integer assertType;

    /**
     * 断言结果
     */
    private String assertResult;
}
