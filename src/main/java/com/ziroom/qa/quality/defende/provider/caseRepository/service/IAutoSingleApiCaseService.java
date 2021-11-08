package com.ziroom.qa.quality.defende.provider.caseRepository.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ziroom.qa.quality.defende.provider.caseRepository.entity.AutoSingleApiCase;
import com.ziroom.qa.quality.defende.provider.caseRepository.entity.dto.AutoSingleApiCaseDto;
import com.ziroom.qa.quality.defende.provider.caseRepository.entity.dto.SingleApiCaseListDto;
import com.ziroom.qa.quality.defende.provider.caseRepository.entity.vo.SingleApiCaseListVO;
import com.ziroom.qa.quality.defende.provider.caseRepository.entity.vo.StaticTargetVo;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author mybatisPlusAutoGenerate
 * @since 2021-09-22
 */
public interface IAutoSingleApiCaseService extends IService<AutoSingleApiCase> {

    Page<SingleApiCaseListVO> findSingleApiListTableData(Integer pageNum, Integer pageSize, SingleApiCaseListDto dto);

    SingleApiCaseListVO getSingleApiCaseById(Integer caseId);

    String saveSingleApi(AutoSingleApiCaseDto dto);

    String saveApiAndCases(AutoSingleApiCaseDto dto);

    boolean caseIsExist(Integer id, AutoSingleApiCaseDto singleApiEnterDto);

    void deleteBatch(Integer apiId, String content);

    /**
     *
     * @param lastDateTime 统计从之前时间到现在区间内
     * @return
     */
    List<Map.Entry<String, StaticTargetVo>> staticAutoTestUsage(LocalDateTime lastDateTime);

    String sendStatic();
}
