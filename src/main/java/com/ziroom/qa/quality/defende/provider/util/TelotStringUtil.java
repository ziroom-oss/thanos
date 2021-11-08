package com.ziroom.qa.quality.defende.provider.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.ziroom.qa.quality.defende.provider.util.http.CommonFunction;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 字符串工具类
 *
 * @author zhanghang
 * @date 2019-06-25
 */
@Slf4j
public class TelotStringUtil {

    public static final String ARRAY_START_SYMBOL = "[";
    public static final String ARRAY_END_SYMBOL = "]";
    public static final Pattern STRING_VARIABLE = Pattern.compile("\\$\\{[^\\(\\$]+?\\}");
    public static final String EMPTY_STRING = "";
    public static final String ENTER_STRING = "\n";
    public static final String COM = ".com";
    private static Pattern PATTERN_IPV4 = Pattern.compile("(\\d{1,3})\\.(\\d{1,3})\\.(\\d{1,3})\\.(\\d{1,3})");
    private static Pattern PATTERN_SPECIAL_STRING = Pattern.compile("\\s*|\t|\r|\n");

    public static final String MYSQL_JDBC_DRIVER = "com.mysql.jdbc.Driver";
    public static final String ORACLE_JDBC_DRIVER = "oracle.jdbc.driver.OracleDriver";

    /***
     * 分离请求地址例如：https://tcrm.ziroom.com/crm-reserve/house/getDistrictList
     * 结果：domain： https://tcrm.ziroom.com
     *      requestUrl：/crm-reserve/house/getDistrictList
     * @return
     */
    public static JSONObject splitRequestUrl(String requesetUrl) {
        JSONObject result = new JSONObject();
        //url格式转换
        String replaceUrl = urlFormat(requesetUrl);
        String hashcode = CommonFunction.toMd5(replaceUrl.getBytes());
        int index = replaceUrl.indexOf(COM);
        String domain = replaceUrl.substring(0, index + 4);
        String uri = replaceUrl.substring(index + 4);
        result.put("domain", domain);
        result.put("requestUri", uri);
        result.put("interfaceId", hashcode);
        result.put("replaceUrl", replaceUrl);
        return result;
    }

    /***
     * Url格式
     * 1.中文字符转化为英文
     * 2.去除末尾的"/","?"
     * @return
     */
    public static String urlFormat(String url) {
        String formatUrl = url.trim();
        if (url.contains("／") || url.contains("：") || url.contains("？")) {
            formatUrl = url.replace("／", "/").replace("：", ":").replace("？", "?").trim();
        }
        if ("/".equals(formatUrl.substring(formatUrl.length() - 1)) || "?".equals(formatUrl.substring(formatUrl.length() - 1))) {
            formatUrl = formatUrl.substring(0, formatUrl.length() - 1);
        }
        return formatUrl;
    }

    /**
     * 1. 通过符号深度/多层迭代的获取Map类型的值，比如{"course":{"id": abc}}，可以写为course.id,
     * 2. 根据数组格式取值，类似：data.moveOrderDate[0].dateKey，如需数组内随机取值格式为 data.moveOrderDate[].dateKey;
     * 3. 数组下标从0开始,数组下标未-1时取最后一个。
     *
     * @param key  String类型的key值
     * @param json Map类型的json
     * @return Object
     */
    public static Object getMapValueByStringKey(Map json, String key) {
        String[] keyArr = key.split("\\.");
        int len = keyArr.length;
        Object targetObject = new JSONObject(json);
        for (int i = 0; i < len; i++) {
            Map<String, String> map = splitVariableByArrayFormat(keyArr[i]);
            if (!map.isEmpty()) {
                if (StringUtils.isEmpty(targetObject)) {
                    return targetObject;
                }
                JSONArray arrJson = (JSONArray) ((JSONObject) targetObject).get(map.get("key"));
                if ("" == map.get("index")) {
                    int j = new Random().nextInt(arrJson.size());
                    targetObject = arrJson.get(j);
                } else if (map.get("index").equals("-1")) {
                    targetObject = arrJson.get(arrJson.size() - 1);
                } else {
                    targetObject = arrJson.get(Integer.valueOf(map.get("index")));
                }
            } else if (targetObject instanceof JSONObject) {
                JSONObject temp = (JSONObject) targetObject;
                targetObject = temp.size() == 0 ? "" : temp.get(keyArr[i]);
            } else {
                return targetObject;
            }
        }
        return targetObject;
    }

