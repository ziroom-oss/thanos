package com.ziroom.qa.quality.defende.provider.util.handler;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.ziroom.qa.quality.defende.provider.util.GetMethodUtil;
import com.ziroom.qa.quality.defende.provider.util.TelotStringUtil;
import org.apache.commons.lang.math.NumberUtils;
import org.springframework.util.StringUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @Author:Elaine
 * @Description:
 * @Date: Created in 上午9:52 2018/8/17
 * @Version: 1.0
 */
public class MethodHandler {

    private static Pattern PATTERN_METHOD_VARIABLE =Pattern.compile("\\$\\{[^\\}\\{\\$]+?\\((.*?)\\)\\}");

     /**
     *返回替换后的参数
     * @param param 待替换的参数
     * @return 替换后的参数
     * @throws ClassNotFoundException
     * @throws NoSuchMethodException
     * @throws InvocationTargetException
     * @throws InstantiationException
     * @throws IllegalAccessException
     */
    public String replaceMethods(String param)
            throws ClassNotFoundException, InvocationTargetException, InstantiationException, IllegalAccessException {
        return replaceMethodsWithVariable(param, new HashMap<>(0));
    }

    /**
     * 根据方法名分解待处理字符串，返回公式方法实际参数列表
     * @param strUnderReplacing 待替换参数字符串
     * @param methodName 需要匹配的方法名
     * @return 公式方法实际参数列表
     */
    private String[] getMethodParams(String strUnderReplacing, String methodName){
        String replacedStrPrefix = ParamVariable.VAR_START + methodName;
        int paramStartIndex = strUnderReplacing.indexOf(replacedStrPrefix) + replacedStrPrefix.length() + 1;
        //取出公式方法参数
        String methodParamStr = strUnderReplacing.substring(paramStartIndex, strUnderReplacing.indexOf(")", paramStartIndex));
        String[] methodParams = StringUtils.isEmpty(methodParamStr)?new String[0]: methodParamStr.split(",");
        return methodParams;
    }

    /**
     * 根据方法名分解待处理字符串，返回待替换公式字符串
     * @param strUnderReplacing 待替换参数字符串
     * @param methodName 需要匹配的方法名
     * @return 待替换公式字符串（包含取值符号${}）
     */
    private String getMethodStrUnderReplaced(String strUnderReplacing, String methodName){
        String replacedStrPrefix = ParamVariable.VAR_START + methodName;
        int paramStartIndex = strUnderReplacing.indexOf(replacedStrPrefix) + replacedStrPrefix.length() + 1;
        String methodUnderReplaced = strUnderReplacing.substring(strUnderReplacing.indexOf(replacedStrPrefix), strUnderReplacing.indexOf("}", strUnderReplacing.indexOf(")", paramStartIndex)) + 1);
        return methodUnderReplaced;
    }

    /**
     * 执行公式方法
     * @param strUnderReplaced
     * @param className 类名
     * @param method 方法
     * @return
     * @throws ClassNotFoundException
     * @throws IllegalAccessException
     * @throws InstantiationException
     * @throws InvocationTargetException
     */
    private String replaceTheMethod(String strUnderReplaced, String className, Method method, Map paramPool)
            throws ClassNotFoundException, IllegalAccessException, InstantiationException, InvocationTargetException {
        Class targetClass = Class.forName(className);
        String result = null;
        Object formulaInstance = targetClass.newInstance();
        String methodName = method.getName();
        if(method.getParameterTypes().length == 0){
            result = String.valueOf(method.invoke(formulaInstance));
        } else {
            String[] paramsBeforeReplaced = getMethodParams(strUnderReplaced, methodName);
            Object[] realParams = (paramPool.size() == 0 ? paramsBeforeReplaced :replaceRealParams(paramsBeforeReplaced, paramPool));
            if(method.invoke(formulaInstance, realParams) instanceof List) {
                result = JSON.toJSONString(method.invoke(formulaInstance, realParams), SerializerFeature.WriteMapNullValue);
            } else {
                result = String.valueOf(method.invoke(formulaInstance, realParams));
            }
        }
        String str = getMethodStrUnderReplaced(strUnderReplaced, methodName);
        return result==null ? strUnderReplaced : strUnderReplaced.replace(str, result);
    }

