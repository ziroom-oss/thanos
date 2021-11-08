package com.ziroom.qa.quality.defende.provider.caseRepository.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ziroom.qa.quality.defende.provider.caseRepository.entity.AutoSingleApi;
import com.ziroom.qa.quality.defende.provider.caseRepository.entity.dto.HeaderUpdateByAppDto;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author mybatisPlusAutoGenerate
 * @since 2021-09-22
 */
public interface IAutoSingleApiService extends IService<AutoSingleApi> {

    List getApiTagsList(String appCode);

    void headerUpdateByAppDto(HeaderUpdateByAppDto dto);
}
