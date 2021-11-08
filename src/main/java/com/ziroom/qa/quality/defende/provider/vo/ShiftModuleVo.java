package com.ziroom.qa.quality.defende.provider.vo;

import lombok.Data;

@Data
public class ShiftModuleVo {

    /**
     * 需要转移的模块id
     */
    private Long sourceModuleId;

    /**
     * 需要转移的目标模块id
     */
    private Long targetModuleId;

    /**
     * 目标模块是否是应用
     */
    private boolean appFlag;
}