    /**
     * 解析数组，分离数组变量及index， 如a[0],返回map类型为{"key": "xx","index":"xx"}
     *
     * @param str 变量名
     * @return Map<String                                                                                                                               ,                                                                                                                                                                                                                                                               String>
     */
    private static Map<String, String> splitVariableByArrayFormat(String str) {
        int startIndex = str.lastIndexOf(ARRAY_START_SYMBOL);
        int endIndex = str.lastIndexOf(ARRAY_END_SYMBOL);
        Map<String, String> map = new HashMap<>(2);
        if (endIndex > startIndex + 1) {
            try {
                int i = Integer.valueOf(str.substring(startIndex + 1, endIndex));
                String key = str.substring(0, startIndex);
                map.put("key", key);
                map.put("index", String.valueOf(i));
            } catch (Exception e) {
            }
        } else if (endIndex == startIndex + 1) {
            String key = str.substring(0, startIndex);
            map.put("key", key);
            map.put("index", "");
        }
        return map;
    }

    /**
     * 判断字符串是否为json格式
     *
     * @param str
     * @return
     */
    public static boolean isJson(Object str) {
        if (str instanceof JSONObject) {
            return true;
        }
        if (str instanceof JSONArray) {
            return true;
        }
        try {
            JSON.parseObject((String) str);
            return true;
        } catch (RuntimeException e) {
            try {
                JSONArray.parseArray((String) str);
                return true;
            } catch (RuntimeException e1) {
                return false;
            }

        }
    }

    public static boolean isJsonObject(Object str) {
        if (str instanceof JSONObject) {
            return true;
        }

        try {
            JSON.parseObject((String) str);
            return true;
        } catch (RuntimeException e) {
            return false;
        }
    }

    public static boolean isJsonArray(Object str) {
        if (str instanceof JSONArray) {
            return true;
        }
        try {
            JSONArray.parseArray((String) str);
            return true;
        } catch (RuntimeException e1) {
            return false;
        }
    }


    /**
     * 如果字符为null，转化为空字符串，如果字符串非空，返回自己。
     *
     * @param str
     * @return
     */
    public static String convertNullToEmptyString(Object str) {
        return str == null ? "" : String.valueOf(str);
    }

    /**
     * 使用.或者[]之类的表达式的参数取值
     *
     * @param param     参数串
     * @param paramPool 参数池
     * @return 转化后的字符串
     */
    public static String replaceAllVariablesWithExpression(String param, Map paramPool) {
        return replaceAllVariables(param, paramPool, true);
    }

    /**
     * 通用替换参数方法
     *
     * @param param         参数串
     * @param paramPool
     * @param useExpression
     * @return
     */
    public static String replaceAllVariables(String param, Map paramPool, boolean useExpression) {
        Matcher matcher = STRING_VARIABLE.matcher(param);
        List<String> paramList = new ArrayList<>();
        while (matcher.find()) {
            String temp = matcher.group();
            paramList.add(temp.substring(2, temp.length() - 1));
        }
        String resultParam = param;
        for (String str : paramList) {
            Object value = useExpression ? getMapValueByStringKey(paramPool, str) : paramPool.get(str);
            if (value != null) {
                String val = String.valueOf(value);
                if(value instanceof List) {
                    //转成jsonString时保留null
                    val = JSON.toJSONString(value, SerializerFeature.WriteMapNullValue);
                }
                resultParam = resultParam.replace("${" + str + "}", val);
            }
        }
        return resultParam;
    }

    /**
     * 将源字符串中所有参数化项，替换为目标字符串中每一个值
     * 示例originStr：{"abc": ${aacc}, "lake": ${dog}, "cat": ${ttt}, "ddd": "water", "popo": ${poge1}}
     * 示例targetStr：111,222,333,444
     * 示例return：{"abc": 111, "lake": 222, "cat": 333, "ddd": "water", "popo": 444}
     *
     * @param originStr
     * @param targetStr
     * @return
     */
//    public static String replaceEachVariables(String originStr, String targetStr) {
//        Matcher matcher = STRING_VARIABLE.matcher(originStr);
//        int i = 0;
//
//        while (matcher.find()) {
//            String temp = matcher.group();
//            originStr = originStr.replace(temp, GetBackValue.getListFromFormattedStr(targetStr, ",", String.valueOf(i)));
//            i++;
//        }
//
//        return originStr;
//    }

