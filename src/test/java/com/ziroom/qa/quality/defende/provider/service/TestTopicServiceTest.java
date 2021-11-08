package com.ziroom.qa.quality.defende.provider.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ziroom.qa.quality.defende.provider.QualityDefendProviderApplication;
import com.ziroom.qa.quality.defende.provider.entity.TestTopic;
import com.ziroom.qa.quality.defende.provider.vo.Pagination;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.awt.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@SpringBootTest(classes = QualityDefendProviderApplication.class,webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@RunWith(SpringRunner.class)
@ActiveProfiles("dev")
public class TestTopicServiceTest {

    @Autowired
    private TestTopicService testTopicService;

    @Before
    public void setStart() {
        this.clearTestMessageInfo();
    }

    @After
    public void setEnd() {
        this.clearTestMessageInfo();
    }


    @Test
    public void queryListByPage(){
        this.addInfo();
        Pagination<TestTopic> pagination = new Pagination<>();
        pagination.setSearchObj(new TestTopic());
        Page page = new Page<>();
        page.setCurrent(1);
        page.setSize(10);
        pagination.setPage(page);
        Page<TestTopic> result = testTopicService.queryListByPage(pagination);
        Assert.assertEquals(11,result.getTotal());
    }

    public void addInfo(){
        for(int i=0;i<11;i++){
            TestTopic topic = new TestTopic();
            topic.setTopicName("项目"+i);
            topic.setRelationRequirement("123"+i);
            topic.setRemark("备注"+i);
            topic.setCreateUser("liangh4");
            topic.setUpdateUser("liangh4");
            topic.setCreateTime(LocalDateTime.now());
            topic.setUpdateTime(LocalDateTime.now());
            testTopicService.saveTestTopic(topic);
        }
    }

    private void clearTestMessageInfo() {
        List<TestTopic> list = testTopicService.list();
        if(CollectionUtils.isNotEmpty(list)){
            List<Long> idList = list.stream().map(TestTopic::getId).collect(Collectors.toList());
            testTopicService.batchDeleteByIdList(idList);
        }
    }

}

