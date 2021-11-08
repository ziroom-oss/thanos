package com.ziroom.qa.quality.defende.provider.util.http;

import com.alibaba.fastjson.JSONObject;
import org.apache.http.client.methods.HttpGet;
import org.springframework.util.StringUtils;

import java.util.Iterator;
import java.util.Map;

/**
 * getRequest
 *
 * @author zhanghang
 * @date 2019-06-20
 */
public class GetRequest implements HttpRequest {
    HttpClientUtils httpClientUtils = new HttpClientUtils();

    public GetRequest() {
    }
    @Override
    public JSONObject invokeRequest(String url, String para, Map<String, String> header){
        HttpGet httpGet;
        if (StringUtils.isEmpty(url)) {
            return null;
        } else {
//            String link = para == "" ? url : url + "?" + para;
            httpGet = new HttpGet(url);
            if (!header.isEmpty()) {
                Iterator var6 = header.keySet().iterator();

                while(var6.hasNext()) {
                    String key = (String)var6.next();
                    httpGet.setHeader(key,header.get(key)+"");
                }
            }
            return this.httpClientUtils.getResult(httpGet, "");
        }
    }
    @Override
    public JSONObject invokeRequest(String url, String para) {
        HttpGet httpGet;
        if (StringUtils.isEmpty(url)) {
            return null;
        } else {
//            if (StringUtils.isEmpty(para)) {
//                httpGet = new HttpGet(url);
//            } else {
//                httpGet = new HttpGet(url + "?" + para);
//            }
            httpGet = new HttpGet(url);
            return this.httpClientUtils.getResult(httpGet, "");
        }
    }
}