    /**
     * 取出字符串中所有以制定字符开闭内的字符串，顺序取，不可嵌套字符。
     * 1, abc${dec}Gshek${ddd} => {dec, ddd}
     * 2, abd${def${dsdfsdf}sdfs} => {def${dsdfsdf}
     *
     * @param originStr      原始字符串
     * @param splitStartSign 前分隔符
     * @param splitEndSign   后分隔符
     * @return List<String>
     */
    public static List<String> getVariableNamesInStr(String originStr, String splitStartSign, String splitEndSign) {
        List<String> variableList = new ArrayList<>();
        int startIndex = 0, endIndex = 0;
        while (startIndex != -1) {
            startIndex = originStr.indexOf(splitStartSign, endIndex);
            endIndex = originStr.indexOf(splitEndSign, startIndex);
            if (startIndex != -1) {
                variableList.add(originStr.substring(startIndex + splitStartSign.length(), endIndex));
            }
        }
        return variableList;
    }

    /**
     * 无表达式的参数取值，变量名相当于一个整体，不会对此进行表达式处理
     *
     * @param param     参数串
     * @param paramPool 参数池
     * @return 转化后的字符串
     */
    public static String replaceAllVariablesWithoutExpression(String param, Map paramPool) {
        return replaceAllVariables(param, paramPool, false);
    }

    /***
     * 判断对象或对象数组中是否为空
     * @param obj
     * @return
     */
    public static boolean isNullOrEmpty(Object obj) {
        if (obj == null) {
            return true;
        }

        if (obj instanceof CharSequence) {
            return ((CharSequence) obj).length() == 0;
        }

        if (obj instanceof Collection) {
            return ((Collection) obj).isEmpty();
        }

        if (obj instanceof Map) {
            return ((Map) obj).isEmpty();
        }

        if (obj instanceof Object[]) {
            Object[] object = (Object[]) obj;
            if (object.length == 0) {
                return true;
            }
            boolean empty = true;
            for (int i = 0; i < object.length; i++) {
                if (!isNullOrEmpty(object[i])) {
                    empty = false;
                    break;
                }
            }
            return empty;
        }
        return false;
    }

    public static boolean isEmpty(String str) {
        if (null == str || str.length() == 0) {
            return true;
        }
        return str.equals("null");
    }

    /**
     * 把具体堆栈信息转化成String类型利于保持
     *
     * @param t throwable类，传入异常
     * @return 字符串
     */
    public static String printStackTraceToString(Throwable t) {
        StringWriter sw = new StringWriter();
        t.printStackTrace(new PrintWriter(sw, true));
        return sw.getBuffer().toString();
    }

    /**
     * 通过URL获取Uri
     *
     * @param url
     * @return
     */
    public static String getRequestUriByUrl(String url) {
        if (StringUtils.isEmpty(url)) {
            return url;
        } else if (url.indexOf(COM) <= 0) {
            return url;
        } else if (url.endsWith(COM)) {
            return "";
        } else {
            return url.split("\\.com")[1];
        }
    }


    /**
     * 格式转换(一)
     * <p>
     * param传参 name=5&code=123
     * 返回格式 [{key: 'name',value: '5'},{key: 'code',value: '123'}]
     */
    public static String convertGetParamToStr(String param) {
        List<Map<String, Object>> resultList = new ArrayList<>();
        if (param.contains("&")) {
            String[] paramArray = param.split("&");
            if (paramArray.length > 0) {
                for (String keyAndValue : paramArray) {
                    resultList = parseParamKeyAndValue(keyAndValue, resultList, "=");
                }
            }
        } else {
            resultList = parseParamKeyAndValue(param, resultList, "=");
        }
        if (!resultList.isEmpty()) {
            return JSON.toJSONString(resultList);
        } else {
            return "";
        }
    }

