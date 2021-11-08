package com.ziroom.qa.quality.defende.provider.util;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.*;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * HttpClientUtils
 *
 * @author zhanghang
 * @date 2019-06-20
 */
@Slf4j
public class HttpClientUtils {

    public static boolean STORE_COOKIE = true;
    private static PoolingHttpClientConnectionManager cm;
    static CookieStore cookieStore = null;
    private static String UTF_8 = "UTF-8";

    public HttpClientUtils() {}

    private static void init() {
        if (cm == null) {
            cm = new PoolingHttpClientConnectionManager();
            cm.setMaxTotal(50);
            cm.setDefaultMaxPerRoute(5);
        }
    }

    private static CloseableHttpClient getHttpClient() {
        init();
        LaxRedirectStrategy reStrategy = new LaxRedirectStrategy();
        HttpClientBuilder builder = HttpClientBuilder.create().setKeepAliveStrategy(new DefaultConnectionKeepAliveStrategy()).setRedirectStrategy(reStrategy);
        if (!STORE_COOKIE) {
            cookieStore = null;
        }
        return builder.setDefaultCookieStore(cookieStore).build();
    }

    private JSONObject getCommonResult(HttpRequestBase request, String requestParam) {
        JSONObject json = new JSONObject();
        CloseableHttpResponse response = null;
        int i = 1;
        try {
            String requestURL = request.getURI().toURL().toString();
            String requsetMethod = request.getMethod();
            while(i < 3) {
                HttpClientContext context = HttpClientContext.create();
                CloseableHttpClient httpClient = getHttpClient();
                response = httpClient.execute(request, context);
                HttpEntity entity = response.getEntity();
                cookieStore = context.getCookieStore();
                if (response.getStatusLine().getStatusCode() == 408) {
                    log.info(requestURL + "第" + i + "次请求");
                    ++i;
                } else if (entity != null) {
                    Header[] headers = response.getAllHeaders();
                    Map<String, String> headerMap = new HashMap(10);
                    Header[] var16 = headers;
                    int var17 = headers.length;
                    for(int var18 = 0; var18 < var17; ++var18) {
                        Header header = var16[var18];
                        headerMap.put(header.getName(), header.getValue());
                    }
                    json.put("returnValue", EntityUtils.toString(entity, UTF_8));
                    json.put("requestMethod", requsetMethod);
                    json.put("returnStatusCode", String.valueOf(response.getStatusLine().getStatusCode()));
                    json.put("responseHeader", headerMap);
                    json.put("requestURL", requestURL);
                    if (!StringUtils.isEmpty(requestParam)) {
                        json.put("requestParam", requestParam);
                    }
                    log.info("返回数据信息: " + json.toString());
                    JSONObject var31 = json;
                    return var31;
                }
            }
            return null;
        } catch (IOException var29) {
            log.info("http处理异常 ");
            var29.printStackTrace();
            return null;
        } finally {
            if (response != null) {
                try {
                    response.close();
                } catch (IOException var28) {
                    log.info("http处理异常 ");
                    var28.printStackTrace();
                }
            }

        }
    }

    public JSONObject getResult(HttpRequestBase request, String requestParam) {
        RequestConfig rConfig = RequestConfig.custom().setConnectTimeout(10000).setConnectionRequestTimeout(10000).setSocketTimeout(60000).build();
        request.setConfig(rConfig);
        try {
            JSONObject result = this.getCommonResult(request, requestParam);
            return result;
        } catch (Exception var5) {
            var5.printStackTrace();
            return null;
        }
    }

    public JSONObject getResult(String url, String requestParam) {
        JSONObject json = new JSONObject();
        String body;
        HttpClient httpClient = null;
        HttpPost method = null;
        if (url != null) {
            httpClient = new DefaultHttpClient();
            method = new HttpPost(url);
        }
        if (method != null & requestParam != null && !"".equals(requestParam.trim())) {
            try {
                method.addHeader("Content-type", "application/json; charset=utf-8");
                method.setHeader("Accept", "application/json");
                method.setEntity(new StringEntity(requestParam, Charset.forName("UTF-8")));
                HttpResponse response = httpClient.execute(method);
                body = EntityUtils.toString(response.getEntity());
                if (body != null) {
                    json.put("returnValue", body);
                    json.put("returnStatusCode", String.valueOf(response.getStatusLine().getStatusCode()));
                    json.put("requestURL", url);
                    if (!StringUtils.isEmpty(requestParam)) {
                        json.put("requestParam", requestParam);
                    }
                    log.info("返回数据信息: " + json.toString());
                }
            } catch (IOException var13) {
            } finally {
            }
        }
        return json;
    }

    public JSONObject getResultPut(String url, String requestParam) {
        JSONObject json = new JSONObject();
        String body;
        HttpClient httpClient = null;
        HttpPut method = null;
        if (url != null) {
            httpClient = new DefaultHttpClient();
            method = new HttpPut(url);
        }
        if (method != null & requestParam != null && !"".equals(requestParam.trim())) {
            try {
                method.addHeader("Content-type", "application/json; charset=utf-8");
                method.setHeader("Accept", "application/json");
                method.setEntity(new StringEntity(requestParam, Charset.forName("UTF-8")));
                HttpResponse response = httpClient.execute(method);
                body = EntityUtils.toString(response.getEntity());
                if (body != null) {
                    json.put("returnValue", body);
                    json.put("returnStatusCode", String.valueOf(response.getStatusLine().getStatusCode()));
                    json.put("requestURL", url);
                    if (!StringUtils.isEmpty(requestParam)) {
                        json.put("requestParam", requestParam);
                    }
                    log.info("返回数据信息: " + json.toString());
                }
            } catch (IOException var13) {
            } finally {
            }
        }
        return json;
    }

    public ArrayList<NameValuePair> covertParams2NVPS(JSONObject paraValue) {
        ArrayList<NameValuePair> pairs = new ArrayList();
        Iterator pv = paraValue.keySet().iterator();
        while(pv.hasNext()) {
            String key = pv.next().toString();
            pairs.add(new BasicNameValuePair(key, paraValue.getString(key)));
        }
        return pairs;
    }
}