    /**
     * 替换方法参数中的变量
     * @param originArr 未进行变量转换的方法参数数组
     * @param paramPool 进行替换的参数池
     * @return 转换后的参数数组
     */
    private String[] replaceRealParams(String[] originArr, Map paramPool){
        int len = originArr.length;
        String[] targetArr = new String[len];
        for(int i =0 ; i < len; i++){
            targetArr[i] = originArr[i];
            if(ApiHandler.hasVariable(targetArr[i])){
                if(paramPool.size()!=0){
                    targetArr[i] = TelotStringUtil.replaceAllVariablesWithoutExpression(targetArr[i],paramPool);
                }
            }
        }
        return targetArr;
    }

    /**
     * 进行替换方法参数列表中带参数的方法，返回替换后的字符串
     * @param param 参数字符串
     * @param paramPool 变量池
     * @return 替换后的参数。
     * @throws ClassNotFoundException
     * @throws InvocationTargetException
     * @throws InstantiationException
     * @throws IllegalAccessException
     */
    public String replaceMethodsWithVariable(String param, Map paramPool)
            throws ClassNotFoundException, InvocationTargetException, InstantiationException, IllegalAccessException {
        if(null == param || !param.contains(ParamVariable.VAR_START)){
            return param;
        }
        String result = param;
        Map<String, List> matchedMethodMap = findAllMatchedMethodsMap(param,true);
        Set<String> set = matchedMethodMap.keySet();
        for(String key : set){
            List methodInfoList = matchedMethodMap.get(key);
            result = replaceTheMethod(result, String.valueOf(methodInfoList.get(0)),
                    (Method)methodInfoList.get(1), paramPool);
        }
        return result;
    }

    /**
     * 查找出参数里面所有方法以及对于的支持的方法信息。
     * @param param 请求参数或者头
     * @return Map<String, List>,其中list为{String className, Method method}
     */
    private Map<String, List> findAllMatchedMethodsMap(String param,boolean needIf) {
        Matcher methodVariablesMatcher = PATTERN_METHOD_VARIABLE.matcher(param);
        Map<String, List> resultMap = new HashMap<>();
        Set<String> methodVariables = new HashSet<>();
        while(methodVariablesMatcher.find()){
            methodVariables.add(methodVariablesMatcher.group());
        }
        Map<String, Method[]> methodMap = GetMethodUtil.getMethodNames();
        Set<String> methodMapKeys = methodMap.keySet();
        for(String className : methodMapKeys){
            Method[] methods= methodMap.get(className);
            for (Method m : methods) {
                String methodName = m.getName();
                for (String methodVariable : methodVariables) {
//                    if (methodVariable.contains(ParamVariable.VAR_START + methodName)) {
                    if (Objects.equals(methodVariable.substring(0, methodVariable.indexOf('(')), ParamVariable.VAR_START + methodName)) {
                        if (needIf) {
                            if (m.getParameterTypes().length == getMethodParams(methodVariable, methodName).length) {
                                List tempList = new ArrayList<>();
                                tempList.add(className);
                                tempList.add(m);
                                resultMap.put(methodVariable, tempList);
                            }
                        } else {
                            List tempList = new ArrayList<>();
                            tempList.add(className);
                            tempList.add(m);
                            resultMap.put(methodVariable, tempList);
                        }
                    } else {
                        continue;
                    }
                }
            }
        }
        return resultMap;
    }


