package com.ziroom.qa.quality.defende.provider.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ziroom.qa.quality.defende.provider.entity.TestTopic;
import com.ziroom.qa.quality.defende.provider.vo.Pagination;
import com.ziroom.qa.quality.defende.provider.vo.TestResultVo;
import com.ziroom.qa.quality.defende.provider.vo.dailyreport.TestTopicTypeVo;

import java.util.List;

public interface TestTopicService extends IService<TestTopic> {

    /**
     * 新增项目信息
     * @param testTopic 消息实体
     * @return
     */
    TestResultVo saveTestTopic(TestTopic testTopic);


    /**
     * 根据id获取信息
     * @param id id
     * @return
     */
    TestTopic findInfoById(Long id);

    /**
     * 初始化新增信息
     * @return
     */
    List<TestTopicTypeVo> initCreateInfo();

    /**
     * 修改信息
     * @param testTopic
     * @return
     */
    TestResultVo updateTestTopic(TestTopic testTopic);

    /**
     * 删除信息
     * @param idList
     * @return flag:true 通过 false 不通过
     *         msgRes: 原因
     */
    TestResultVo batchDeleteByIdList(List<Long> idList);

    /**
     * 校验项目标题是否可以通过（通过代表不重复可以新增）
     * @param id
     * @param topicName
     * @return flag:true 通过 false 不通过
     *         msgRes: 原因
     */
    TestResultVo checkNewTopicName(Long id,String topicName);

    /**
     * 分页获取信息集合
     * @param pagination 分页信息
     * @return
     */
    Page<TestTopic> queryListByPage(Pagination<TestTopic> pagination);

    /**
     * 查询项目信息下拉菜单
     * @param userName
     * @return
     */
    List<TestTopic> queryDropdownList(String userName);
}
