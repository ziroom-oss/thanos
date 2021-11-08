package com.ziroom.qa.quality.defende.provider.execTask.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ziroom.qa.quality.defende.provider.execTask.entity.TestTask;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

@Component
@Mapper
public interface TestTaskMapper extends BaseMapper<TestTask> {

}
