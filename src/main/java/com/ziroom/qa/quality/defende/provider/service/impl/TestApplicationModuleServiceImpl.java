package com.ziroom.qa.quality.defende.provider.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.ziroom.qa.quality.defende.provider.entity.TestApplication;
import com.ziroom.qa.quality.defende.provider.entity.TestApplicationModule;
import com.ziroom.qa.quality.defende.provider.caseRepository.entity.TestCase;
import com.ziroom.qa.quality.defende.provider.mapper.TestApplicationModuleMapper;
import com.ziroom.qa.quality.defende.provider.result.CustomException;
import com.ziroom.qa.quality.defende.provider.result.RestResultVo;
import com.ziroom.qa.quality.defende.provider.service.TestApplicationModuleService;
import com.ziroom.qa.quality.defende.provider.service.TestApplicationService;
import com.ziroom.qa.quality.defende.provider.caseRepository.service.TestCaseService;
import com.ziroom.qa.quality.defende.provider.util.idgen.IdGenUtil;
import com.ziroom.qa.quality.defende.provider.vo.ApplicationModuleTree;
import com.ziroom.qa.quality.defende.provider.vo.ShiftModuleVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class TestApplicationModuleServiceImpl extends ServiceImpl<TestApplicationModuleMapper, TestApplicationModule> implements TestApplicationModuleService {

    @Autowired
    private TestApplicationModuleMapper testApplicationModuleMapper;
    @Autowired
    private TestCaseService testCaseService;
    @Autowired
    private TestApplicationService testApplicationService;

    @Override
    public List<ApplicationModuleTree> queryApplicationModuleTreeRootListByApplicationId(Long applicationId) {
        return testApplicationModuleMapper.queryApplicationModuleTreeRootListByApplicationId(applicationId);
    }

    @Override
    public List<ApplicationModuleTree> queryApplicationModuleTreeByParentId(TestApplicationModule testApplicationModule) {
        return testApplicationModuleMapper.queryApplicationModuleTreeByParentId(testApplicationModule);
    }

    /**
     * 这里需要递归展示模块树
     *
     * @param applicationId
     * @return
     */
    @Override
    public List<ApplicationModuleTree> queryApplicationModuleTreeByApplicationId(Long applicationId) {
        //1. 查询应用的根节点
        List<ApplicationModuleTree> rootApplicationModuleTreeList = queryApplicationModuleTreeRootListByApplicationId(applicationId);

        //2. 递归循环获取孩子节点
        appendChildrenTree(rootApplicationModuleTreeList, applicationId);
        return rootApplicationModuleTreeList;
    }

    /**
     * 此方法被重复引用，切记修改返回值参数不可以随意更改
     *
     * @param id
     * @return
     */
    @Override
    public RestResultVo deleteTestApplicationModuleById(Long id) {
        //1. 判断当前模块是否有子模块
        List<Long> childrenIdList = this.queryApplicationModuleByParentId(id).stream().map(TestApplicationModule::getId).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(childrenIdList)) {
            childrenIdList = Lists.newArrayList();
        }
        childrenIdList.add(id);

        //2. 验证是否有测试用例关联
        boolean result = testCaseService.validateApplicationModuleContainsTestCase(childrenIdList);
        if (result) {
            return RestResultVo.fromErrorMessage("存在测试用例关联所属模块，删除失败");
        } else {
            //3. 查询模块节点树
            List<Long> idList = this.queryApplicationModuleIdListByRootId(id);
            result = this.removeByIds(idList);
            if (result) {
                return RestResultVo.fromSuccess("删除成功");
            } else {
                return RestResultVo.fromErrorMessage("删除失败");
            }
        }
    }

    /**
     * 批量删除所属模块
     *
     * @param idList
     * @return
     */
    @Override
    public RestResultVo deleteTestApplicationModuleByIdList(List<Long> idList) {
        //1. 判断当前模块是否有子模块
        List<Long> allModuleIdList = new ArrayList<>();
        for (Long id : idList) {
            List<Long> childrenIdList = this.queryApplicationModuleByParentId(id).stream().map(TestApplicationModule::getId).collect(Collectors.toList());
            if (CollectionUtils.isEmpty(childrenIdList)) {
                childrenIdList = Lists.newArrayList();
            }
            childrenIdList.add(id);
            allModuleIdList.addAll(childrenIdList);
        }
        allModuleIdList = allModuleIdList.stream().distinct().collect(Collectors.toList());

        //2. 验证是否有测试用例关联
        boolean result = testCaseService.validateApplicationModuleContainsTestCase(allModuleIdList);
        if (result) {
            return RestResultVo.fromErrorMessage("存在测试用例关联所属模块，删除失败");
        } else {
            //3. 查询模块节点树
            this.removeByIds(allModuleIdList);
            return RestResultVo.fromSuccess("成功");
        }
    }

    /**
     * 递归调用叶子节点树
     *
     * @param id
     * @return
     */
    private List<Long> queryApplicationModuleIdListByRootId(Long id) {
        //1. 递归调用叶子节点
        Set<ApplicationModuleTree> resultSet = Sets.newHashSet();
        List<ApplicationModuleTree> children = getChildMoudleListByParent(id);
        while (CollectionUtils.isNotEmpty(children)) {
            resultSet.addAll(children);
            for (ApplicationModuleTree child : children) {
                children = getChildMoudleListByParent(child.getId());
            }
        }

        //2. 获取ID列表
        List<Long> idList = resultSet.stream().map(tree -> tree.getId()).collect(Collectors.toList());
        idList.add(id);
        return idList;
    }

    /**
     * 获取每个根节点的叶子节点
     *
     * @param rootId
     * @return
     */
    public List<ApplicationModuleTree> getChildMoudleListByParent(Long rootId) {
        //1. 获取根节点
        TestApplicationModule rootTestApplicationModule = this.getById(rootId);
        //2. 获取叶子节点列表
        TestApplicationModule params = new TestApplicationModule();
        params.setParentId(rootTestApplicationModule.getId());
        params.setApplicationId(rootTestApplicationModule.getApplicationId());
        List<ApplicationModuleTree> childrenApplicationModuleTreeList = testApplicationModuleMapper.queryApplicationModuleTreeByParentId(params);
        return childrenApplicationModuleTreeList;
    }

    /**
     * 递归获取孩子节点
     *
     * @param applicationModuleTreeList
     * @param applicationId
     */
    public void appendChildrenTree(List<ApplicationModuleTree> applicationModuleTreeList, Long applicationId) {
        if (CollectionUtils.isNotEmpty(applicationModuleTreeList)) {
            for (ApplicationModuleTree applicationModuleTree : applicationModuleTreeList) {
                TestApplicationModule testApplicationModuleParams = new TestApplicationModule();
                testApplicationModuleParams.setApplicationId(applicationId);
                testApplicationModuleParams.setParentId(applicationModuleTree.getId());
                List<ApplicationModuleTree> children = queryApplicationModuleTreeByParentId(testApplicationModuleParams);
                if (CollectionUtils.isNotEmpty(children)) {
                    applicationModuleTree.setChildren(children);
                    appendChildrenTree(children, applicationId);
                }
            }
        }
    }

    @Override
    public List<TestApplicationModule> queryApplicationModuleByParentId(Long parentId) {
        QueryWrapper<TestApplicationModule> testApplicationModuleQueryWrapper = new QueryWrapper<>();
        testApplicationModuleQueryWrapper.like("module_tree_path", parentId);
        return this.list(testApplicationModuleQueryWrapper);
    }

    @Override
    public boolean updateApplicationModuleNameById(TestApplicationModule tam, String userName) {
        TestApplicationModule testApplicationModule = this.getById(tam.getId());
        testApplicationModule.setModuleName(tam.getModuleName());
        testApplicationModule.setUpdateTime(LocalDateTime.now());
        testApplicationModule.setUpdateUser(userName);
        return this.updateById(testApplicationModule);
    }

    @Override
    public ApplicationModuleTree appendChildApplicationModule(TestApplicationModule testApplicationModule, String userName) {
        boolean flag = this.validateModuleName(testApplicationModule);
        if (flag) {
            throw new CustomException("该模块名称已存在！");
        }
        TestApplicationModule parentApplicationModule = null;
        //1. 获取父模块
        if (!Objects.isNull(testApplicationModule.getParentId())) {
            parentApplicationModule = this.getById(testApplicationModule.getParentId());
        }

        if (!Objects.isNull(parentApplicationModule)) {
            int level = parentApplicationModule.getLevel() + 1;
            if (level > 3) {
                throw new CustomException("模块层级大于限制（最多为4级模块）！");
            }
            testApplicationModule.setLevel(level);
            testApplicationModule.setApplicationId(parentApplicationModule.getApplicationId());
            testApplicationModule.setId(IdGenUtil.nextId());
            testApplicationModule.setModuleTreePath(parentApplicationModule.getModuleTreePath() + "," + testApplicationModule.getId());
        }
        if (Objects.isNull(testApplicationModule.getApplicationId())) {
            throw new CustomException("所属应用ID不存在！");
        }
        //2. 新增子模块
        testApplicationModule = saveTestApplicationModule(testApplicationModule, userName);

        //3. 转换为前端的树形结构
        return convertToApplicationModuleTree(testApplicationModule);
    }

    public TestApplicationModule saveTestApplicationModule(TestApplicationModule testApplicationModule, String userName) {
        boolean flag = this.validateModuleName(testApplicationModule);
        if (flag) {
            throw new CustomException("该模块名称已存在！");
        }
        // liangh4增加自动生成主键，以便生成TreePath
        if (Objects.isNull(testApplicationModule.getId())) {
            testApplicationModule.setId(IdGenUtil.nextId());
        }
        if (StringUtils.isBlank(testApplicationModule.getModuleTreePath())) {
            testApplicationModule.setModuleTreePath(String.valueOf(testApplicationModule.getId()));
        }
        testApplicationModule.setCreateUser(userName);
        testApplicationModule.setUpdateUser(userName);
        testApplicationModule.setCreateTime(LocalDateTime.now());
        testApplicationModule.setUpdateTime(LocalDateTime.now());
        save(testApplicationModule);
        log.info("保存模块：{}", JSON.toJSONString(testApplicationModule));
        return testApplicationModule;
    }


    /**
     * 根据应用id集合获取模块树map
     *
     * @param appIdList
     * @return
     */
    @Override
    public Map<Long, ApplicationModuleTree> queryModuleMapByAppIdList(List<Long> appIdList) {
        Map<Long, ApplicationModuleTree> resMap = new HashMap<>();
        // 1.查询所有应用对应的模块
        if (CollectionUtils.isEmpty(appIdList)) {
            return resMap;
        }
        QueryWrapper<TestApplicationModule> queryWrapper = new QueryWrapper<>();
        queryWrapper.in("application_id", appIdList);
        List<TestApplicationModule> moduleList = this.list(queryWrapper);
        if (CollectionUtils.isEmpty(moduleList)) {
            return resMap;
        }
        // 2.map进行分组（每个应用id对应的模块集合）
        Map<Long, List<TestApplicationModule>> appMap = moduleList.stream().collect(Collectors.groupingBy(TestApplicationModule::getApplicationId));
        // 3.递归遍历模块树状结构
        appMap.keySet().forEach(key -> {
            // 应用关联的模块集合
            List<TestApplicationModule> modules = appMap.get(key);
            // 需要转换的模块数据结构（返回给前端的）
            ApplicationModuleTree appTree = new ApplicationModuleTree();
            appTree.setId(key);
            // 遍历组装每个应用下的模块树状结构(递归遍历)
            List<ApplicationModuleTree> newModuleList = new ArrayList<>();
            modules.forEach(module -> {
                ApplicationModuleTree amt = new ApplicationModuleTree();
                amt.setModuleTreePath(module.getModuleTreePath());
                amt.setId(module.getId());
                amt.setParentId(module.getParentId());
                amt.setApp(false);
                amt.setLabel(module.getModuleName());
                amt.setApplicationId(module.getApplicationId());
                newModuleList.add(amt);
            });
            this.assableAppModule(newModuleList, appTree);
            resMap.put(key, appTree);
        });
        return resMap;
    }

    /**
     * 递归遍历把list转为树放到应用的子节点里面
     *
     * @param newModuleList
     * @param appTree
     */
    private void assableAppModule(List<ApplicationModuleTree> newModuleList, ApplicationModuleTree appTree) {
        List<ApplicationModuleTree> moduleTree = this.list2Tree(newModuleList, null);
        appTree.setChildren(moduleTree);
    }

    /**
     * 递归获取模块的树
     *
     * @param list
     * @param pid
     * @return
     */
    public List<ApplicationModuleTree> list2Tree(List<ApplicationModuleTree> list, Long pid) {
        List<ApplicationModuleTree> treeList = new ArrayList<>();
        Iterator<ApplicationModuleTree> it = list.iterator();
        while (it.hasNext()) {
            ApplicationModuleTree moduleTree = it.next();
            if (moduleTree.getParentId() == null || moduleTree.getParentId().equals(pid)) {
                // 判断是否为限制的第三级
                if (StringUtils.isNotBlank(moduleTree.getModuleTreePath())
                        && moduleTree.getModuleTreePath().split(",").length > 3) {
                    moduleTree.setLeaf(true);
                }
                treeList.add(moduleTree);
                // 已添加的元素删除掉
                it.remove();
            }
        }
        // 寻找子模块
        treeList.forEach(moduleTree -> moduleTree.setChildren(list2Tree(list, moduleTree.getId())));
        return treeList;
    }

    private ApplicationModuleTree convertToApplicationModuleTree(TestApplicationModule testApplicationModule) {
        ApplicationModuleTree applicationModuleTree = new ApplicationModuleTree();
        applicationModuleTree.setActive(true);
        applicationModuleTree.setLevel(testApplicationModule.getLevel());
        applicationModuleTree.setId(testApplicationModule.getId());
        applicationModuleTree.setLabel(testApplicationModule.getModuleName());
        applicationModuleTree.setModuleTreePath(testApplicationModule.getModuleTreePath());
        applicationModuleTree.setApplicationId(testApplicationModule.getApplicationId());
        applicationModuleTree.setParentId(testApplicationModule.getParentId());
        return applicationModuleTree;
    }

    private boolean validateModuleName(TestApplicationModule testApplicationModule) {
        QueryWrapper<TestApplicationModule> queryWrapper = new QueryWrapper<>();
        queryWrapper
                .eq((null != testApplicationModule.getApplicationId()), "application_id", testApplicationModule.getApplicationId())
                .eq(StringUtils.isNotBlank(testApplicationModule.getModuleName()), "module_name", testApplicationModule.getModuleName())
                .eq((null != testApplicationModule.getParentId()), "parent_id", testApplicationModule.getParentId());
        List<TestApplicationModule> list = super.list(queryWrapper);
        return CollectionUtils.isNotEmpty(list);
    }

    /**
     * 根据应用名称查询模块列表
     *
     * @param applicationId
     * @return
     */
    @Override
    public List<TestApplicationModule> queryApplicationModuleListByApplicationId(Long applicationId) {
        LambdaQueryWrapper<TestApplicationModule> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(TestApplicationModule::getApplicationId, applicationId);
        return super.list(queryWrapper);
    }

    /**
     * 模块转移
     *
     * @param userName
     * @param shiftModuleVo
     */
    @Override
    public void shiftModule(String userName, ShiftModuleVo shiftModuleVo) {
        if (Objects.isNull(shiftModuleVo.getSourceModuleId())
                || Objects.isNull(shiftModuleVo.getTargetModuleId())) {
            throw new CustomException("需要转移的模块为空！");
        }
        // 获取所有的模块id集合
        List<Long> allModuleIdList = this.queryApplicationModuleIdListByRootId(shiftModuleVo.getSourceModuleId());
        List<TestApplicationModule> allModules = super.listByIds(allModuleIdList);
        if (CollectionUtils.isEmpty(allModules)) {
            throw new CustomException("转移的模块不存在！");
        }
        Long appId;
        //如果是应用，则首个treepath为null
        if (shiftModuleVo.isAppFlag()) {
            TestApplication application = testApplicationService.getById(shiftModuleVo.getTargetModuleId());
            if (Objects.isNull(application)) {
                throw new CustomException("转移的目标应用不存在！");
            }
            appId = application.getId();
            allModules.stream().forEach(module -> {
                String newPath = String.valueOf(shiftModuleVo.getSourceModuleId());
                if (shiftModuleVo.getSourceModuleId().equals(module.getId())) {
                    module.setModuleTreePath(newPath);
                    module.setParentId(null);
                    module.setLevel(0);
                } else {
                    String treePath = shiftModuleVo.getSourceModuleId() + module.getModuleTreePath().split(String.valueOf(shiftModuleVo.getSourceModuleId()))[1];
                    module.setModuleTreePath(treePath);
                    module.setLevel(treePath.split(",").length - 1);
                }
                module.setApplicationId(shiftModuleVo.getTargetModuleId());
                module.setUpdateUser(userName);
                module.setUpdateTime(LocalDateTime.now());
            });
        } else {
            TestApplicationModule targetModule = super.getById(shiftModuleVo.getTargetModuleId());
            if (Objects.isNull(targetModule)) {
                throw new CustomException("转移的目标模块不存在！");
            }
            appId = targetModule.getApplicationId();
            allModules.stream().forEach(module -> {
                String newPath = targetModule.getModuleTreePath() + "," + shiftModuleVo.getSourceModuleId();
                if (shiftModuleVo.getSourceModuleId().equals(module.getId())) {
                    module.setModuleTreePath(newPath);
                    module.setParentId(targetModule.getId());
                    module.setLevel(newPath.split(",").length - 1);
                } else {
                    String treePath = newPath + module.getModuleTreePath().split(String.valueOf(shiftModuleVo.getSourceModuleId()))[1];
                    module.setModuleTreePath(treePath);
                    module.setLevel(treePath.split(",").length - 1);
                }
                if (module.getLevel() > 3) {
                    throw new CustomException("转移后的模块层级大于限制（最多为4级模块）！");
                }
                module.setApplicationId(targetModule.getApplicationId());
                module.setUpdateUser(userName);
                module.setUpdateTime(LocalDateTime.now());
            });
        }
        // 更新转移后的测试用例所属应用
        QueryWrapper testCaseQuery = new QueryWrapper();
        testCaseQuery.eq("belong_to_module", shiftModuleVo.getSourceModuleId());
        // 源模块下用例集合
        List<TestCase> souModuleCaseList = testCaseService.list(testCaseQuery);
        if (CollectionUtils.isNotEmpty(souModuleCaseList)) {
            souModuleCaseList.stream().forEach(source -> {
                source.setBelongToSystem(appId);
            });
            testCaseService.updateBatchById(souModuleCaseList);
        }
        // 更新模块
        super.updateBatchById(allModules);
    }

