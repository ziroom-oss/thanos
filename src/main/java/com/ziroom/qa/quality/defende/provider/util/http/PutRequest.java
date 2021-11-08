package com.ziroom.qa.quality.defende.provider.util.http;

import com.alibaba.fastjson.JSONObject;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class PutRequest implements HttpRequest  {

    HttpClientUtils httpClientUtils = new HttpClientUtils();
    private static String UTF_8 = "UTF-8";

    public PutRequest() {}

    @Override
    public JSONObject invokeRequest(String url, String para, Map<String, String> header){
        if (StringUtils.isEmpty(url)) {
            return null;
        } else {
            HttpPut httpPut = new HttpPut(url);
            ContentType contentType = header.containsKey("Content-Type") ? ContentType.create(header.get("Content-Type")+"", UTF_8) : ContentType.create("application/x-www-form-urlencoded", UTF_8);
            httpPut.setEntity(new StringEntity(para, contentType));
            Iterator var6 = header.keySet().iterator();
            while(var6.hasNext()) {
                String key = (String)var6.next();
                String value = header.get(key);
                httpPut.setHeader(key, value);
            }
            return this.httpClientUtils.getResult(httpPut, para);
        }
    }


    @Override
    public JSONObject invokeRequest(String url, String para) {
        return this.httpClientUtils.getResultPut(url, para);
    }

    public Map<String, String> splitNameValuePairs(String param) {
        Map<String, String> map = new HashMap();
        String[] pairs = param.split("&");
        String[] var4 = pairs;
        int var5 = pairs.length;
        for(int var6 = 0; var6 < var5; ++var6) {
            String p = var4[var6];
            String[] kv = p.split("=", 2);
            map.put(kv[0], kv[1]);
        }
        return map;
    }

}
