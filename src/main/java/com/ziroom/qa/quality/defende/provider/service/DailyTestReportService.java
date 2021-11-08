package com.ziroom.qa.quality.defende.provider.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ziroom.qa.quality.defende.provider.entity.DailyTestReport;
import com.ziroom.qa.quality.defende.provider.vo.Pagination;
import com.ziroom.qa.quality.defende.provider.vo.TestResultVo;
import com.ziroom.qa.quality.defende.provider.vo.dailyreport.DailyReportVo;

import java.util.List;

/**
 * <p>
 *  项目日报服务类
 * </p>
 *
 * @author liangh4
 * @since 2021-05-26
 */
public interface DailyTestReportService extends IService<DailyTestReport> {

    /**
     * 创建日报信息
     * @param userName 创建人
     * @param topicId 项目id
     * @return
     */
    TestResultVo createDailyTestReport(String userName,Long topicId) throws Exception;

    /**
     * 查询日报分页信息
     * @param pagination 分页信息
     * @return
     */
    Page<DailyTestReport> queryDailyTestReportPage(Pagination<DailyTestReport> pagination);

    /**
     * 显示日报详情信息
     * @param id 日报id
     * @return
     */
    DailyReportVo findDailyReportInfo(Long id) throws Exception;

    /**
     * 根据项目id获取日报列表
     * @param topicId
     * @return
     */
    List<DailyTestReport> getListByTopicId(Long topicId);

    /**
     * 根据项目id集合删除日报信息
     * @param topicIds
     * @return
     */
    TestResultVo delByTopicIds(List<Long> topicIds);

    /**
     * 获取日报邮件信息，如果没有则创建
     * @param userName
     * @param topicId
     * @param id
     * @return
     */
    DailyTestReport findDailyTestReportEmail(String userName, Long topicId, Long id);

    /**
     * 创建日报邮件信息，没有则创建
     * @param report
     * @return
     */
    TestResultVo saveDailyTestReportEmail(DailyTestReport report);

    /**
     * 发送邮件
     * @param userName
     * @param dailyId
     * @return
     */
    TestResultVo sendDailyTestReportEmail(String userName, Long dailyId) throws Exception;

    /**
     * 校验当日有没有创建日报
     * @param userName
     * @param topicId
     * @return
     */
    TestResultVo checkNewDailyReport(String userName, Long topicId);

    /**
     * 邮件预览
     * @param userName
     * @param dailyId
     * @return
     */
    TestResultVo emailPreview(String userName, Long dailyId)throws Exception;
}
