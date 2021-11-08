package com.ziroom.qa.quality.defende.provider.properties;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ConfigurationProperties(prefix = "zip")
public class ZipHomeProperties {
    private String sourceHome;
    private String destHome;
}
