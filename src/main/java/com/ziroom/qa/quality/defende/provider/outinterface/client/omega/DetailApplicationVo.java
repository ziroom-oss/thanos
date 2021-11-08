package com.ziroom.qa.quality.defende.provider.outinterface.client.omega;

import lombok.*;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.List;

/**
 * Omega--allApp  接口响应结构体
 */
@Data
public class DetailApplicationVo implements Serializable {

    private static final long serialVersionUID = 2L;

    /* 原生 */
    private Long id;
    private String aimsId;
    private Long groupId;
    private String groupIds;
    private Integer projectId;
    private String applicationName;
    private String applicationDesc;
    private String repoUrl;
    private String language;
    private String systemType;
    private String binaryPath;
    private String binaryType;
    private String domainName;
    private String teamCode;
    private String appLevel;
    private Boolean syncApiStatus;

    /* 扩展 */
    private String groupName;
    private String mavenProfile;
    private String teamName;
    private CIInfo cIInfo;
    private String registerTimeLabel;
    private Boolean isOpenCr;
    private String appGroupLevel;
    private String appGroupName;

    private List<EnvInfo> envInfoList;
    private List<ManagerUserInfo> userInfoList;
    private ManagerUserInfo managerUserInfo;
    private AppStandardRelate appStandardRelate;


    @Getter
    @Setter
    @Builder
    @ToString
    @NoArgsConstructor
    @AllArgsConstructor
    @Accessors(chain = true)
    public static class EnvInfo {
        private String envLevel;
        private String mavenProfile;
        private String profile;
        private String envDomain;
        private String nameSpace;
        private String cluster;
        private String otherDomain;
        private String httpsDomain;

    }

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Accessors(chain = true)
    public static class ManagerUserInfo {
        private String email;
        private String name;
        private String code;
        private String role;
    }

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Accessors(chain = true)
    public static class CIInfo {

        private Boolean sonarSwitch = false;
        private Boolean junitSwitch = false;
        private Boolean standardInspectionSwitch = false;
        //阻断bug数
        private Integer sonarBlocker;
        //严重bug数阈值
        private Integer sonarCritical;
        //单测数阈值
        private Integer utCoverageRate;

        private List<CodeReviewSwitch> codeReviewSwitchList;

        @Getter
        @Setter
        @Builder
        @NoArgsConstructor
        @AllArgsConstructor
        @Accessors(chain = true)
        public static class CodeReviewSwitch {
            private String envLevel;
            private Boolean codeReviewSwitch = false;
        }

    }


    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AppStandardRelate {
        private Long id;
        private Long appId;
        private Boolean pinpointFlag;
        private Boolean graylogFlag;
        private String appName;
        private Boolean errorlogFlag;
        private Boolean lambdaFlag;
    }


}
