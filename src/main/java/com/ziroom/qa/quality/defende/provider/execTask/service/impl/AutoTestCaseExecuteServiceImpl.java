package com.ziroom.qa.quality.defende.provider.execTask.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.ziroom.qa.quality.defende.provider.caseRepository.entity.AutoSingleApi;
import com.ziroom.qa.quality.defende.provider.caseRepository.entity.AutoSingleApiCase;
import com.ziroom.qa.quality.defende.provider.caseRepository.service.IAutoSingleApiCaseService;
import com.ziroom.qa.quality.defende.provider.caseRepository.service.IAutoSingleApiService;
import com.ziroom.qa.quality.defende.provider.caseRepository.service.IAutoSingleAssertService;
import com.ziroom.qa.quality.defende.provider.constant.enums.AssertTypeEnum;
import com.ziroom.qa.quality.defende.provider.constant.enums.ExecutionResultEnum;
import com.ziroom.qa.quality.defende.provider.execTask.entity.AutoExecutionRecord;
import com.ziroom.qa.quality.defende.provider.execTask.entity.AutoExecutionRecordAssert;
import com.ziroom.qa.quality.defende.provider.execTask.entity.dto.RunByCaseDetailDto;
import com.ziroom.qa.quality.defende.provider.execTask.entity.dto.RunCaseByIdListDto;
import com.ziroom.qa.quality.defende.provider.execTask.entity.dto.SUTRequestDto;
import com.ziroom.qa.quality.defende.provider.execTask.entity.vo.AutoExecutionRecordVo;
import com.ziroom.qa.quality.defende.provider.execTask.service.IAutoExecutionRecordAssertService;
import com.ziroom.qa.quality.defende.provider.execTask.service.IAutoExecutionRecordService;
import com.ziroom.qa.quality.defende.provider.execTask.service.IAutoTestCaseExecuteService;
import com.ziroom.qa.quality.defende.provider.execTask.service.TestTaskOutService;
import com.ziroom.qa.quality.defende.provider.execTask.service.api.RequestHandlerService;
import com.ziroom.qa.quality.defende.provider.outinterface.client.service.OmegaService;
import com.ziroom.qa.quality.defende.provider.outinterface.client.service.MatrixService;
import com.ziroom.qa.quality.defende.provider.outinterface.client.service.WeChatService;
import com.ziroom.qa.quality.defende.provider.util.DateUtil;
import com.ziroom.qa.quality.defende.provider.util.StringUtil;
import com.ziroom.qa.quality.defende.provider.util.handler.ApiHandler;
import com.ziroom.qa.quality.defende.provider.util.handler.FormatResultVo;
import com.ziroom.qa.quality.defende.provider.vo.MatrixUserDetail;
import com.ziroom.qa.quality.defende.provider.vo.outvo.OutReqVo;
import com.ziroom.qa.quality.defende.provider.vo.outvo.OutResVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author zhujj5
 * @Title:
 * @Description:
 * @date 2021/9/24 5:36 下午
 */
@Service
@Slf4j
public class AutoTestCaseExecuteServiceImpl implements IAutoTestCaseExecuteService {
    @Autowired
    private IAutoSingleApiCaseService autoSingleApiCaseService;
    @Autowired
    private IAutoSingleApiService autoSingleApiService;
    @Autowired
    private RequestHandlerService requestHandlerService;
    @Autowired
    private ApiHandler apiHandler;
    @Autowired
    private IAutoExecutionRecordAssertService autoExecutionRecordAssertService;
    @Autowired
    private IAutoExecutionRecordService autoExecutionRecordService;
    @Autowired
    private IAutoSingleAssertService autoSingleAssertService;

    @Autowired
    private TestTaskOutService testTaskOutService;
    @Autowired
    private OmegaService omegaService;
    @Autowired
    private MatrixService matrixService;

