package com.ziroom.qa.quality.defende.provider.caseRepository.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ziroom.qa.quality.defende.provider.caseRepository.entity.AutoSingleAssert;
import com.ziroom.qa.quality.defende.provider.caseRepository.mapper.AutoSingleAssertMapper;
import com.ziroom.qa.quality.defende.provider.caseRepository.service.IAutoSingleAssertService;
import com.ziroom.qa.quality.defende.provider.execTask.service.IAutoExecutionRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author mybatisPlusAutoGenerate
 * @since 2021-09-23
 */
@Service
public class AutoSingleAssertServiceImpl extends ServiceImpl<AutoSingleAssertMapper, AutoSingleAssert> implements IAutoSingleAssertService {
    @Autowired
    private IAutoExecutionRecordService autoExecutionRecordService;

    public List<AutoSingleAssert> getAutoSingleAssertsByCaseId(Integer caseId) {
        QueryWrapper<AutoSingleAssert> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("case_id",caseId);
        List<AutoSingleAssert> list = list(queryWrapper);
        return list;
    }

}
