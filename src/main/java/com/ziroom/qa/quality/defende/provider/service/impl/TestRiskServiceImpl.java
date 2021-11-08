package com.ziroom.qa.quality.defende.provider.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ziroom.qa.quality.defende.provider.constant.TestCenterConstants;
import com.ziroom.qa.quality.defende.provider.entity.TestRisk;
import com.ziroom.qa.quality.defende.provider.entity.TopicRiskRel;
import com.ziroom.qa.quality.defende.provider.mapper.TestRiskMapper;
import com.ziroom.qa.quality.defende.provider.service.TestRiskService;
import com.ziroom.qa.quality.defende.provider.service.TopicRiskRelService;
import com.ziroom.qa.quality.defende.provider.vo.TestResultVo;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

/**
 * <p>
 * 测试风险信息 服务实现类
 * </p>
 *
 * @author liangh4
 * @since 2021-06-02
 */
@Service
public class TestRiskServiceImpl extends ServiceImpl<TestRiskMapper, TestRisk> implements TestRiskService {

    @Autowired
    private TopicRiskRelService topicRiskRelService;

    /**
     * 新增
     *
     * @param testRisk
     * @return
     */
    @Override
    public TestResultVo saveTestRisk(TestRisk testRisk) {
        TestResultVo resultVo = this.checkTestRiskName(testRisk);
        if(!resultVo.getFlag()){
            return resultVo;
        }
        boolean resFlag = this.save(testRisk);
        return TestResultVo.builder().flag(resFlag).msgRes(resFlag?TestCenterConstants.RES_MSG_SUCCESS:TestCenterConstants.RES_MSG_ERROR).build();
    }

    /**
     * 修改
     *
     * @param testRisk
     * @return
     */
    @Override
    public TestResultVo updateTestRisk(TestRisk testRisk) {
        TestResultVo resultVo = this.checkTestRiskName(testRisk);
        if(!resultVo.getFlag()){
            return resultVo;
        }
        boolean resFlag = this.updateById(testRisk);
        return TestResultVo.builder().flag(resFlag).msgRes(resFlag?TestCenterConstants.RES_MSG_SUCCESS:TestCenterConstants.RES_MSG_ERROR).build();
    }

    /**
     * 批量删除
     *
     * @param idList
     * @return
     */
    @Override
    public TestResultVo delTeskRiskByIds(List<Long> idList) {
        if(CollectionUtils.isEmpty(idList)){
            return TestResultVo.builder().flag(false).msgRes(TestCenterConstants.RES_MSG_PARAMS_EMPTY).build();
        }
        List<TopicRiskRel> relList = topicRiskRelService.findListByRiskIds(idList);
        if(CollectionUtils.isNotEmpty(relList)){
            return TestResultVo.builder().flag(false).msgRes(TestCenterConstants.RES_MSG_RELATION).build();
        }
        boolean resFlag = super.removeByIds(idList);
        return TestResultVo.builder().flag(resFlag).msgRes(resFlag?TestCenterConstants.RES_MSG_SUCCESS:TestCenterConstants.RES_MSG_ERROR).build();
    }

    /**
     * 校验名称是否存在
     * @param testRisk
     * @return
     */
    private TestResultVo checkTestRiskName(TestRisk testRisk){
        if(Objects.isNull(testRisk)
                || StringUtils.isBlank(testRisk.getRiskName())
                || StringUtils.isBlank(testRisk.getRiskName().trim())){
            return TestResultVo.builder().flag(false).msgRes(TestCenterConstants.RES_MSG_PARAMS_EMPTY).build();
        }
        testRisk.setRiskName(testRisk.getRiskName().trim());
        QueryWrapper<TestRisk> infoQueryWrapper = new QueryWrapper<>();
        if(Objects.nonNull(testRisk.getId())){
            infoQueryWrapper.notIn("id",testRisk.getId());
        }
        infoQueryWrapper.eq("risk_name",testRisk.getRiskName());
        List<TestRisk> list = this.list(infoQueryWrapper);
        if(CollectionUtils.isNotEmpty(list)){
            return TestResultVo.builder().flag(false).msgRes(TestCenterConstants.RES_MSG_REPPEAT).build();
        }
        return TestResultVo.builder().flag(true).msgRes(TestCenterConstants.RES_MSG_SUCCESS).build();
    }
}
