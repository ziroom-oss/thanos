package com.ziroom.qa.quality.defende.provider.util.http;

import com.alibaba.fastjson.JSONObject;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.StringBody;
import org.springframework.util.StringUtils;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

public class PostRequest implements HttpRequest {
    HttpClientUtils httpClientUtils = new HttpClientUtils();
    private static String UTF_8 = "UTF-8";

    public PostRequest() {}

    @Override
    public JSONObject invokeRequest(String url, String para, Map<String, String> header){
        if (StringUtils.isEmpty(url)) {
            return null;
        } else {
            HttpPost httpPost = new HttpPost(url);
            ContentType contentType = header.containsKey("Content-Type") ? ContentType.create(header.get("Content-Type")+"", UTF_8) : ContentType.create("application/x-www-form-urlencoded", UTF_8);
            para= para==null?"":para;
            httpPost.setEntity(new StringEntity(para, contentType));
            Iterator var6 = header.keySet().iterator();
            while(var6.hasNext()) {
                String key = (String)var6.next();
                String value = header.get(key);
                httpPost.setHeader(key, value);
            }
            return this.httpClientUtils.getResult(httpPost, para);
        }
    }

    public JSONObject invokeRequest(String url, String para, String fileKey, File file, Map<String, String> header){
        if (StringUtils.isEmpty(url)) {
            return null;
        } else {
            HttpPost httpPost = new HttpPost(url);
            MultipartEntityBuilder mEntityBuilder = MultipartEntityBuilder.create().setMode(HttpMultipartMode.RFC6532);
            mEntityBuilder.addBinaryBody(fileKey, file);
            Map<String, String> map = this.splitNameValuePairs(para);
            Iterator var9 = map.entrySet().iterator();
            while(var9.hasNext()) {
                Entry<String, String> entry = (Entry)var9.next();
                mEntityBuilder.addPart(entry.getKey()+"", new StringBody(entry.getValue()+"", ContentType.APPLICATION_FORM_URLENCODED));
            }
            httpPost.setEntity(mEntityBuilder.build());
            var9 = header.keySet().iterator();
            while(var9.hasNext()) {
                String key = (String)var9.next();
                String value = header.get(key)+"";
                httpPost.setHeader(key, value);
            }
            return this.httpClientUtils.getResult(httpPost, para);
        }
    }

    @Override
    public JSONObject invokeRequest(String url, String para) {
        return this.httpClientUtils.getResult(url, para);
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
