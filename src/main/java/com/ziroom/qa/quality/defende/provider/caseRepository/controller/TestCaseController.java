package com.ziroom.qa.quality.defende.provider.caseRepository.controller;

import com.alibaba.excel.EasyExcel;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ziroom.qa.quality.defende.provider.caseRepository.entity.TestCase;
import com.ziroom.qa.quality.defende.provider.caseRepository.service.TestCaseService;
import com.ziroom.qa.quality.defende.provider.config.OperateLogAnnotation;
import com.ziroom.qa.quality.defende.provider.constant.OperateLogModuleConstants;
import com.ziroom.qa.quality.defende.provider.constant.OperateLogTypeConstants;
import com.ziroom.qa.quality.defende.provider.constant.enums.TestCaseStatusEnum;
import com.ziroom.qa.quality.defende.provider.listener.UploadExcelTestCaseListener;
import com.ziroom.qa.quality.defende.provider.result.RestResultVo;
import com.ziroom.qa.quality.defende.provider.util.xmind.XmindParser;
import com.ziroom.qa.quality.defende.provider.util.xmind.pojo.Attached;
import com.ziroom.qa.quality.defende.provider.vo.Pagination;
import com.ziroom.qa.quality.defende.provider.vo.TestCaseVo;
import com.ziroom.qa.quality.defende.provider.vo.TestResultVo;
import com.ziroom.qa.quality.defende.provider.vo.testcase.TestCaseDownVo;
import com.ziroom.qa.quality.defende.provider.vo.testcase.TestCaseUploadVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Api(value = "测试用例管理", tags = {"测试用例管理"})
@Slf4j
@RestController
@RequestMapping("/testCase")
public class TestCaseController {

    @Autowired
    private TestCaseService testCaseService;

    @ApiOperation("测试用例分页查询")
    @PostMapping(value = "/queryTestCaseByPage", produces = {"application/json;charset=UTF-8"})
    public RestResultVo<Page<TestCase>> queryTestCaseByPage(@RequestBody Pagination<TestCase> pagination) {
        //1. 设置分页查询参数
        TestCase testCase = pagination.getSearchObj();
        if (null == testCase) {
            testCase = new TestCase();
        }
        testCase.setDeleteFlag(false);
        testCase.setFlag(true);

        Page<TestCase> testCasePageResult = testCaseService.queryTestCaseByPage(pagination.getPage(), testCase);
        return RestResultVo.fromData(testCasePageResult);
    }

    @ApiOperation("查询待审核的测试用例Id集合")
    @PostMapping(value = "/queryIdsByUncheck", produces = {"application/json;charset=UTF-8"})
    public RestResultVo<List<Long>> queryIdsByUncheck(@RequestBody TestCase testCase) {
        RestResultVo restResultVo;
        // 设置待审核状态条件
        testCase.setTestCaseStatus(TestCaseStatusEnum.PENDING.getTestCaseStatus());
        testCase.setDeleteFlag(false);
        testCase.setFlag(true);
        try {
            List<Long> idList = testCaseService.queryIdsByTestCase(testCase);
            restResultVo = RestResultVo.fromData(idList);
        } catch (Exception e) {
            log.error("testCase/queryIdsByUncheck testCase=={} 查询待审核的测试用例Id集合失败!", testCase, e);
            restResultVo = RestResultVo.fromErrorMessage(e.getMessage());
        }
        return restResultVo;
    }

    @ApiOperation("查询审核通过的测试用例Id集合")
    @PostMapping(value = "/queryIdsByCheckPass", produces = {"application/json;charset=UTF-8"})
    public RestResultVo<List<Long>> queryIdsByCheckPass(@RequestBody TestCase testCase) {
        RestResultVo restResultVo;
        // 设置待审核状态条件
        testCase.setTestCaseStatus(TestCaseStatusEnum.APPROVED.getTestCaseStatus());
        testCase.setDeleteFlag(false);
        testCase.setFlag(true);
        try {
            List<Long> idList = testCaseService.queryIdsByTestCase(testCase);
            restResultVo = RestResultVo.fromData(idList);
        } catch (Exception e) {
            log.error("testCase/queryIdsByCheckPass testCase=={} 查询审核通过的测试用例Id集合失败!", testCase, e);
            restResultVo = RestResultVo.fromErrorMessage(e.getMessage());
        }
        return restResultVo;
    }

