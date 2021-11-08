package com.ziroom.qa.quality.defende.provider.util;

/**
 * @author zhujj5
 * @Title:
 * @Description:
 * @date 2021/10/20 11:01 上午
 */
public class StringUtil {
    /**
     * 判断str1中包含str2的个数
     * @param str1
     * @param str2
     * @return counter
     */
    public synchronized static int countStr(String str1, String str2) {
        if(str1==null||str2==null)
            return 0;
        String[] split = str1.split(str2);
        return split.length-1;
    }

    public static void main(String[] args) {
        String a ="{\"returnValue\":\"{\\\"_links\\\":{\\\"self\\\":{\\\"href\\\":\\\"http://yzo2oactivityreward.kq.ziroom.com/actuator\\\",\\\"templated\\\":false},\\\"health\\\":{\\\"href\\\":\\\"http://yzo2oactivityreward.kq.ziroom.com/actuator/health\\\",\\\"templated\\\":false},\\\"health-path\\\":{\\\"href\\\":\\\"http://yzo2oactivityreward.kq.ziroom.com/actuator/health/{*path}\\\",\\\"templated\\\":true},\\\"info\\\":{\\\"href\\\":\\\"http://yzo2oactivityreward.kq.ziroom.com/actuator/info\\\",\\\"templated\\\":false}}}\",\"returnStatusCode\":\"200\",\"requestURL\":\"http://10.216.4.19/actuator\",\"requestMethod\":\"GET\",\"responseHeader\":{\"Transfer-Encoding\":\"chunked\",\"Connection\":\"keep-alive\",\"Date\":\"Wed, 27 Oct 2021 11:09:50 GMT\",\"Content-Type\":\"application/vnd.spring-boot.actuator.v3+json\"}}";
        String b=":";
        System.out.println(countStr(a,b));
    }
}
