package com.ziroom.qa.quality.defende.provider.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ziroom.qa.quality.defende.provider.constant.TestCenterConstants;
import com.ziroom.qa.quality.defende.provider.entity.TopicTaskRel;
import com.ziroom.qa.quality.defende.provider.execTask.entity.TestTask;
import com.ziroom.qa.quality.defende.provider.execTask.service.TestTaskService;
import com.ziroom.qa.quality.defende.provider.mapper.TopicTaskRelMapper;
import com.ziroom.qa.quality.defende.provider.service.TopicTaskRelService;
import com.ziroom.qa.quality.defende.provider.vo.TestResultVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.collections4.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class TopicTaskRelServiceImpl extends ServiceImpl<TopicTaskRelMapper, TopicTaskRel> implements TopicTaskRelService {

    @Autowired
    private TestTaskService testTaskService;

    /**
     * 根据项目id查询所有关联和未关联的测试执行任务列表
     *
     * @param topicId 项目id
     * @return
     */
    @Override
    public List<TopicTaskRel> queryAllRelAndUnRelTaskList(Long topicId) {
        if (Objects.isNull(topicId)) {
            //所有已经关联的用例执行任务
            List<TopicTaskRel> relList = super.list();
            List<Long> taskIdList = null;
            if (CollectionUtils.isNotEmpty(relList)) {
                taskIdList = relList.stream().map(TopicTaskRel::getTaskId).collect(Collectors.toList());
            }
            return this.getTopicTaskRelList(taskIdList, null, topicId);
        }
        // 1.查询关联的测试执行信息
        // 1.1 查询已关联的任务
        QueryWrapper<TopicTaskRel> infoQueryWrapper = new QueryWrapper<>();
        infoQueryWrapper.eq("topic_id", topicId);
        List<TopicTaskRel> relList = super.list(infoQueryWrapper);
        // 1.2 查询其他项目关联的任务
        infoQueryWrapper = new QueryWrapper<>();
        infoQueryWrapper.notIn("topic_id", topicId);
        List<TopicTaskRel> unrelList = super.list(infoQueryWrapper);

        // 2.查询已关联的测试执行任务信息
        Map<Long, Long> taskMap = new HashMap<>();
        if (CollectionUtils.isNotEmpty(relList)) {
            taskMap = relList.stream().collect(Collectors.toMap(TopicTaskRel::getTaskId, TopicTaskRel::getId));
        }
        // 3.查询排除被其他项目关联的测试执行任务信息
        List<Long> taskIdList = unrelList.stream().map(TopicTaskRel::getTaskId).collect(Collectors.toList());
        return this.getTopicTaskRelList(taskIdList, taskMap, topicId);
    }

    /**
     * 根据项目id查询
     *
     * @param topicIds 项目id集合
     * @return
     */
    @Override
    public List<TopicTaskRel> findByTopicIds(List<Long> topicIds) {
        if (CollectionUtils.isEmpty(topicIds)) {
            return null;
        }
        QueryWrapper<TopicTaskRel> infoQueryWrapper = new QueryWrapper<>();
        infoQueryWrapper.in("topic_id", topicIds);
        List<TopicTaskRel> relList = super.list(infoQueryWrapper);
        return relList;
    }

    /**
     * 根据任务id查询
     *
     * @param taskIds 任务id集合
     * @return
     */
    @Override
    public List<TopicTaskRel> findByTaskIds(List<Long> taskIds) {
        if (CollectionUtils.isEmpty(taskIds)) {
            return null;
        }
        QueryWrapper<TopicTaskRel> infoQueryWrapper = new QueryWrapper<>();
        infoQueryWrapper.in("task_id", taskIds);
        List<TopicTaskRel> relList = super.list(infoQueryWrapper);
        return relList;
    }

    /**
     * 根据TopicIds取消关联
     *
     * @param topicIds 关联id集合
     * @return
     */
    @Override
    public TestResultVo delTopicTaskListByTopicIds(List<Long> topicIds) {
        if (CollectionUtils.isEmpty(topicIds)) {
            return TestResultVo.builder().flag(true).msgRes(TestCenterConstants.RES_MSG_PARAMS_EMPTY).build();
        }
        QueryWrapper<TopicTaskRel> infoQueryWrapper = new QueryWrapper<>();
        infoQueryWrapper.in("topic_id", topicIds);
        boolean delFlag = super.remove(infoQueryWrapper);
        return TestResultVo.builder().flag(delFlag).msgRes(delFlag ? TestCenterConstants.RES_MSG_SUCCESS : TestCenterConstants.RES_MSG_ERROR).build();
    }

    /**
     * 关联项目和任务id集合
     *
     * @param topicId    项目id
     * @param taskIdList 测试执行任务id集合
     * @param userName
     * @return
     */
    @Override
    public TestResultVo saveTopicIdAndTaskIds(Long topicId, List<Long> taskIdList, String userName) {
        if (Objects.isNull(topicId) && CollectionUtils.isEmpty(taskIdList)) {
            return TestResultVo.builder().flag(false).msgRes(TestCenterConstants.RES_MSG_PARAMS_EMPTY).build();
        } else if (Objects.nonNull(topicId) && CollectionUtils.isEmpty(taskIdList)) {
            QueryWrapper<TopicTaskRel> infoQueryWrapper = new QueryWrapper<>();
            infoQueryWrapper.eq("topic_id", topicId);
            this.remove(infoQueryWrapper);
            return TestResultVo.builder().flag(true).msgRes(TestCenterConstants.RES_MSG_NOCHANGE_SUCCESS).build();
        } else if (Objects.isNull(topicId) && CollectionUtils.isNotEmpty(taskIdList)) {
            QueryWrapper<TopicTaskRel> infoQueryWrapper = new QueryWrapper<>();
            infoQueryWrapper.in("task_id", taskIdList);
            this.remove(infoQueryWrapper);
            return TestResultVo.builder().flag(true).msgRes(TestCenterConstants.RES_MSG_NOCHANGE_SUCCESS).build();
        }
        //1.校验是否包含已关联的任务（如果已经关联则需要排除掉）
        List<Long> saveTaskList = this.getSaveTaskList(topicId, taskIdList);
        if (CollectionUtils.isEmpty(saveTaskList)) {
            return TestResultVo.builder().flag(true).msgRes(TestCenterConstants.RES_MSG_NOCHANGE_SUCCESS).build();
        }
        //2.组织数据去保存
        List<TopicTaskRel> saveList = this.getSaveList(saveTaskList, topicId, userName);
        boolean saveFlag = super.saveBatch(saveList);
        return TestResultVo.builder().flag(saveFlag).msgRes(saveFlag ? TestCenterConstants.RES_MSG_SUCCESS : TestCenterConstants.RES_MSG_ERROR).build();
    }

    /**
     * 测试执行关联项目
     *
     * @param topicId  项目id
     * @param taskId   测试执行任务id
     * @param userName
     * @return
     */
    @Override
    public void taskRelTopic(Long topicId, Long taskId, String userName) {
        QueryWrapper<TopicTaskRel> infoQueryWrapper = new QueryWrapper<>();
        if (Objects.isNull(topicId)) {
            infoQueryWrapper.eq("task_id", taskId);
            super.remove(infoQueryWrapper);
            return;
        }
        infoQueryWrapper = new QueryWrapper<>();
        infoQueryWrapper.eq("topic_id", topicId);
        infoQueryWrapper.eq("task_id", taskId);
        List<TopicTaskRel> relList = super.list(infoQueryWrapper);
        if (CollectionUtils.isNotEmpty(relList)) {
            return;
        }
        super.saveBatch(this.getSaveList(Arrays.asList(taskId), topicId, userName));
    }

    /**
     * 给前端返回需要查询的关联关系
     *
     * @param taskIdlist
     * @param taskMap
     * @param topicId
     * @return
     */
    private List<TopicTaskRel> getTopicTaskRelList(List<Long> taskIdlist, Map<Long, Long> taskMap, Long topicId) {
        // 1.查询排除被其他项目关联的测试执行任务信息
        List<TopicTaskRel> list = new ArrayList<>();
        QueryWrapper<TestTask> taskQueryWrapper = new QueryWrapper<>();
        if (CollectionUtils.isNotEmpty(taskIdlist)) {
            taskQueryWrapper.notIn("id", taskIdlist);
        }
        taskQueryWrapper.orderByDesc("id");
        List<TestTask> taskList = testTaskService.list(taskQueryWrapper);
        // 2.组装关联的数据
        for (TestTask task : taskList) {
            TopicTaskRel taskRel = new TopicTaskRel();
            if (MapUtils.isNotEmpty(taskMap) && Objects.nonNull(taskMap.get(task.getId()))) {
                taskRel.setId(taskMap.get(task.getId()));
                taskRel.setRelFlag(true);
            }
            taskRel.setTaskId(task.getId());
            taskRel.setTestTaskName(task.getTaskName());
            taskRel.setTopicId(topicId);
            list.add(taskRel);
        }
        return list;
    }

    /**
     * 获取需要保存的测试任务id集合
     *
     * @param topicId
     * @param taskIdList
     * @return
     */
    private List<Long> getSaveTaskList(Long topicId, List<Long> taskIdList) {
        QueryWrapper<TopicTaskRel> infoQueryWrapper = new QueryWrapper<>();
        infoQueryWrapper.eq("topic_id", topicId);
        List<TopicTaskRel> list = super.list(infoQueryWrapper);
        List<Long> saveTaskList = new ArrayList<>();
        if (CollectionUtils.isEmpty(list)) {
            saveTaskList = taskIdList;
        } else {
            // 判断是否变更了关联
            List<Long> oldTaskList = list.stream().map(TopicTaskRel::getTaskId).collect(Collectors.toList());
            boolean isequal = ListUtils.isEqualList(oldTaskList, taskIdList);
            // 如果变更了关联，则删除现有关联的信息
            if (!isequal) {
                this.removeByIds(list.stream().map(TopicTaskRel::getId).collect(Collectors.toList()));
                saveTaskList = taskIdList;
            }
        }
        return saveTaskList;
    }

    /**
     * 获取需要保存的关联列表
     *
     * @param saveTaskList
     * @param topicId
     * @param userName
     * @return
     */
    private List<TopicTaskRel> getSaveList(List<Long> saveTaskList, Long topicId, String userName) {
        List<TopicTaskRel> saveList = new ArrayList<>();
        for (Long taskId : saveTaskList) {
            TopicTaskRel rel = new TopicTaskRel();
            rel.setTopicId(topicId);
            rel.setTaskId(taskId);
            rel.setCreateUser(userName);
            rel.setCreateTime(LocalDateTime.now());
            rel.setUpdateUser(userName);
            rel.setUpdateTime(LocalDateTime.now());
            saveList.add(rel);
        }
        return saveList;
    }

}
