package com.ziroom.qa.quality.defende.provider.caseRepository.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ziroom.qa.quality.defende.provider.caseRepository.entity.AutoSingleApiCase;
import com.ziroom.qa.quality.defende.provider.caseRepository.entity.dto.SingleApiCaseListDto;
import com.ziroom.qa.quality.defende.provider.caseRepository.entity.vo.SingleApiCaseListVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author mybatisPlusAutoGenerate
 * @since 2021-09-22
 */
public interface AutoSingleApiCaseMapper extends BaseMapper<AutoSingleApiCase> {

    List<SingleApiCaseListVO> findSingleApiListTableData(@Param("page") Page<SingleApiCaseListVO> page, @Param("entity") SingleApiCaseListDto dto);
}
