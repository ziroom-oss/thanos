package com.ziroom.qa.quality.defende.provider.properties;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.context.annotation.Configuration;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Configuration
//@ConfigurationProperties(prefix = "wechat")
public class WeChatProperties {
    private String token;
    private String templateId;
    private String robotKey;
    private String url;
}
