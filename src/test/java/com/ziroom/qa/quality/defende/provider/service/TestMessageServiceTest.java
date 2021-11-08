package com.ziroom.qa.quality.defende.provider.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ziroom.qa.quality.defende.provider.QualityDefendProviderApplication;
import com.ziroom.qa.quality.defende.provider.entity.TestMessage;
import com.ziroom.qa.quality.defende.provider.vo.Pagination;
import com.ziroom.qa.quality.defende.provider.vo.TestMessageInfoVo;
import com.ziroom.qa.quality.defende.provider.vo.TestResultVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@SpringBootTest(classes = QualityDefendProviderApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@RunWith(SpringRunner.class)
@ActiveProfiles("dev")
public class TestMessageServiceTest {

    @Autowired
    private TestMessageService testMessageService;

    String userName = "liangh4";

    String msgTitle = "我的消息标题1";

    String content = "我的消息体1";

    String msgTitle2 = "我的消息标题2";

    String content2 = "我的消息体2";

    String upuserName = "liangh5";

    String upmsgTitle = "修改我的消息标题1";

    String upcontent = "修改我的消息体1";

    @Before
    public void setStart() throws Exception {
        this.clearTestMessageInfo();
    }

    @After
    public void setEnd() throws Exception {
        this.clearTestMessageInfo();
    }

    @Test
    public void testAddMessage(){
        for(int i=0;i<30;i++){
            TestMessage vo = new TestMessage();
            vo.setContent(content+i);
            vo.setMsgTitle("2021-版本"+i);
            vo.setCreateUser(userName);
            vo.setCreateTime(LocalDateTime.now());
            vo.setUpdateUser(userName);
            vo.setUpdateTime(LocalDateTime.now());
            testMessageService.insertTestMessage(vo);
        }

    }

    /**
     * 验证管理员对消息进行增删改查的业务逻辑
     */
    @Test
    public void msgCRUDTest() {
        this.addMessageInfo();
        // 1.查询用户的消息列表
        List<TestMessage> list = getListByMsgTitle(null);
        Assert.assertEquals(list.size(), 2);
        Assert.assertEquals(list.get(0).getMsgTitle(), msgTitle2);
        Assert.assertEquals(list.get(0).getContent(), content2);
        Assert.assertEquals(list.get(1).getMsgTitle(), msgTitle);
        Assert.assertEquals(list.get(1).getContent(), content);
        // 2.修改用户消息
        TestMessage ts1 = new TestMessage();
        ts1.setId(list.get(0).getId());
        ts1.setUpdateUser(upuserName);
        ts1.setUpdateTime(LocalDateTime.now());
        ts1.setMsgTitle(upmsgTitle);
        ts1.setContent(upcontent);
        boolean updateFlag = testMessageService.updateTestMessage(ts1);
        Assert.assertTrue(updateFlag);
        // 2.1 查询修改后的消息
        List<TestMessage> list1 = getListByMsgTitle(upmsgTitle);
        Assert.assertEquals(list1.size(), 1);
        Assert.assertEquals(list1.get(0).getMsgTitle(), upmsgTitle);
        Assert.assertEquals(list1.get(0).getContent(), upcontent);
        // 3.校验修改相同msgTitle被校验
        TestMessage ts2 = new TestMessage();
        ts2.setId(list.get(1).getId());
        ts2.setUpdateUser(upuserName);
        ts2.setUpdateTime(LocalDateTime.now());
        ts2.setMsgTitle(upmsgTitle);
        ts2.setContent(upcontent);
        boolean updateFlag2 = testMessageService.updateTestMessage(ts2);
        Assert.assertFalse(updateFlag2);
        // 4.删除消息
        List<Long> msgIdList = list.stream().map(msg -> msg.getId()).collect(Collectors.toList());
        boolean delFlag = testMessageService.deleteTestMessage(msgIdList).getFlag();
        Assert.assertTrue(delFlag);
    }

    @Test
    public void insertTestMessageTest() {
        //新增验证
        boolean insertFlag = testMessageService.insertTestMessage(getTestMessage());
        Assert.assertTrue(insertFlag);
        List<TestMessage> list = getListByMsgTitle(msgTitle);
        Assert.assertEquals(list.size(),1);
        Assert.assertEquals(list.get(0).getContent(),content);
        Assert.assertEquals(list.get(0).getMsgTitle(),msgTitle);
    }

    @Test
    public void findInfoByUserIdTest() {
        this.addMessageInfo();
        // 查询用户的消息列表
        List<TestMessage> list = getListByMsgTitle(null);
        Assert.assertEquals(list.size(), 2);
        Assert.assertEquals(list.get(0).getMsgTitle(), msgTitle2);
        Assert.assertEquals(list.get(0).getContent(), content2);
        Assert.assertEquals(list.get(1).getMsgTitle(), msgTitle);
        Assert.assertEquals(list.get(1).getContent(), content);
    }

    @Test
    public void batchReadMessageTest() {
        this.addMessageInfo();
        // 1.查询用户的未读消息列表
        TestMessageInfoVo vo = testMessageService.findInfoByUserName(userName);
        Assert.assertEquals(vo.getMsgCount(), 2);
        Assert.assertEquals(vo.getTestMessageVo().size(), 2);
        Assert.assertEquals(vo.getTestMessageVo().get(0).getMsgTitle(), msgTitle2);
        Assert.assertEquals(vo.getTestMessageVo().get(0).getContent(), content2);
        Assert.assertEquals(vo.getTestMessageVo().get(1).getMsgTitle(), msgTitle);
        Assert.assertEquals(vo.getTestMessageVo().get(1).getContent(), content);
        // 2.批量读取用户的未读消息
        List<Long> msgIdList = vo.getTestMessageVo().stream().map(msg -> msg.getTestMessageId()).collect(Collectors.toList());
        TestResultVo resultVo = testMessageService.batchReadMessage(msgIdList, userName);
        Assert.assertTrue(resultVo.getFlag());
        // 3.校验该用户是否还有未读取消息
        vo = testMessageService.findInfoByUserName(userName);
        Assert.assertEquals(vo.getMsgCount(), 0);
    }

    @Test
    public void updateTestMessageTest() {
        this.addMessageInfo();
        // 1.查询用户的消息列表
        List<TestMessage> list = getListByMsgTitle(null);
        Assert.assertEquals(list.size(), 2);
        Assert.assertEquals(list.get(0).getMsgTitle(), msgTitle2);
        Assert.assertEquals(list.get(0).getContent(), content2);
        Assert.assertEquals(list.get(1).getMsgTitle(), msgTitle);
        Assert.assertEquals(list.get(1).getContent(), content);
        // 2.修改用户未读取消息
        TestMessage ts1 = new TestMessage();
        ts1.setId(list.get(0).getId());
        ts1.setUpdateUser(upuserName);
        ts1.setUpdateTime(LocalDateTime.now());
        ts1.setMsgTitle(upmsgTitle);
        ts1.setContent(upcontent);
        boolean updateFlag = testMessageService.updateTestMessage(ts1);
        Assert.assertTrue(updateFlag);
        // 3. 查询修改后的消息
        List<TestMessage> list1 = getListByMsgTitle(upmsgTitle);
        Assert.assertEquals(list1.size(), 1);
        Assert.assertEquals(list1.get(0).getMsgTitle(), upmsgTitle);
        Assert.assertEquals(list1.get(0).getContent(), upcontent);
    }

    @Test
    public void deleteTestMessageTest() {
        this.addMessageInfo();
        // 1.查询用户的消息列表
        List<TestMessage> list = getListByMsgTitle(null);
        Assert.assertEquals(list.size(), 2);
        // 2.删除消息
        List<Long> msgIdList = list.stream().map(msg -> msg.getId()).collect(Collectors.toList());
        boolean delFlag = testMessageService.deleteTestMessage(msgIdList).getFlag();
        Assert.assertTrue(delFlag);
        // 3.查询用户的消息列表
        list = getListByMsgTitle(null);
        Assert.assertTrue(CollectionUtils.isEmpty(list));
    }

    @Test
    public void checkNewMessageTitleTest() {
        this.addMessageInfo();
        // 1.查询用户的消息列表
        List<TestMessage> list = getListByMsgTitle(null);
        // 2.校验修改相同msgTitle被校验
        boolean checkFlag = testMessageService.checkNewMessageTitle(null,msgTitle).getFlag();
        Assert.assertFalse(checkFlag);
    }

    @Test
    public void queryTestMessageByPageTest() {
        this.addMessageInfo();
        Pagination pagination = new Pagination();
        TestMessage test = new TestMessage();
        pagination.setSearchObj(test);
        pagination.setPage(new Page());
        Page<TestMessage> page = testMessageService.queryTestMessageByPage(pagination);
        List<TestMessage> list = page.getRecords();
        Assert.assertEquals(list.size(), 2);
    }

    /**
     * 清除测试数据
     */
    private void clearTestMessageInfo() {
        List<TestMessage> list = getListByMsgTitle(null);
        if (CollectionUtils.isNotEmpty(list)) {
            List<Long> msgIdList = list.stream().map(msg -> msg.getId()).collect(Collectors.toList());
            boolean delFlag = testMessageService.deleteTestMessage(msgIdList).getFlag();
            Assert.assertTrue(delFlag);
        }
    }

    /**
     * 根据msgTitle查询列表信息
     *
     * @param msgTitle
     * @return
     */
    private List<TestMessage> getListByMsgTitle(String msgTitle) {
        Pagination pagination = new Pagination();
        pagination.setPage(new Page().setSize(1000));
        TestMessage test = new TestMessage();
        if (StringUtils.isNotBlank(msgTitle)) {
            test.setMsgTitle(msgTitle);
        }
        pagination.setSearchObj(test);
        Page<TestMessage> page = testMessageService.queryTestMessageByPage(pagination);
        List<TestMessage> list = page.getRecords();
        return list;
    }


    private TestMessage getTestMessage() {
        TestMessage vo = new TestMessage();
        vo.setContent(content);
        vo.setMsgTitle(msgTitle);
        vo.setCreateUser(userName);
        vo.setCreateTime(LocalDateTime.now());
        vo.setUpdateUser(userName);
        vo.setUpdateTime(LocalDateTime.now());
        return vo;
    }

    private TestMessage getTestMessage2() {
        TestMessage vo = new TestMessage();
        vo.setContent(content2);
        vo.setMsgTitle(msgTitle2);
        vo.setCreateUser(userName);
        vo.setCreateTime(LocalDateTime.now());
        vo.setUpdateUser(userName);
        vo.setUpdateTime(LocalDateTime.now());
        return vo;
    }

    /**
     * 初始化两条信息
     */
    private void addMessageInfo() {
        boolean insertFlag = testMessageService.insertTestMessage(getTestMessage());
        Assert.assertTrue(insertFlag);
        boolean insertFlag2 = testMessageService.insertTestMessage(getTestMessage2());
        Assert.assertTrue(insertFlag2);
    }
}

