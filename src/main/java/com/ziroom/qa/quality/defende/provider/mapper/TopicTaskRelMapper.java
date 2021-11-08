package com.ziroom.qa.quality.defende.provider.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ziroom.qa.quality.defende.provider.entity.TopicTaskRel;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

@Component
@Mapper
public interface TopicTaskRelMapper extends BaseMapper<TopicTaskRel> {
}
