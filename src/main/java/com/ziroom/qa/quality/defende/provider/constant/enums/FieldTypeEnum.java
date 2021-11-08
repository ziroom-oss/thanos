package com.ziroom.qa.quality.defende.provider.constant.enums;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public enum FieldTypeEnum {
    INPUT("input","单行文本框"),
    SELECT("select","下拉选单"),
    CHECKBOX("checkbox","多选框"),
    TEXTAREA("textarea","多行文本框");


    private String fieldType;
    private String fieldTypeLable;

    FieldTypeEnum(String fieldType, String fieldTypeLable) {
        this.fieldType = fieldType;
        this.fieldTypeLable = fieldTypeLable;
    }

    private static final Map<String, FieldTypeEnum> MAP = new HashMap();

    static {
        for (FieldTypeEnum item : FieldTypeEnum.values()) {
            MAP.put(item.fieldType,item);
        }
    }

    public static String getFieldTypeLableByKey(String fieldType){
        return MAP.get(fieldType).fieldTypeLable;
    }

    public static List<String> getApplicationTypeList(){
        return new ArrayList<>(MAP.keySet());
    }

    public String getFieldType() {
        return fieldType;
    }

    public String getFieldTypeLable() {
        return fieldTypeLable;
    }

    @Override
    public String toString() {
        return "FieldTypeEnum{" +
                "fieldType='" + fieldType + '\'' +
                ", fieldTypeName='" + fieldTypeLable + '\'' +
                '}';
    }
}

