package com.ziroom.qa.quality.defende.provider.util.handler;

import com.ziroom.qa.quality.defende.provider.util.encrypt.DESEncrypt;
import com.ziroom.qa.quality.defende.provider.util.encrypt.DesKeyEnum;
import com.ziroom.qa.quality.defende.provider.util.encrypt.MD5Util;

/**
 * @Author: yinm5
 * @Description: DES加解密处理器
 * @Date: 17:50 2018/6/13
 */
public class DesHandler {

    /**
     * 根据encryptType进行对应类型DES解密，EncryptTypeEnum类型名需与DesKeyEnum对应。
     * @param encryptedStr 加密文
     * @param encryptType 加密方式
     * @return 明文
     */
    public String decryptDES(String encryptedStr, EncryptTypeEnum encryptType){
        if(encryptType == EncryptTypeEnum.PAINT){
            return encryptedStr;
        }
        try {
            DesKeyEnum keyEnum = DesKeyEnum.valueOf(encryptType.toString());
            return DESEncrypt.decrypt(encryptedStr,keyEnum);
        }catch (Exception e){
            throw new RuntimeException("DES解密失败: "+encryptType.getMessage(),e);
        }
    }

    /**
     * 根据encryptType进行对应类型DES加密，EncryptTypeEnum类型名需与DesKeyEnum对应。
     * @param plainStr 明文
     * @param encryptType 加密方式
     * @return 加密文
     */
    public String encryptDES(String plainStr, EncryptTypeEnum encryptType){
        if(encryptType == EncryptTypeEnum.PAINT){
            return plainStr;
        }
        try {
            DesKeyEnum keyEnum = DesKeyEnum.valueOf(encryptType.toString());
            return DESEncrypt.encrypt(plainStr,keyEnum);
        }catch (Exception e){
            throw new RuntimeException("DES加密失败: "+encryptType.getMessage(), e);
        }
    }

    /**
     * 兼容服务及民宿特殊参数请求方式，请求参数加密后封装成格式： param=DES密文&sign=MD5密文
     * @param plainStr String类型请求参数
     * @param encryptType EncryptTypeEnum类型加密方式
     * @return String类型 param=DES密文&sign=MD5密文
     */
    public String wrapDesEncryptedStringByEncryptType(String plainStr, EncryptTypeEnum encryptType){
        if(encryptType == EncryptTypeEnum.PAINT){
            return plainStr;
        }
        try {
            DesKeyEnum keyEnum = DesKeyEnum.valueOf(encryptType.toString());
            String targetStr = DESEncrypt.encrypt(plainStr, keyEnum);
            if(!("").equals(encryptType.getParam())) {
                String enSign = MD5Util.MD5Encode(plainStr);
                targetStr = encryptType.getParam().concat("=").concat(targetStr).concat("&")
                        .concat(encryptType.getSign()).concat("=").concat(enSign);
            }
            return targetStr;
        }catch (Exception e){
            throw new RuntimeException("DES或MD5解密失败: "+encryptType.getMessage(), e);
        }
    }

    /**
     * 兼容服务，民宿自如驿请求参数直接解密。
     * @param str String类型的：param=DES密文&sign=MD5密文
     * @param encryptType EncryptTypeEnum类型加密方式
     * @return String类型明文
     */
    public String decryptWrappedDesStringByEncryptType(String str, EncryptTypeEnum encryptType){
        if(encryptType == EncryptTypeEnum.PAINT){
            return str;
        }
        try {
            DesKeyEnum keyEnum = DesKeyEnum.valueOf(encryptType.toString());
            if(encryptType == EncryptTypeEnum.GATEWAY){
                return DESEncrypt.decrypt(str,keyEnum);
            } else {
                String encryptedStr = str.substring(str.indexOf(encryptType.getParam())+encryptType.getParam().length()+1,
                        str.indexOf("&"+encryptType.getSign()));
                return DESEncrypt.decrypt(encryptedStr,keyEnum);
            }
        }catch (Exception e){
            throw new RuntimeException("DES解密失败: "+encryptType.getMessage(),e);
        }
    }
}
