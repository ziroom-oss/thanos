package com.ziroom.qa.quality.defende.provider.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ziroom.qa.quality.defende.provider.constant.TestCenterConstants;
import com.ziroom.qa.quality.defende.provider.entity.TestMessageRead;
import com.ziroom.qa.quality.defende.provider.mapper.TestMessageReadMapper;
import com.ziroom.qa.quality.defende.provider.service.TestMessageReadService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class TestMessageReadServiceImpl extends ServiceImpl<TestMessageReadMapper, TestMessageRead> implements TestMessageReadService {

    @Autowired
    private TestMessageReadMapper testMessageReadMapper;

    @Override
    public List<TestMessageRead> findReadMsgByIdListAndUserId(List<Long> msgIdList, String userId) {
        if(CollectionUtils.isEmpty(msgIdList)|| StringUtils.isBlank(userId)){
            return null;
        }
        QueryWrapper<TestMessageRead> queryWrapper =  new QueryWrapper<>();
        queryWrapper.in("msg_id",msgIdList);
        queryWrapper.eq("create_user",userId);
        return testMessageReadMapper.selectList(queryWrapper);
    }

    @Override
    public boolean batchTestMsgRead(List<Long> msgIdList, String userId) {
        if(CollectionUtils.isEmpty(msgIdList)|| StringUtils.isBlank(userId)){
            return false;
        }
        List<TestMessageRead> readList = new ArrayList<>();
        for(Long msgId:msgIdList){
            TestMessageRead read = new TestMessageRead();
            read.setCreateTime(LocalDateTime.now());
            read.setCreateUser(userId);
            read.setMsgId(msgId);
            read.setMsgStatus(TestCenterConstants.MSG_STATUS_READ);
            readList.add(read);
        }
        return this.saveBatch(readList);
    }

    @Override
    public boolean deleteByMsgIdList(List<Long> msgIdList) {
        if(CollectionUtils.isEmpty(msgIdList)){
            return false;
        }
        QueryWrapper<TestMessageRead> queryWrapper =  new QueryWrapper<>();
        queryWrapper.in("msg_id",msgIdList);
        int count = testMessageReadMapper.delete(queryWrapper);
        if(count>0){
            return true;
        }
        return false;
    }
}