    /**
     * 获取方法返回值
     * @param apiReturnValue
     * @param assertStr
     * @param assertSubStr
     * @return
     * @throws ClassNotFoundException
     * @throws IllegalAccessException
     * @throws InstantiationException
     * @throws InvocationTargetException
     */
    public String returnMethodResult(String apiReturnValue, String assertStr,String assertSubStr)
            throws ClassNotFoundException, IllegalAccessException, InstantiationException, InvocationTargetException {
        String methodResult = "";
        Map<String, List> matchedMethodMap = findAllMatchedMethodsMap(assertStr,false);
        Set<String> set = matchedMethodMap.keySet();
        for (String key : set) {
            List methodInfoList = matchedMethodMap.get(key);

            methodResult = invokeMethod(String.valueOf(methodInfoList.get(0)),
                    (Method) methodInfoList.get(1), apiReturnValue,assertSubStr);
        }
        return methodResult;

    }

    /**
     * 智能断言通过反射调用方法
     * @param returnValue
     * @param expectResults
     * @param assertType
     * @return
     * @throws ClassNotFoundException
     * @throws NoSuchMethodException
     * @throws IllegalAccessException
     * @throws InstantiationException
     * @throws InvocationTargetException
     */
    public String smartAssertResult(String returnValue, String expectResults, Integer assertType)
            throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InstantiationException, InvocationTargetException {
        Class<?> aClass = Class.forName("com.ziroom.qa.telot.common.function.impl.Assert.SmartAssertion");
        Method method = aClass.getMethod("smartAssertResult", String.class, String.class, Integer.class);
        return (String)method.invoke(aClass.newInstance(), returnValue, expectResults, assertType);
    }

    /**
     * 传入指定参数，执行响应方法
     * @param className
     * @param method
     * @return
     * @throws ClassNotFoundException
     * @throws IllegalAccessException
     * @throws InstantiationException
     * @throws InvocationTargetException
     */

    private String invokeMethod(String className, Method method, String returnValue, String assertSubStr)
            throws ClassNotFoundException, IllegalAccessException, InstantiationException, InvocationTargetException {
            Class targetClass = Class.forName(className);
            Object formulaInstance = targetClass.newInstance();
            String result = (String)method.invoke(formulaInstance, returnValue,assertSubStr);
            return result ;
        }


    /**
     * 预处理生成sign
     * @param paramStr
     * @param preRequestStr
     * @return
     * @throws Exception
     */
    public String changeSign (String paramStr, String preRequestStr, Map totalMap) throws Exception {
        String appId = "";
        String secret = "";
        Map paramMap = TelotStringUtil.convertParamToMap(paramStr);
        //如果preRequest不为空，取出appId和secret(单接口)
        JSONObject preRequest = JSONObject.parseObject(preRequestStr);
        Set keySet = preRequest.keySet();
        //获取appId和secret
        for (Object key : keySet) {
            if(NumberUtils.isNumber((String)preRequest.get(key))) {
                if(totalMap != null) {
                    //多接口从公共参数里取
                    appId = (String) totalMap.get(key);
                    if(StringUtils.isEmpty(appId)) {
                        throw new RuntimeException("生成sign的过程找不到" + key + "字段");
                    }
                } else {
                    //单接口从预处理字段取
                    appId = (String)preRequest.get(key);
                }
            } else {
                if(totalMap != null) {
                    secret = (String) totalMap.get(key);
                    if(StringUtils.isEmpty(appId)) {
                        throw new RuntimeException("生成sign的过程找不到" + key + "字段");
                    }
                } else {
                    secret = (String)preRequest.get(key);
                }
            }
        }
        Class<?> aClass = Class.forName("com.ziroom.qa.telot.common.function.impl.BuildAppSign");
        Method method = aClass.getMethod("buildPreRequestSign", Map.class, String.class, String.class);
        String sign = (String)method.invoke(aClass.newInstance(), paramMap, appId, secret);
        paramMap.put("sign", sign);
        //参数恢复成之前的格式,JSON或者String
        if(TelotStringUtil.isJson(paramStr)) {
            return JSON.toJSONString(paramMap);
        } else {
            return TelotStringUtil.convertMapToStr(paramMap);
        }
    }
}