    @Autowired
    private WeChatService weChatService;
    private String jiraId = "ISSUE-6666";
    @Override
    public List<AutoExecutionRecordVo> runCaseByIdList(RunCaseByIdListDto dto) {
        List<Integer> idList = dto.getCaseIdList();
        ArrayList<AutoExecutionRecordVo> res=new ArrayList<>();
        idList.stream().forEach(id -> {
            AutoSingleApiCase apiCase = autoSingleApiCaseService.getById(id);
            AutoSingleApi api = autoSingleApiService.getById(apiCase.getApiId());
//          构造组装请求参数
            RunByCaseDetailDto runByCaseDetailDto =new RunByCaseDetailDto();
            BeanUtils.copyProperties(api, runByCaseDetailDto);
            BeanUtils.copyProperties(apiCase, runByCaseDetailDto);
            BeanUtils.copyProperties(dto, runByCaseDetailDto);
//          组装断言
            runByCaseDetailDto.setAssertList(autoSingleAssertService.getAutoSingleAssertsByCaseId(id));
            AutoExecutionRecordVo autoExecutionRecordVo = this.runCaseByDetail(runByCaseDetailDto);
            res.add(autoExecutionRecordVo);
        });
        return res;
    }

    @Override
    public AutoExecutionRecordVo runCaseByDetail(RunByCaseDetailDto runByCaseDetailDto) {
//      返回结果
        AutoExecutionRecordVo autoExecutionRecordVo =new AutoExecutionRecordVo();
        autoExecutionRecordVo.setCaseName(runByCaseDetailDto.getCaseName());
        autoExecutionRecordVo.setApplicationName(runByCaseDetailDto.getApplicationName());
        FormatResultVo formatResultVo;
        try {
            //      依赖处理，组装出直接可发起http请求的对象
            SUTRequestDto requestDto = requestHandlerService.requestProcess(runByCaseDetailDto);
            //      发起http请求
            formatResultVo = apiHandler.runApi(requestDto);
        }catch (Exception e){
            formatResultVo = new FormatResultVo();
            String s = e.getCause() == null ? "" : "。\n原因：" + e.getCause();
            formatResultVo.setActualResult("失败提示:"+e.getMessage()+s);
//      初始化断言记录并保存
            AutoExecutionRecord executeRecord = autoExecutionRecordService.saveExecuteRecord(runByCaseDetailDto,formatResultVo);
            BeanUtils.copyProperties(executeRecord,autoExecutionRecordVo);
            return autoExecutionRecordVo;
        }
//      初始化断言记录并保存
        AutoExecutionRecord executeRecord = autoExecutionRecordService.saveExecuteRecord(runByCaseDetailDto,formatResultVo);

//      开始断言并保存每一条断言记录
        List<AutoExecutionRecordAssert> executionRecordAssertList =
                autoExecutionRecordAssertService.assertAndSave(executeRecord,formatResultVo);
//      汇总断言结果，断言覆盖率 并更新断言记录

        updateAssertRecord(executeRecord, executionRecordAssertList,formatResultVo);
//        存储时有可能被截取了，要恢复原样。更新时仍然要保持被截取状态！！！所以要在这修改
        executeRecord.setActualResult(formatResultVo.getActualResult());
        BeanUtils.copyProperties(executeRecord,autoExecutionRecordVo);
//        autoExecutionRecordVo.setRequestUrl(executeRecord.getRequestUrl());
        autoExecutionRecordVo.setList(executionRecordAssertList);
        return autoExecutionRecordVo;
    }

    private void updateAssertRecord(AutoExecutionRecord executeRecord, List<AutoExecutionRecordAssert> executionRecordAssertList, FormatResultVo formatResultVo) {
        List<AutoExecutionRecordAssert> failAssert = executionRecordAssertList.stream().filter(item ->
                item.getAssertResult().equals(ExecutionResultEnum.FAILURE.toString())).collect(Collectors.toList());
        List<AutoExecutionRecordAssert> successAssert = executionRecordAssertList.stream().filter(item ->
                item.getAssertResult().equals(ExecutionResultEnum.SUCCESS.toString())&&item.getAssertType()==AssertTypeEnum.JSONPATH.getKey().longValue()).collect(Collectors.toList());
        if(failAssert.size()<=0){
//          更新为失败
            executeRecord.setRecordResult(ExecutionResultEnum.SUCCESS.toString());
            int i = StringUtil.countStr(formatResultVo.getActualResult(), ":");
            BigDecimal bigDecimal = new BigDecimal(successAssert.size()).divide(new BigDecimal(i),3, RoundingMode.HALF_UP).multiply(new BigDecimal(100));
            executeRecord.setSuccessCoverage(bigDecimal);
        }
        autoExecutionRecordService.updateById(executeRecord);
    }

