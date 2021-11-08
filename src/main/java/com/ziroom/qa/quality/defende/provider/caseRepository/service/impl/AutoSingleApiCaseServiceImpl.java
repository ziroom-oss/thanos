package com.ziroom.qa.quality.defende.provider.caseRepository.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ziroom.qa.quality.defende.provider.caseRepository.entity.AutoSingleApi;
import com.ziroom.qa.quality.defende.provider.caseRepository.entity.AutoSingleApiCase;
import com.ziroom.qa.quality.defende.provider.caseRepository.entity.AutoSingleAssert;
import com.ziroom.qa.quality.defende.provider.caseRepository.entity.dto.AutoSingleApiCaseDto;
import com.ziroom.qa.quality.defende.provider.caseRepository.entity.dto.SingleApiCaseListDto;
import com.ziroom.qa.quality.defende.provider.caseRepository.entity.vo.SingleApiCaseListVO;
import com.ziroom.qa.quality.defende.provider.caseRepository.entity.vo.StaticTargetVo;
import com.ziroom.qa.quality.defende.provider.caseRepository.mapper.AutoSingleApiCaseMapper;
import com.ziroom.qa.quality.defende.provider.caseRepository.service.IAutoSingleApiCaseService;
import com.ziroom.qa.quality.defende.provider.caseRepository.service.IAutoSingleApiService;
import com.ziroom.qa.quality.defende.provider.caseRepository.service.IAutoSingleAssertService;
import com.ziroom.qa.quality.defende.provider.outinterface.client.service.OmegaService;
import com.ziroom.qa.quality.defende.provider.outinterface.client.service.MatrixService;
import com.ziroom.qa.quality.defende.provider.outinterface.client.service.WeChatService;
import com.ziroom.qa.quality.defende.provider.vo.MatrixUserDetail;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author mybatisPlusAutoGenerate
 * @since 2021-09-22
 */
@Service
public class AutoSingleApiCaseServiceImpl extends ServiceImpl<AutoSingleApiCaseMapper, AutoSingleApiCase> implements IAutoSingleApiCaseService {
    @Autowired
    private IAutoSingleApiService autoSingleApiService;
    @Autowired
    private IAutoSingleAssertService autoSingleAssertService;
    @Autowired
    private MatrixService matrixService;
    @Autowired
    private WeChatService weChatService;
    @Autowired
    private OmegaService omegaService;
    @Override
    public Page<SingleApiCaseListVO> findSingleApiListTableData(Integer pageNum, Integer pageSize, SingleApiCaseListDto dto) {
        Page<SingleApiCaseListVO> page = new Page<>(pageNum, pageSize);
        List<SingleApiCaseListVO> singleApiListTableData = getBaseMapper().findSingleApiListTableData(page, dto);
        /*
         * 修改姓名
         * 组装断言
         */
        try {
            List<String> collect = singleApiListTableData.stream().map(SingleApiCaseListVO::getUpdateUserCode).
                    collect(Collectors.toSet()).stream().collect(Collectors.toList());
            Map<String, MatrixUserDetail> userDetailByEmailPre = matrixService.getUserDetailByEmailPre(collect);
            singleApiListTableData.forEach(item ->{
//              组装断言
                item.setAssertList(autoSingleAssertService.getAutoSingleAssertsByCaseId(item.getId()));
//              修改姓名
                if(userDetailByEmailPre!=null&& userDetailByEmailPre.get(item.getUpdateUserCode())!=null){
                    item.setUpdateUserName(userDetailByEmailPre.get(item.getUpdateUserCode()).getUserName());
                }else{
                    item.setUpdateUserName(item.getUpdateUserCode());
                }
            });
        }catch (Exception e){
            log.error("修改姓名失败",e);
        }
        return page.setRecords(singleApiListTableData);
    }
    @Override
    public SingleApiCaseListVO getSingleApiCaseById(Integer caseId) {
        AutoSingleApiCase apiCase = this.getById(caseId);
        AutoSingleApi api = autoSingleApiService.getById(apiCase.getApiId());
        SingleApiCaseListVO vo =new SingleApiCaseListVO();
        BeanUtils.copyProperties(apiCase,vo);
        BeanUtils.copyProperties(api,vo);
        vo.setAssertList(autoSingleAssertService.getAutoSingleAssertsByCaseId(caseId));
        List<String> userCodeList = new ArrayList<>();
        userCodeList.add(apiCase.getUpdateUserCode());
        Map<String, MatrixUserDetail> userDetailByEmailPre = matrixService.getUserDetailByEmailPre(userCodeList);
//              修改姓名
        Optional.ofNullable(userDetailByEmailPre).ifPresent(userDetailByEmailPreTemp ->{
            Optional.ofNullable(apiCase.getUpdateUserCode()).ifPresent(realName ->{
                Optional.ofNullable(userDetailByEmailPreTemp.get(realName)).ifPresent(realNameDto -> {
                    vo.setUpdateUserName(realNameDto.getUserName());
                });
            });
        });
        return vo;
    }
    @Override
    public String saveSingleApi(AutoSingleApiCaseDto dto) {

//        校验接口是否存在
        AutoSingleApi api = findApiByUri(dto);
//        接口存在,校验用例或保存用例
        if(api!=null){
            return saveOrUpdateCase(api,dto);
        }else{
//        接口不存在，保存接口和用例
            dto.setCreateUserCode(dto.getUpdateUserCode());
            return saveApiAndCase(dto);
        }
    }

