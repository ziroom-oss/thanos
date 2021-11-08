package com.ziroom.qa.quality.defende.provider.caseRepository.entity.dto;

import com.ziroom.qa.quality.defende.provider.caseRepository.entity.AutoApplicationProperties;
import lombok.Data;

@Data
/**
 * @author zhujj5
 * @Title:
 * @Description:
 * @date 2021/10/12 2:23 下午
 */
public class AutoApplicationPropertiesDto extends AutoApplicationProperties {
    private String env;
}