    /**
     * 格式转换(二)
     * JSON转JsonArray(bootstrap表格通用)
     *
     * @return [{"key":"code","value":2},{"key":"name","value":"zh"}]
     * @param: {"code":2,"name":"zh"}
     */
    public static String convertPostParamToStr(String param, String type) {
        List<Map<String, Object>> resultList = new ArrayList<>();
        if (type.equals("jsonArray")) {
            param = param.substring(1, param.length() - 1);
            String[] jsonObjArray = param.split(",");
            for (String jsonObjStr : jsonObjArray) {
                resultList = parseJsonParam(jsonObjStr, resultList);
            }
        } else {
            resultList = parseJsonParam(param, resultList);
        }
        if (!resultList.isEmpty()) {
            return JSON.toJSONString(resultList);
        } else {
            return "";
        }
    }

    /**
     * 格式转换(三)
     *
     * @return code=1&name=2
     * @param: [{"key":"code","value":"1"},{"key":"name","value":"2"}]
     */
    public static String jsonArray2Str(String str) {
        return pairsList2UrlString(str, "key", "value");
    }

    /**
     * 格式转换(四)
     *
     * @return {"code":"1","name":"zh"}
     * @param： [{"key":"code","value":"1"},{"key":"name","value":"zh"}]
     */
    public static String jsonArray2Json(String str) {
        JSONArray jsonArray = JSONArray.parseArray(str);
        if (null == jsonArray || jsonArray.size() == 0) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        for (Object obj : jsonArray) {
            sb.append("\"").append(((JSONObject) obj).get("key")).append("\"").append(":")
                    .append("\"").append(((JSONObject) obj).get("value")).append("\"").append(",");
        }
        return sb.substring(0, sb.length() - 1) + "}";
    }

    /**
     * 固定格式的List<Map<String,Object>字符串转化为url格式的字符串： string=object&string=object
     *
     * @param str         [{"key":"code","value":"1"},{"key":"name","value":"2"}]
     * @param keyString   可变如key,name
     * @param valueString 可变如value
     * @return code=1&name=2
     */
    public static String pairsList2UrlString(String str, String keyString, String valueString) {
        JSONArray jsonArray = JSONArray.parseArray(str);
        if (null == jsonArray || jsonArray.size() == 0) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        for (Object obj : jsonArray) {
            sb.append(((JSONObject) obj).get(keyString)).append("=").append(((JSONObject) obj).get(valueString)).append("&");
        }
        return sb.substring(0, sb.length() - 1);
    }

    /**
     * 按照逗号分隔jsonObj的key和value
     *
     * @param param
     * @param resultList
     * @return
     */
    private static List<Map<String, Object>> parseJsonParam(String param, List<Map<String, Object>> resultList) {
        param = param.substring(1, param.length() - 1);
        String[] paramArray = param.split(",");
        if (null != paramArray && paramArray.length > 0) {
            for (String keyAndVvalue : paramArray) {
                resultList = parseParamKeyAndValue(keyAndVvalue, resultList, ":");
            }
        }
        return resultList;
    }

    /**
     * 按照符号分割存储到Map键值对中
     *
     * @param param
     * @param resultList
     * @param symbol
     * @return
     */
    private static List<Map<String, Object>> parseParamKeyAndValue(String param, List<Map<String, Object>> resultList, String symbol) {
        if (!"".endsWith(param) && null != param) {
            if (param.contains(symbol)) {
                String[] result = param.split(symbol);
                Map<String, Object> map = new HashMap<>();
                String key = result[0];
                String value = "";
                if (result.length != 1) {
                    value = result[1];
                }
                if (key.contains("\"")) {
                    key = key.replaceAll("\"", "");
                }
                if (value.contains("\"")) {
                    value = value.replaceAll("\"", "");
                }
                map.put("key", key);
                map.put("value", value);
                resultList.add(map);
            }
        }
        return resultList;
    }

    /***
     * String 串变成 Map
     * @param interfaceStr
     * @return
     */
    public static Map<String, String> convertQueryString2Map(String interfaceStr) {
        String[] firstSplit = interfaceStr.split("&");
        HashMap<String, String> params = new HashMap<>(64);
        for (String firstStr : firstSplit) {
            String[] secSplit = firstStr.split("=");
            if (secSplit.length != 2) {
                continue;
            }
            String key = secSplit[0].trim();
            String val = secSplit[1].trim();
            if ("".equals(key)) {
                continue;
            }

            if (!params.containsKey(key)) {
                params.put(key, val);
            }
        }
        return params;
    }

