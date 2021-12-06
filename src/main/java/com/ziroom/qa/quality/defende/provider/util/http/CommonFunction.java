package com.ziroom.qa.quality.defende.provider.util.http;

import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Map.Entry;

public class CommonFunction {
    private static Logger logger = LoggerFactory.getLogger(CommonFunction.class);
    public static final String EMPTY_STRING = "";

    public CommonFunction() {
    }

    public static boolean isEmpty(String someStr) {
        return someStr == null || "".equals(someStr);
    }

    public static String toHexString(byte[] bytes, String separator) {
        StringBuilder hexString = new StringBuilder();
        byte[] var3 = bytes;
        int var4 = bytes.length;

        for(int var5 = 0; var5 < var4; ++var5) {
            byte b = var3[var5];
            String hex = Integer.toHexString(255 & b);
            if (hex.length() == 1) {
                hexString.append('0');
            }

            hexString.append(hex).append(separator);
        }

        return hexString.toString();
    }

    public static String toMd5(byte[] bytes) {
        try {
            MessageDigest algorithm = MessageDigest.getInstance("MD5");
            algorithm.reset();
            algorithm.update(bytes);
            return toHexString(algorithm.digest(), "");
        } catch (NoSuchAlgorithmException var2) {
            throw new RuntimeException(var2);
        }
    }

//    public static String getAppSign(String uid, String timeStamp) {
//        String key = "7srzT88FcNiRQA3n";
//        if (uid == null || uid.length() < 1) {
//            uid = "0";
//        }
//
//        String sign = toMd5((uid + timeStamp + key).getBytes());
//        return sign;
//    }

    public static String getTimeStampOf10() {
        Date date = new Date();
        String timestamp = String.valueOf(date.getTime()).substring(0, 10);
        return timestamp;
    }

    public static String getTimeStampOf13() {
        Date date = new Date();
        String timestamp = String.valueOf(date.getTime()).substring(0, 13);
        return timestamp;
    }

    public static String getCrmSign(Map<String, String> map) {
        Map<String, Object> m = new HashMap();
        Iterator var2 = map.keySet().iterator();

        while(var2.hasNext()) {
            String key = (String)var2.next();
            Object obj = map.get(key);
            m.put(key, obj);
        }

        String sign = CrmToMD5(m);
        return sign;
    }

    public static String CrmToMD5(Map<String, Object> map) {
        StringBuffer signCode = new StringBuffer();
        String[] keys = new String[map.size()];
        map.keySet().toArray(keys);

        int i;
        for(i = 0; i < keys.length; ++i) {
            for(int j = 0; j < keys.length - i - 1; ++j) {
                if (keys[j].compareTo(keys[j + 1]) > 0) {
                    String temp = keys[j];
                    keys[j] = keys[j + 1];
                    keys[j + 1] = temp;
                }
            }
        }

        for(i = 0; i < map.size(); ++i) {
            if (map.get(keys[i]) != null) {
                signCode.append(keys[i] + "=" + map.get(keys[i]));
            }
        }

        signCode.append("7srzT88FcNiRQA3n");
        String sign = MD5Util.md5Hex(signCode.toString());
        return sign;
    }

    public static String GetZzSign(Map<String, String> map) {
        StringBuffer signCode = new StringBuffer();
        String[] keys = new String[map.size()];
        map.keySet().toArray(keys);

        int i;
        for(i = 0; i < keys.length; ++i) {
            for(int j = 0; j < keys.length - i - 1; ++j) {
                if (keys[j].compareTo(keys[j + 1]) > 0) {
                    String temp = keys[j];
                    keys[j] = keys[j + 1];
                    keys[j + 1] = temp;
                }
            }
        }

        for(i = 0; i < map.size(); ++i) {
            if (map.get(keys[i]) != null) {
                if (i == map.size() - 1) {
                    signCode.append(keys[i] + "=" + map.get(keys[i]));
                } else {
                    signCode.append(keys[i] + "=" + map.get(keys[i]) + "&");
                }
            }
        }

        signCode.append("b29df42e480325d2e8fb6fa17fba037f");
        String sign = MD5Util.md5Hex(signCode.toString());
        return sign;
    }

    public static String jsonFieldSort(JSONObject jObject) {
        StringBuffer strMap = new StringBuffer();
        String[] keys = new String[jObject.size()];
        Iterator<?> iterator = jObject.keySet().iterator();
        String temp;
        for(int y = 0; iterator.hasNext(); ++y) {
            temp = (String)iterator.next();
            String value = jObject.getString(temp);
            keys[y] = '"' + temp + '"' + ":" + '"' + value + '"';
        }

        int i;
        for(i = 0; i < keys.length; ++i) {
            for(int j = 0; j < keys.length - i - 1; ++j) {
                if (keys[j].compareTo(keys[j + 1]) > 0) {
                    temp = keys[j];
                    keys[j] = keys[j + 1];
                    keys[j + 1] = temp;
                }
            }
        }

        strMap.append("{");

        for(i = 0; i < jObject.size(); ++i) {
            if (i == jObject.size() - 1) {
                strMap.append(keys[i].toString() + "}");
            } else {
                strMap.append(keys[i].toString() + ",");
            }
        }

        return strMap.toString();
    }

    public static ArrayList<String> stringToList(String jsonStr) {
        ArrayList<String> arrylist = new ArrayList();
        String[] arr = jsonStr.split("}");

        for(int i = 0; i < arr.length; ++i) {
            if (i == 0) {
                arrylist.add(arr[i] + "}");
            } else {
                arrylist.add(arr[i].substring(1, arr[i].length()) + "}");
            }
        }

        return arrylist;
    }

