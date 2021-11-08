package com.ziroom.qa.quality.defende.provider.util;

/**
 * @author zhujj5
 * @Title: 正则工具类
 * @Description:
 * @date 2021/9/26 3:18 下午
 */

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 正则表达式匹配两个字符串之间的内容
 * @author Administrator
 *
 */
public class RegexUtil {

    public static void main(String[] args) {
//        String str = "dasf{{aaa}}dsaf{{bb}}dsa";
//        //String str = "abc3443abcfgjhgabcgfjabc";
//        String rgex = "\\{\\{(.*?)\\}\\}";
//
//        System.out.println((new RegexUtil()).getSubUtil(str,rgex));
//        List<String> lists = (new RegexUtil()).getSubUtil(str,rgex);
//        for (String string : lists) {
//            System.out.println(string);
//        }
//        System.out.println((new RegexUtil()).getSubUtilSimple(str, rgex));
    }
    /**
     * 正则表达式匹配两个指定字符串中间的内容
     * @param soap
     * @return
     */
    public static List<String> getVariable(String soap){
        String rgex = "\\{\\{(.*?)\\}\\}";
        return getSubUtil(soap,rgex);
    }
    /**
     * 正则表达式匹配两个指定字符串中间的内容
     * @param soap
     * @return
     */
    public static List<String> getSubUtil(String soap,String rgex){
        List<String> list = new ArrayList<String>();
        Pattern pattern = Pattern.compile(rgex);// 匹配的模式
        Matcher m = pattern.matcher(soap);
        while (m.find()) {
            int i = 1;
            list.add(m.group(i));
            i++;
        }
        return list;
    }

    /**
     * 返回单个字符串，若匹配到多个的话就返回第一个，方法与getSubUtil一样
     * @param soap
     * @param rgex
     * @return
     */
    public static String getSubUtilSimple(String soap,String rgex){
        Pattern pattern = Pattern.compile(rgex);// 匹配的模式
        Matcher m = pattern.matcher(soap);
        while(m.find()){
            return m.group(1);
        }
        return "";
    }
}
