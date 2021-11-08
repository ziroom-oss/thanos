package com.ziroom.qa.quality.defende.provider.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ziroom.qa.quality.defende.provider.entity.TestApplicationModule;
import com.ziroom.qa.quality.defende.provider.vo.ApplicationModuleTree;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface TestApplicationModuleMapper extends BaseMapper<TestApplicationModule> {

    List<ApplicationModuleTree> queryApplicationModuleTreeRootListByApplicationId(Long applicationId);

    List<ApplicationModuleTree> queryApplicationModuleTreeByParentId(TestApplicationModule testApplicationModule);

}