    @ApiOperation("查询用例库历史版本")
    @GetMapping(value = "/queryTestCaseHistoryByCaseId")
    public RestResultVo<List<TestCase>> queryTestCaseHistoryByCaseId(@RequestParam String caseKey) {
        RestResultVo restResultVo;
        try {
            List<TestCase> list = testCaseService.queryTestCaseHistoryByCaseId(caseKey);
            restResultVo = RestResultVo.fromData(list);
        } catch (Exception e) {
            log.error("testCase/queryTestCaseHistoryByCaseId id=={} 查询用例库历史版本失败!", caseKey, e);
            restResultVo = RestResultVo.fromErrorMessage(e.getMessage());
        }
        return restResultVo;
    }

    @ApiOperation("测试执行表单关联测试用例")
    @PostMapping(value = "/queryTestCaseList4Task", produces = {"application/json;charset=UTF-8"})
    public RestResultVo<List<TestCase>> queryTestCaseList4Task(@RequestBody TestCase testCase) {
        List<TestCase> testCaseList = testCaseService.queryTestCaseList4Task(testCase);
        return RestResultVo.fromData(testCaseList);
    }

    @OperateLogAnnotation(moduleName = OperateLogModuleConstants.TESTCASE, option = OperateLogTypeConstants.DETAIL)
    @ApiOperation("传值选中的测试用例")
    @PostMapping("/getCheckedTestCaseList")
    public RestResultVo<List<TestCase>> getCheckedTestCaseList(@RequestBody List<Long> testCaseIdList) {
        return RestResultVo.fromData(testCaseService.listByIds(testCaseIdList));
    }

    @ApiOperation("根据ID获取测试用例详情")
    @GetMapping(value = "/getTestCaseDetailById", produces = {"application/json;charset=UTF-8"})
    public RestResultVo<TestCase> getTestCaseDetailById(@RequestParam("id") Long id) {
        TestCase testCase = testCaseService.getTestCaseById(id);
        return RestResultVo.fromData(testCase);
    }


    @OperateLogAnnotation(moduleName = OperateLogModuleConstants.TESTCASE, option = OperateLogTypeConstants.APPROVED)
    @ApiOperation("批量审批测试用例")
    @PostMapping(value = "/batchApprovedTestCase", produces = {"application/json;charset=UTF-8"})
    public RestResultVo<String> batchApprovedTestCase(@RequestBody TestCaseVo testCaseVo,
                                                      @RequestHeader String userName) {
        try {
            testCaseVo.setUserName(userName);
            boolean result = testCaseService.batchApprovedTestCase(testCaseVo);
            if (result) {
                return RestResultVo.fromData("审批成功");
            } else {
                return RestResultVo.fromErrorMessage("审批失败");
            }
        } catch (Exception e) {
            return RestResultVo.fromErrorMessage(e.getMessage());
        }
    }

    @ApiOperation("获取测试用例所有状态")
    @GetMapping(value = "/getTestCaseStatusList", produces = {"application/json;charset=UTF-8"})
    public RestResultVo<Map<String, String>> getTestCaseStatusList() {
        Map<String, String> statusMap = new HashMap<>();
        TestCaseStatusEnum.getTestCaseStatusEnumList().stream().forEach(status -> {
            statusMap.put(status.getTestCaseStatus(), status.getTestCaseStatusName());
        });
        return RestResultVo.fromData(statusMap);
    }

    @ApiOperation("验证测试用例所属系统是否存在")
    @GetMapping(value = "/validateBelongToSystem", produces = {"application/json;charset=UTF-8"})
    public RestResultVo validateBelongToSystem(@RequestParam("belongToSystem") String belongToSystem) {
        return testCaseService.validateBelongToSystem(belongToSystem);
    }

    @ApiOperation("验证测试用例名称是否重复")
    @GetMapping("/validateTestcaseName")
    public RestResultVo validateTestcaseName(@RequestParam(value = "id", required = false) Long id,
                                             @RequestParam("casename") String casename,
                                             @RequestParam(value = "moduleId", required = false) Long moduleId) {
        return testCaseService.validateTestcaseName(id, casename, moduleId);
    }


