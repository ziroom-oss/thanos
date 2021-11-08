package com.ziroom.qa.quality.defende.provider.util.handler;

import com.alibaba.fastjson.JSONObject;
import com.ziroom.qa.quality.defende.provider.execTask.entity.dto.RunnableApiDto;
import com.ziroom.qa.quality.defende.provider.execTask.entity.dto.SUTRequestDto;
import com.ziroom.qa.quality.defende.provider.util.FileUtil;
import com.ziroom.qa.quality.defende.provider.util.TelotStringUtil;
import com.ziroom.qa.quality.defende.provider.util.http.HttpRequestFactory;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @Author: yinm5
 * @Description: http请求核心处理器
 * @Date: 17:18 2018/6/20
 */
@Service
@Slf4j
public class ApiHandler {

    private static final Logger logger = LoggerFactory.getLogger(ApiHandler.class);

    public static final String CONTENT_TYPE_STRING = "Content-Type";
    public static final String CONTENT_TYPE_CHARSET_STRING = "charset=";
    public static final String CONTENT_LENGTH_STRING = "Content-Length";
    private static final String FILE = "file-";
    private static final String FILE_KEY = "fileKey";
    private static final String PATH = "path";
    private static final String SII = "static/image/";
    private static final String F = "file-";

    private DesHandler desHandler = new DesHandler();
    /**
     * 转化RunnableApiDto为http请求，及格式化结果。
     *
     * @return JSONObject类
     */
    public FormatResultVo runApi(SUTRequestDto apiDto) {

        JSONObject response;
        Map<String, String> headerMap = formatHeaderString(apiDto);
        log.info("headerMap:[{}],apiDto:[{}]",headerMap,apiDto);

        AtomicReference<String> requestUrl = new AtomicReference<>("");
        //进行http请求
        requestUrl.set(apiDto.getRequestUri().trim().replace("|", "%7C"));
        String requestType = apiDto.getRequestType();
        LocalDateTime startTime = LocalDateTime.now();
        response = new HttpRequestFactory().httpRequest(requestType, requestUrl.get(), apiDto.getRequestBody(), headerMap);
        LocalDateTime endTime = LocalDateTime.now();

        return formatResult(response,startTime,endTime,apiDto,headerMap);
    }

    /**
     * 参数指定类型加密操作，返回加密后字符串，供生成Api性能测试jmx文件使用
     *
     * @param para        参数
     * @param encryptType 加密类型
     * @return
     */
    public String jmxParamEncryptByEncryptType(String para, int encryptType) {
        String params = !StringUtils.isEmpty(para) ? para : "";
        EncryptTypeEnum encryptTypeEnum = EncryptTypeEnum.getByCode(encryptType);
        return desHandler.wrapDesEncryptedStringByEncryptType(params, encryptTypeEnum);
    }

    private JSONObject getHttpRequestValue(String para, RunnableApiDto apiDto, Map<String, String> headerMap) {
        JSONObject jsonObject;
        Map<String, String> map = getFileColumnAndName(para);
        String fileKey = map.get(FILE_KEY);
        String path = map.get(PATH);
        File file = new File(path);
        logger.info("===文件名称===:" + file.getName());
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream(SII + path);
        logger.info("===字节流===:" + inputStream);
        //字节赋给file
        if(!StringUtils.isEmpty(inputStream)) {
            FileUtil.inputstreamtofile(inputStream, file);
        }
        logger.info("===文件大小===:" + file.length());
        para = para.replace(F, "");
        jsonObject = new HttpRequestFactory().httpRequestPostMultiData(apiDto.getRequestUrl().trim(), para, fileKey, file, headerMap);
        return jsonObject;
    }

    /**
     * 文件字段 和 文件路径 获取
     * todo 后续界面优化成支持文件，此处需要作废
     * 目前方案是 字段名需要加 file-
     * eg:  file-字段名
     *
     * @param para
     * @return
     */
    private Map<String, String> getFileColumnAndName(String para) {
        Map<String, String> map = new HashMap<>(16);
        String[] paraMap = para.split("&");
        for (int i = 0; i < paraMap.length; i++) {
            String[] kv = paraMap[i].split("=", 2);
            //文件的名字必须以 file-xx 格式
            if (kv[0] != null && kv[0].contains("file-")) {
                map.put("fileKey", kv[0].substring(5));
                map.put("path", kv[1]);
            }
        }
        return map;
    }


