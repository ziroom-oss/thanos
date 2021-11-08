package com.ziroom.qa.quality.defende.provider.result;

import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.AbstractJackson2HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

abstract public class WebAbstractResponseAdapter implements ResponseBodyAdvice<Object> {

    @Override
    public boolean supports(MethodParameter methodParameter,
                            Class<? extends HttpMessageConverter<?>> converterType) {
        //过滤String类型，String单独序列化方式
        return AbstractJackson2HttpMessageConverter.class.isAssignableFrom(converterType);
    }

    @Override
    public Object beforeBodyWrite(Object o,
                                  MethodParameter methodParameter,
                                  MediaType mediaType,
                                  Class<? extends HttpMessageConverter<?>> aClass,
                                  ServerHttpRequest serverHttpRequest,
                                  ServerHttpResponse serverHttpResponse) {
        // 兼容swagger调用接口
        if (serverHttpRequest.getURI().getPath().contains("/v2/api-docs")||serverHttpRequest.getURI().getPath().contains("swagger-resources")) {
            return o;
        }
        //包装后直接返回
        if (o instanceof RestResultVo) {
            return o;
        }
        return RestResultVo.fromData(o);
    }
}
