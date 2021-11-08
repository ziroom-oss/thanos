package com.ziroom.qa.quality.defende.provider.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ziroom.qa.quality.defende.provider.entity.DailyTestExeReport;
import com.ziroom.qa.quality.defende.provider.mapper.DailyTestExeReportMapper;
import com.ziroom.qa.quality.defende.provider.service.DailyTestExeReportService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

/**
 * <p>
 * 项目执行报表信息 服务实现类
 * </p>
 *
 * @author liangh4
 * @since 2021-06-09
 */
@Service
public class DailyTestExeReportServiceImpl extends ServiceImpl<DailyTestExeReportMapper, DailyTestExeReport> implements DailyTestExeReportService {

    /**
     * 根据任务id获取bug列表
     *
     * @param testTaskId
     * @return
     */
    @Override
    public List<DailyTestExeReport> getBugListByTaskId(Long testTaskId) {
        if(Objects.isNull(testTaskId)){
            return null;
        }
        QueryWrapper<DailyTestExeReport> infoQueryWrapper = new QueryWrapper<>();
        infoQueryWrapper.eq("task_id", testTaskId);
        infoQueryWrapper.isNotNull("bug_id");
        return super.list(infoQueryWrapper);
    }
}