    @Override
    public String saveApiAndCases(AutoSingleApiCaseDto dto) {
        // 区分接口用例来源获取userCode方式不同
        if (dto.getCaseOrigin() == null) {
            dto.setCaseOrigin(0);
        }
        dto.setCaseName(dto.getCaseName().replace("[swagger生成]",""));
        return this.saveSingleApi(dto);
    }

    @Override
    public boolean caseIsExist(Integer apiId, AutoSingleApiCaseDto singleApiEnterDto) {
        String requestType = singleApiEnterDto.getRequestType();
//        先判断名称是否重复
        QueryWrapper<AutoSingleApiCase> queryWrapper1 =new QueryWrapper<>();
        queryWrapper1.eq("case_name",singleApiEnterDto.getCaseName())
                .eq("api_id",apiId);
        List<AutoSingleApiCase> singleCaseByApplication = this.list(queryWrapper1);
        if(singleCaseByApplication.size()>0){
            return true;
        }
        QueryWrapper<AutoSingleApiCase> queryWrapper =new QueryWrapper<>();
        queryWrapper.eq("api_id",apiId);
        List<AutoSingleApiCase> caseEntities = this.list(queryWrapper);
        String param = singleApiEnterDto.getRequestParam();
        for (AutoSingleApiCase singleApiCaseEntity : caseEntities) {
            if ("get".equalsIgnoreCase(requestType)&&singleApiCaseEntity.getRequestParam().equals(param)) {
                singleApiEnterDto.setId(singleApiCaseEntity.getId());
                return true;
            }else if ("post".equalsIgnoreCase(requestType)&&singleApiEnterDto.getRequestBody()!=null
                    &&singleApiEnterDto.getRequestBody().equals(singleApiCaseEntity.getRequestBody())) {
                singleApiEnterDto.setId(singleApiCaseEntity.getId());
                return true;
            }
        }
        return false;
    }

    @Override
    public void deleteBatch(Integer apiId, String content) {
        QueryWrapper<AutoSingleApiCase> queryWrapper =new QueryWrapper<>();
        queryWrapper.eq("api_id",apiId)
                .like("case_name",content);
        this.remove(queryWrapper);
    }

