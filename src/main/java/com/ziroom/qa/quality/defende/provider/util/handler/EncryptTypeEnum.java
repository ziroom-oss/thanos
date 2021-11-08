package com.ziroom.qa.quality.defende.provider.util.handler;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author: yinm5
 * @Description: 加密类型枚举
 * @Date: 16:41 2018/6/13
 */
public enum EncryptTypeEnum {

    PAINT(0, "不加密", "", ""),
    GATEWAY(1, "网关加密", "", ""),
    SERVICE(2, "服务加密","2y5QfvAy", "hPtJ39Xs"),
    MINSU(3, "民宿加密", "2y5QfvAy", "hPtJ39Xs"),
    ZIROOMYI(4,"自如驿加密", "p", "sign");

    private int code;
    private String message;
    private String param;
    private String sign;
    private static final Map<Integer, EncryptTypeEnum> MAP = new HashMap<>();

    static {
        for (EncryptTypeEnum item : EncryptTypeEnum.values()) {
            MAP.put(item.code, item);
        }
    }

    EncryptTypeEnum(int code, String message, String param, String sign){
        this.code = code;
        this.message = message;
        this.param = param;
        this.sign = sign;
    }

    public static EncryptTypeEnum getByCode(int code){
        return MAP.get(code);
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public String getParam() {
        return param;
    }

    public String getSign() {
        return sign;
    }
}
