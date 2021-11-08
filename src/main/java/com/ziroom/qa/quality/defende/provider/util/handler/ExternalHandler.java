package com.ziroom.qa.quality.defende.provider.util.handler;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.ziroom.qa.quality.defende.provider.util.TelotStringUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author: yinm5
 * @Description: 特殊流程处理，非正常通用流程处理，如无必要，不建议做处理
 * @Date: 11:53 2018/11/26
 */
public class ExternalHandler {

    private final String HANDLER_SYMBOL_START = "${$";
    private final String HANDLER_SYMBOL_END = "}";
    private final String LEFT_BRACKET = "[";
    private final String RIGHT_BRACKET = "]";
    private final String FROM_TO_SYMBOL = "|";
    private final String NVPS_SEPARATOR = "&";
    private final String NVPS_ASSIGN_SIGN = "=";

    /**
     * 对外统一处理参数方法，如需添加新处理方法，需在此方法中添加流程。
     * @param param
     * @return
     */
    public String handleParam(String param){
        if(param == null ||!param.contains(HANDLER_SYMBOL_START)){
            return param;
        }
        return handleSequentialRequestParams(param);
    }

    /**
     * 处理参数化逻辑
     * @param param
     * @return
     */
    private String handleSequentialRequestParams(String param){
        List<String> variableList = TelotStringUtil.getVariableNamesInStr(param, HANDLER_SYMBOL_START, HANDLER_SYMBOL_END);
        variableList.stream().filter(s -> !s.contains(LEFT_BRACKET) && !s.contains(RIGHT_BRACKET)).forEach(variableList::remove);
        if(variableList.isEmpty()){
            return param;
        }
        if(TelotStringUtil.isJson(param)){
            JSONObject json = JSON.parseObject(param);
            for(String expression : variableList) {
                String valueList = json.getString(HANDLER_SYMBOL_START+expression+HANDLER_SYMBOL_END);
                json.remove(HANDLER_SYMBOL_START+expression+HANDLER_SYMBOL_END);
                json.putAll(generateSequentialParam(expression, valueList));
            }
            return json.toString();
        }else {
            String result =param;
            for(String expression : variableList) {
                String strKey2BeReplaced = HANDLER_SYMBOL_START+expression+HANDLER_SYMBOL_END;
                int endOfExpression = result.indexOf(strKey2BeReplaced)+strKey2BeReplaced.length();
                String valueList = result.substring(endOfExpression+1, result.indexOf(NVPS_SEPARATOR, endOfExpression));
                Map<String, String> resultMap = generateSequentialParam(expression,valueList);
                StringBuffer sb = new StringBuffer("");
                for(String key : resultMap.keySet()){
                    sb.append(key+NVPS_ASSIGN_SIGN+resultMap.get(key)+NVPS_SEPARATOR);
                }
                result = result.replace(strKey2BeReplaced+NVPS_ASSIGN_SIGN+valueList+NVPS_SEPARATOR, sb);
            }
            return result;
        }
    }

    /**
     * 生成需要替换的参数序列
     * @param expression
     * @param valueList
     * @return
     */
    private Map<String, String> generateSequentialParam(String expression, String valueList){
        Map<String, String> resultMap = new HashMap<>(16);
        String numExp = expression.substring(expression.indexOf(LEFT_BRACKET)+1, expression.indexOf(RIGHT_BRACKET, expression.indexOf(LEFT_BRACKET)));
        String[] nums = numExp.split("\\"+FROM_TO_SYMBOL);
        String[] values = valueList.substring(1, valueList.length()-1).split(",");
        int start = Integer.valueOf(nums[0]);
        int end = Integer.valueOf(nums[1]);
        for(int i = start; i < end; i++){
            String key = expression.replace(numExp, String.valueOf(i));
            resultMap.put(key, values[i]);
        }
        return resultMap;
    }
}
