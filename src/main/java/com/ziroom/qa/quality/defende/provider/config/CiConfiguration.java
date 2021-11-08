package com.ziroom.qa.quality.defende.provider.config;

import com.ziroom.qa.quality.defende.provider.properties.CiProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(CiProperties.class)
public class CiConfiguration {
}
