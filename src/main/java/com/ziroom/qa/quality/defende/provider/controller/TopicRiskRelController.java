package com.ziroom.qa.quality.defende.provider.controller;


import com.ziroom.qa.quality.defende.provider.entity.TopicRiskRel;
import com.ziroom.qa.quality.defende.provider.service.TopicRiskRelService;
import com.ziroom.qa.quality.defende.provider.result.RestResultVo;
import com.ziroom.qa.quality.defende.provider.vo.TestResultVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 项目风险关联 前端控制器
 * </p>
 *
 * @author liangh4
 * @since 2021-06-02
 */
@Api(value = "项目风险管理", tags = {"项目风险管理"})
@Slf4j
@RestController
@RequestMapping("/topicRiskRel")
public class TopicRiskRelController {

    @Autowired
    private TopicRiskRelService topicRiskRelService;

    @ApiOperation("查询已关联数据")
    @GetMapping(value = "/queryRelList")
    public RestResultVo<List<TopicRiskRel>> queryRelList(@RequestHeader String userName,
                                                         @RequestParam Long topicId) {
        RestResultVo restResultVo;
        try {
            List<TopicRiskRel> list = topicRiskRelService.queryRelList(topicId);
            restResultVo = RestResultVo.fromData(list);
        } catch (Exception e) {
            log.error("/topicRiskRel/queryRelList topicId=={} 查询失败!", topicId, e);
            restResultVo = RestResultVo.fromErrorMessage(e.getMessage());
        }
        return restResultVo;
    }

    @ApiOperation("查询未关联数据")
    @GetMapping(value = "/queryUnRelList")
    public RestResultVo<List<TopicRiskRel>> queryUnRelList(@RequestHeader String userName,
                                                           @RequestParam Long topicId) {
        RestResultVo restResultVo;
        try {
            List<TopicRiskRel> list = topicRiskRelService.queryUnRelList(topicId);
            restResultVo = RestResultVo.fromData(list);
        } catch (Exception e) {
            log.error("/topicRiskRel/queryUnRelList topicId=={} 查询失败!", topicId, e);
            restResultVo = RestResultVo.fromErrorMessage(e.getMessage());
        }
        return restResultVo;
    }

    @ApiOperation("保存关联信息")
    @GetMapping(value = "/saveRelInfo")
    public RestResultVo saveRelInfo(@RequestHeader String userName,
                                    @RequestParam Long topicId,
                                    @RequestParam(value = "riskIdList", required = false) List<Long> riskIdList) {
        RestResultVo restResultVo;
        try {
            TestResultVo resultVo = topicRiskRelService.saveRelInfo(topicId, riskIdList, userName);
            if (resultVo.getFlag()) {
                restResultVo = RestResultVo.fromSuccess(resultVo.getMsgRes());
            } else {
                restResultVo = RestResultVo.fromErrorMessage(resultVo.getMsgRes());
            }
        } catch (Exception e) {
            log.error("/topicRiskRel/saveRelInfo topicId=={} 查询失败!", topicId, e);
            restResultVo = RestResultVo.fromErrorMessage(e.getMessage());
        }
        return restResultVo;
    }


    @ApiOperation("新增风险信息")
    @GetMapping(value = "/createRiskInfo")
    public RestResultVo createRiskInfo(@RequestHeader String userName,
                                       @RequestParam String riskName,
                                       @RequestParam(value = "topicId", required = false) Long topicId) {
        RestResultVo restResultVo;
        try {
            TestResultVo resultVo = topicRiskRelService.createRiskInfo(userName, riskName, topicId);
            if (resultVo.getFlag()) {
                restResultVo = RestResultVo.fromSuccess(resultVo.getMsgRes());
            } else {
                restResultVo = RestResultVo.fromErrorMessage(resultVo.getMsgRes());
            }
        } catch (Exception e) {
            log.error("/topicRiskRel/createRiskInfo topicId=={} 查询失败!", topicId, e);
            restResultVo = RestResultVo.fromErrorMessage(e.getMessage());
        }
        return restResultVo;
    }

    @ApiOperation("删除风险信息")
    @GetMapping(value = "/delInfo")
    public RestResultVo delInfo(@RequestHeader String userName,
                                @RequestParam(value = "riskName", required = false) String riskName,
                                @RequestParam(value = "topicId", required = false) Long topicId) {
        RestResultVo restResultVo;
        try {
            topicRiskRelService.delInfo(userName, riskName, topicId);
            restResultVo = RestResultVo.fromData(true);
        } catch (Exception e) {
            log.error("/topicRiskRel/delInfo topicId=={} riskName=={}!", topicId, riskName, e);
            restResultVo = RestResultVo.fromErrorMessage(e.getMessage());
        }
        return restResultVo;
    }


}

