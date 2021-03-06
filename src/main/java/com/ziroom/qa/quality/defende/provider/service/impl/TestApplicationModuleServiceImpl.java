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
     * ?????????????????????????????????
     *
     * @param applicationId
     * @return
     */
    @Override
    public List<ApplicationModuleTree> queryApplicationModuleTreeByApplicationId(Long applicationId) {
        //1. ????????????????????????
        List<ApplicationModuleTree> rootApplicationModuleTreeList = queryApplicationModuleTreeRootListByApplicationId(applicationId);

        //2. ??????????????????????????????
        appendChildrenTree(rootApplicationModuleTreeList, applicationId);
        return rootApplicationModuleTreeList;
    }

    /**
     * ???????????????????????????????????????????????????????????????????????????
     *
     * @param id
     * @return
     */
    @Override
    public RestResultVo deleteTestApplicationModuleById(Long id) {
        //1. ????????????????????????????????????
        List<Long> childrenIdList = this.queryApplicationModuleByParentId(id).stream().map(TestApplicationModule::getId).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(childrenIdList)) {
            childrenIdList = Lists.newArrayList();
        }
        childrenIdList.add(id);

        //2. ?????????????????????????????????
        boolean result = testCaseService.validateApplicationModuleContainsTestCase(childrenIdList);
        if (result) {
            return RestResultVo.fromErrorMessage("???????????????????????????????????????????????????");
        } else {
            //3. ?????????????????????
            List<Long> idList = this.queryApplicationModuleIdListByRootId(id);
            result = this.removeByIds(idList);
            if (result) {
                return RestResultVo.fromSuccess("????????????");
            } else {
                return RestResultVo.fromErrorMessage("????????????");
            }
        }
    }

    /**
     * ????????????????????????
     *
     * @param idList
     * @return
     */
    @Override
    public RestResultVo deleteTestApplicationModuleByIdList(List<Long> idList) {
        //1. ????????????????????????????????????
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

        //2. ?????????????????????????????????
        boolean result = testCaseService.validateApplicationModuleContainsTestCase(allModuleIdList);
        if (result) {
            return RestResultVo.fromErrorMessage("???????????????????????????????????????????????????");
        } else {
            //3. ?????????????????????
            this.removeByIds(allModuleIdList);
            return RestResultVo.fromSuccess("??????");
        }
    }

    /**
     * ???????????????????????????
     *
     * @param id
     * @return
     */
    private List<Long> queryApplicationModuleIdListByRootId(Long id) {
        //1. ????????????????????????
        Set<ApplicationModuleTree> resultSet = Sets.newHashSet();
        List<ApplicationModuleTree> children = getChildMoudleListByParent(id);
        while (CollectionUtils.isNotEmpty(children)) {
            resultSet.addAll(children);
            for (ApplicationModuleTree child : children) {
                children = getChildMoudleListByParent(child.getId());
            }
        }

        //2. ??????ID??????
        List<Long> idList = resultSet.stream().map(tree -> tree.getId()).collect(Collectors.toList());
        idList.add(id);
        return idList;
    }

    /**
     * ????????????????????????????????????
     *
     * @param rootId
     * @return
     */
    public List<ApplicationModuleTree> getChildMoudleListByParent(Long rootId) {
        //1. ???????????????
        TestApplicationModule rootTestApplicationModule = this.getById(rootId);
        //2. ????????????????????????
        TestApplicationModule params = new TestApplicationModule();
        params.setParentId(rootTestApplicationModule.getId());
        params.setApplicationId(rootTestApplicationModule.getApplicationId());
        List<ApplicationModuleTree> childrenApplicationModuleTreeList = testApplicationModuleMapper.queryApplicationModuleTreeByParentId(params);
        return childrenApplicationModuleTreeList;
    }

    /**
     * ????????????????????????
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
            throw new CustomException("???????????????????????????");
        }
        TestApplicationModule parentApplicationModule = null;
        //1. ???????????????
        if (!Objects.isNull(testApplicationModule.getParentId())) {
            parentApplicationModule = this.getById(testApplicationModule.getParentId());
        }

        if (!Objects.isNull(parentApplicationModule)) {
            int level = parentApplicationModule.getLevel() + 1;
            if (level > 3) {
                throw new CustomException("????????????????????????????????????4???????????????");
            }
            testApplicationModule.setLevel(level);
            testApplicationModule.setApplicationId(parentApplicationModule.getApplicationId());
            testApplicationModule.setId(IdGenUtil.nextId());
            testApplicationModule.setModuleTreePath(parentApplicationModule.getModuleTreePath() + "," + testApplicationModule.getId());
        }
        if (Objects.isNull(testApplicationModule.getApplicationId())) {
            throw new CustomException("????????????ID????????????");
        }
        //2. ???????????????
        testApplicationModule = saveTestApplicationModule(testApplicationModule, userName);

        //3. ??????????????????????????????
        return convertToApplicationModuleTree(testApplicationModule);
    }

    public TestApplicationModule saveTestApplicationModule(TestApplicationModule testApplicationModule, String userName) {
        boolean flag = this.validateModuleName(testApplicationModule);
        if (flag) {
            throw new CustomException("???????????????????????????");
        }
        // liangh4???????????????????????????????????????TreePath
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
        log.info("???????????????{}", JSON.toJSONString(testApplicationModule));
        return testApplicationModule;
    }


    /**
     * ????????????id?????????????????????map
     *
     * @param appIdList
     * @return
     */
    @Override
    public Map<Long, ApplicationModuleTree> queryModuleMapByAppIdList(List<Long> appIdList) {
        Map<Long, ApplicationModuleTree> resMap = new HashMap<>();
        // 1.?????????????????????????????????
        if (CollectionUtils.isEmpty(appIdList)) {
            return resMap;
        }
        QueryWrapper<TestApplicationModule> queryWrapper = new QueryWrapper<>();
        queryWrapper.in("application_id", appIdList);
        List<TestApplicationModule> moduleList = this.list(queryWrapper);
        if (CollectionUtils.isEmpty(moduleList)) {
            return resMap;
        }
        // 2.map???????????????????????????id????????????????????????
        Map<Long, List<TestApplicationModule>> appMap = moduleList.stream().collect(Collectors.groupingBy(TestApplicationModule::getApplicationId));
        // 3.??????????????????????????????
        appMap.keySet().forEach(key -> {
            // ???????????????????????????
            List<TestApplicationModule> modules = appMap.get(key);
            // ?????????????????????????????????????????????????????????
            ApplicationModuleTree appTree = new ApplicationModuleTree();
            appTree.setId(key);
            // ????????????????????????????????????????????????(????????????)
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
     * ???????????????list???????????????????????????????????????
     *
     * @param newModuleList
     * @param appTree
     */
    private void assableAppModule(List<ApplicationModuleTree> newModuleList, ApplicationModuleTree appTree) {
        List<ApplicationModuleTree> moduleTree = this.list2Tree(newModuleList, null);
        appTree.setChildren(moduleTree);
    }

    /**
     * ????????????????????????
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
                // ?????????????????????????????????
                if (StringUtils.isNotBlank(moduleTree.getModuleTreePath())
                        && moduleTree.getModuleTreePath().split(",").length > 3) {
                    moduleTree.setLeaf(true);
                }
                treeList.add(moduleTree);
                // ???????????????????????????
                it.remove();
            }
        }
        // ???????????????
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
     * ????????????????????????????????????
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
     * ????????????
     *
     * @param userName
     * @param shiftModuleVo
     */
    @Override
    public void shiftModule(String userName, ShiftModuleVo shiftModuleVo) {
        if (Objects.isNull(shiftModuleVo.getSourceModuleId())
                || Objects.isNull(shiftModuleVo.getTargetModuleId())) {
            throw new CustomException("??????????????????????????????");
        }
        // ?????????????????????id??????
        List<Long> allModuleIdList = this.queryApplicationModuleIdListByRootId(shiftModuleVo.getSourceModuleId());
        List<TestApplicationModule> allModules = super.listByIds(allModuleIdList);
        if (CollectionUtils.isEmpty(allModules)) {
            throw new CustomException("???????????????????????????");
        }
        Long appId;
        //???????????????????????????treepath???null
        if (shiftModuleVo.isAppFlag()) {
            TestApplication application = testApplicationService.getById(shiftModuleVo.getTargetModuleId());
            if (Objects.isNull(application)) {
                throw new CustomException("?????????????????????????????????");
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
                throw new CustomException("?????????????????????????????????");
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
                    throw new CustomException("????????????????????????????????????????????????4???????????????");
                }
                module.setApplicationId(targetModule.getApplicationId());
                module.setUpdateUser(userName);
                module.setUpdateTime(LocalDateTime.now());
            });
        }
        // ??????????????????????????????????????????
        QueryWrapper testCaseQuery = new QueryWrapper();
        testCaseQuery.eq("belong_to_module", shiftModuleVo.getSourceModuleId());
        // ????????????????????????
        List<TestCase> souModuleCaseList = testCaseService.list(testCaseQuery);
        if (CollectionUtils.isNotEmpty(souModuleCaseList)) {
            souModuleCaseList.stream().forEach(source -> {
                source.setBelongToSystem(appId);
            });
            testCaseService.updateBatchById(souModuleCaseList);
        }
        // ????????????
        super.updateBatchById(allModules);
    }

//    /**
//     * ????????????????????????????????????????????????????????????????????????????????????
//     *
//     * @param shiftModuleVo
//     */
//    private List<TestCase> checkModuleTestCase(ShiftModuleVo shiftModuleVo) {
//        QueryWrapper testCaseQuery = new QueryWrapper();
//        testCaseQuery.eq("belong_to_module", shiftModuleVo.getSourceModuleId());
//        //????????????????????????
//        List<TestCase> souModuleCaseList = testCaseService.list(testCaseQuery);
//        testCaseQuery.eq("belong_to_module", shiftModuleVo.getTargetModuleId());
//        //???????????????????????????
//        List<TestCase> tarModuleCaseList = testCaseService.list(testCaseQuery);
//        if (CollectionUtils.isEmpty(souModuleCaseList) || CollectionUtils.isEmpty(tarModuleCaseList)) {
//            return souModuleCaseList;
//        }
//        StringBuilder sb = new StringBuilder();
//        // ??????????????????
//        Map<String, TestCase> testCaseMap = tarModuleCaseList.stream().collect(Collectors.toMap(TestCase::getCasename, Function.identity()));
//        souModuleCaseList.stream().forEach(source -> {
//            if (Objects.nonNull(testCaseMap.get(source.getCasename()))) {
//                sb.append(source.getCasename() + "\n");
//            }
//        });
//
//        if (StringUtils.isNotBlank(sb.toString())) {
//            throw new CustomException("??????????????????????????????????????????:\n" + sb);
//        }
//        return souModuleCaseList;
//    }


}