    public static String spliceUrl(Map<String, String> map) {
        StringBuffer sBufferUrl = new StringBuffer();
        Iterator var2 = map.entrySet().iterator();

        while(var2.hasNext()) {
            Entry<String, String> m = (Entry)var2.next();
            sBufferUrl.append(m.getKey() + "=" + m.getValue() + "&");
        }

        return sBufferUrl.toString();
    }

    public static String replaceString(String repString) {
        return repString == null ? "" : repString;
    }

    public static int getRandomNum(int max, int min) {
        Random random = new Random();
        int result = random.nextInt(max) % (max - min + 1) + min;
        System.out.println(result);
        return result;
    }

    public static void printMap(Map map) {
        Set<Entry<String, String>> entrySet = map.entrySet();
        Iterator it = entrySet.iterator();

        while(it.hasNext()) {
            Entry<String, String> entry = (Entry)it.next();
            String key = entry.getKey();
            String value = entry.getValue();
            logger.info("Map key: " + key + "\tMap value: " + value);
        }

    }

    public static String getNowDate(String formatType) {
        Date date = new Date();
        SimpleDateFormat df = new SimpleDateFormat(formatType);
        return df.format(date);
    }

    public static String getNowDateAddDay(String formatType, int plusOrMinusNum) {
        Date date = new Date();
        DateFormat sdf = new SimpleDateFormat(formatType);
        GregorianCalendar cal = new GregorianCalendar();
        cal.setTime(date);
        cal.add(1, 1);
        cal.add(5, -13);
        Date rtn = cal.getTime();
        return sdf.format(rtn.getTime());
    }

    public static String[] splitString(String input, String separator) {
        if (input != null && separator != null) {
            String[] retArrary = input.split(separator);
            String[] var3 = retArrary;
            int var4 = retArrary.length;

            for(int var5 = 0; var5 < var4; ++var5) {
                String str = var3[var5];
                logger.debug("split后的数据: " + str);
            }

            return retArrary;
        } else {
            throw new IllegalArgumentException("参数异常");
        }
    }

    public static String subString(String paraString, String beginStr, String endStr) {
        int a = paraString.indexOf(beginStr);
        int b = paraString.indexOf(endStr);
        return paraString.substring(a + 1, b);
    }

    public static String getDistanceTime(String str1, String str2) {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        long day = 0L;
        long hour = 0L;
        long min = 0L;
        long sec = 0L;
        long sss = 0L;

        try {
            Date one = df.parse(str1);
            Date two = df.parse(str2);
            long time1 = one.getTime();
            long time2 = two.getTime();
            long diff;
            if (time1 < time2) {
                diff = time2 - time1;
            } else {
                diff = time1 - time2;
            }

            day = diff / 86400000L;
            hour = diff / 3600000L - day * 24L;
            min = diff / 60000L - day * 24L * 60L - hour * 60L;
            sec = diff / 1000L - day * 24L * 60L * 60L - hour * 60L * 60L - min * 60L;
            sss = diff - day * 24L * 60L * 60L * 1000L - hour * 60L * 60L * 1000L - min * 60L * 1000L - sec * 1000L;
        } catch (Exception var21) {
            var21.printStackTrace();
        }

        return min + "分" + sec + "秒" + sss + "毫秒";
    }

    public static boolean charJudge(String[] arr, String targetValue) {
        String[] var2 = arr;
        int var3 = arr.length;

        for(int var4 = 0; var4 < var3; ++var4) {
            String s = var2[var4];
            if (s.equals(targetValue)) {
                return true;
            }
        }

        return false;
    }

    public static JSONObject formateResponse(JSONObject json) {
        JSONObject returnValue = JSONObject.parseObject(json.getString("returnValue"));
        json.remove("returnValue");
        json.putAll(returnValue);
        return json;
    }

    public static Map<String, String> stringToMap(String str) {
        Map<String, String> map = new HashMap();
        if (StringUtils.isEmpty(str)) {
            return map;
        } else {
            String[] firstSplit = str.split("&");
            new ArrayList();

            for(int i = 0; i < firstSplit.length; ++i) {
                String firstStr = firstSplit[i];
                String[] secSplit = firstStr.split("=");
                if (secSplit.length == 2) {
                    map.put(secSplit[0], secSplit[1]);
                } else {
                    map.put(secSplit[0], "");
                }
            }

            return map;
        }
    }

    public static Map<String, String> jsonToMap(String str) {
        Map<String, String> map = new HashMap();
        if (StringUtils.isEmpty(str)) {
            return map;
        } else {
            String removeStr = str.replace("{", "").replace("}", "");
            String[] firstSplit = removeStr.split(",");
            new ArrayList();

            for(int i = 0; i < firstSplit.length; ++i) {
                String firstStr = firstSplit[i];
                String[] secSplit = firstStr.split(":");
                if (secSplit.length == 2) {
                    map.put(secSplit[0].replace("\"", ""), secSplit[1].replace("\"", ""));
                } else {
                    map.put(secSplit[0].replace("\"", ""), "");
                }
            }

            System.out.print(map.toString());
            return map;
        }
    }

    public static String stringToJson(JSONObject json) {
        String str = json.toString().replace("{", "").replace("}", "");
        String[] firstSplit = str.split(",");
        StringBuffer sb = new StringBuffer();
        String[] var4 = firstSplit;
        int var5 = firstSplit.length;

        for(int var6 = 0; var6 < var5; ++var6) {
            String fs = var4[var6];
            String appendStr = fs.replace(":", "=").replace("\"", "").trim() + "&";
            sb.append(appendStr);
        }

        return sb.toString().substring(0, sb.toString().length() - 1);
    }
}

