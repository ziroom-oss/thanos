package com.ziroom.qa.quality.defende.provider.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ziroom.qa.quality.defende.provider.entity.TestRisk;
import com.ziroom.qa.quality.defende.provider.vo.TestResultVo;

import java.util.List;

/**
 * <p>
 * 测试风险信息 服务类
 * </p>
 *
 * @author liangh4
 * @since 2021-06-02
 */
public interface TestRiskService extends IService<TestRisk> {

    /**
     * 新增
     * @param testRisk
     * @return
     */
    TestResultVo saveTestRisk(TestRisk testRisk);

    /**
     * 修改
     * @param testRisk
     * @return
     */
    TestResultVo updateTestRisk(TestRisk testRisk);

    /**
     * 批量删除
     * @param idList
     * @return
     */
    TestResultVo delTeskRiskByIds(List<Long> idList);

}