    /**
     * 获取以当前时间为基准加减天数的格式化时间字符串
     * 例如： getFormatTimeStrByDiffDays(“yyyy-MM-dd”, 1)，今天时间为2019年1月23日，返回“2019-01-24”
     *
     * @param formatStr 格式化字符串
     * @param dayDiff   差异天数，可以为负数
     * @return 格式化后的字符串
     */
    public static String getFormatTimeStrByDiffDays(String formatStr, int dayDiff) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, dayDiff);
        return new SimpleDateFormat(formatStr).format(calendar.getTimeInMillis());
    }

    /**
     * 去除字符串中的空格、回车、换行符、制表符等
     *
     * @param str
     * @return
     */
    public static String replaceSpecialStr(String str) {
        String real = "";
        if (null != str) {
            Matcher m = PATTERN_SPECIAL_STRING.matcher(str);
            real = m.replaceAll(EMPTY_STRING);
        }
        return real;
    }

    /**
     * 获取字符串中的ip地址，没有为空list
     *
     * @param res
     * @return
     */
    public static List<String> getIpListFromString(String res) {
        List<String> list = new ArrayList<>();
        if (res != null && !res.equals(EMPTY_STRING)) {
            Matcher m = PATTERN_IPV4.matcher(res);

            while (m.find()) {
                list.add(m.group());
            }
        }
        return list;
    }

    /***
     * 读取绝对路径
     * @param path
     * @return
     */
    public static String handlePath(String path) {
        String begin = path.substring(0, 1);
        String last = path.substring(1);
        return begin + last.substring(0, last.indexOf("/"));
    }

    /**
     * 判断子字符串在父字符串中出现的次数
     *
     * @param string
     * @param subString
     * @return
     */
    public static Integer getSubstringCount(String string, String subString) {
        int count = 0;
        while (string.contains(subString)) {
            string = string.substring(string.indexOf(subString) + subString.length());
            count++;
        }
        return count;
    }

    public static String getSQLConnectionStr(String dbType, String ip) {
        String mysql = "MYSQL";
        if (isEmpty(ip)) {
            return "";
        } else if (mysql.equals(dbType.toUpperCase())) {
            return "jdbc:mysql://" + ip + ":3306/";
        } else {
            return "jdbc:oracle:thin:@" + ip + ":1521:";
        }
    }

    /***
     * String串变成JSON
     * @param interfaceStr
     * @return
     */
    public static net.sf.json.JSONObject convertInterfaceString(String interfaceStr) {
        HashMap<String, String> result = splitInterfaceString(interfaceStr);
        return net.sf.json.JSONObject.fromObject(result);
    }

    /***
     * String串变成Map
     * @param interfaceStr
     * @return
     */
    public static HashMap<String, String> splitInterfaceString(String interfaceStr) {
        String[] firstSplit = interfaceStr.split("&");
        HashMap<String, String> params = new HashMap<>(64);
        for (int i = 0; i < firstSplit.length; i++) {
            String firstStr = firstSplit[i];
            String[] secSplit = firstStr.split("=");
            if (secSplit.length != 2) {
                continue;
            }
            String key = secSplit[0].trim();
            String val = secSplit[1].trim();
            if ("".equals(key)) {
                continue;
            }

            if (!params.containsKey(key)) {
                params.put(key, val);
            }
        }
        return params;
    }


    /**
     * @Description 整数计算，保存2位小数点
     * @author
     */
    public static String getCountResult(int a, int b) {
        NumberFormat numberFormat = NumberFormat.getNumberInstance();
        numberFormat.setMaximumFractionDigits(2);
        String rate = numberFormat.format((float) a / (float) b * 100);

        return rate;
    }

    /**
     * 日期类型转化
     *
     * @param dateLong
     * @return
     */
    public static Date longToDate(long dateLong) {
        Date date = new Date(dateLong);
        return date;
    }

    /***
     * Json串变成String
     * @param json
     * @return
     */
    public static String stringToJson(net.sf.json.JSONObject json) {
        // 去除json串中的大括号
        String str = json.toString().replace("{", "").replace("}", "");
        String[] firstSplit = str.split(",");
        StringBuffer sb = new StringBuffer();
        for (String fs : firstSplit) {
            String appendStr = fs.replace(":", "=").replace("\"", "").trim() + "&";
            sb.append(appendStr);
        }

        return sb.toString().substring(0, sb.toString().length() - 1);
    }


    /**
     * 转义html中的标签
     * <  转以后  &lt;
     * >  转义后  &gt;
     *
     * @param str
     * @return
     */
    public static String transferHtml(String str) {
        return str.replace("<", "&lt;").replace(">", "&gt;");
    }

    /**
     * 时间戳转换成日期格式字符串
     *
     * @param seconds 精确到秒的字符串
     * @param format
     * @return
     */
    public static String seconds2Date(String seconds, String format) {
        if (seconds == null || seconds.isEmpty() || seconds.equals("")) {
            return "";
        }
        if (format == null || format.isEmpty()) {
            format = "yyyy-MM-dd HH:mm:ss";
        }
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        return sdf.format(new Date(Long.valueOf(seconds)));
    }

    /**
     * 将请求参数转化成Map类型，如果转化失败则返回null
     *
     * @param param 请求参数字符串
     * @return
     */
    public static Map convertParamToMap(String param) {
        Map resultMap = null;
        try {
            resultMap = JSON.parseObject(param);
        } catch (Exception e) {
            if (param.contains("=")) {
                resultMap = splitInterfaceString(param);
            }
        }
        return resultMap;
    }

    /**
     * 将map参数转成请求参数字符串类型
     * return: name=ccc&age=11
     */
    public static String convertMapToStr(Map<String, String> map) {
        String param = "";
        for (String key : map.keySet()) {
            param += key + "=" + map.get(key) + "&";
        }
        return param.substring(0, param.length()-1);
    }

    /**
     * 判断是否为数字格式不限制位数
     *
     * @param o
     * 待校验参数
     * @return 如果全为数字，返回true；否则，返回false
     */
    private static Pattern PATTERN = Pattern.compile("[0-9]*");

    public static boolean isNumber(Object o) {
        return PATTERN.matcher(String.valueOf(o)).matches();
    }


    /**
     * 格式化Har,json文件request里面的头
     *
     * @param header
     * @return
     */
    public static String formatHeaderArray2String(String header, String key, String value) {
        JSONArray headerArr = JSON.parseArray(header);
        StringBuffer headerStr = new StringBuffer("");
        for (Object h : headerArr) {
            JSONObject json = (JSONObject) h;
            headerStr.append(json.getString(key))
                    .append(":")
                    .append(json.getString(value))
                    .append("\n");
        }
        return headerStr.toString();
    }

    /**
     * 处理数据库里面的原始头字符串，包含\n换行符
     *
     * @param headerStr
     * @return Map<String       ,               String>
     */
    public static Map<String, String> convertHeaderString2Map(String headerStr) {
        if (isEmpty(headerStr)) {
            return new HashMap<>(0);
        }
        String[] entries = headerStr.split(";");
        int len = entries.length;
        Map<String, String> headerMap = new HashMap<>(5);
        for (int i = 0; i < len; i++) {
            int colonIndex = entries[i].indexOf(':');
            if (colonIndex <= 0) {
                continue;
            }
            headerMap.put(entries[i].substring(0, colonIndex).trim(), entries[i].substring(colonIndex + 1).trim());
        }
        return headerMap;
    }

    /**
     * 找出两个数组中相同的元素
     *
     * @param a 源数组
     * @param b 目标数组
     * @return
     */
    public static Set<Integer> getSame(Integer[] a, Integer[] b) {
        Set<Integer> same = new HashSet<>();
        Set<Integer> set = new HashSet<>();
        Collections.addAll(set, a);

        for (Integer aB : b) {
            if (!set.add(aB)) {
                same.add(aB);
            }
        }
        return same;
    }

    /**
     * 找出两个数组中不相同的元素
     *
     * @param source
     * @param target
     * @return
     */
    public static List<Integer> getDiff(List<Integer> source, List<Integer> target) {
        List<Integer> diff = new ArrayList<>();
        for (Integer t : source) {
            if (!target.contains(t)) {
                diff.add(t);
            }
        }
        return diff;
    }

    /**
     * 去掉字符串中\n 和 \t
     *
     * @param requestParam
     * @return
     */
    public static String replaceSymbol(String requestParam) {
        if (StringUtils.isEmpty(requestParam)) {
            return "";
        }
        return requestParam.replaceAll("\n", "").replaceAll("\t", "");
    }

    private static int compare(String str, String target) {
        int[][] d;              // 矩阵
        int n = str.length();
        int m = target.length();
        int i;                  // 遍历str的
        int j;                  // 遍历target的
        char ch1;               // str的
        char ch2;               // target的
        int temp;               // 记录相同字符,在某个矩阵位置值的增量,不是0就是1
        if (n == 0) {
            return m;
        }
        if (m == 0) {
            return n;
        }
        d = new int[n + 1][m + 1];
        // 初始化第一列
        for (i = 0; i <= n; i++) {
            d[i][0] = i;
        }
        // 初始化第一行
        for (j = 0; j <= m; j++) {
            d[0][j] = j;
        }
        for (i = 1; i <= n; i++) {
            // 遍历str
            ch1 = str.charAt(i - 1);
            // 去匹配target
            for (j = 1; j <= m; j++) {
                ch2 = target.charAt(j - 1);
                if (ch1 == ch2 || ch1 == ch2 + 32 || ch1 + 32 == ch2) {
                    temp = 0;
                } else {
                    temp = 1;
                }
                // 左边+1,上边+1, 左上角+temp取最小
                d[i][j] = min(d[i - 1][j] + 1, d[i][j - 1] + 1, d[i - 1][j - 1] + temp);
            }
        }
        return d[n][m];
    }

    /**
     * 获取最小的值
     */
    private static int min(int one, int two, int three) {
        return (one = one < two ? one : two) < three ? one : three;
    }

    /**
     * 获取两个字符串的相似度
     */
    public static float getSimilarityRatio(String str, String target) {
        int max = Math.max(str.length(), target.length());
        return 1 - (float) compare(str, target) / max;
    }

    /**
     * 计算字符串中某字符出现次数
     *
     * @param str
     * @param key
     * @return
     */
    public static int getSubString(String str, String key) {
        int count = 0;
        int index = 0;
        while ((index = str.indexOf(key, index)) != -1) {
            index = index + key.length();
            count++;
        }
        return count;
    }

    /**
     * 字符串转list
     * @param str
     * @return
     */
    public static List stringToList (String str ){
        //字符串转list<String>

        //此处为了将字符串中的空格去除做了一下操作
        List<String> result = Arrays.asList(str.split(","));
        return result;

    }

    /**
     * 获取当前周开始时间和结束时间的时间戳
     */
    public static JSONObject getCurrentWeekToStamp() {
        JSONObject obj = new JSONObject();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Calendar c = Calendar.getInstance();
            int weekday = c.get(Calendar.DAY_OF_WEEK) - 2;
            c.add(Calendar.DATE, -weekday);
            c.setTime(format.parse(format.format(c.getTime()) + " 00:00:00"));
            String weekBegin = stringToStamp(format.format(c.getTime())+ " 00:00:00");

            Calendar cal = Calendar.getInstance();
            int weekdaylast = cal.get(Calendar.DAY_OF_WEEK);
            //当前周结束时间
            cal.add(Calendar.DATE, 8 - weekdaylast);
            cal.setTime(format.parse(format.format(cal.getTime()) + " 23:59:59"));
            String weekEnd = stringToStamp(format.format(cal.getTime())+ " 23:59:59") ;

            obj.put("weekBegin", weekBegin);
            obj.put("weekEnd", weekEnd);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return obj;
    }

    /**
     * yyyy-mm-dd HH:mm:ss类型的字符串转换为时间戳
     *
     * @param tsStr
     * @return
     */
    public static String stringToStamp(String tsStr) {

        DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String ts;

        try {
            Date date = format.parse(tsStr);
            ts = String.valueOf(date.getTime());
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("日期格式转换失败！");

        }
        return ts;
    }


    /**
     * 判断字符串中是否包含中文
     * @param str
     * 待校验字符串
     * @return 是否为中文
     * @warn 不能校验是否为中文标点符号
     */
    public static boolean isContainChinese(String str) {
        Pattern p = Pattern.compile("[\u4e00-\u9fa5]");
        Matcher m = p.matcher(str);
        if (m.find()) {
            return true;
        }
        return false;
    }


}