    /**
     * omega ci 过程中提测，qa及其它需求环境部署后，触发全量回归类型接口任务集合
     *
     */
    public void runAllCaseByAppName(String env ,String applicationName,String branch){
//        1.构造要执行的用例集合
        List<AutoSingleApiCase> list = getCaseList(applicationName);
        if (list.size()<=0){
            log.warn("业务线未添加测试用例请尽快添加",applicationName);
            //        4.发送通知：邮件+企微
            dealResult(null,applicationName,env,"http://qa.kp.ziroom.com/testCase");
        }else{
//        2.执行任务集合
            List<AutoExecutionRecordVo> executionRecordEntities = runCaseList(env, list);
//        3.同步质量保障平台
            String taskUrl = synDefend(executionRecordEntities, applicationName, env, branch);

//        4.发送通知：邮件+企微
            dealResult(executionRecordEntities,applicationName,env,taskUrl);
        }
    }

    /**
     *
     * @param executionRecordEntities
     * @param applicationName
     * @param env
     * @param taskUrl
     */
    private void dealResult(List<AutoExecutionRecordVo> executionRecordEntities, String applicationName, String env, String taskUrl) {
//        通知的人
        List<String> userEmailList = omegaService.getEmailList(applicationName,null);
//        构造发送邮件数据
        Map<String, Object> model =null;
        if(!CollectionUtils.isEmpty(executionRecordEntities)){
            model = getModel(executionRecordEntities, applicationName, env);
//        发送邮件
//        sendEmail(userEmailList, model);
        }

//      构造发送企微数据
        String content = getContent(applicationName,userEmailList,model,taskUrl);
//        发送企微
        weChatService.sendMarkdown(userEmailList,content);

    }

    /** 您的参与的服务已成回归测试
     *                                 >**项目详情**
     *                                 >名  称：<font color=\"info\">telot</font>
     *                                 >环  境：qua
     *                                 >参与者：
     *                                 >
     *                                 >总 数：<font color=\"info\"></font>
     *                                 >失 败：<font color=\"warning\"></font>
     *                                 >成 功：<font color=\"comment\"></font>
     *                                 >成功率：<font color=\"warning\"></font>
     *                                 >
     *                                 >如需修改会议信息，请点击：[修改会议信息](https://work.weixin.qq.com)
     */
    private String getContent(String applicationName, List<String> userEmailList, Map<String, Object> model, String taskUrl) {
        StringBuffer stringBuffer = new StringBuffer();
        Map<String, MatrixUserDetail> userDetailByEmailPre = matrixService.getUserDetailByEmailPre(userEmailList);
        userEmailList.forEach(user ->{
            if(userDetailByEmailPre.containsKey(user)){
                stringBuffer.append(userDetailByEmailPre.get(user).getUserName()).append(" ");
            }
        });
        String format ;
        if(model==null||model.get("totalNum")==null||"0".equals(model.get("totalNum").toString())){
            format = String.format("您参与的服务尚未添加测试用例，请尽快添加！\n" +
                            "                              >名　称：<font color=\"info\">%s</font>\n" +
                            "                              >环　境：%s\n" +
                            "                              >参与者：%s\n" +
                            "                              [点击添加](%s)", applicationName, "qua", stringBuffer,taskUrl);
        }else{
            format = String.format("您参与的服务完成自动化回归测试\n" +
                            "                              >名　称：<font color=\"info\">%s</font>\n" +
                            "                              >环　境：%s\n" +
                            "                              >参与者：%s\n" +
                            "                              >\n" +
                            "                              >总　数：<font color=\"info\">%d</font>\n" +
                            "                              >失　败：<font color=\"warning\">%d</font>\n" +
                            "                              >成　功：<font color=\"info\">%d</font>\n" +
                            "                              >成功率：<font color=\"warning\">%d%%</font>\n" +
                            "                              [点击处理失败用例](%s)", model.get("applicationName"), model.get("env"), stringBuffer,model.get("totalNum"),
                    model.get("failNum"), model.get("successNum"), model.get("successRate"),taskUrl);
        }
        return format;
    }

