package com.ziroom.qa.quality.defende.provider.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ziroom.qa.quality.defende.provider.constant.TestCenterConstants;
import com.ziroom.qa.quality.defende.provider.entity.TestMessage;
import com.ziroom.qa.quality.defende.provider.entity.TestMessageRead;
import com.ziroom.qa.quality.defende.provider.mapper.TestMessageMapper;
import com.ziroom.qa.quality.defende.provider.service.TestMessageReadService;
import com.ziroom.qa.quality.defende.provider.service.TestMessageService;
import com.ziroom.qa.quality.defende.provider.vo.Pagination;
import com.ziroom.qa.quality.defende.provider.vo.TestMessageInfoVo;
import com.ziroom.qa.quality.defende.provider.vo.TestMessageVo;
import com.ziroom.qa.quality.defende.provider.vo.TestResultVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Service
public class TestMessageServiceImpl extends ServiceImpl<TestMessageMapper, TestMessage> implements TestMessageService {

    @Autowired
    private TestMessageMapper testMessageMapper;

    @Autowired
    private TestMessageReadService testMessageReadService;

    @Override
    public boolean insertTestMessage(TestMessage testMessage) {
        if(Objects.isNull(testMessage)
                ||!checkNewMessageTitle(null,testMessage.getMsgTitle()).getFlag()) {
            return false;
        }
        boolean flag = false;
        try {
            flag = this.save(testMessage);
        } catch (Exception e) {
            log.error("保存用例变更消息信息失败，msgTitle=={}",testMessage.getMsgTitle(),e);
        }
        return flag;
    }

    @Override
    public TestMessageInfoVo findInfoByUserName(String userId) {
        TestMessageInfoVo testVo = TestMessageInfoVo.builder().userId(userId).build();
        // 1.校验userId
        if(StringUtils.isBlank(userId)){
            return testVo;
        }
        // 2.查询sql获取消息信息
        List<TestMessageVo> testMessageList = testMessageMapper.getTestMessageVoList(userId, TestCenterConstants.LIMIT_COUNT);
        if(CollectionUtils.isEmpty(testMessageList)){
            return testVo;
        }
        // 3.组织信息集合到消息体
        testVo.setMsgCount(testMessageList.size());
        testVo.setTestMessageVo(testMessageList);
        return testVo;
    }

    @Override
    public TestResultVo batchReadMessage(List<Long> mesIdList, String userId) {
        TestResultVo resultVo = TestResultVo.builder().flag(false).msgRes(TestCenterConstants.RES_MSG_ERROR).build();
        // 1.校验入参
        if(CollectionUtils.isEmpty(mesIdList)||StringUtils.isBlank(userId)){
            resultVo.setMsgRes(TestCenterConstants.RES_MSG_PARAMS_EMPTY);
            return resultVo;
        }
        // 2.业务校验，查询mesIdList是否在数据库存在
        List<TestMessage> tsList = testMessageMapper.selectBatchIds(mesIdList);
        if(CollectionUtils.isEmpty(tsList)){
            resultVo.setMsgRes(TestCenterConstants.RES_MSG_SEARCH_EMPTY);
            return resultVo;
        }
        // 3.匹配用户未读取的消息
        List<Long> idList = tsList.stream().map(TestMessage::getId).collect(Collectors.toList());
        // 3.1 获取该用户已经读取的消息集合
        List<TestMessageRead> readList = testMessageReadService.findReadMsgByIdListAndUserId(idList,userId);
        List<Long> newIdList = new ArrayList<>();
        // 3.2 排除已经读取的消息id集合
        if(CollectionUtils.isNotEmpty(readList)){
            List<Long> msgIdList = readList.stream().map(TestMessageRead::getMsgId).collect(Collectors.toList());
            idList.removeAll(msgIdList);
        }
        // 3.3 去新增消息读取信息
        boolean saveFlag = testMessageReadService.batchTestMsgRead(idList,userId);
        if(saveFlag){
            resultVo.setFlag(true);
            resultVo.setMsgRes(TestCenterConstants.RES_MSG_SUCCESS);
        }
        return resultVo;
    }

    @Override
    public boolean updateTestMessage(TestMessage testMessage) {
        if(Objects.isNull(testMessage)
                ||!checkNewMessageTitle(testMessage.getId(),testMessage.getMsgTitle()).getFlag()) {
            return false;
        }
        boolean flag = false;
        try {
            flag = this.updateById(testMessage);
        } catch (Exception e) {
            log.error("修改用例变更消息信息失败，msgTitle=={}",testMessage.getMsgTitle(),e);
        }
        return flag;
    }

    @Override
    public TestResultVo deleteTestMessage(List<Long> msgIdList) {
        if(CollectionUtils.isEmpty(msgIdList)){
            return TestResultVo.builder().flag(false).msgRes(TestCenterConstants.RES_MSG_PARAMS_EMPTY).build();
        }
        testMessageReadService.deleteByMsgIdList(msgIdList);
        int count = testMessageMapper.deleteBatchIds(msgIdList);
        if(count>0){
            return TestResultVo.builder().flag(true).msgRes(TestCenterConstants.RES_MSG_SUCCESS).build();
        }
        return TestResultVo.builder().flag(false).msgRes(TestCenterConstants.RES_MSG_ERROR).build();
    }

    @Override
    public TestResultVo checkNewMessageTitle(Long msgId,String msgTitle) {
        if(StringUtils.isBlank(msgTitle)||StringUtils.isBlank(msgTitle.trim())){
            return TestResultVo.builder().flag(false).msgRes(TestCenterConstants.RES_MSG_PARAMS_EMPTY).build();
        }

        QueryWrapper<TestMessage> queryWrapper =  new QueryWrapper<>();
        if(!Objects.isNull(msgId)){
            queryWrapper.notIn("id",msgId);
        }
        queryWrapper.eq("msg_title",msgTitle);
        List<TestMessage> list = testMessageMapper.selectList(queryWrapper);
        if(CollectionUtils.isEmpty(list)){
            return TestResultVo.builder().flag(true).msgRes(TestCenterConstants.RES_MSG_SUCCESS).build();
        }
        return TestResultVo.builder().flag(false).msgRes(TestCenterConstants.RES_MSG_REPPEAT).build();
    }

    @Override
    public Page<TestMessage> queryTestMessageByPage(Pagination<TestMessage> pagination) {
        //1. 分页查询参数处理
        TestMessage testMessage = pagination.getSearchObj();
        QueryWrapper<TestMessage> testTaskQueryWrapper = this.getTestMessageQueryWrapper(testMessage);
        //2. 调用分页查询
        Page<TestMessage> testMessagePageResult = this.page(pagination.getPage(), testTaskQueryWrapper);
        return testMessagePageResult;
    }

    /**
     * 组织查询条件
     * @param testMessage
     * @return
     */
    private QueryWrapper<TestMessage> getTestMessageQueryWrapper(TestMessage testMessage){
        QueryWrapper<TestMessage> testTaskQueryWrapper = new QueryWrapper<>();
        testTaskQueryWrapper
                .like(StringUtils.isNotBlank(testMessage.getMsgTitle()),"msg_title",testMessage.getMsgTitle())
                .like(StringUtils.isNotBlank(testMessage.getContent()),"content",testMessage.getContent())
                .eq(StringUtils.isNotBlank(testMessage.getCreateUser()),"create_user",testMessage.getCreateUser())
                .eq(StringUtils.isNotBlank(testMessage.getUpdateUser()),"update_user",testMessage.getUpdateUser());
        testTaskQueryWrapper.orderByDesc("id");
        return testTaskQueryWrapper;
    }
}
