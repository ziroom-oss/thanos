package com.ziroom.qa.quality.defende.provider.util.encrypt;

public enum DesKeyEnum {
    GATEWAY("vpRZ1kmU", "EbpU4WtY"),
    SERVICE("vpRZ1kmU", "EbpU4WtY"),
    MINSU("vpRU1kmU", "EbpZ1WtY"),
    ZIROOMYI("ZiR00mYi", "vpRZ1kmU");

    private String key;
    private String iv;

    DesKeyEnum(String key, String iv) {
        this.key = key;
        this.iv = iv;
    }

    public String getKey() {
        return this.key;
    }

    public String getIv() {
        return this.iv;
    }
}