    private Map<String, Object> getModel(List<AutoExecutionRecordVo> executionRecordEntities, String applicationName, String env) {
        Map<String, Object> model = new HashMap<>(6);
        model.put("applicationName", applicationName);
        model.put("env", env);
        model.put("successNum", 0);
        model.put("failNum", 0);
        executionRecordEntities.forEach(record ->{
            if("success".equalsIgnoreCase(record.getRecordResult())){
                calNum("successNum",model);
            }else{
                calNum("failNum",model);
            }
        });
//        计算总数和成功率
        calTotal(model);
        return model;
    }
    private void calNum(String key ,Map<String, Object> model) {
        Object successNum = model.get(key);
        if(successNum==null){
            model.put(key,1);
        }else{
            model.put(key,(Integer)successNum+1);
        }
    }
    private void calTotal(Map<String, Object> model) {
        Integer successNum = (Integer) Optional.ofNullable(model.get("successNum")).orElse(0);
        Integer failNum = (Integer) Optional.ofNullable(model.get("failNum")).orElse(0);
        Integer total = successNum+failNum;
        model.put("totalNum", total);
        if(total>0){
            model.put("successRate", new BigDecimal(successNum).divide(new BigDecimal(total),2, BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal(100)).intValue());
        }else{
            model.put("successRate",0);
        }
    }

    private String synDefend(List<AutoExecutionRecordVo> executionRecordEntities, String applicationName, String env, String branch) {
        if(executionRecordEntities==null||executionRecordEntities.size()<=0){
            return null;
        }
        OutReqVo outReqVo = new OutReqVo();
        outReqVo.setJiraId(jiraId);
        outReqVo.setAutoRecordList(executionRecordEntities);
        outReqVo.setBranchName(branch);
        List<String> testEmailList = omegaService.getEmailList(applicationName,"管理员");
        if(!CollectionUtils.isEmpty(testEmailList)){
            outReqVo.setTestUserName(testEmailList.get(0));
        }else{
            log.error("没找到项目负责人！！！！");
            outReqVo.setTestUserName("zhujj5");
        }
        outReqVo.setTaskName("自动回归测试-"+applicationName+"-"+ DateUtil.dateStr("yyyyMMdd-HHmmss"));
        OutResVo autoTestExecution = testTaskOutService.createAutoTestExecution(outReqVo);
        log.info("createAutoTestExecution res -> [{}]",autoTestExecution);
        if(autoTestExecution!=null){
            return autoTestExecution.getTestTaskUrl();
        }else{
            return null;
        }
    }

    private List<AutoExecutionRecordVo> runCaseList(String env, List<AutoSingleApiCase> list) {
        List<Integer> caseIdList = list.stream().map(AutoSingleApiCase::getId).map(item ->Integer.valueOf(item.toString())).collect(Collectors.toList());
        RunCaseByIdListDto dto = new RunCaseByIdListDto();
        dto.setEnv(env);
        dto.setCaseIdList(caseIdList);
        dto.setUserCode("zhujj5");
        List<AutoExecutionRecordVo> autoExecutionRecordVos = runCaseByIdList(dto);
        return autoExecutionRecordVos;
    }


    public List<AutoSingleApiCase> getCaseList(String applicationName) {
        QueryWrapper<AutoSingleApi> queryWrapper =new QueryWrapper<>();
        queryWrapper.eq("application_name",applicationName)
            .eq("deleted",0);
        List<Integer> apiList = autoSingleApiService.list(queryWrapper).stream().
                map(AutoSingleApi::getId).collect(Collectors.toList());
        QueryWrapper<AutoSingleApiCase> caseListWrapper =new QueryWrapper<>();
        if(apiList.size()<=0){
            return new ArrayList();
        }
        caseListWrapper.in("api_id",apiList)
            .eq("deleted",0);
        List<AutoSingleApiCase> list = autoSingleApiCaseService.list(caseListWrapper);
        List<AutoSingleApiCase> runCaseList = list.stream().
                filter(caseDto -> !caseDto.getCaseName().toLowerCase(Locale.ROOT).contains("swagger生成")).
                collect(Collectors.toList());

        return runCaseList;
    }


}
