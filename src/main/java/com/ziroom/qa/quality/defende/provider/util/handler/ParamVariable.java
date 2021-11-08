package com.ziroom.qa.quality.defende.provider.util.handler;

/**
 * @Author: yinm5
 * @Description: 参数变量相关常量
 * @Date: 11:24 2018/11/30
 */
public interface ParamVariable {
    /**
     * 参数变量开始字符串
     */
    String VAR_START = "${";

    /**
     * 参数变量截止字符串
     */
    String VAR_END = "}";

    /**
     * 方法变量参数开始字符串
     */
    String METHOD_PARAM_START = "(";

    /**
     * 方法变量参数结束字符串
     */
    String METHOD_PARAM_END = ")";
}
