package com.ziroom.qa.quality.defende.provider.entity.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @Author: yinm5
 * @Description: 部署信息相关DTO
 * @Date: 9:42 2020/1/9
 */
@ToString
@Getter
@Setter
public class KubeDeploymentDto {

    /**
     * Omega 应用名称
     */
    private String appName;

    /**
     * 命名空间，如 tech-daily
     */
    private String nameSpace;

    /**
     * 部署信息的 yaml 文件
     */
    private String deploymentInfo;

    /**
     * 项目 id
     */
    private String projectId;

    /**
     * 是否 https
     */
    private String isHttps;

    /**
     * 项目域名
     */
    private String envDomain;

    /**
     * 分支名称
     */
    private String imageBranch;
    /**
     * vue
     */
    private String systemType;
}
