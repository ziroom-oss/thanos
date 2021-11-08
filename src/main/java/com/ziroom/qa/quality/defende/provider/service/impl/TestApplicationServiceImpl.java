package com.ziroom.qa.quality.defende.provider.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ziroom.qa.quality.defende.provider.caseRepository.service.TestCaseService;
import com.ziroom.qa.quality.defende.provider.entity.TestApplication;
import com.ziroom.qa.quality.defende.provider.entity.TestApplicationModule;
import com.ziroom.qa.quality.defende.provider.mapper.TestApplicationMapper;
import com.ziroom.qa.quality.defende.provider.outinterface.client.service.EhrService;
import com.ziroom.qa.quality.defende.provider.outinterface.client.service.MatrixService;
import com.ziroom.qa.quality.defende.provider.result.RestResultVo;
import com.ziroom.qa.quality.defende.provider.service.*;
import com.ziroom.qa.quality.defende.provider.vo.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class TestApplicationServiceImpl extends ServiceImpl<TestApplicationMapper, TestApplication> implements TestApplicationService {
    @Autowired
    private TestApplicationModuleService testApplicationModuleService;
    @Autowired
    private TestCaseService testCaseService;
    @Autowired
    private EhrService ehrService;
    @Autowired
    private MatrixService matrixService;

    @Override
    public Page<TestApplication> queryTestApplicationByPage(Pagination<TestApplication> pagination) {
        //1. 格式化查询参数
        QueryWrapper<TestApplication> testApplicationWrapper = new QueryWrapper<>();
        TestApplication testApplication = pagination.getSearchObj();
        if (Objects.nonNull(testApplication)) {
            testApplicationWrapper
                    .like(StringUtils.isNotBlank(testApplication.getApplicationName()), "application_name", testApplication.getApplicationName())
                    .like(StringUtils.isNotBlank(testApplication.getApplicationType()), "application_type", testApplication.getApplicationType())
                    .like(StringUtils.isNotBlank(testApplication.getEhrTreePath()), "ehr_tree_path", testApplication.getEhrTreePath())
                    .like(StringUtils.isNotBlank(testApplication.getApplicationHostname()), "application_hostname", testApplication.getApplicationHostname())
            ;
        }
        testApplicationWrapper.orderByDesc("id");
        //2. 分页查询
        Page<TestApplication> page = this.page(pagination.getPage(), testApplicationWrapper);

        if (Objects.nonNull(page) && CollectionUtils.isNotEmpty(page.getRecords())) {
            List<String> updateUserList = page.getRecords().stream().distinct().map(TestApplication::getUpdateUser).collect(Collectors.toList());
            Map<String, MatrixUserDetail> userMap = matrixService.getUserDetailByEmailPre(updateUserList);
            if (MapUtils.isNotEmpty(userMap)) {
                page.getRecords().stream().forEach(application -> {
                    application.setUpdateUserStr(Optional.ofNullable(userMap)
                            .map(item -> item.get(application.getUpdateUser()))
                            .map(detail -> detail.getUserName())
                            .orElse(""));
                });
            }
        }
        return page;
    }

    @Override
    public RestResultVo saveOrUpdateTestApplication(TestApplication testApplication, String userName) {
        //1. 新增应用
        String ehrTreePath = testApplication.getEhrTreePath();
        String deptCode = ehrTreePath.substring(ehrTreePath.lastIndexOf(",") + 1);
        EhrDeptInfo ehrInfo = ehrService.getDeptInfoFromEhrByDeptId(deptCode);
        testApplication.setEhrGroup(ehrInfo.getDescrShort());
        testApplication.setCreateTime(LocalDateTime.now());
        testApplication.setUpdateTime(LocalDateTime.now());
        testApplication.setCreateUser(userName);
        testApplication.setUpdateUser(userName);
        boolean result = this.saveOrUpdate(testApplication);

        //2. 新增所属模块
        if (result) {
            return RestResultVo.fromData("保存所属应用成功");
        } else {
            return RestResultVo.fromErrorMessage("保存所属应用失败");
        }
    }

    @Override
    public boolean validateTestApplicationName(String applicationName, Long id) {
        boolean result;
        if (Objects.nonNull(id)) {//编辑
            TestApplication testApplication = this.getById(id);
            String oldApplicationName = testApplication.getApplicationName();
            if (oldApplicationName.equals(applicationName)) {
                result = true;
            } else {
                result = queryTestApplication(applicationName);
            }
        } else {
            result = queryTestApplication(applicationName);
        }
        return result;
    }

    @Override
    public TestApplication queryTestApplicationDetailById(Long id) {
        //1. 获取所属应用信息
        TestApplication testApplication = this.getById(id);

        //2. 获取所属应用模块信息
        List<ApplicationModuleTree> applicationModuleTreeList = testApplicationModuleService.queryApplicationModuleTreeByApplicationId(id);
        testApplication.setApplicationModuleTreeList(applicationModuleTreeList);

        return testApplication;
    }

    @Autowired
    private TestAppUserRelService testAppUserRelService;

    /**
     * 搜索应用和模块的树状结构
     *
     * @param userName
     * @param ehrTreePath
     * @return
     */
    @Override
    public List<ApplicationModuleTree> queryApplicationAndModule(String userName, String ehrTreePath) {
        // 获取treePath所有应用
        List<TestApplication> appList = this.listByEhrTreePath(ehrTreePath);
        if (CollectionUtils.isEmpty(appList)) {
            return null;
        }
        List<ApplicationModuleTree> resList = this.getAppTreeList(appList);
        // 查询已经关联的appid集合
        List<Long> followAppIdList = testAppUserRelService.findFollowAppByEmailPre(userName);
        if (CollectionUtils.isNotEmpty(followAppIdList)) {
            resList.stream().forEach(app -> {
                if (app.isApp() && followAppIdList.contains(app.getId())) {
                    app.setFollowFlag(true);
                }
            });
        }
        return resList;
    }

    /**
     * 搜索我的应用和模块的树状结构
     *
     * @param userName
     * @param ehrTreePath
     * @return
     */
    @Override
    public List<ApplicationModuleTree> queryMyApplicationAndModule(String userName, String ehrTreePath) {
        // 获取treePath所有应用
        List<TestApplication> appList = this.listByEhrTreePath(ehrTreePath);
        if (CollectionUtils.isEmpty(appList)) {
            return null;
        }
        List<Long> followAppIdList = testAppUserRelService.findFollowAppByEmailPre(userName);
        if (CollectionUtils.isEmpty(followAppIdList)) {
            return null;
        }
        // 过滤已关注的用户应用
        appList = appList.stream().filter(app -> followAppIdList.contains(app.getId())).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(appList)) {
            return null;
        }
        List<ApplicationModuleTree> resList = this.getAppTreeList(appList);
        // 查询已经关联的appid集合
        if (CollectionUtils.isNotEmpty(resList)) {
            resList.stream().forEach(app -> {
                if (app.isApp()) {
                    app.setFollowFlag(true);
                }
            });
        }
        return resList;
    }

    /**
     * 获取应用模块树
     *
     * @param appList
     * @return
     */
    private List<ApplicationModuleTree> getAppTreeList(List<TestApplication> appList) {
        List<ApplicationModuleTree> resList = new ArrayList<>();
        // 1.转成所有的应用id集合去查询所有模块
        List<Long> appIdList = appList.stream().map(TestApplication::getId).collect(Collectors.toList());
        Map<Long, ApplicationModuleTree> appMap = testApplicationModuleService.queryModuleMapByAppIdList(appIdList);

        if (MapUtils.isEmpty(appMap)) {
            List<ApplicationModuleTree> finalResList = resList;
            appList.forEach(app -> {
                ApplicationModuleTree tree = ApplicationModuleTree.builder().id(app.getId()).isApp(true).label(app.getApplicationName()).build();
                finalResList.add(tree);
            });
            return finalResList;
        }
        // 2.遍历map赋值上应用名称
        for (TestApplication application : appList) {
            if (Objects.isNull(appMap.get(application.getId()))) {
                ApplicationModuleTree newTree = ApplicationModuleTree.builder().id(application.getId()).isApp(true).build();
                appMap.put(application.getId(), newTree);
            }
        }

        Map<Long, String> nameMap = appList.stream().collect(Collectors.toMap(TestApplication::getId, TestApplication::getApplicationName));
        appMap.keySet().forEach(key -> {
            appMap.get(key).setLabel(nameMap.get(key));
        });
        // 3.转换为前端使用的list
        resList = appMap.entrySet().stream().map(e -> e.getValue()).collect(Collectors.toList());
        return resList;
    }

    /**
     * 根据ehrtreepath获取应用列表信息
     *
     * @param ehrTreePath
     * @return
     */
    @Override
    public List<TestApplication> listByEhrTreePath(String ehrTreePath) {
        QueryWrapper<TestApplication> testApplicationQueryWrapper = new QueryWrapper<>();
        testApplicationQueryWrapper.like(StringUtils.isNotBlank(ehrTreePath), "ehr_tree_path", ehrTreePath);
        return this.list(testApplicationQueryWrapper);
    }

    /**
     * 根据ID删除应用
     *
     * @param id
     */
    @Override
    public String deleteTestApplicationById(Long id) {
        String message = "删除应用成功";
        List<TestApplicationModule> testApplicationModuleList = testApplicationModuleService.queryApplicationModuleListByApplicationId(id);
        if (CollectionUtils.isNotEmpty(testApplicationModuleList)) {
            RestResultVo result = testApplicationModuleService.deleteTestApplicationModuleByIdList(testApplicationModuleList.stream().map(TestApplicationModule::getId).collect(Collectors.toList()));
            if (!result.isSuccess()) {
                return result.getMessage();
            }
            this.removeById(id);
        }
        return message;
    }

    public boolean queryTestApplication(String applicationName) {
        QueryWrapper<TestApplication> testApplicationQueryWrapper = new QueryWrapper<>();
        testApplicationQueryWrapper.eq("application_name", applicationName);
        List<TestApplication> testApplicationList = this.list(testApplicationQueryWrapper);
        if (CollectionUtils.isEmpty(testApplicationList)) {
            return true;
        } else {
            return false;
        }
    }
}