    @Override
    public List<Map.Entry<String, StaticTargetVo>> staticAutoTestUsage(LocalDateTime lastDateTime) {
//        统计用例数 统计近100 万条
        Page<SingleApiCaseListVO> objectPage = new Page<>(1,1000000);
        SingleApiCaseListDto dto = new SingleApiCaseListDto();
        dto.setRunnable(1);
        List<SingleApiCaseListVO> singleApiListTableData = getBaseMapper().findSingleApiListTableData(objectPage, dto);
        Map<String, List<SingleApiCaseListVO>> listMap = singleApiListTableData.stream().
                collect(Collectors.groupingBy(SingleApiCaseListVO::getApplicationName));
        TreeMap<String,StaticTargetVo> staticTargetVoHashMap = new TreeMap<>();
        for (Map.Entry<String, List<SingleApiCaseListVO>> entry:listMap.entrySet()) {
            StaticTargetVo vo =new StaticTargetVo();
            String key = entry.getKey();
            List<SingleApiCaseListVO> value = entry.getValue();
            //用例数量
            vo.setCaseNumber(value.size());
            AtomicInteger caseNumContainSql = new AtomicInteger();
            AtomicInteger caseNumContainApi = new AtomicInteger();
            value.stream().forEach(singleApiCaseListVO ->{
                if(singleApiCaseListVO.getPreSql()!=null&&singleApiCaseListVO.getPreSql().trim().length()>0){
                    //统计SQL依赖
                    caseNumContainSql.getAndIncrement();
                }
                if(singleApiCaseListVO.getPreRequest()!=null&&singleApiCaseListVO.getPreRequest().trim().length()>0){
                    //统计API依赖数量
                    caseNumContainApi.getAndIncrement();
                }
            });
            vo.setCaseNumContainSql(caseNumContainSql.get());
            vo.setCaseNumContainApi(caseNumContainApi.get());
            String groupName = omegaService.getGroupNameByAppName(key);
            staticTargetVoHashMap.put(StringUtils.isNotBlank(groupName)?groupName:key,vo);
        }
        List<Map.Entry<String, StaticTargetVo>> list = new ArrayList<Map.Entry<String, StaticTargetVo>>(staticTargetVoHashMap.entrySet());

        Collections.sort(list,new Comparator<Map.Entry<String,StaticTargetVo>>() {
            //升序排序
            public int compare(Map.Entry<String, StaticTargetVo> o1, Map.Entry<String, StaticTargetVo> o2) {
                return o2.getValue().getCaseNumber().compareTo(o1.getValue().getCaseNumber());
            }
        });
        return list;
    }

    @Override
    public String sendStatic() {
        List<Map.Entry<String, StaticTargetVo>> stringStaticTargetVoMap = staticAutoTestUsage(LocalDateTime.now());

        StringBuilder stringBuilder =new StringBuilder("##### 　自动化测试使用指标度量结果:\n \n　**组名**　　　　　　**用例数量**　**SQL依赖**　**API依赖**\n");
        for (int i = 0;i<stringStaticTargetVoMap.size();i++) {
            Map.Entry<String, StaticTargetVo> entry = stringStaticTargetVoMap.get(i);
            StaticTargetVo value = entry.getValue();
            String key = entry.getKey();
            String newKey = fillWithBlack(key, 8);
            String newCaseNum = fillWithBlack(String.valueOf(value.getCaseNumber()), 5);
            String newCaseSQl = fillWithBlack(String.valueOf(value.getCaseNumContainSql()), 5);
            String newCaseSApi = fillWithBlack(String.valueOf(value.getCaseNumContainApi()), 5);
            stringBuilder.append(">");
            if(i==0||i==1){
                stringBuilder.
                        append("<font color=\"warning\">")
                        .append(newKey)
                        .append(newCaseNum)
                        .append("　")
                        .append(newCaseSQl)
                        .append("　")
                        .append(newCaseSApi)
                        .append("</font>\n");
            }else if(i==2||i==3||i==4){
                stringBuilder.
                        append("<font color=\"info\">")
                        .append(newKey)
                        .append(newCaseNum)
                        .append("　")
                        .append(newCaseSQl)
                        .append("　")
                        .append(newCaseSApi)
                        .append("</font>\n");
            }else{
                stringBuilder.
                         append("<font color=\"comment\">")
                        .append(newKey)
                        .append(newCaseNum)
                        .append("　")
                        .append(newCaseSQl)
                        .append("　")
                        .append(newCaseSApi)
                        .append("</font>\n");
            }

        }
        stringBuilder.append(String.format("[点击添加](%s)","http://qa.kp.ziroom.com/testCase"));
        weChatService.sendDeptRankRobotMsg(stringBuilder.toString());
        return null;
    }

    private String fillWithBlack(String key, int i) {
        if(key==null)
            return "";
        if(key.length()>i){
            return key.substring(0,i);
        }else if (i == key.length()){
            return key;
        }else{
            key = key+"　";
            return fillWithBlack(key,i);
        }
    }


