package com.ziroom.qa.quality.defende.provider.config;

import com.ziroom.qa.quality.defende.provider.properties.EhrProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(EhrProperties.class)
public class EhrConfiguration {
}
