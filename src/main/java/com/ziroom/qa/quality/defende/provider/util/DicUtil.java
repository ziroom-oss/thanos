package com.ziroom.qa.quality.defende.provider.util;

import com.ziroom.qa.quality.defende.provider.entity.DataDictionary;
import com.ziroom.qa.quality.defende.provider.service.DataDictionaryService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 字典工具类
 */
@Slf4j
@Component
public class DicUtil {

    private static DataDictionaryService dataDictionaryService;

    @Autowired
    public DicUtil(DataDictionaryService dataDictionaryService) {
        DicUtil.dataDictionaryService = dataDictionaryService;
    }

    /**
     * 获取字典的值，key为中文name
     * @param type
     * @return
     */
    public static Map<String,String> dicListToMapName(String type){
        Map<String, String> dicMap = new HashMap<>();
        if(StringUtils.isBlank(type)){
            return dicMap;
        }
        List<DataDictionary> dicList = dataDictionaryService.queryDataDictionaryListByType(type);
        if(CollectionUtils.isNotEmpty(dicList)){
            dicMap = dicList.stream().collect(Collectors.toMap(DataDictionary::getName,DataDictionary::getEnglishName));
        }
        return dicMap;
    }

    /**
     * 获取字典的值，key为英文name
     * @param type
     * @return
     */
    public static Map<String,String> dicListToMapEnName(String type){
        Map<String, String> dicMap = new HashMap<>();
        if(StringUtils.isBlank(type)){
            return dicMap;
        }
        List<DataDictionary> dicList = dataDictionaryService.queryDataDictionaryListByType(type);
        if(CollectionUtils.isNotEmpty(dicList)){
            dicMap = dicList.stream().collect(Collectors.toMap(DataDictionary::getEnglishName,DataDictionary::getName));
        }
        return dicMap;
    }
}
