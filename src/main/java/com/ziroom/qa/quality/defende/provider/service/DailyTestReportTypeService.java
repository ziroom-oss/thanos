package com.ziroom.qa.quality.defende.provider.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ziroom.qa.quality.defende.provider.entity.DailyTestReportType;
import com.ziroom.qa.quality.defende.provider.execTask.entity.TestTask;
import com.ziroom.qa.quality.defende.provider.vo.TestResultVo;

import java.util.List;

/**
 * <p>
 *  日报类型详情服务类
 * </p>
 *
 * @author liangh4
 * @since 2021-05-26
 */
public interface DailyTestReportTypeService extends IService<DailyTestReportType> {

    /**
     * 组织日报统计数据(根据测试类型)
     *
     * @param testTask
     * @param dailyId
     * @param topicId
     * @param userName
     * @return
     */
    DailyTestReportType assableReportInfoByTask(TestTask testTask,Long dailyId,Long topicId,String userName);

    /**
     * 批量保存日报统计详情数据并删除旧数据
     * @param dailyId
     * @param reportTypes
     * @return
     */
    TestResultVo batchSaveAndDelReportInfo(Long dailyId,List<DailyTestReportType> reportTypes);

    /**
     * 根据日报id查询日报详情
     * @param dailyId
     * @return
     */
    List<DailyTestReportType> getListByDailyId(Long dailyId);
}
