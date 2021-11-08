package com.ziroom.qa.quality.defende.provider.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ziroom.qa.quality.defende.provider.entity.TestMessage;
import com.ziroom.qa.quality.defende.provider.vo.TestMessageVo;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Mapper
public interface TestMessageMapper extends BaseMapper<TestMessage> {

    /**
     * 查询该用户未读取的消息集合
     * @param userId 用户id
     * @param limitCount 限制数量
     * @return
     */
    List<TestMessageVo> getTestMessageVoList(String userId,int limitCount);

}
