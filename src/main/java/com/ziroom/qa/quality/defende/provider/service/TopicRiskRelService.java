package com.ziroom.qa.quality.defende.provider.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ziroom.qa.quality.defende.provider.entity.TopicRiskRel;
import com.ziroom.qa.quality.defende.provider.vo.TestResultVo;

import java.util.List;

/**
 * <p>
 * 项目风险关联 服务类
 * </p>
 *
 * @author liangh4
 * @since 2021-06-02
 */
public interface TopicRiskRelService extends IService<TopicRiskRel> {

    /**
     * 获取项目对应的关联关系
     * @param topicList
     * @return
     */
    List<TopicRiskRel> findListByTopicIds(List<Long> topicList);

    /**
     * 获取风险对应的关联关系
     * @param riskIdList
     * @return
     */
    List<TopicRiskRel> findListByRiskIds(List<Long> riskIdList);

    /**
     * 项目关联风险集合
     * @param topicId
     * @param riskIdList
     * @param userName
     * @return
     */
    TestResultVo saveRelInfo(Long topicId,List<Long> riskIdList,String userName);

    /**
     * 根据TopicIds取消关联
     * @param topicIds   关联id集合
     * @return
     */
    TestResultVo delRelByTopicIds(List<Long> topicIds);

    /**
     * 查询已关联数据
     * @param topicId
     * @return
     */
    List<TopicRiskRel> queryRelList(Long topicId);

    /**
     * 查询未关联数据
     * @param topicId
     * @return
     */
    List<TopicRiskRel> queryUnRelList(Long topicId);

    /**
     * 新增测试风险
     * @param userName
     * @param riskName
     * @param topicId
     * @return
     */
    TestResultVo createRiskInfo(String userName, String riskName, Long topicId);

    /**
     * 删除所有信息
     * @param userName
     * @param riskName
     * @param topicId
     */
    void delInfo(String userName, String riskName, Long topicId);
}
