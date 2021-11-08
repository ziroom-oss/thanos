package com.ziroom.qa.quality.defende.provider.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ApplicationModuleTree {
    /**
     * 是否应用
     */
    private boolean isApp = true;

    /**
     * 关注状态 true 已关注，false 未关注
     */
    private boolean followFlag = false;

    /**
     * 是否叶子节点
     */
    private boolean isLeaf = false;

    private Long id;
    private String label;
    private int level;
    private String moduleTreePath;
    private Long parentId;

    /**
     * 应用id
     */
    private Long applicationId;

    /**
     * 用于控制编辑状态
     */
    private Boolean active = false;
    private List<ApplicationModuleTree> children;

}
