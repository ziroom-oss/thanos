package com.ziroom.qa.quality.defende.provider.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ziroom.qa.quality.defende.provider.entity.DataDictionary;
import com.ziroom.qa.quality.defende.provider.mapper.DataDictionaryMapper;
import com.ziroom.qa.quality.defende.provider.service.DataDictionaryService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class DataDictionaryServiceImpl extends ServiceImpl<DataDictionaryMapper, DataDictionary> implements DataDictionaryService {

    @Override
    public boolean saveOrUpdateDataDictionary(DataDictionary dataDictionary) {
        return saveOrUpdateDataDictionary(dataDictionary);
    }

    @Override
    public boolean batchSaveOrUpdateDataDictionary(List<DataDictionary> dataDictionaryList) {
        return saveOrUpdateBatch(dataDictionaryList);
    }

    @Override
    public List<DataDictionary> queryDataDictionaryListByType(String type) {
        QueryWrapper<DataDictionary> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("type", type);
        return this.list(queryWrapper);
    }

    @Override
    public DataDictionary queryDataDictionaryListByTypeAndKey(String type, String key) {
        QueryWrapper<DataDictionary> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("type", type);
        queryWrapper.eq("english_name", key);
        List<DataDictionary> list = this.list(queryWrapper);
        return CollectionUtils.isEmpty(list)?null:list.get(0);
    }
}
