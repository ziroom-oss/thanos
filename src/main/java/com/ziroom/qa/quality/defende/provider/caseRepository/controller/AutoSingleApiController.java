package com.ziroom.qa.quality.defende.provider.caseRepository.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.ziroom.qa.quality.defende.provider.caseRepository.entity.AutoSingleApi;
import com.ziroom.qa.quality.defende.provider.caseRepository.entity.dto.AutoSingleApiDto;
import com.ziroom.qa.quality.defende.provider.caseRepository.entity.dto.HeaderUpdateByAppDto;
import com.ziroom.qa.quality.defende.provider.caseRepository.service.IAutoSingleApiService;
import com.ziroom.qa.quality.defende.provider.result.RestResultVo;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author mybatisPlusAutoGenerate
 * @since 2021-09-22
 */
@RestController
@RequestMapping("/caseRepository/auto-single-api")
public class AutoSingleApiController {

    @Autowired
    private IAutoSingleApiService autoSingleApiService;
    @PostMapping("/headerUpdateByApp")
    public RestResultVo headerUpdateByApp(@Valid @RequestBody HeaderUpdateByAppDto dto) {
        autoSingleApiService.headerUpdateByAppDto(dto);
        return RestResultVo.fromData(null);
    }
    @ApiOperation("保存单接口,不保存用例")
    @PostMapping("/saveSingleApi")
    public RestResultVo saveSingleApi(@RequestHeader("userName")String userCode,
                                      @Valid @RequestBody AutoSingleApiDto dto) {
        dto.setUpdateUserCode(userCode);
        dto.setCreateUserCode(userCode);
        AutoSingleApi api = new AutoSingleApi();
        BeanUtils.copyProperties(dto,api);
        QueryWrapper<AutoSingleApi> queryWrapper =new QueryWrapper<>();
        queryWrapper.eq("application_name",dto.getApplicationName())
                .eq("request_uri",dto.getRequestUri())
                .eq("deleted",0);
        autoSingleApiService.saveOrUpdate(api,queryWrapper);
        return RestResultVo.fromData("保存成功");

    }
}

