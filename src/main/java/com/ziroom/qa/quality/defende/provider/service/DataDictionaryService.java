package com.ziroom.qa.quality.defende.provider.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ziroom.qa.quality.defende.provider.entity.DataDictionary;

import java.util.List;

public interface DataDictionaryService extends IService<DataDictionary> {
    boolean saveOrUpdateDataDictionary(DataDictionary dataDictionary);

    boolean batchSaveOrUpdateDataDictionary(List<DataDictionary> dataDictionaryList);

    List<DataDictionary> queryDataDictionaryListByType(String type);

    DataDictionary queryDataDictionaryListByTypeAndKey(String type,String key);
}
