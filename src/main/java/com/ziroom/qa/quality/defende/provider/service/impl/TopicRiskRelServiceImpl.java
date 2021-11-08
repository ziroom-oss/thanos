package com.ziroom.qa.quality.defende.provider.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ziroom.qa.quality.defende.provider.constant.TestCenterConstants;
import com.ziroom.qa.quality.defende.provider.entity.TestRisk;
import com.ziroom.qa.quality.defende.provider.entity.TopicRiskRel;
import com.ziroom.qa.quality.defende.provider.mapper.TopicRiskRelMapper;
import com.ziroom.qa.quality.defende.provider.service.TestRiskService;
import com.ziroom.qa.quality.defende.provider.service.TopicRiskRelService;
import com.ziroom.qa.quality.defende.provider.vo.TestResultVo;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * <p>
 * 项目风险关联 服务实现类
 * </p>
 *
 * @author liangh4
 * @since 2021-06-02
 */
@Service
public class TopicRiskRelServiceImpl extends ServiceImpl<TopicRiskRelMapper, TopicRiskRel> implements TopicRiskRelService {

    @Autowired
    private TestRiskService testRiskService;

    /**
     * 获取项目对应的关联关系
     *
     * @param topicList
     * @return
     */
    @Override
    public List<TopicRiskRel> findListByTopicIds(List<Long> topicList) {
        if(CollectionUtils.isEmpty(topicList)){
            return null;
        }
        QueryWrapper<TopicRiskRel> queryWrapper =  new QueryWrapper<>();
        queryWrapper.in("topic_id",topicList);
        return super.list(queryWrapper);
    }

    /**
     * 获取风险对应的关联关系
     *
     * @param riskIdList
     * @return
     */
    @Override
    public List<TopicRiskRel> findListByRiskIds(List<Long> riskIdList) {
        if(CollectionUtils.isEmpty(riskIdList)){
            return null;
        }
        QueryWrapper<TopicRiskRel> queryWrapper =  new QueryWrapper<>();
        queryWrapper.in("risk_id",riskIdList);
        return super.list(queryWrapper);
    }

    /**
     * 项目关联风险集合
     * @param topicId
     * @param riskIdList
     * @param userName
     * @return
     */
    @Override
    public TestResultVo saveRelInfo(Long topicId,List<Long> riskIdList,String userName){
        if(Objects.isNull(topicId)){
            return TestResultVo.builder().flag(false).msgRes(TestCenterConstants.RES_MSG_PARAMS_EMPTY).build();
        }
        //1.如果关联风险为空，则直接删除所有关联信息
        QueryWrapper<TopicRiskRel> queryWrapper =  new QueryWrapper<>();
        queryWrapper.eq("topic_id",topicId);
        if(CollectionUtils.isEmpty(riskIdList)){
            boolean resFlag = super.remove(queryWrapper);
            return TestResultVo.builder().flag(resFlag).msgRes(resFlag?TestCenterConstants.RES_MSG_SUCCESS:TestCenterConstants.RES_MSG_ERROR).build();
        }
        //2.比较需要关联是否跟已关联的相同
        // list去重，去除重复的riskid集合
        List<Long> myList = riskIdList.stream().distinct().collect(Collectors.toList());
        List<TopicRiskRel> relList = super.list(queryWrapper);
        if(CollectionUtils.isNotEmpty(relList)){
            List<Long> relRiskIdList = relList.stream().map(TopicRiskRel::getRiskId).collect(Collectors.toList());
            myList.sort(Comparator.comparing(Long::longValue));
            relRiskIdList.sort(Comparator.comparing(Long::longValue));
            // 如果传入的list跟已关联的list相同，则不做处理直接成功
            if(myList.toString().equals(relRiskIdList.toString())){
                return TestResultVo.builder().flag(true).msgRes(TestCenterConstants.RES_MSG_NOCHANGE_SUCCESS).build();
            }
        }
        //3.去关联新的风险id
        super.remove(queryWrapper);
        List<TopicRiskRel> batchList = new ArrayList<>();
        for (Long riskId:myList){
            if(Objects.nonNull(riskId)){
                TopicRiskRel rel = new TopicRiskRel();
                rel.setTopicId(topicId);
                rel.setRiskId(riskId);
                rel.setCreateTime(LocalDateTime.now());
                rel.setUpdateTime(LocalDateTime.now());
                rel.setCreateUser(userName);
                rel.setUpdateUser(userName);
                batchList.add(rel);
            }
        }
        boolean resFlag = super.saveBatch(batchList);
        return TestResultVo.builder().flag(resFlag).msgRes(resFlag?TestCenterConstants.RES_MSG_SUCCESS:TestCenterConstants.RES_MSG_ERROR).build();
    }

    /**
     * 根据TopicIds取消关联
     *
     * @param topicIds 关联id集合
     * @return
     */
    @Override
    public TestResultVo delRelByTopicIds(List<Long> topicIds) {
        if(CollectionUtils.isEmpty(topicIds)){
            return TestResultVo.builder().flag(true).msgRes(TestCenterConstants.RES_MSG_PARAMS_EMPTY).build();
        }
        QueryWrapper<TopicRiskRel> queryWrapper =  new QueryWrapper<>();
        queryWrapper.in("topic_id",topicIds);
        boolean resFlag = super.remove(queryWrapper);
        return TestResultVo.builder().flag(resFlag).msgRes(resFlag?TestCenterConstants.RES_MSG_SUCCESS:TestCenterConstants.RES_MSG_ERROR).build();
    }

