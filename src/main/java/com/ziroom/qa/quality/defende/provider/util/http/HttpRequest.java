package com.ziroom.qa.quality.defende.provider.util.http;


import com.alibaba.fastjson.JSONObject;

import java.net.URISyntaxException;
import java.util.Map;

/**
 * http
 *
 * @author zhanghnag
 * @date 2019-06-20
 */
public interface HttpRequest {

    /**
     * invokeRequest(String var1, String var2, Map<String, String> var3)
     * @param var1
     * @param var2
     * @param var3
     * @return
     * @throws URISyntaxException
     */
    JSONObject invokeRequest(String var1, String var2, Map<String, String> var3) throws URISyntaxException;

    /**
     * invokeRequest(String var1, String var2)
     * @param var1
     * @param var2
     * @return
     */
    JSONObject invokeRequest(String var1, String var2);
}