//    /**
//     * 校验源模块下测试用例和现在的测试用例是否有相同名称的模块
//     *
//     * @param shiftModuleVo
//     */
//    private List<TestCase> checkModuleTestCase(ShiftModuleVo shiftModuleVo) {
//        QueryWrapper testCaseQuery = new QueryWrapper();
//        testCaseQuery.eq("belong_to_module", shiftModuleVo.getSourceModuleId());
//        //源模块下用例集合
//        List<TestCase> souModuleCaseList = testCaseService.list(testCaseQuery);
//        testCaseQuery.eq("belong_to_module", shiftModuleVo.getTargetModuleId());
//        //目标模块下用例集合
//        List<TestCase> tarModuleCaseList = testCaseService.list(testCaseQuery);
//        if (CollectionUtils.isEmpty(souModuleCaseList) || CollectionUtils.isEmpty(tarModuleCaseList)) {
//            return souModuleCaseList;
//        }
//        StringBuilder sb = new StringBuilder();
//        // 比较用例的值
//        Map<String, TestCase> testCaseMap = tarModuleCaseList.stream().collect(Collectors.toMap(TestCase::getCasename, Function.identity()));
//        souModuleCaseList.stream().forEach(source -> {
//            if (Objects.nonNull(testCaseMap.get(source.getCasename()))) {
//                sb.append(source.getCasename() + "\n");
//            }
//        });
//
//        if (StringUtils.isNotBlank(sb.toString())) {
//            throw new CustomException("要转移的模块有名称重复的用例:\n" + sb);
//        }
//        return souModuleCaseList;
//    }


}
