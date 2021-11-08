package com.ziroom.qa.quality.defende.provider.vo.wechat;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WechatResult {

    private String code;
    private String message;
    private String responseCode;
    private String errorInfo;
    private String messgeInfo;
    private String serialNo;
    private String data;

}
