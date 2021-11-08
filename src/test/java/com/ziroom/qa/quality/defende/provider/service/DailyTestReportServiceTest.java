package com.ziroom.qa.quality.defende.provider.service;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ziroom.qa.quality.defende.provider.QualityDefendProviderApplication;
import com.ziroom.qa.quality.defende.provider.entity.DailyTestReport;
import com.ziroom.qa.quality.defende.provider.vo.Pagination;
import com.ziroom.qa.quality.defende.provider.vo.dailyreport.DailyReportVo;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

@Slf4j
@SpringBootTest(classes = QualityDefendProviderApplication.class,webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@RunWith(SpringRunner.class)
@ActiveProfiles("dev")
public class DailyTestReportServiceTest {
    @Autowired
    private DailyTestReportService dailyTestReportService;

    Long topicId = 94L;

    String userName = "liangh4";

    @Test
    public void createDailyTestReport() throws Exception {
//        dailyTestReportService.createDailyTestReport(userName,topicId);
        Pagination<DailyTestReport> pagination = new Pagination<>();
        DailyTestReport dtr = new DailyTestReport();
        dtr.setTopicId(topicId);
        Page page = new Page();
        pagination.setPage(page);
        pagination.setSearchObj(dtr);
        Page<DailyTestReport> newPage = dailyTestReportService.queryDailyTestReportPage(pagination);
        List<DailyTestReport> list = newPage.getRecords();
        Assert.assertEquals(1,list.size());

        DailyReportVo vo = dailyTestReportService.findDailyReportInfo(list.get(0).getId());
        Assert.assertEquals(list.get(0).getDailyDate(),vo.getDailyReportName());
        String sss = JSON.toJSONString(vo);
        System.out.println(sss);
    }
}
