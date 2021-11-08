package com.ziroom.qa.quality.defende.provider.execTask.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ziroom.qa.quality.defende.provider.execTask.entity.TaskTestCase;
import com.ziroom.qa.quality.defende.provider.vo.ExecutionListVo;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Mapper
public interface TaskTestCaseMapper extends BaseMapper<TaskTestCase> {
    Integer updateExecutionByTaskIdAndCaseId(TaskTestCase taskTestCase);

    List<ExecutionListVo> getExecutionListPage(Page page, Long taskId);
}
