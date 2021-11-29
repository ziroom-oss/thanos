package com.ziroom.qa.quality.defende.provider.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ziroom.qa.quality.defende.provider.constant.QualityDefendeConstants;
import com.ziroom.qa.quality.defende.provider.constant.TestCenterConstants;
import com.ziroom.qa.quality.defende.provider.entity.DailyTestReport;
import com.ziroom.qa.quality.defende.provider.entity.TestTopic;
import com.ziroom.qa.quality.defende.provider.entity.TopicTaskRel;
import com.ziroom.qa.quality.defende.provider.mapper.TestTopicMapper;
import com.ziroom.qa.quality.defende.provider.outinterface.client.service.EhrService;
import com.ziroom.qa.quality.defende.provider.outinterface.client.service.MatrixService;
import com.ziroom.qa.quality.defende.provider.service.DailyTestReportService;
import com.ziroom.qa.quality.defende.provider.service.TestTopicService;
import com.ziroom.qa.quality.defende.provider.service.TopicRiskRelService;
import com.ziroom.qa.quality.defende.provider.service.TopicTaskRelService;
import com.ziroom.qa.quality.defende.provider.util.DicUtil;
import com.ziroom.qa.quality.defende.provider.vo.MatrixUserDetail;
import com.ziroom.qa.quality.defende.provider.vo.Pagination;
import com.ziroom.qa.quality.defende.provider.vo.TestResultVo;
import com.ziroom.qa.quality.defende.provider.vo.dailyreport.TestTopicTypeVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class TestTopicServiceImpl extends ServiceImpl<TestTopicMapper, TestTopic> implements TestTopicService {

    @Autowired
    private TopicTaskRelService topicTaskRelService;
    @Autowired
    private DailyTestReportService dailyTestReportService;
    @Autowired
    private TopicRiskRelService topicRiskRelService;
    @Autowired
    private EhrService ehrService;

    @Autowired
    private MatrixService matrixService;

    /**
     * 新增项目信息
     *
     * @param testTopic 消息实体
     * @return
     */
    @Override
    public TestResultVo saveTestTopic(TestTopic testTopic) {
        // 1. 校验入参是否为空
        if (Objects.isNull(testTopic)) {
            return TestResultVo.builder().flag(false).msgRes(TestCenterConstants.RES_MSG_APPROVED_EMPTY).build();
        }
        // 2. 校验项目名称是否相同
        TestResultVo checkVo = checkNewTopicName(testTopic.getId(), testTopic.getTopicName());
        if (!checkVo.getFlag()) {
            return checkVo;
        }
        // 3. 校验jiraid
//        TestResultVo jiraRes = JiraUtils.validateJiraInfo(testTopic.getRelationRequirement());
//        if (!jiraRes.getFlag()) {
//            return jiraRes;
//        }
        // 4. 对名称去除空格进行保存操作
        testTopic.setTopicName(testTopic.getTopicName().trim());
        boolean resFlag = this.save(testTopic);
        // 5. 去关联测试任务列表
        if (resFlag) {
            topicTaskRelService.saveTopicIdAndTaskIds(testTopic.getId(), testTopic.getTaskIdList(), testTopic.getUpdateUser());
        }
        return TestResultVo.builder().flag(resFlag).msgRes(resFlag ? TestCenterConstants.RES_MSG_SUCCESS : TestCenterConstants.RES_MSG_ERROR).data(testTopic.getId()).build();
    }

    /**
     * 根据id获取信息
     *
     * @param id id
     * @return
     */
    @Override
    public TestTopic findInfoById(Long id) {
        TestTopic topic = this.getById(id);
        if (Objects.isNull(topic)) {
            return null;
        }
        List<TopicTaskRel> taskRelList = topicTaskRelService.findByTopicIds(Arrays.asList(id));
        if (CollectionUtils.isNotEmpty(taskRelList)) {
            List<Long> taskRelIds = taskRelList.stream().map(TopicTaskRel::getTaskId).collect(Collectors.toList());
            topic.setTaskIdList(taskRelIds);
        }
        return topic;
    }

    /**
     * 初始化新增信息
     *
     * @return
     */
    @Override
    public List<TestTopicTypeVo> initCreateInfo() {
        Map<String, String> map = DicUtil.dicListToMapEnName(QualityDefendeConstants.DATA_DICTIONARY_STRATEGY_TYPE);
        if (MapUtils.isEmpty(map)) {
            return new ArrayList<>();
        }
        List<TestTopicTypeVo> list = new ArrayList<>();
        for (String key : map.keySet()) {
            TestTopicTypeVo vo = new TestTopicTypeVo();
            vo.setEnName(key);
            vo.setName(map.get(key));
            list.add(vo);
        }
        return list;
    }

    /**
     * 修改信息
     *
     * @param testTopic
     * @return
     */
    @Override
    public TestResultVo updateTestTopic(TestTopic testTopic) {
        // 1. 校验入参是否为空
        if (Objects.isNull(testTopic)) {
            return TestResultVo.builder().flag(false).msgRes(TestCenterConstants.RES_MSG_APPROVED_EMPTY).build();
        }
        // 2. 校验项目名称是否相同
        TestResultVo checkVo = checkNewTopicName(testTopic.getId(), testTopic.getTopicName());
        if (!checkVo.getFlag()) {
            return checkVo;
        }
        // 3. 校验jiraid
//        TestResultVo jiraRes = JiraUtils.validateJiraInfo(testTopic.getRelationRequirement());
//        if (!jiraRes.getFlag()) {
//            return jiraRes;
//        }
//        testTopic.setRelationRequirement(jiraRes.getData().toString());
        // 4. 对名称去除空格进行保存操作
        testTopic.setTopicName(testTopic.getTopicName().trim());
        boolean resFlag = this.updateById(testTopic);
        // 5. 去关联测试任务列表
        if (resFlag) {
            topicTaskRelService.saveTopicIdAndTaskIds(testTopic.getId(), testTopic.getTaskIdList(), testTopic.getUpdateUser());
        }
        return TestResultVo.builder().flag(resFlag).msgRes(resFlag ? TestCenterConstants.RES_MSG_SUCCESS : TestCenterConstants.RES_MSG_ERROR).build();
    }

    /**
     * 删除信息
     *
     * @param idList
     * @return flag:true 通过 false 不通过
     * msgRes: 原因
     */
    @Override
    public TestResultVo batchDeleteByIdList(List<Long> idList) {
        // 1. 校验入参是否为空
        if (CollectionUtils.isEmpty(idList)) {
            return TestResultVo.builder().flag(false).msgRes(TestCenterConstants.RES_MSG_APPROVED_EMPTY).build();
        }
        // 2. 删除有关联测试用例执行任务
        TestResultVo resultVo = topicTaskRelService.delTopicTaskListByTopicIds(idList);
        if (!resultVo.getFlag()) {
            return resultVo;
        }
        // 3. 删除有关联测试风险信息
        resultVo = topicRiskRelService.delRelByTopicIds(idList);
        if (!resultVo.getFlag()) {
            return resultVo;
        }
        // 4. 删除关联的日报信息
        resultVo = dailyTestReportService.delByTopicIds(idList);
        if (!resultVo.getFlag()) {
            return resultVo;
        }
        // 5. 进行删除
        boolean resFlag = this.removeByIds(idList);
        return TestResultVo.builder().flag(resFlag).msgRes(resFlag ? TestCenterConstants.RES_MSG_SUCCESS : TestCenterConstants.RES_MSG_ERROR).build();
    }

    /**
     * 校验项目标题是否可以通过（通过代表不重复可以新增）
     *
     * @param id
     * @param topicName
     * @return flag:true 通过 false 不通过
     * msgRes: 原因
     */
    @Override
    public TestResultVo checkNewTopicName(Long id, String topicName) {
        TestResultVo resultVo = null;
        if (Objects.isNull(topicName) || StringUtils.isBlank(topicName.trim())) {
            return TestResultVo.builder().flag(false).msgRes(TestCenterConstants.RES_MSG_PARAMS_EMPTY).build();
        }
        topicName = topicName.trim();
        QueryWrapper<TestTopic> infoQueryWrapper = new QueryWrapper<>();
        if (!Objects.isNull(id)) {
            infoQueryWrapper.notIn("id", id);
        }
        infoQueryWrapper.eq("topic_name", topicName);
        List<TestTopic> list = this.list(infoQueryWrapper);
        if (CollectionUtils.isEmpty(list)) {
            resultVo = TestResultVo.builder().flag(true).msgRes(TestCenterConstants.RES_MSG_SUCCESS).build();
        } else {
            resultVo = TestResultVo.builder().flag(false).msgRes(TestCenterConstants.RES_MSG_REPPEAT).build();
        }
        return resultVo;
    }

    /**
     * 分页获取信息集合
     *
     * @param pagination 分页信息
     * @return
     */
    @Override
    public Page<TestTopic> queryListByPage(Pagination<TestTopic> pagination) {
        //1. 分页查询参数处理
        TestTopic testTopic = pagination.getSearchObj();
        QueryWrapper<TestTopic> queryWrapper = this.getInfoQueryWrapper(testTopic);
        //2. 调用分页查询
        Page<TestTopic> page = this.page(pagination.getPage(), queryWrapper);
        //3. 加入日报详情信息
        List<TestTopic> testTopics = page.getRecords();
        this.assableDailyInfo4Topic(testTopics);
        return page;
    }

    /**
     * 查询项目信息下拉菜单
     *
     * @param userName
     * @return
     */
    @Override
    public List<TestTopic> queryDropdownList(String userName) {
        return super.list();
    }

    /**
     * 组织查询条件
     *
     * @param testTopic
     * @return
     */
    private QueryWrapper<TestTopic> getInfoQueryWrapper(TestTopic testTopic) {
        QueryWrapper<TestTopic> infoQueryWrapper = new QueryWrapper<>();
        infoQueryWrapper
                .like(StringUtils.isNotBlank(testTopic.getTopicName()), "topic_name", testTopic.getTopicName())
                .like(StringUtils.isNotBlank(testTopic.getRelationRequirement()), "relation_requirement", testTopic.getRelationRequirement())
        ;
        if (StringUtils.isNotBlank(testTopic.getTopicMaster())
                || StringUtils.isNotBlank(testTopic.getTopicParticipant())) {
            // 嵌套查询
            infoQueryWrapper.and(
                    QueryWrapper -> QueryWrapper.eq(StringUtils.isNotBlank(testTopic.getTopicMaster()), "topic_master", testTopic.getTopicMaster())
                            .or()
                            .like(StringUtils.isNotBlank(testTopic.getTopicParticipant()), "topic_participant", testTopic.getTopicParticipant())
            );
        }
        infoQueryWrapper.orderByDesc("id");
        return infoQueryWrapper;
    }

    /**
     * 给项目添加日报详情信息
     *
     * @param testTopics
     */
    private void assableDailyInfo4Topic(List<TestTopic> testTopics) {
        if (CollectionUtils.isNotEmpty(testTopics)) {
            List<String> emailPreList = this.getEmailPreByList(testTopics);
            Map<String, MatrixUserDetail> userMap = matrixService.getUserDetailByEmailPre(emailPreList);
            testTopics.stream().forEach(testTop -> {
                try {
                    List<DailyTestReport> dailyList = dailyTestReportService.getListByTopicId(testTop.getId());
                    if (CollectionUtils.isNotEmpty(dailyList)) {
                        Long dailyId = dailyList.get(0).getId();
                        testTop.setNewDailyId(dailyId);
                    }

                    testTop.setTopicMasterStr(Optional.ofNullable(userMap)
                            .map(item -> item.get(testTop.getTopicMaster()))
                            .map(detail -> detail.getUserName())
                            .orElse(""));
                    if (StringUtils.isNotBlank(testTop.getTopicParticipant())) {
                        String executors = "";
                        for (String emailPre : testTop.getTopicParticipant().split(",")) {
                            String userName = Optional.ofNullable(userMap)
                                    .map(item -> item.get(emailPre))
                                    .map(detail -> detail.getUserName())
                                    .orElse("");
                            if (StringUtils.isNotBlank(userName)) {
                                executors += userName + " ";
                            }
                        }
                        testTop.setTopicParticipantStr(executors);
                    }

                } catch (Exception e) {
                    log.info("查询项目日报信息出错 topicId=={}", testTop.getId(), e);
                }
            });
        }
    }

    /**
     * 获取用户邮箱前缀的集合
     *
     * @param taskList
     * @return
     */
    private List<String> getEmailPreByList(List<TestTopic> taskList) {
        List<String> emailPreList = new ArrayList<>();
        if (CollectionUtils.isEmpty(taskList)) {
            return emailPreList;
        }
        taskList.stream().forEach(topic -> {
            String name = topic.getTopicMaster();
            if (StringUtils.isNotBlank(name)) {
                emailPreList.add(name);
            }
            if (StringUtils.isNotBlank(topic.getTopicParticipant())) {
                String[] strs = topic.getTopicParticipant().split(",");
                emailPreList.addAll(Arrays.asList(strs));
            }
        });
        if (CollectionUtils.isEmpty(emailPreList)) {
            return emailPreList;
        }
        return emailPreList.stream().distinct().collect(Collectors.toList());
    }
}
