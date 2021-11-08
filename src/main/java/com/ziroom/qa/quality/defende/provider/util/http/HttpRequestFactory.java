package com.ziroom.qa.quality.defende.provider.util.http;


import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.util.Map;

@Slf4j
public class HttpRequestFactory {
    private final String HTPPS_PROTOCAL = "https://";

    public HttpRequestFactory() {
    }

    public JSONObject httpRequest(String requestType, String url, String para, Map<String, String> header){
        log.debug("httpRequest request ：requestType[{}],url[{}],para[{}],<String, String>[{}]",requestType,url,para,header);
        byte var6 = -1;
        if (requestType.equalsIgnoreCase("GET")) {
            var6 = 0;
        }
        if (requestType.equalsIgnoreCase("POST")) {
            var6 = 1;
        }
        if (requestType.equalsIgnoreCase("PUT")) {
            var6 = 2;
        }
        switch(var6) {
            case 0:
                if (header.size() == 0) {
                    return (new GetRequest()).invokeRequest(url, para);
                }

                return (new GetRequest()).invokeRequest(url, para, header);
            case 1:
                if (url.indexOf("https://") == 0) {
                    return HttpsClientUtil.httpsPostRequestWithHeadersAndBody(url, header, para);
                }

                return (new PostRequest()).invokeRequest(url, para, header);
            case 2:
//                if (url.indexOf("https://") == 0) {
//                    return HttpsClientUtil.httpsPostRequestWithHeadersAndBody(url, header, para);
//

                return (new PutRequest()).invokeRequest(url, para, header);
            default:
                log.debug("res [null]");
                return null;
        }
    }

    public JSONObject httpRequest(String requestType, String url, String para) {
        byte var5 = -1;
        switch(requestType.hashCode()) {
            case 70454:
                if (requestType.equals("GET")) {
                    var5 = 0;
                }
                break;
            case 2461856:
                if (requestType.equals("POST")) {
                    var5 = 1;
                }
            case 79599 :
                if (requestType.equals("PUT")) {
                    var5 = 2;
                }
        }

        switch(var5) {
            case 0:
                return (new GetRequest()).invokeRequest(url, para);
            case 1:
                return (new PostRequest()).invokeRequest(url, para);
            case 2:
                return (new PutRequest()).invokeRequest(url, para);
            default:
                return null;
        }
    }

    public JSONObject httpRequestPostMultiData(String url, String para, String fileKey, File file, Map<String, String> header) {
        return (new PostRequest()).invokeRequest(url, para, fileKey, file, header);
    }

    public static void enableCookie() {
        HttpClientUtils.STORE_COOKIE = true;
    }

    public static void disableCookie() {
        HttpClientUtils.STORE_COOKIE = false;
    }
}

