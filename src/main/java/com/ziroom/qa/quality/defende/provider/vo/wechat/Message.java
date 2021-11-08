package com.ziroom.qa.quality.defende.provider.vo.wechat;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Message {
    private String pushType;
    private String msgType;
    private String token;
    private Map<String, Object> params;
    private Map<String, Object> markdown;
    private String toUser;
    private String modelCode;
    private String robotKey;

}
