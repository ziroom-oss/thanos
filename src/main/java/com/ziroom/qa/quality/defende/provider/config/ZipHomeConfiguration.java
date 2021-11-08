package com.ziroom.qa.quality.defende.provider.config;

import com.ziroom.qa.quality.defende.provider.properties.ZipHomeProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(ZipHomeProperties.class)
public class ZipHomeConfiguration {
}