    @OperateLogAnnotation(moduleName = OperateLogModuleConstants.TESTCASE, option = OperateLogTypeConstants.SAVE)
    @ApiOperation("保存测试用例")
    @PostMapping(value = "/saveTestCase", produces = {"application/json;charset=UTF-8"})
    public RestResultVo saveTestCase(@RequestBody TestCase testCase, HttpServletRequest request) {
        //1. 格式化测试用例参数
        String userName = request.getHeader("userName");
        testCase.setId(null);
        testCase.setCaseKey(null);
        testCase.setCreateUser(userName);
        testCase.setUpdateUser(userName);
        testCase.setPeopSaveFlag(true);
        try {
            TestResultVo resultVo = testCaseService.saveOrUpdateTestCase(testCase);
            if (resultVo.getFlag()) {
                return RestResultVo.fromData(resultVo.getMsgRes());
            } else {
                return RestResultVo.fromErrorMessage(resultVo.getMsgRes());
            }
        } catch (Exception e) {
            return RestResultVo.fromErrorMessage(e.getMessage());
        }
    }

    @OperateLogAnnotation(moduleName = OperateLogModuleConstants.TESTCASE, option = OperateLogTypeConstants.UPDATE)
    @ApiOperation("更新测试用例")
    @PostMapping(value = "/updateTestCase", produces = {"application/json;charset=UTF-8"})
    public RestResultVo updateTestCase(@RequestBody TestCase testCase, HttpServletRequest request) {
        testCase.setUpdateUser(request.getHeader("userName"));
        testCase.setPeopSaveFlag(true);
        try {
            TestResultVo resultVo = testCaseService.update4TestCase(testCase);
            if (resultVo.getFlag()) {
                return RestResultVo.fromData(resultVo.getMsgRes());
            } else {
                return RestResultVo.fromErrorMessage(resultVo.getMsgRes());
            }
        } catch (Exception e) {
            return RestResultVo.fromErrorMessage(e.getMessage());
        }
    }


    //    @OperateLogAnnotation(moduleName = OperateLogModuleConstants.TESTCASE, option = OperateLogTypeConstants.UPLOAD_EXCEL_DATA)
    @ApiOperation("上传Excel测试用例模板")
    @PostMapping(value = "/uploadTestCase")
    public RestResultVo uploadTestCase(@RequestParam("file") MultipartFile file,
                                       TestCaseUploadVo testCaseUploadVo,
                                       HttpServletRequest request) {

        RestResultVo<TestCase> restResultVo = RestResultVo.fromSuccess("成功");
        String userName = request.getHeader("userName");
        testCaseUploadVo.setUserName(userName);

        try {
            //1. 获取文件路劲
            String fileName = file.getOriginalFilename();
            if (fileName.endsWith("xmind")) {
                Attached xmindRoot = XmindParser.getXmindRootBean(file.getInputStream());
                String result = testCaseService.uploadTestCase4Xmind(xmindRoot, testCaseUploadVo);
                if (StringUtils.isNotBlank(result)) {
                    restResultVo = RestResultVo.fromErrorMessage(result);
                }
            } else {
                testCaseUploadVo.setTestCaseService(testCaseService);
                testCaseUploadVo.setUserName(userName);
                testCaseUploadVo.setRestResultVo(restResultVo);
                //导入文件名称、导入时间、导入人、导入成功总数、导入失败总数、所属系统、关联需求、用例类型
                EasyExcel.read(file.getInputStream(), TestCaseDownVo.class, new UploadExcelTestCaseListener(testCaseUploadVo)).sheet().doRead();
                restResultVo = testCaseUploadVo.getRestResultVo();
                if (!restResultVo.isSuccess()) {
                    restResultVo = RestResultVo.fromErrorMessage(restResultVo.getMessage());
                }
            }
        } catch (Exception e) {
            log.error("/testCase/uploadTestCase4Excel userName=={},失败原因:", userName, e);
            return RestResultVo.fromErrorMessage(e.getMessage());
        }
        return restResultVo;
    }

    @OperateLogAnnotation(moduleName = OperateLogModuleConstants.TESTCASE, option = OperateLogTypeConstants.DOWNLOAD_EXCEL_TEMPLATE)
    @ApiOperation("导出Excel测试用例模板")
    @PostMapping(value = "/exportTestCaseExcelTemplate", produces = {"application/json;charset=UTF-8"})
    public void exportTestCaseExcelTemplate(@RequestParam(value = "idList", required = false) List<Long> idList, HttpServletResponse response) throws IOException {
        try {
            //1. 设置导出变量名称
            String fileName = URLEncoder.encode("TestCaseTemplate", "UTF-8");
            String sheetName = URLEncoder.encode("TestCaseTemplate", "UTF-8");
            //2. 执行导出
            testCaseService.exportTestCaseExcel(idList, response, fileName, sheetName);
        } catch (Exception e) {
            //3. 处理异常
            log.error("导出Excel测试用例模板 idList=={},失败原因:", JSON.toJSONString(idList), e);
        }
    }

