package com.ziroom.qa.quality.defende.provider.outinterface.client.omega;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OmegeApplicationLevel {
    private Long id;
    private Long projectId;
    private String applicationName;
    private String applicationDesc;
    private String repoUrl;
    private String language;
    private String teamCode;
    private String appLevel;
    private Boolean syncApiStatus;
    private String groupName;
    private String teamName;
    private String label;
    private List<EnvInfo> envInfoList;
    private List children;
    private boolean follow;
}
