package com.ziroom.qa.quality.defende.provider.service;

import com.ziroom.qa.quality.defende.provider.entity.DailyTestExeReport;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 项目执行报表信息 服务类
 * </p>
 *
 * @author liangh4
 * @since 2021-06-09
 */
public interface DailyTestExeReportService extends IService<DailyTestExeReport> {

    /**
     * 根据任务id获取bug列表
     * @param testTaskId
     * @return
     */
    List<DailyTestExeReport> getBugListByTaskId(Long testTaskId);
}
