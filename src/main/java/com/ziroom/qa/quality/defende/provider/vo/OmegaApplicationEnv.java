package com.ziroom.qa.quality.defende.provider.vo;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OmegaApplicationEnv {

    private Long id;
    private Long applicationId;
    private String envLevel;
    private String mavenProfile;
    private String envDomain;
    private String nameSpace;
    private String cluster;
    private String otherDomain;
    private String httpsDomain;
}
