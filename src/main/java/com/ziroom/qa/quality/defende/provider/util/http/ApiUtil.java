package com.ziroom.qa.quality.defende.provider.util.http;//package com.ziroom.api.utils.http;
//
//import net.sf.json.JSONObject;
//
//import java.util.HashMap;
//import java.util.Map;
//
///**
// * 接口运行处理类
// *
// * @author zhanghang
// * @date 2019-06-25
// */
//public class ApiUtil {
//    /**
//     * http请求封装
//     * @param requestType
//     * @param requestUrl
//     * @param para
//     * @param headerMap
//     * @return
//     */
//    public static JSONObject runHttpRequest(String requestType,String requestUrl,String para,Map headerMap){
//        JSONObject response;
//        response = new HttpRequestFactory().httpRequest(requestType, requestUrl, para, headerMap);
//        return response;
//    }
//
//    /**
//     * http 请求封装
//     * @param requestType
//     * @param requestUrl
//     * @param para
//     * @param headerMap
//     * @return
//     */
//    public static JSONObject executeHttpRequest(String requestType,String requestUrl,String para,Map headerMap){
//
//        Map map = preHandle(requestType,requestUrl,para,headerMap);
//
//        JSONObject jsonObject = runHttpRequest(requestType,requestUrl,para,headerMap);
//
//        return jsonObject;
//    }
//
//    /**
//     * 运行单接口
//     * @return
//     */
//    public static JSONObject runApi(){
//
//        return null;
//    }
//
//    private static Map<String,String> preHandle(String requestType, String requestUrl, String para, Map headerMap) {
//        Map<String,String> map = new HashMap<>(16);
//
//
//        return map;
//    }
//
//
//}
