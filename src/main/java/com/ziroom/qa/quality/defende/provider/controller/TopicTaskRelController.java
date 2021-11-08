package com.ziroom.qa.quality.defende.provider.controller;

import com.ziroom.qa.quality.defende.provider.entity.TopicTaskRel;
import com.ziroom.qa.quality.defende.provider.service.TopicTaskRelService;
import com.ziroom.qa.quality.defende.provider.result.RestResultVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(value = "项目关联测试执行任务", tags = {"项目关联测试执行任务"})
@RequestMapping("/topicTaskRel")
@RestController
@Slf4j
public class TopicTaskRelController {

    @Autowired
    private TopicTaskRelService topicTaskRelService;

    @ApiOperation("根据项目id查询所有关联和未关联的测试执行任务列表")
    @GetMapping(value = "/queryAllRelAndUnRelTaskList")
    public RestResultVo<List<TopicTaskRel>> queryAllRelAndUnRelTaskList(@RequestHeader String userName,
                                                                        @RequestParam(value = "topicId", required = false) Long topicId) {
        RestResultVo restResultVo;
        try {
            List<TopicTaskRel> list = topicTaskRelService.queryAllRelAndUnRelTaskList(topicId);
            restResultVo = RestResultVo.fromData(list);
        } catch (Exception e) {
            log.error("/topicTaskRel/queryAllRelAndUnRelTaskList topicId=={} 查询失败!", topicId, e);
            restResultVo = RestResultVo.fromErrorMessage(e.getMessage());
        }
        return restResultVo;
    }

}
