package com.ziroom.qa.quality.defende.provider.caseRepository.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ziroom.qa.quality.defende.provider.caseRepository.entity.TestCase;
import com.ziroom.qa.quality.defende.provider.util.xmind.pojo.Attached;
import com.ziroom.qa.quality.defende.provider.result.RestResultVo;
import com.ziroom.qa.quality.defende.provider.vo.TestCaseVo;
import com.ziroom.qa.quality.defende.provider.vo.TestResultVo;
import com.ziroom.qa.quality.defende.provider.vo.testcase.TestCaseUploadVo;
import org.xmind.core.CoreException;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

public interface TestCaseService  extends IService<TestCase> {

    QueryWrapper<TestCase> getTestCaseQueryWrapper(TestCase testCase);

    RestResultVo<TestCase> formatTestCase(TestCase testCase);

    RestResultVo validateBelongToSystem(String belongToSystem);

    RestResultVo validateTestcaseName(Long id, String casename,Long moduleId);

    boolean batchApprovedTestCase(TestCaseVo testCaseVo);

    List<TestCase> queryTestCaseList4Task(TestCase testCase);

    TestResultVo batchDeleteTestCase(String userName, List<Long> idList);

    /**
     * 导出testcase的excel文件
     * @param idList 导出id集合，为空则只导出模板
     * @param response 请求的response
     * @param fileName 文件名
     * @param sheetName sheet名称
     */
    void exportTestCaseExcel(List<Long> idList,HttpServletResponse response,String fileName,String sheetName)throws IOException ;

    /**
     * 更新用例信息
     * @param testCase
     * @return
     */
    TestResultVo update4TestCase(TestCase testCase);

    boolean validateApplicationModuleContainsTestCase(List<Long> belongToModuleList);

    /**
     * 根据caseKey获取用例历史
     * @param caseKey
     * @return
     */
    List<TestCase> queryTestCaseHistoryByCaseId(String caseKey);

    /**
     * 根据查询条件查询待审批的测试用例数量
     * @param testCase
     * @return
     */
    List<Long> queryIdsByTestCase(TestCase testCase);

    /**
     * 分页查询
     * @param page
     * @param testCase
     * @return
     */
    Page<TestCase> queryTestCaseByPage(Page<TestCase> page, TestCase testCase);

    /**
     * 根据ID获取测试用例详情
     * @param id
     * @return
     */
    TestCase getTestCaseById(Long id);

    boolean updateTestCaseUserInfo();

    /**
     * 导出xmind信息
     * @param idList
     * @param response
     * @param fileName
     */
    void exportTestCaseXmind(List<Long> idList, HttpServletResponse response,String fileName);

    /**
     * 获取Xmind的每一个孩子节点测试用例
     * @param allChildren
     * @param testCaseUploadVo
     * @param testCaseList
     * @return
     */
    List<TestCase> getXmindChildren(List<Attached> allChildren, TestCaseUploadVo testCaseUploadVo, List<TestCase> testCaseList, StringBuilder sb ) throws Exception;

    /**
     * 上传测试用例默认属性值设置
     * @param testCase
     * @param testCaseUploadVo
     * @return
     */
    public TestCase initBelongToAttr(TestCase testCase, TestCaseUploadVo testCaseUploadVo);

    /**
     * 批量验证测试用例名称是否重复
     * @param testCaseList
     */
    public String batchValidateUploadFileTestCaseNameListRepeat(List<TestCase> testCaseList);

    /**
     * 上传Xmind测试用例
     * @param xmindRoot
     * @param testCaseUploadVo
     * @return
     * @throws IOException
     * @throws CoreException
     */
    String uploadTestCase4Xmind(Attached xmindRoot, TestCaseUploadVo testCaseUploadVo) throws Exception;

    /**
     * 根据所属模块查询测试用例列表
     * @param belongToModule
     * @return
     */
    List<TestCase> queryTestCaseListByBelongToModule(Long belongToModule);

    /**
     * 组织参数给列表页面
     * @param caseList
     * @param testCase
     */
    void assableTestInfo(List<TestCase> caseList, TestCase testCase);

    /**
     * 根据caseKey获取测试用例信息
     * @param caseKey
     * @return
     */
    TestCase getTestCaseByCaseKey(String caseKey);

    TestResultVo saveOrUpdateTestCase(TestCase testCase);

    /**
     * 批量保存测试用例信息
     *
     * @param testCaseList
     * @param uploadFlag   上传标记，如果是false则更新同名测试用例
     */
    void batchSaveTestCase(List<TestCase> testCaseList, boolean uploadFlag);
}
