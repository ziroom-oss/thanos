package com.ziroom.qa.quality.defende.provider.caseRepository.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.ziroom.qa.quality.defende.provider.caseRepository.entity.AutoSingleApi;
import com.ziroom.qa.quality.defende.provider.caseRepository.entity.AutoSingleApiCase;
import com.ziroom.qa.quality.defende.provider.caseRepository.entity.dto.AutoSingleApiCaseDto;
import com.ziroom.qa.quality.defende.provider.caseRepository.entity.dto.SingleApiCaseListDto;
import com.ziroom.qa.quality.defende.provider.caseRepository.service.IAutoSingleApiCaseService;
import com.ziroom.qa.quality.defende.provider.caseRepository.service.IAutoSingleApiService;
import com.ziroom.qa.quality.defende.provider.result.CustomException;
import com.ziroom.qa.quality.defende.provider.result.RestResultVo;
import com.ziroom.qa.quality.defende.provider.util.FileUtil;
import com.ziroom.qa.quality.defende.provider.util.TelotStringUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author mybatisPlusAutoGenerate
 * @since 2021-09-22
 */
@RestController
@RequestMapping("/caseRepository/auto-single-api-case")
@Api("测试用例模块")
@Valid
@Slf4j
public class AutoSingleApiCaseController {
    @Autowired
    private IAutoSingleApiCaseService autoSingleApiCaseService;
    @Autowired
    private IAutoSingleApiService autoSingleApiService;
    public static String STORAGE_DIRECTORY = TelotStringUtil.handlePath(AutoSingleApiCaseController.class.getResource("/")
            .toString().replaceAll("^file:", ""));
    private static final String API_STORAGE = "/xiaoneng/telot/api/";
    @RequestMapping("findSingleApiListTableData")
    @ApiOperation("分页查询测试用例")
    private RestResultVo findSingleApiListTableData(Integer pageNum, Integer pageSize, @RequestBody SingleApiCaseListDto dto){
        return RestResultVo.fromData(autoSingleApiCaseService.findSingleApiListTableData(pageNum, pageSize, dto));
    }
    @RequestMapping("findSingleApiById")
    @ApiOperation("分页查询测试用例")
    private RestResultVo findSingleApiById( Integer caseId){
        return RestResultVo.fromData(autoSingleApiCaseService.getSingleApiCaseById(caseId));
    }
    @ApiOperation("保存单接口")
    @PostMapping("/saveSingleApiCase")
    public RestResultVo saveSingleApiCase(@RequestHeader("userName")String userCode,
                                    @Validated @RequestBody AutoSingleApiCaseDto dto) {
        dto.setUpdateUserCode(userCode);
        return RestResultVo.fromData(autoSingleApiCaseService.saveApiAndCases(dto));
    }
    @ApiOperation("校验用例是否存在")
    @PostMapping("/checkCaseExist")
    public RestResultVo checkCaseExist(@Valid @RequestBody AutoSingleApiCaseDto singleApiEnterDto) {
        //如果系统已经存在并且Uri和RequestType已经存在,只保存用例信息
        QueryWrapper<AutoSingleApi> queryWrapper =new QueryWrapper<>();
        queryWrapper.eq("application_name",singleApiEnterDto.getApplicationName())
                .eq("request_uri",singleApiEnterDto.getRequestUri())
                .eq("deleted",0);
        List<AutoSingleApi> list = autoSingleApiService.list(queryWrapper);
        if(list.size()<=0){
            return RestResultVo.fromData(false);
        }
        boolean exist = autoSingleApiCaseService.caseIsExist(list.get(0).getId(), singleApiEnterDto);
        return RestResultVo.fromData(exist);
    }
    @ApiOperation("删除测试用例")
    @PostMapping("/deleteSingleApiCaseById")
    public RestResultVo deleteSingleApiCaseById(@RequestHeader("userName")String userCode, @RequestBody List<Integer> idList) {
        List<Integer> list =new ArrayList<>();
        for (Integer id :idList) {
            AutoSingleApiCase byId = autoSingleApiCaseService.getById(id);
            if(byId!=null&&(userCode.equals(byId.getCreateUserCode())||userCode.equals(byId.getUpdateUserCode()))){
                autoSingleApiCaseService.removeById(id);
            }else{
                list.add(id);
            }
        }
        if(CollectionUtils.isEmpty(list)){
            return RestResultVo.fromData(null);
        }else{
            return RestResultVo.fromData(list);
        }
    }
    @ApiOperation("帮助postman做解析")
    @ResponseBody
    @RequestMapping(value = "/upload", method = RequestMethod.POST)
    public RestResultVo uploadRequestFile(@RequestHeader("userName")String userCode, MultipartFile file) {
        String dirPath = STORAGE_DIRECTORY + API_STORAGE + userCode + "/";
        return RestResultVo.fromData(uploadFile(file, dirPath));
    }

    /**
     * 上传文件
     *
     * @param file
     * @return
     */
    public String uploadFile(MultipartFile file, String dirPath) {
        String saveFilePath = dirPath + file.getOriginalFilename();
        if (file.isEmpty()) {
            log.error("文件{}为空，无法上传！", saveFilePath);
            throw new CustomException("文件为空，无法上传！");
        }
        return FileUtil.analyse(file);
    }
    @ApiOperation("群机器人发送统计")
    @ResponseBody
    @RequestMapping(value = "/sendStaticTarget", method = RequestMethod.POST)
    public RestResultVo sendStaticTarget(){
        return RestResultVo.fromData(autoSingleApiCaseService.sendStatic());
    }
}

