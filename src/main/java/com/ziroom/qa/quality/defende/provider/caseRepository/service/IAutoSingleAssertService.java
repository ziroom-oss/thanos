package com.ziroom.qa.quality.defende.provider.caseRepository.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ziroom.qa.quality.defende.provider.caseRepository.entity.AutoSingleAssert;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author mybatisPlusAutoGenerate
 * @since 2021-09-23
 */
public interface IAutoSingleAssertService extends IService<AutoSingleAssert> {

    List<AutoSingleAssert> getAutoSingleAssertsByCaseId(Integer caseId);

}
