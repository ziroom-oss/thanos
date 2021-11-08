package com.ziroom.qa.quality.defende.provider.controller;

import com.ziroom.qa.quality.defende.provider.service.TestAppUserRelService;
import com.ziroom.qa.quality.defende.provider.result.RestResultVo;
import com.ziroom.qa.quality.defende.provider.vo.TestResultVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(value = "关注应用", tags = {"关注应用"})
@RequestMapping("/testAppUserRel")
@RestController
@Slf4j
public class TestAppUserRelController {

    @Autowired
    private TestAppUserRelService testAppUserRelService;

    @ApiOperation("关注一个应用")
    @GetMapping("/followApplication")
    public RestResultVo followApplication(@RequestHeader String userName,
                                          @RequestParam Long appId) {

        RestResultVo restResultVo;
        try {
            TestResultVo resultVo = testAppUserRelService.followApplication(appId, userName);
            if (resultVo.getFlag()) {
                restResultVo = RestResultVo.fromData(resultVo.getMsgRes());
            } else {
                restResultVo = RestResultVo.fromErrorMessage(resultVo.getMsgRes());
            }
        } catch (Exception e) {
            log.error("/applicatioin/followApplication queryVo=={} 关注一个应用失败!", userName, e);
            restResultVo = RestResultVo.fromErrorMessage(e.getMessage());
        }
        return restResultVo;
    }

    @ApiOperation("取消关注应用")
    @GetMapping("/unfollowApplication")
    public RestResultVo unfollowApplication(@RequestHeader String userName,
                                            @RequestParam Long appId) {

        RestResultVo restResultVo;
        try {
            TestResultVo resultVo = testAppUserRelService.unfollowApplication(appId, userName);
            if (resultVo.getFlag()) {
                restResultVo = RestResultVo.fromData(resultVo.getMsgRes());
            } else {
                restResultVo = RestResultVo.fromErrorMessage(resultVo.getMsgRes());
            }
        } catch (Exception e) {
            log.error("/applicatioin/unfollowApplication queryVo=={} 取消关注应用失败!", userName, e);
            restResultVo = RestResultVo.fromErrorMessage(e.getMessage());
        }
        return restResultVo;
    }

    @ApiOperation("批量关注")
    @GetMapping("/batchFollowApplication")
    public RestResultVo batchFollowApplication(@RequestHeader String userName,
                                               @RequestParam List<Long> appIdList) {

        RestResultVo restResultVo;
        try {
            TestResultVo resultVo = testAppUserRelService.batchFollowApplication(appIdList, userName);
            if (resultVo.getFlag()) {
                restResultVo = RestResultVo.fromData(resultVo.getMsgRes());
            } else {
                restResultVo = RestResultVo.fromErrorMessage(resultVo.getMsgRes());
            }
        } catch (Exception e) {
            log.error("/applicatioin/batchFollowApplication queryVo=={} 批量关注失败!", userName, e);
            restResultVo = RestResultVo.fromErrorMessage(e.getMessage());
        }
        return restResultVo;
    }

}
