package com.ziroom.qa.quality.defende.provider.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ziroom.qa.quality.defende.provider.entity.TestMessageRead;

import java.util.List;

public interface TestMessageReadService extends IService<TestMessageRead> {

    /**
     * 获取用户消息读取信息
     * @param msgIdList 消息主体id集合
     * @param userId 用户id
     * @return
     */
    List<TestMessageRead> findReadMsgByIdListAndUserId(List<Long> msgIdList, String userId);

    /**
     * 批量更新用户的读取信息
     * @param msgIdList 消息主体id集合
     * @param userId 用户id
     * @return
     */
    boolean batchTestMsgRead(List<Long> msgIdList, String userId);

    /**
     * 批量删除用户的读取信息
     * @param msgIdList 消息主体id集合
     * @return
     */
    boolean deleteByMsgIdList(List<Long> msgIdList);
}
