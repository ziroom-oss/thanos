package com.ziroom.qa.quality.defende.provider.execTask.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ziroom.qa.quality.defende.provider.execTask.entity.TestExecution;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

@Component
@Mapper
public interface TestExecutionMapper extends BaseMapper<TestExecution> {
    Integer updateFlagByCaseKey(TestExecution testExecution);

    Integer updateFlagByCaseId(TestExecution testExecution);

    Integer updateFlagByCaseList(TestExecution testExecution);
}
