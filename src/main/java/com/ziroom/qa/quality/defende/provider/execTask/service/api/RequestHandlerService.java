package com.ziroom.qa.quality.defende.provider.execTask.service.api;

import com.alibaba.fastjson.JSONPath;
import com.ziroom.qa.quality.defende.provider.data.entity.AutoDataDatabase;
import com.ziroom.qa.quality.defende.provider.data.entity.dto.AutoDataDatabaseDto;
import com.ziroom.qa.quality.defende.provider.data.entity.dto.ExecSqlDto;
import com.ziroom.qa.quality.defende.provider.data.service.IAutoDataDatabaseService;
import com.ziroom.qa.quality.defende.provider.execTask.entity.dto.RunByCaseDetailDto;
import com.ziroom.qa.quality.defende.provider.execTask.entity.dto.RunCaseByIdListDto;
import com.ziroom.qa.quality.defende.provider.execTask.entity.dto.SUTRequestDto;
import com.ziroom.qa.quality.defende.provider.execTask.entity.vo.AutoExecutionRecordVo;
import com.ziroom.qa.quality.defende.provider.execTask.service.IAutoTestCaseExecuteService;
import com.ziroom.qa.quality.defende.provider.outinterface.client.service.OmegaService;
import com.ziroom.qa.quality.defende.provider.result.CustomException;
import com.ziroom.qa.quality.defende.provider.util.RegexUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author zhujj5
 * @Title:
 * @Description:
 * @date 2021/9/26 2:09 下午
 */
@Service
@Slf4j
public class RequestHandlerService {
    @Autowired
    private IAutoDataDatabaseService autoDataDatabaseService;
    @Autowired
    private OmegaService omegaService;
    @Autowired
    private IAutoTestCaseExecuteService autoTestCaseExecuteService;
    public SUTRequestDto requestProcess(RunByCaseDetailDto detail){
        HashMap requestPool = new HashMap();
        if(!StringUtils.isEmpty(detail.getPreRequest())){
//          接口依赖参数
            apiPoolProcess(detail,requestPool);
        }
        if(!StringUtils.isEmpty(detail.getPreSql())){
//          sql依赖参数
            sqlPoolProcess(detail,requestPool);
        }
//          参数替换
        requestReplace(detail,requestPool);
//      组装请求参数
        SUTRequestDto requestDto = initRequestDto(detail);
        return requestDto;
    }
    private void apiPoolProcess(RunByCaseDetailDto detail, HashMap requestPool) {
        List<AutoExecutionRecordVo> autoExecutionRecordVos = runDepencyCase(detail);
        autoExecutionRecordVos.forEach(record -> {
//            if(ExecutionResultEnum.SUCCESS.toString().equals(recordResult)){
//                断言成功的用例才能被依赖
                String actualResult = record.getActualResult();
                requestPool.put(String.valueOf(record.getCaseId()),actualResult);
//            }else{
//                throw new CustomException("用例断言失败，请检查用例。caseId"+record.getCaseId());
//            }

        });
    }

    private List<AutoExecutionRecordVo> runDepencyCase(RunByCaseDetailDto detail) {
        String preRequest = detail.getPreRequest();
        RunCaseByIdListDto dto = new RunCaseByIdListDto();
        List<Integer> strings = Arrays.asList(preRequest.split(",")).stream().map(id ->Integer.valueOf(id))
                .collect(Collectors.toList());
//        if(strings.contains(detail.getId())){
//            throw new CustomException("用例出现循环依赖，用例ID"+detail.getId());
//        }
        dto.setCaseIdList(strings);
        dto.setEnv(detail.getEnv());
        dto.setUserCode(detail.getUserCode());
        List<AutoExecutionRecordVo> autoExecutionRecordVos = autoTestCaseExecuteService.runCaseByIdList(dto);
        return autoExecutionRecordVos;
    }

    private SUTRequestDto initRequestDto(RunByCaseDetailDto detail) {
        SUTRequestDto requestDto = new SUTRequestDto();
        BeanUtils.copyProperties(detail,requestDto);
        requestDto.setAssertList(detail.getAssertList());
        requestDto.setHeader(detail.getHeader());
        /*
         * 请求URL组装
         */
//      从omega获取域名
        String domain = omegaService.getDomainByEvn(detail.getApplicationName(),detail.getEnv());
        final String[] url = {detail.getProtocolType() + "://" + domain + detail.getRequestUri()};
//      拼接参数
        Optional.ofNullable(detail.getRequestParam()).ifPresent(param ->{
                    if(param.trim().length()>0){
                        url[0] = url[0] +"?"+param;
                    }
                }
        );
        requestDto.setRequestUri(url[0]);
        return requestDto;
    }

