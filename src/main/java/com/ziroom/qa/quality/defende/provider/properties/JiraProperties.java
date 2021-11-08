package com.ziroom.qa.quality.defende.provider.properties;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ConfigurationProperties(prefix = "jira")
public class JiraProperties {
    private String baseurl;
    private String username;
    private String password;

}
