package com.ziroom.qa.quality.defende.provider.config;

import com.ziroom.qa.quality.defende.provider.properties.GitlabProperties;
import org.gitlab4j.api.GitLabApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.text.MessageFormat;

@Configuration
@EnableConfigurationProperties(GitlabProperties.class)
public class GitlabConfiguration {
    @Autowired
    GitlabProperties gitlabProperties;

    @Bean
    public GitLabApi gitlabapi() {
        String url = MessageFormat.format("{0}://{1}", gitlabProperties.getHttpType(), gitlabProperties.getIp());
        return new GitLabApi(url, gitlabProperties.getToken());
    }
}
