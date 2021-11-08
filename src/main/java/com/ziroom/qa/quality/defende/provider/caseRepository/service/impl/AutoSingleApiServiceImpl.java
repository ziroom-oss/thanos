package com.ziroom.qa.quality.defende.provider.caseRepository.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ziroom.qa.quality.defende.provider.caseRepository.entity.AutoSingleApi;
import com.ziroom.qa.quality.defende.provider.caseRepository.entity.dto.HeaderUpdateByAppDto;
import com.ziroom.qa.quality.defende.provider.caseRepository.entity.vo.AutoSingleApiVo;
import com.ziroom.qa.quality.defende.provider.caseRepository.mapper.AutoSingleApiMapper;
import com.ziroom.qa.quality.defende.provider.caseRepository.service.IAutoSingleApiService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author mybatisPlusAutoGenerate
 * @since 2021-09-22
 */
@Service
public class AutoSingleApiServiceImpl extends ServiceImpl<AutoSingleApiMapper, AutoSingleApi> implements IAutoSingleApiService {

    @Override
    public List getApiTagsList(String appCode) {
        QueryWrapper<AutoSingleApi> queryWrapper =new QueryWrapper<>();
        queryWrapper.eq("application_name",appCode);
        List<AutoSingleApi> list = this.list(queryWrapper);
        Map<String, List<AutoSingleApi>> collect = list.stream().collect(Collectors.groupingBy(AutoSingleApi::getControllerName));
        Set<Map.Entry<String, List<AutoSingleApi>>> entries = collect.entrySet();
        ArrayList listRes =new ArrayList();
        entries.forEach(entry ->{
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("label",entry.getKey());
            List<AutoSingleApi> value = entry.getValue();
            List<AutoSingleApiVo> listVo =new ArrayList<>();
            value.forEach(item ->{
                AutoSingleApiVo vo = new AutoSingleApiVo();
                BeanUtils.copyProperties(item,vo);
                vo.setLabel(item.getRequestUri());
                listVo.add(vo);
            });
            jsonObject.put("children", listVo);
            listRes.add(jsonObject);
        });
        return listRes;
    }

    @Override
    public void headerUpdateByAppDto(HeaderUpdateByAppDto dto) {
        if(dto == null|| StringUtils.isEmpty(dto.getHeader())){
            return;
        }
        String headerNewAdd = dto.getHeader();
        String[] headerNewAddArray = headerNewAdd.split(";");
        QueryWrapper<AutoSingleApi> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("application_name",dto.getApplicationName());
        List<AutoSingleApi> list = this.list(queryWrapper);
        list.forEach(item ->{
            Optional.of(item.getHeader()).ifPresent(header ->{
                HashMap<String,Object> headerNew = new HashMap();
                Optional.ofNullable(header).ifPresent(headerTemp ->{
                    String[] splitOld = headerTemp.split(";");
//                旧的header
                    Arrays.stream(splitOld).forEach(kv ->{
                        if(kv.length()>0){
                            String[] kvArray = kv.split(":");
                            headerNew.put(kvArray[0],kvArray[1]);
                        }
                    });
                });

                //                新加入覆盖旧的
                Arrays.stream(headerNewAddArray).forEach(kv ->{
                    if(kv.length()>0){
                        String[] kvArray = kv.split(":");
                        headerNew.put(kvArray[0],kvArray[1]);
                    }
                });
                Set<Map.Entry<String, Object>> entries = headerNew.entrySet();
                StringBuffer stringBuffer = new StringBuffer();
                entries.forEach(entityNewKV ->{
                    stringBuffer.append(entityNewKV.getKey())
                            .append(":")
                            .append(entityNewKV.getValue())
                            .append(";");
                });
                String headerNewStr ;
                if(stringBuffer.lastIndexOf(";")>0){
                    headerNewStr = stringBuffer.substring(0,stringBuffer.length()-1);
                }else{
                    headerNewStr = stringBuffer.toString();
                }
//                更新header
                AutoSingleApi singleApiEntity = new AutoSingleApi();
                singleApiEntity.setId(item.getId());
                singleApiEntity.setHeader(headerNewStr);
                singleApiEntity.setUpdateTime(LocalDateTime.now());
                this.updateById(singleApiEntity);
            });
        });
    }
}
