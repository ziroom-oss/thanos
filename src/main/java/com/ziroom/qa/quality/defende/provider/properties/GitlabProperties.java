package com.ziroom.qa.quality.defende.provider.properties;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ConfigurationProperties(prefix = "gitlab")
public class GitlabProperties {
    private String ip;
    private String token;
    private String httpType;
}
