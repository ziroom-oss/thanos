package com.ziroom.qa.quality.defende.provider.result;


import com.alibaba.fastjson.JSONObject;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

@Data
public class RestResultVo<T> {

    private boolean success;
    private T data;
    private int code;
    private String message;
    private String errorMessage;
    private static final String STATUE = "status";
    private static final String SUCCESS = "success";

    public static RestResultVo fromErrorMessage(String message) {
        return new RestResultVo(false, message, CustomExceptionTypeEnum.SYSTEM_ERROR.getCode());
    }

    public static RestResultVo fromErrorMessage(String message, int code) {
        return new RestResultVo(false, message, code);
    }

    public static RestResultVo fromErrorMessage(CustomExceptionTypeEnum customExceptionTypeEnum) {
        return new RestResultVo(false, customExceptionTypeEnum.getDesc(), customExceptionTypeEnum.getCode());
    }

    public static RestResultVo fromSuccess(String message) {
        if (StringUtils.isBlank(message)) {
            message = SUCCESS;
        }
        return new RestResultVo(true, message, CustomExceptionTypeEnum.SUCCESS.getCode());
    }

    /**
     * @param result
     * @return
     */
    public static RestResultVo fromResult(JSONObject result) {
        if (result.get(STATUE).equals(SUCCESS)) {
            return RestResultVo.fromData(result);
        } else {
            return RestResultVo.fromErrorMessage(result.toString());
        }
    }

    public static <T> RestResultVo<T> fromData(T data) {
        return new RestResultVo<T>(true, data, CustomExceptionTypeEnum.SUCCESS.getCode());
    }

    public static <T> RestResultVo<T> fromData(boolean status, T data, int code) {
        return new RestResultVo<T>(status, data, code);
    }

    public RestResultVo() {
    }

    public RestResultVo(boolean success, String message, T data, int code) {
        this.success = success;
        this.message = message;
        this.data = data;
        this.code = code;
    }

    private RestResultVo(boolean success, String message, int code) {
        this.success = success;
        this.message = message;
        this.code = code;
    }

    private RestResultVo(boolean success, T data, int code) {
        this.success = success;
        this.data = data;
        this.code = code;
    }

}