    /**
     * 请求返回结果处理器，如果returnVale解密后是json串则替换掉原来的returnValue返回。
     * 1. 如果返回结果属于加密类型，通过请求的加密类型进行解密处理
     * 2. 如果解密处理失败，则进行warning日志输出，不进行解密；
     * 3. 解密后验证是否是json串，如果是json串则把解密后的json串放入response的returnValue内返回response；
     * 如果不是json串则不做任何处理返回原response。
     *
     * @param response JSONObject类型
     * @param apiDto
     * @param headerMap
     * @return JSONObject类型
     * @Param encryptTypeEnum EncryptTypeEnum类型
     */
    private FormatResultVo formatResult(JSONObject response, LocalDateTime startTime, LocalDateTime endTime, SUTRequestDto apiDto, Map<String, String> headerMap) {
        FormatResultVo vo = new FormatResultVo();
        vo.setRequestBody(apiDto.getRequestBody());
        if(apiDto.getRequestUri().contains("10.216.4.19")){
            apiDto.setRequestUri(apiDto.getRequestUri().replace("10.216.4.19",headerMap.get("host")));
        }
        vo.setRequestUrl(apiDto.getRequestUri());
        Double executionDouble = ((endTime.toInstant(ZoneOffset.of("+8")).toEpochMilli() - startTime.toInstant(ZoneOffset.of("+8")).toEpochMilli())/1000d);
        String executionTime =  String.format("%.1f", executionDouble);
        vo.setResponseTime(executionTime);
        vo.setStartTime(startTime);
        vo.setEndTime(endTime);
        vo.setList(apiDto.getAssertList());
        vo.setResponseHeader(response.getString("responseHeader"));
        vo.setRequestType(apiDto.getRequestType());
         if(null == response){
            vo.setActualResult("【平台提示】无返回值，请检查参数");
        }else {
             String returnValue = response.getString("returnValue");
//             if(returnValue!=null&&returnValue.length()>5000){
//                 returnValue = returnValue.substring(0,5000);
//             }
             vo.setActualResult(returnValue);
        }
         return vo;

    }

    /**
     * 格式化头字符串为适合HttpRequest使用的Map<String, String>
     *
     * @param headStr 待格式化的原始请求头，String类型
     * @return 格式化后的Map<String               ,                               String>
     */
    private Map<String, String> formatHeaderString(SUTRequestDto apiDto) {
        String headStr = apiDto.getHeader();
        String urlStr = apiDto.getRequestUri();
        Map<String, String> headerMap;
        try {
            URL url = new URL(urlStr);
            String host = url.getHost();
            headerMap = TelotStringUtil.convertHeaderString2Map(headStr);
            // 修改host的值
            if(!urlStr.contains("10.30")){
                headerMap.put("host",host);
                apiDto.setRequestUri(urlStr.replace(host,"10.216.4.19"));
            }
        } catch (Exception e) {
            throw new RuntimeException("头部格式解析失败！");
        }
        String contentType = headerMap.get(CONTENT_TYPE_STRING);
        //移除Content-Type编码格式
        if (!StringUtils.isEmpty(contentType) && contentType.contains(CONTENT_TYPE_CHARSET_STRING)) {
            headerMap.put(CONTENT_TYPE_STRING, contentType.split(";")[0]);
        }
        //移除Content-Length
        String contentLength = headerMap.get(CONTENT_LENGTH_STRING);
        if (!StringUtils.isEmpty(contentLength)) {
            headerMap.remove(CONTENT_LENGTH_STRING);
        }

        return headerMap;
    }

    /**
     * 检查是否含有未赋值的变量
     *
     * @param str 被检查的String字符串
     * @return 有的话为true，没有的话为false
     */
    public static boolean hasVariable(String str) {
        if (StringUtils.isEmpty(str)) {
            return false;
        }
        return str.contains(ParamVariable.VAR_START) && str.contains(ParamVariable.VAR_END);
    }

    public static void main(String[] args) throws MalformedURLException {
        URL url = new URL("http://baidu.com/sss/dddd");
        System.out.println(url.getHost());

//        JSONObject jsonObject = new HttpRequestFactory().httpRequest("post", "site.baidu.com", "", null);
//        System.out.println(jsonObject);
    }
}
