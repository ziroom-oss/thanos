package com.ziroom.qa.quality.defende.provider.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ziroom.qa.quality.defende.provider.entity.TestApplicationModule;
import com.ziroom.qa.quality.defende.provider.vo.ApplicationModuleTree;
import com.ziroom.qa.quality.defende.provider.result.RestResultVo;
import com.ziroom.qa.quality.defende.provider.vo.ShiftModuleVo;

import java.util.List;
import java.util.Map;

public interface TestApplicationModuleService extends IService<TestApplicationModule> {

    /**
     * 获取每个应用的根节点
     * @param applicationId
     * @return
     */
    List<ApplicationModuleTree> queryApplicationModuleTreeRootListByApplicationId(Long applicationId);

    /**
     * 获取每个模块的孩子节点
     * @param testApplicationModule
     * @return
     */
    List<ApplicationModuleTree> queryApplicationModuleTreeByParentId(TestApplicationModule testApplicationModule);

    /**
     * 获取每个应用模块树
     * @param applicationId
     * @return
     */
    List<ApplicationModuleTree> queryApplicationModuleTreeByApplicationId(Long applicationId);

    /**
     * 根据父ID获取孩子节点
     * @param parentId
     * @return
     */
    List<TestApplicationModule> queryApplicationModuleByParentId(Long parentId);

    /**
     * 删除所属模块
     * @param id
     * @return
     */
    RestResultVo deleteTestApplicationModuleById(Long id);

    /**
     * 批量删除所属模块
     * @param idList
     * @return
     */
    RestResultVo deleteTestApplicationModuleByIdList(List<Long> idList);

    /**
     * 修改所属模块名称
     * @param testApplicationModule
     * @return
     */
    boolean updateApplicationModuleNameById(TestApplicationModule testApplicationModule,String userName);

    /**
     * 新增子模块
     * @param testApplicationModule
     * @return
     */
    ApplicationModuleTree appendChildApplicationModule(TestApplicationModule testApplicationModule,String userName) throws Exception;

    /**
     * 新增根节点
     * @param testApplicationModule
     * @param userName
     * @return
     */
    TestApplicationModule saveTestApplicationModule(TestApplicationModule testApplicationModule, String userName);

    /**
     * 根据应用id集合获取模块树map
     * @param appIdList
     * @return
     */
    Map<Long,ApplicationModuleTree> queryModuleMapByAppIdList(List<Long> appIdList);

    /**
     * 根据应用名称查询模块列表
     * @param applicationId
     * @return
     */
    List<TestApplicationModule> queryApplicationModuleListByApplicationId(Long applicationId);

    /**
     * 模块转移
     * @param userName
     * @param shiftModuleVo
     */
    void shiftModule(String userName, ShiftModuleVo shiftModuleVo);
}
