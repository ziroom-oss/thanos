package com.ziroom.qa.quality.defende.provider.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ziroom.qa.quality.defende.provider.entity.TestApplication;
import com.ziroom.qa.quality.defende.provider.vo.ApplicationModuleTree;
import com.ziroom.qa.quality.defende.provider.vo.Pagination;
import com.ziroom.qa.quality.defende.provider.result.RestResultVo;

import java.util.List;

public interface TestApplicationService extends IService<TestApplication> {

    Page<TestApplication> queryTestApplicationByPage(Pagination<TestApplication> pagination);

    RestResultVo saveOrUpdateTestApplication(TestApplication testApplication, String userName);

    boolean validateTestApplicationName(String applicationName,Long id);

    TestApplication queryTestApplicationDetailById(Long id);

    /**
     * 根据ehrtreepath获取应用列表信息
     * @param ehrTreePath
     * @return
     */
    List<TestApplication> listByEhrTreePath(String ehrTreePath);

    String deleteTestApplicationById(Long id);


    /**
     * 搜索应用和模块的树状结构
     * @param userName
     * @param ehrTreePath
     * @return
     */
    List<ApplicationModuleTree> queryApplicationAndModule(String userName,String ehrTreePath);

    /**
     * 搜索我的应用和模块的树状结构
     * @param userName
     * @param ehrTreePath
     * @return
     */
    List<ApplicationModuleTree> queryMyApplicationAndModule(String userName, String ehrTreePath);
}
