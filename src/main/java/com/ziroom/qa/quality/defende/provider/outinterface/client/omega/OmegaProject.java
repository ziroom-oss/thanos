package com.ziroom.qa.quality.defende.provider.outinterface.client.omega;

import lombok.Data;

import java.util.Date;

@Data
public class OmegaProject {
    private Long id;
    private String aimsId;
    private Long groupId;
    private String groupName;
    private Long projectId;
    private String applicationName;
    private String applicationDesc;
    private String type;
    private String namespacePath;
    private String repoUrl;
    private Date registerTime;
    private String language;
    private String systemType;
    private String binaryType;
    private String domainName;
    private Boolean hasRelated;
    private String teamCode;
    private Boolean syncApiStatus;
}