    /**
     * 查询已关联数据
     *
     * @param topicId
     * @return
     */
    @Override
    public List<TopicRiskRel> queryRelList(Long topicId) {
        if(Objects.isNull(topicId)){
            return null;
        }
        QueryWrapper<TopicRiskRel> infoQueryWrapper = new QueryWrapper<>();
        infoQueryWrapper.eq("topic_id", topicId);
        List<TopicRiskRel> relList = super.list(infoQueryWrapper);
        if(CollectionUtils.isNotEmpty(relList)){
            for(TopicRiskRel rel:relList){
                TestRisk risk = testRiskService.getById(rel.getRiskId());
                if(Objects.nonNull(risk)){
                    rel.setTestRiskName(risk.getRiskName());
                }
            }
        }
        return relList;
    }

    /**
     * 查询未关联数据
     *
     * @param topicId
     * @return
     */
    @Override
    public List<TopicRiskRel> queryUnRelList(Long topicId) {
        if(Objects.isNull(topicId)){
            return null;
        }
        QueryWrapper<TopicRiskRel> infoQueryWrapper = new QueryWrapper<>();
        infoQueryWrapper.eq("topic_id", topicId);
        List<TopicRiskRel> relList = super.list(infoQueryWrapper);
        List<Long> riskIds = null;
        if(CollectionUtils.isNotEmpty(relList)){
            riskIds = relList.stream().map(TopicRiskRel::getRiskId).collect(Collectors.toList());
        }
        QueryWrapper<TestRisk> riskQueryWrapper = new QueryWrapper<>();
        if(CollectionUtils.isNotEmpty(riskIds)){
            infoQueryWrapper.notIn("id", riskIds);
        }
        List<TestRisk> riskList = testRiskService.list(riskQueryWrapper);
        relList = new ArrayList<>();
        for(TestRisk risk:riskList){
            TopicRiskRel rel = new TopicRiskRel();
            rel.setTopicId(topicId);
            rel.setRiskId(risk.getId());
            rel.setTestRiskName(risk.getRiskName());
            relList.add(rel);
        }
        return relList;
    }

    /**
     * 新增测试风险
     *
     * @param userName
     * @param riskName
     * @param topicId
     * @return
     */
    @Override
    public TestResultVo createRiskInfo(String userName, String riskName, Long topicId) {
        if (StringUtils.isBlank(userName)
                || StringUtils.isBlank(riskName)){
            return TestResultVo.builder().flag(false).msgRes(TestCenterConstants.RES_MSG_PARAMS_EMPTY).build();
        }

        TestRisk risk = new TestRisk();
        risk.setRiskName(riskName);
        risk.setCreateTime(LocalDateTime.now());
        risk.setCreateTime(LocalDateTime.now());
        risk.setUpdateTime(LocalDateTime.now());
        risk.setCreateUser(userName);
        risk.setUpdateUser(userName);
        TestResultVo resultVo = testRiskService.saveTestRisk(risk);
        if(!resultVo.getFlag()){
            if(TestCenterConstants.RES_MSG_REPPEAT.equals(resultVo.getMsgRes())){
                return TestResultVo.builder().flag(true).msgRes(TestCenterConstants.RES_MSG_NOCHANGE_SUCCESS).build();
            }
            return resultVo;
        }
        if(Objects.isNull(topicId)){
            return TestResultVo.builder().flag(true).msgRes("测试风险新增成功！").build();
        }
        TopicRiskRel rel = new TopicRiskRel();
        rel.setTopicId(topicId);
        rel.setRiskId(risk.getId());
        rel.setCreateTime(LocalDateTime.now());
        rel.setUpdateTime(LocalDateTime.now());
        rel.setCreateUser(userName);
        rel.setUpdateUser(userName);
        boolean resFlag = super.save(rel);
        return TestResultVo.builder().flag(resFlag).msgRes(resFlag?TestCenterConstants.RES_MSG_SUCCESS:TestCenterConstants.RES_MSG_ERROR).build();
    }

    /**
     * 删除所有信息
     *
     * @param userName
     * @param riskName
     * @param topicId
     */
    @Override
    public void delInfo(String userName, String riskName, Long topicId) {
        List<Long> riskIds = null;
        if(StringUtils.isNotBlank(riskName)){
            QueryWrapper<TestRisk> riskQueryWrapper = new QueryWrapper<>();
            riskQueryWrapper.like("risk_name", riskName);
            List<TestRisk> riskList = testRiskService.list(riskQueryWrapper);
            if(CollectionUtils.isNotEmpty(riskList)){
                riskIds = riskList.stream().map(TestRisk::getId).collect(Collectors.toList());
            }
        }
        if(Objects.nonNull(topicId)||CollectionUtils.isNotEmpty(riskIds)){
            QueryWrapper<TopicRiskRel> infoQueryWrapper = new QueryWrapper<>();
            infoQueryWrapper.eq(Objects.nonNull(topicId),"topic_id", topicId);
            infoQueryWrapper.in(CollectionUtils.isNotEmpty(riskIds),"risk_id", riskIds);
            super.remove(infoQueryWrapper);
            testRiskService.removeByIds(riskIds);
        }



    }
}
