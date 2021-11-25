package com.ziroom.qa.quality.defende.provider.caseRepository.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.ziroom.qa.quality.defende.provider.caseRepository.entity.AutoApplicationProperties;
import com.ziroom.qa.quality.defende.provider.caseRepository.entity.dto.AutoApplicationPropertiesDto;
import com.ziroom.qa.quality.defende.provider.caseRepository.entity.vo.AutoApplicationPropertiesVo;
import com.ziroom.qa.quality.defende.provider.caseRepository.service.IAutoApplicationPropertiesService;
import com.ziroom.qa.quality.defende.provider.result.CustomException;
import com.ziroom.qa.quality.defende.provider.result.RestResultVo;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Optional;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author mybatisPlusAutoGenerate
 * @since 2021-10-12
 */
@RestController
@RequestMapping("/caseRepository/auto-application-properties")
public class AutoApplicationPropertiesController {
    @Autowired
    private IAutoApplicationPropertiesService autoApplicationPropertiesService;

    @RequestMapping("saveOrUpdate")
    public RestResultVo saveOrUpdate(@RequestBody AutoApplicationPropertiesDto dto) {
        if (StringUtils.isEmpty(dto.getEnv())) {
            dto.setEnv("qua");
        }
        QueryWrapper<AutoApplicationProperties> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("application_name", dto.getApplicationName())
                .eq("env", dto.getEnv())
                .eq("deleted", 0);
        autoApplicationPropertiesService.saveOrUpdate(dto, queryWrapper);
        return RestResultVo.fromData(null);
    }

    @RequestMapping("find")
    public RestResultVo find(@RequestBody AutoApplicationPropertiesDto dto) {
        AutoApplicationPropertiesVo vo = autoApplicationPropertiesService.find(dto);
        return RestResultVo.fromData(vo);
    }

    @ApiOperation(value = "同步swagger接口", tags = {"根据springboot应用名和url同步接口"})
    @RequestMapping(value = "/synchronizeApiByUrl")
    public RestResultVo synchronizeApiByUrl(@RequestHeader("userName") String userCode, @RequestParam(required = false) String domainContextPath, @RequestParam String applicationName) {
        if (!StringUtils.isEmpty(domainContextPath)) {
            Base64.Decoder decoder = Base64.getDecoder();
            domainContextPath = new String(decoder.decode(domainContextPath));
        }
        return RestResultVo.fromData(autoApplicationPropertiesService.synchronizeApiByUrl(userCode, applicationName, domainContextPath, "qua"));
    }

    @RequestMapping(value = "/getApplicationNameByDomain")
    public RestResultVo getApplicationNameByDomain(@RequestParam("domain") String domain) {
        String applicationName = autoApplicationPropertiesService.getApplicationName(domain);
        return RestResultVo.fromData(Optional.ofNullable(applicationName).orElseThrow(() -> new CustomException("未找到对应应用,domain:" + domain)));

    }

    @ApiOperation("参与的应用,不含接口")
    @RequestMapping("/myJoinApplicationSimple")
    public RestResultVo myJoinApplicationSimple(@RequestHeader("uid") String uid) {
        List list = new ArrayList<>();
//        RestResultVo<List<ResourceVo>> listRestResult = merakApiClient.listApp(uid);
//        listRestResult.getData().stream().forEach(vo ->{
//            OmegeApplicationLevel omegaProject =new OmegeApplicationLevel();
//            omegaProject.setApplicationName(vo.getAppCode());
//            list.add(omegaProject);
//        });
        return RestResultVo.fromData(list);
    }
}

