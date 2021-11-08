package com.ziroom.qa.quality.defende.provider.outinterface.client.omega;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EnvInfo {
    private String envLevel;
    // setting profile
    private String mavenProfile;
    private String envDomain;
    private String nameSpace;
    private String cluster;
    private String otherDomain;
    private String httpsDomain;
    // profile
    private String profile;
}