    public String saveOrUpdateCase(AutoSingleApi api, AutoSingleApiCaseDto dto) {
//       用例是否存在
        boolean exist = caseIsExist(api, dto);
//            更新header
        api.setHeader(dto.getHeader());
        api.setUpdateTime(LocalDateTime.now());
        autoSingleApiService.updateById(api);
        if(exist){
//            更新用例
            dto.setUpdateTime(LocalDateTime.now());
            this.updateById(dto);
//            更新断言
            updateCase(dto);
//            更新断言
            return "测试用例已更新！";
        }else {
            dto.setCreateUserCode(dto.getUpdateUserCode());
            dto.setApiId(api.getId());
            this.save(dto);
//            保存断言
            this.saveAssert(dto,dto.getId());
            return "用例保存成功！";
        }
    }

    private void updateCase(AutoSingleApiCaseDto dto) {
//        先删除后保存断言
        Integer caseId = dto.getId();
        QueryWrapper<AutoSingleAssert> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("case_id",caseId);
        autoSingleAssertService.remove(queryWrapper);
//        保存断言
        saveAssert(dto, caseId);
    }

    private void saveAssert(AutoSingleApiCaseDto dto, Integer caseId) {
        for (AutoSingleAssert autoSingleAssert : dto.getAssertList()){
            AutoSingleAssert entity =new AutoSingleAssert();
            BeanUtils.copyProperties(autoSingleAssert,entity);
            entity.setCaseId(caseId);
            entity.setDeleted(0);
            autoSingleAssertService.save(entity);
        }
    }

    public String saveApiAndCase(AutoSingleApiCaseDto dto) {
        AutoSingleApi entity = new AutoSingleApi();
        BeanUtils.copyProperties(dto,entity);
        autoSingleApiService.save(entity);
//        save case
        dto.setApiId(entity.getId());
        this.save(dto);
//        保存断言
        saveAssert(dto,dto.getId());
        return "接口和用例保存成功!";
    }
    public boolean caseIsExist(AutoSingleApi api, AutoSingleApiCaseDto apiEnterDto) {
        Integer apiId =api.getId();
        if(apiId==null||apiId==0){
            return false;
        }
//        先判断名称是否重复
        List<AutoSingleApiCase> singleCaseByApplication = getSingleCaseByCaseName(apiId,apiEnterDto);
        if(singleCaseByApplication.size()>0){
            apiEnterDto.setId(singleCaseByApplication.get(0).getId());
            return true;
        }
        List<AutoSingleApiCase> caseEntities = getSingleCaseListByApiId(apiEnterDto);
        String param = apiEnterDto.getRequestParam();
        String requestType = api.getRequestType();
        for (AutoSingleApiCase apiCase : caseEntities) {
            if ("get".equalsIgnoreCase(requestType)&&apiCase.getRequestParam().equals(param)) {
                apiEnterDto.setId(apiCase.getId());
                return true;
            }else if ("post".equalsIgnoreCase(requestType)&&apiEnterDto.getRequestBody()!=null
                    &&apiEnterDto.getRequestBody().equals(apiCase.getRequestBody())) {
                apiEnterDto.setId(apiCase.getId());
                return true;
            }
        }
        return false;
    }

    private List<AutoSingleApiCase> getSingleCaseListByApiId(AutoSingleApiCaseDto dto) {
        QueryWrapper<AutoSingleApiCase> queryWrapper =new QueryWrapper<>();
        queryWrapper.eq("api_id",dto.getApiId());
        return this.list(queryWrapper);
    }

    public List<AutoSingleApiCase> getSingleCaseByCaseName(Integer apiId, AutoSingleApiCaseDto dto){
        QueryWrapper<AutoSingleApiCase> queryWrapper =new QueryWrapper<>();
        queryWrapper.eq("api_id",apiId);
        Optional.ofNullable(dto.getCaseName()).ifPresent(caseName -> {
            queryWrapper.eq("case_name",dto.getCaseName());
        });
        return this.list(queryWrapper);
    }


    public AutoSingleApi findApiByUri(AutoSingleApiCaseDto dto) {
        QueryWrapper<AutoSingleApi> queryWrapper =new QueryWrapper<>();
        queryWrapper.eq("application_name",dto.getApplicationName())
                .eq("request_uri",dto.getRequestUri())
                .eq("deleted",0);
        return autoSingleApiService.getOne(queryWrapper);
    }
}