    @OperateLogAnnotation(moduleName = OperateLogModuleConstants.TESTCASE, option = OperateLogTypeConstants.DETELE)
    @ApiOperation("批量删除测试用例")
    @GetMapping(value = "/batchDeleteTestCase", produces = {"application/json;charset=UTF-8"})
    public RestResultVo<String> batchDeleteTestCase(@RequestParam List<Long> idList, HttpServletRequest request) {
        RestResultVo restResultVo;
        String userName = request.getHeader("userName");
        try {
            TestResultVo vo = testCaseService.batchDeleteTestCase(userName, idList);
            if (vo.getFlag()) {
                restResultVo = RestResultVo.fromSuccess(vo.getMsgRes());
            } else {
                restResultVo = RestResultVo.fromErrorMessage(vo.getMsgRes());
            }
        } catch (Exception e) {
            log.error("批量删除测试用例失败idList=={},失败原因:", JSON.toJSONString(idList), e);
            restResultVo = RestResultVo.fromErrorMessage(e.getMessage());
        }
        return restResultVo;
    }

    @ApiOperation("数据清洗-测试用例用户信息")
    @GetMapping("/updateTestCaseUserInfo")
    public RestResultVo<Boolean> updateTestCaseUserInfo() {
        boolean result = testCaseService.updateTestCaseUserInfo();
        return RestResultVo.fromData(result);
    }


    @OperateLogAnnotation(moduleName = OperateLogModuleConstants.TESTCASE, option = OperateLogTypeConstants.DOWNLOAD_XMIND_TEMPLATE)
    @ApiOperation("导出XMIND测试用例")
    @GetMapping(value = "/exportTestCaseXmindTemplate", produces = {"application/json;charset=UTF-8"})
    public void exportTestCaseXmindTemplate(@RequestParam(value = "idList", required = false) List<Long> idList, HttpServletResponse response) throws IOException {
        try {
            //1. 执行导出
            String fileName = URLEncoder.encode("测试用例.xmind", "UTF-8").replaceAll("\\+", "%20");
            fileName = new String(fileName.getBytes(), "UTF-8");
            response.setContentType("application/octet-stream;charset=UTF-8");
            response.setHeader("Content-Disposition", "attachment;filename=" + fileName);
            response.addHeader("Pargam", "no-cache");
            response.addHeader("Cache-Control", "no-cache");
            testCaseService.exportTestCaseXmind(idList, response, fileName);
        } catch (Exception e) {
            //2. 处理异常
//            response.reset();
//            response.setContentType("application/json");
//            response.setCharacterEncoding("utf-8");
//            Map<String, String> map = new HashMap<String, String>();
//            map.put("status", "failure");
//            map.put("message", "下载文件失败" + e.getMessage());
//            response.getWriter().println(JSON.toJSONString(map));
            log.error("导出XMIND测试用例 idList=={},失败原因:", JSON.toJSONString(idList), e);
        }
    }


    @GetMapping("/updateTestCaseKeyInfo")
    public RestResultVo updateTestCaseKeyInfo(@RequestParam List<String> caseKeyList) {
        QueryWrapper<TestCase> queryWrapper = new QueryWrapper();
        queryWrapper.in("case_key", caseKeyList);
        queryWrapper.eq("flag", true);
        queryWrapper.eq("delete_flag", false);
        List<TestCase> list = testCaseService.list(queryWrapper);
        if (CollectionUtils.isEmpty(list)) {
            return RestResultVo.fromErrorMessage("没有找到数据");
        }
//        Map<String, List<TestCase>> maps = list.stream().collect(Collectors.groupingBy(TestCase::getCaseKey));
        list.stream().forEach(testCase -> {
            testCase.setRemarks(testCase.getCaseKey());
            testCase.setCaseKey(UUID.randomUUID().toString().replace("-", ""));
        });
        testCaseService.updateBatchById(list);
        return RestResultVo.fromData("成功");
    }

}
