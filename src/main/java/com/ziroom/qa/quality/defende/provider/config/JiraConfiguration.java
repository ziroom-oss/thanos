package com.ziroom.qa.quality.defende.provider.config;

import com.ziroom.qa.quality.defende.provider.properties.JiraProperties;
import net.rcarz.jiraclient.BasicCredentials;
import net.rcarz.jiraclient.JiraClient;
import net.rcarz.jiraclient.JiraException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(JiraProperties.class)
public class JiraConfiguration {

    @Autowired
    private JiraProperties jiraProperties;

//    @Bean
//    public JiraRestClient initJiraRestClient() {
//        AsynchronousJiraRestClientFactory asynchronousJiraRestClientFactory = new AsynchronousJiraRestClientFactory();
//        JiraRestClient jiraRestClient = asynchronousJiraRestClientFactory.createWithBasicHttpAuthentication(URI.create(jiraProperties.getBaseurl()), jiraProperties.getUsername(), jiraProperties.getPassword());
//        return jiraRestClient;
//    }

    @Bean
    public JiraClient getJiraClient() throws JiraException {
        BasicCredentials creds = new BasicCredentials(jiraProperties.getUsername(), jiraProperties.getPassword());
        return new JiraClient(jiraProperties.getBaseurl(), creds);
    }
}
