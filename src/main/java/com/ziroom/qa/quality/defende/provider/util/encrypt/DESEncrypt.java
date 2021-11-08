package com.ziroom.qa.quality.defende.provider.util.encrypt;

import com.google.common.base.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.IvParameterSpec;

public class DESEncrypt {
    private static Logger logger = LoggerFactory.getLogger(DESEncrypt.class);
    private static final String keyString;
    private static final String ivString;

    public DESEncrypt() {
    }

    public static String encrypt(String content) {
        try {
            if (Strings.isNullOrEmpty(content)) {
                return null;
            } else {
                IvParameterSpec iv = new IvParameterSpec(ivString.getBytes());
                DESKeySpec dks = new DESKeySpec(keyString.getBytes());
                SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
                SecretKey key = keyFactory.generateSecret(dks);
                Cipher cipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
                cipher.init(1, key, iv);
                byte[] result = cipher.doFinal(content.getBytes("utf-8"));
                return DESPlus.byteArr2HexStr(result);
            }
        } catch (Exception var7) {
            logger.error("ENCRYPT ERROR:" + var7);
            return null;
        }
    }

    public static String decrypt(String content) {
        try {
            if (Strings.isNullOrEmpty(content)) {
                return null;
            } else {
                IvParameterSpec iv = new IvParameterSpec(ivString.getBytes());
                DESKeySpec dks = new DESKeySpec(keyString.getBytes());
                SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
                SecretKey key = keyFactory.generateSecret(dks);
                Cipher cipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
                cipher.init(2, key, iv);
                byte[] result = cipher.doFinal(DESPlus.hexStr2ByteArr(content));
                return new String(result, "utf-8");
            }
        } catch (Exception var7) {
            logger.error("ENCRYPT ERROR:" + var7);
            return null;
        }
    }

    public static String encrypt(String content, DesKeyEnum keyEnum) throws RuntimeException {
        try {
            if (Strings.isNullOrEmpty(content)) {
                return null;
            } else {
                IvParameterSpec iv = new IvParameterSpec(keyEnum.getIv().getBytes());
                DESKeySpec dks = new DESKeySpec(keyEnum.getKey().getBytes());
                SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
                SecretKey key = keyFactory.generateSecret(dks);
                Cipher cipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
                cipher.init(1, key, iv);
                byte[] result = cipher.doFinal(content.getBytes("utf-8"));
                return DESPlus.byteArr2HexStr(result);
            }
        } catch (Exception var8) {
            throw new RuntimeException("ENCRYPT ERROR:" + var8);
        }
    }

    public static String decrypt(String content, DesKeyEnum keyEnum) throws RuntimeException {
        try {
            if (Strings.isNullOrEmpty(content)) {
                return null;
            } else {
                IvParameterSpec iv = new IvParameterSpec(keyEnum.getIv().getBytes());
                DESKeySpec dks = new DESKeySpec(keyEnum.getKey().getBytes());
                SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
                SecretKey key = keyFactory.generateSecret(dks);
                Cipher cipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
                cipher.init(2, key, iv);
                byte[] result = cipher.doFinal(DESPlus.hexStr2ByteArr(content));
                return new String(result, "utf-8");
            }
        } catch (Exception var8) {
            throw new RuntimeException("ENCRYPT ERROR:" + var8);
        }
    }

    public static void main(String[] args) {
        String str = "b29f8942196125a57aaf20c96d255d6dd229a9b7855e13d4dd5cb805c7235d43b2dc1ada027a60d2b70bf55bb95ca7e2283ff73b6fd18120e8190815bdeb73b34ce480f04fb68729";
        System.out.println(decrypt(str, DesKeyEnum.MINSU));
    }

    static {
        keyString = DesKeyEnum.GATEWAY.getKey();
        ivString = DesKeyEnum.GATEWAY.getIv();
    }
}