    private void sqlPoolProcess(RunByCaseDetailDto detail, HashMap requestPool) {
        String preSql = detail.getPreSql();
        Optional.ofNullable(preSql).filter(preSqlTemp -> preSqlTemp.length()>0).ifPresent(preSqlTemp -> {
            preSqlTemp = preSqlTemp.replace("\n","");
            String[] split = preSqlTemp.split(";");
            AutoDataDatabaseDto appAndEnv = new AutoDataDatabaseDto();
            appAndEnv.setApplicationName(detail.getApplicationName());
            appAndEnv.setEnv(detail.getEnv());
            List<AutoDataDatabase> autoDataDatabaseVos = autoDataDatabaseService.
                    getAutoDataDatabases(appAndEnv);
            if(autoDataDatabaseVos.size()<=0){
                throw new CustomException("未绑定数据库，执行前置sql失败！");
            }
            Arrays.stream(split).forEach(sql ->{
                Optional.ofNullable(sql).filter(sqlTem -> sqlTem.length()>0).ifPresent(sqlTemp -> {
                    if(sqlTemp.toLowerCase(Locale.ROOT).contains("select")){
//                    查询sql执行，并构造参数
                        ExecSqlDto execSqlDto =new ExecSqlDto();
                        execSqlDto.setDatabaseId(autoDataDatabaseVos.get(0).getId());
                        execSqlDto.setSql(sqlTemp);
                        List<Map<String, Object>> maps = autoDataDatabaseService.execSelectSQL(execSqlDto);
                        /*
                         *
                         */
                        if(maps.size()<=0){
                            throw new CustomException("sql查不到数据！");
                        }
                        requestPool.putAll(maps.get(0));
                    }else{
//                    写sql执行
                        ExecSqlDto execSqlDto =new ExecSqlDto();
                        execSqlDto.setDatabaseId(autoDataDatabaseVos.get(0).getId());
                        execSqlDto.setSql(sqlTemp);
                        autoDataDatabaseService.execUpdateSQL(execSqlDto);
                    }
                });
            });
        });

    }

    private void requestReplace(RunByCaseDetailDto detail, HashMap requestPool) {
//        处理header
        String header = detail.getHeader();
        Optional.ofNullable(header).ifPresent(headerTemp ->{
            if(headerTemp.length()>0){
                detail.setHeader(replaceVariable(headerTemp,requestPool));
            }
        });
//        处理param
        String requestParam = detail.getRequestParam();
        Optional.ofNullable(requestParam).ifPresent(requestParamTemp ->{
            if(requestParamTemp.length()>0){
                detail.setRequestParam(replaceVariable(requestParamTemp,requestPool));
            }
        });
//        处理body
        String requestBody = detail.getRequestBody();
        Optional.ofNullable(requestBody).ifPresent(requestBodyTemp ->{
            if(requestBodyTemp.length()>0){
                detail.setRequestBody(replaceVariable(requestBodyTemp,requestPool));
            }
        });
//        TODO 处理方法参数
    }

    private String replaceVariable(String content, HashMap requestPool) {
        List<String> variable = RegexUtil.getVariable(content);
        for (String variableTemp : variable) {
            String format = String.format("{{%s}}", variableTemp);
            if(variableTemp.contains("$")){// 处理jsonpath变量，列如 接口的依赖
//                ArrayList<String> arrayList = (ArrayList)requestPool.get(API_RECORD);
                String[] split = variableTemp.split("\\|");
                if(split.length!=2){
                    throw new CustomException("格式不符合[caseId|path]规范,变量名称："+variableTemp);
                }
                String jsonObj = String.valueOf(requestPool.get(split[0]));
                Object readObj = JSONPath.read(jsonObj, split[1]);
                log.info("变量[{}]转换后的参数[{}]", variableTemp, readObj);
                if(readObj!=null){
                    String read = String.valueOf(readObj);
                    content = content.replace(format, read);
                }else{
                    throw new CustomException("变量["+variableTemp+"]替换失败。变量路径错误或值为Null");
                }
            }else{// 处理普通变量，列如 sql依赖的变量。
                if(requestPool.get(variableTemp)!=null){
                    content = content.replace(format, requestPool.get(variableTemp).toString());
                }else{
                    throw new CustomException("变量["+variableTemp+"]替换失败");
                }
            }
        }
        return content;
    }
}
