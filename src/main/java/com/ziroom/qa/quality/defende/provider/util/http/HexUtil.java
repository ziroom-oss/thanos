package com.ziroom.qa.quality.defende.provider.util.http;

public class HexUtil {
    private static final String HEX_CHARS = "0123456789abcdef";

    public HexUtil() {
    }

    public static String toHexString(byte[] bytes) {
        StringBuilder buffer = new StringBuilder();
        byte[] arr$ = bytes;
        int len$ = bytes.length;

        for(int i$ = 0; i$ < len$; ++i$) {
            byte aByte = arr$[i$];
            buffer.append("0123456789abcdef".charAt(aByte >>> 4 & 15));
            buffer.append("0123456789abcdef".charAt(aByte & 15));
        }

        return buffer.toString();
    }

    public static byte[] toByteArray(String string) {
        byte[] buffer = new byte[string.length() / 2];
        int j = 0;

        for(int i = 0; i < buffer.length; ++i) {
            buffer[i] = (byte)(Character.digit(string.charAt(j++), 16) << 4 | Character.digit(string.charAt(j++), 16));
        }

        return buffer;
    }

    public static String appendParam(String returnStr, String paramId, String paramValue) {
        if (!returnStr.equals("")) {
            if (!paramValue.equals("")) {
                returnStr = returnStr + "&" + paramId + "=" + paramValue;
            }
        } else if (!paramValue.equals("")) {
            returnStr = paramId + "=" + paramValue;
        }

        return returnStr;
    }
}
