package com.ziroom.qa.quality.defende.provider.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ziroom.qa.quality.defende.provider.entity.TopicTaskRel;
import com.ziroom.qa.quality.defende.provider.vo.TestResultVo;

import java.util.List;

public interface TopicTaskRelService extends IService<TopicTaskRel> {

    /**
     * 根据项目id查询所有关联和未关联的测试执行任务列表
     *
     * @param topicId 项目id
     * @return
     */
    List<TopicTaskRel> queryAllRelAndUnRelTaskList(Long topicId);

    /**
     * 根据项目id查询
     *
     * @param topicIds 项目id集合
     * @return
     */
    List<TopicTaskRel> findByTopicIds(List<Long> topicIds);

    /**
     * 根据任务id查询
     *
     * @param taskIds 任务id集合
     * @return
     */
    List<TopicTaskRel> findByTaskIds(List<Long> taskIds);

    /**
     * 根据TopicIds取消关联
     * @param topicIds   关联id集合
     * @return
     */
    TestResultVo delTopicTaskListByTopicIds(List<Long> topicIds);

    /**
     * 关联项目和任务id集合
     *
     * @param topicId    项目id
     * @param taskIdList 测试执行任务id集合
     * @param userName
     * @return
     */
    TestResultVo saveTopicIdAndTaskIds(Long topicId, List<Long> taskIdList, String userName);

    /**
     * 测试执行关联项目
     *
     * @param topicId 项目id
     * @param taskId 测试执行任务id
     * @param userName
     * @return
     */
    void taskRelTopic(Long topicId, Long taskId, String userName);

}
