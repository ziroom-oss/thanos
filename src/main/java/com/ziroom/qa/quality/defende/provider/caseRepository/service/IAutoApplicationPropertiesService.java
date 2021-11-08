package com.ziroom.qa.quality.defende.provider.caseRepository.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ziroom.qa.quality.defende.provider.caseRepository.entity.AutoApplicationProperties;
import com.ziroom.qa.quality.defende.provider.caseRepository.entity.dto.AutoApplicationPropertiesDto;
import com.ziroom.qa.quality.defende.provider.caseRepository.entity.vo.AutoApplicationPropertiesVo;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author mybatisPlusAutoGenerate
 * @since 2021-10-12
 */
public interface IAutoApplicationPropertiesService extends IService<AutoApplicationProperties> {

    AutoApplicationPropertiesVo find(AutoApplicationPropertiesDto dto);

    String getApplicationName(String domain);

    Integer synchronizeApiByUrl(String userCode, String applicationName, String domain, String env);

    Integer synchronizeApiByApp(String applicationName, String env);
}
