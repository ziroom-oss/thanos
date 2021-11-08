package com.ziroom.qa.quality.defende.provider.util.http;

import com.alibaba.fastjson.JSONObject;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.client.CookieStore;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.SSLContext;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class HttpsClientUtil {
    private static final Logger logger = LoggerFactory.getLogger(HttpsClientUtil.class);
    private static String UTF_8 = "UTF-8";
    static CookieStore cookieStore = null;

    public HttpsClientUtil() {}

    public static JSONObject httpsPostRequestWithHeadersAndBody(String url, Map<String, String> headerMap, String bodyParam) {
        HttpPost httpPost = new HttpPost(url);
        if (headerMap.size() != 0) {
            Iterator var4 = headerMap.keySet().iterator();
            while(var4.hasNext()) {
                String key = (String)var4.next();
                String value = headerMap.get(key);
                httpPost.setHeader(key, value);
            }
        }

        ContentType contentType = headerMap.containsKey("Content-Type") ? ContentType.create(headerMap.get("Content-Type"), UTF_8) : ContentType.create("application/x-www-form-urlencoded", UTF_8);
        httpPost.setEntity(new StringEntity(bodyParam, contentType));
        RequestConfig rConfig = RequestConfig.custom().setConnectTimeout(5000).setConnectionRequestTimeout(10000).setSocketTimeout(60000).build();
        httpPost.setConfig(rConfig);
        return getResult(httpPost);
    }

    private static CloseableHttpClient init() {
        CloseableHttpClient httpClient;
        try {
            SSLContext sslcontext = SSLClient.createIgnoreVerifySSL();
            RequestConfig defaultRequestConfig = RequestConfig.custom().build();
            Registry socketFactoryRegistry = RegistryBuilder.create().register("http", PlainConnectionSocketFactory.INSTANCE).register("https", new SSLConnectionSocketFactory(sslcontext)).build();
            PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager(socketFactoryRegistry);
            if (!HttpClientUtils.STORE_COOKIE) {
                cookieStore = null;
            }
            httpClient = HttpClients.custom().setConnectionManager(connectionManager).setDefaultRequestConfig(defaultRequestConfig).setDefaultCookieStore(cookieStore).build();
            return httpClient;
        } catch (KeyManagementException var5) {
            logger.info("KeyManagementException");
            var5.printStackTrace();
        } catch (NoSuchAlgorithmException var6) {
            logger.info("NoSuchAlgorithmException");
            var6.printStackTrace();
        }

        return null;
    }

    public static JSONObject getResult(HttpRequestBase request) {
        JSONObject json = new JSONObject();
        CloseableHttpResponse response = null;

        try {
            String requestURL = request.getURI().toURL().toString();
            String requsetMethod = request.getMethod();
            CloseableHttpClient httpsClient = init();
            String startTime = CommonFunction.getNowDate("yyyy-MM-dd HH:mm:ss.SSS");
            response = httpsClient.execute(request);
            String endTime = CommonFunction.getNowDate("yyyy-MM-dd HH:mm:ss.SSS");
            String diffTime = CommonFunction.getDistanceTime(startTime, endTime);
            HttpEntity entity = response.getEntity();
            json.put("requestURL", requestURL);
            json.put("requestMethod", requsetMethod);
            json.put("responseTime", diffTime);
            json.put("returnStatusCode", String.valueOf(response.getStatusLine().getStatusCode()));
            json.put("returnValue", EntityUtils.toString(entity));
            Header[] headers = response.getAllHeaders();
            HashMap headerMap = new HashMap(10);
            Header[] var16 = headers;
            int e1 = headers.length;

            for(int var18 = 0; var18 < e1; ++var18) {
                Header header = var16[var18];
                headerMap.put(header.getName(), header.getValue());
            }

            json.put("responseHeader", headerMap);
            JSONObject var26 = json;
            return var26;
        } catch (IOException var24) {
            logger.info("http处理异常 ");
            var24.printStackTrace();
        } finally {
            if (response != null) {
                try {
                    response.close();
                } catch (IOException var23) {
                    logger.info("http处理异常 ");
                    var23.printStackTrace();
                }
            }

        }

        return null;
    }
}
