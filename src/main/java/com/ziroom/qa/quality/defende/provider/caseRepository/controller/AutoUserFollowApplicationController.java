package com.ziroom.qa.quality.defende.provider.caseRepository.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.ziroom.qa.quality.defende.provider.caseRepository.entity.AutoUserFollowApplication;
import com.ziroom.qa.quality.defende.provider.caseRepository.entity.dto.AutoUserFollowApplicationDto;
import com.ziroom.qa.quality.defende.provider.caseRepository.service.IAutoUserFollowApplicationService;
import com.ziroom.qa.quality.defende.provider.outinterface.client.omega.OmegeApplicationLevel;
import com.ziroom.qa.quality.defende.provider.result.RestResultVo;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author mybatisPlusAutoGenerate
 * @since 2021-10-09
 */
@RestController
@RequestMapping("/caseRepository/auto-user-follow-application")
@Slf4j
public class AutoUserFollowApplicationController {
    @Autowired
    private IAutoUserFollowApplicationService autoUserFollowApplicationService;

    @RequestMapping("/follow")
    @ApiOperation("收藏")
    public RestResultVo test(@RequestHeader("userName") String userCode, @RequestBody @Valid AutoUserFollowApplicationDto ent) {
        ent.setUserCode(userCode);
        QueryWrapper<AutoUserFollowApplication> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_code", userCode)
                .eq("application_code", ent.getApplicationCode());
        List<AutoUserFollowApplication> list = autoUserFollowApplicationService.list(queryWrapper);
        if (list.size() <= 0) {
            autoUserFollowApplicationService.save(ent);
        }
        return RestResultVo.fromData(null);
    }

    @RequestMapping("/cancelFollow")
    @ApiOperation("取消收藏")
    public RestResultVo cancelFollow(@RequestHeader("userName") String userCode, @RequestBody @Valid AutoUserFollowApplicationDto ent) {
        QueryWrapper<AutoUserFollowApplication> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_code", userCode)
                .eq("application_code", ent.getApplicationCode());
        autoUserFollowApplicationService.remove(queryWrapper);
        return RestResultVo.fromData(null);
    }


    @ApiOperation("参与的应用，包含接口")
    @RequestMapping("/myJoinApplication")
    public RestResultVo myJoinApplication(@RequestHeader("userName") String userName, @RequestHeader("uid") String uid) {
        List<OmegeApplicationLevel> list = new ArrayList<>();
//        RestResultVo<List<ResourceVo>> listRestResult = merakApiClient.listApp(uid);
////        设置是否收藏
//        QueryWrapper<AutoUserFollowApplication> queryWrapper = new QueryWrapper<>();
//        queryWrapper.eq("user_code",userName);
//        List<AutoUserFollowApplication> listFollow = autoUserFollowApplicationService.list(queryWrapper);
//        Map<String, List<AutoUserFollowApplication>> collect = listFollow.stream().collect(Collectors.groupingBy(AutoUserFollowApplication::getApplicationCode));
//        listRestResult.getData().stream().forEach(vo ->{
//            OmegeApplicationLevel omegaProject =new OmegeApplicationLevel();
//            String appCode = vo.getAppCode();
//            omegaProject.setApplicationName(appCode);
//            omegaProject.setLabel(appCode);
//            List apiTagsList = autoSingleApiService.getApiTagsList(appCode);
//            log.info("getApiTags list [{}]",apiTagsList);
//            if(collect.containsKey(appCode)){
//                omegaProject.setFollow(true);
//            }
//            omegaProject.setChildren(apiTagsList);
//            list.add(omegaProject);
//        });
        return RestResultVo.fromData(list);
    }
}

