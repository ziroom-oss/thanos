package com.ziroom.qa.quality.defende.provider.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ziroom.qa.quality.defende.provider.entity.TestMessage;
import com.ziroom.qa.quality.defende.provider.vo.Pagination;
import com.ziroom.qa.quality.defende.provider.vo.TestMessageInfoVo;
import com.ziroom.qa.quality.defende.provider.vo.TestResultVo;

import java.util.List;

public interface TestMessageService extends IService<TestMessage> {

    /**
     * 新增message消息信息
     * @param testMessage 消息实体
     * @return
     */
    boolean insertTestMessage(TestMessage testMessage);


    /**
     * 根据用户id获取用户未读取的消息
     * @param userName 用户userName
     * @return
     */
    TestMessageInfoVo findInfoByUserName(String userName);

    /**
     * 批量去读取消息
     * @param mesIdList 消息主体id集合
     * @param userName 用户userName
     * @return
     */
    TestResultVo batchReadMessage(List<Long> mesIdList, String userName);

    /**
     * 修改消息
     * @param testMessage
     * @return
     */
    boolean updateTestMessage(TestMessage testMessage);

    /**
     * 删除消息
     * @param msgIdList
     * @return flag:true 通过 false 不通过
     *         msgRes: 原因
     */
    TestResultVo deleteTestMessage(List<Long> msgIdList);

    /**
     * 校验消息标题是否可以通过（通过代表不重复可以新增）
     * @param msgId
     * @param msgTitle
     * @return flag:true 通过 false 不通过
     *         msgRes: 原因
     */
    TestResultVo checkNewMessageTitle(Long msgId,String msgTitle);

    /**
     * 分页获取用户消息集合
     * @param pagination 分页信息
     * @return
     */
    Page<TestMessage> queryTestMessageByPage(Pagination<TestMessage> pagination);
}
