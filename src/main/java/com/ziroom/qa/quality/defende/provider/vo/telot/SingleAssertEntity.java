package com.ziroom.qa.quality.defende.provider.vo.telot;

import lombok.Data;

@Data
public class SingleAssertEntity {
    /**
     * 主键
     */
    private Integer id;

    /**
     * case id
     */
    private Integer caseId;

    /**
     * 断言内容
     */
    private String assertContent;

    /**
     * 断言类型
     */
    private Integer assertType;

    /**
     * 是否删除
     */
    private Integer isDel;

    /**
     * step id 用于多接口断言
     */
    private Integer stepId;
}
