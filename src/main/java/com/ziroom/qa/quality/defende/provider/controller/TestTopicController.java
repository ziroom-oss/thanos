package com.ziroom.qa.quality.defende.provider.controller;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ziroom.qa.quality.defende.provider.config.OperateLogAnnotation;
import com.ziroom.qa.quality.defende.provider.constant.OperateLogModuleConstants;
import com.ziroom.qa.quality.defende.provider.constant.OperateLogTypeConstants;
import com.ziroom.qa.quality.defende.provider.entity.TestTopic;
import com.ziroom.qa.quality.defende.provider.service.TestTopicService;
import com.ziroom.qa.quality.defende.provider.vo.Pagination;
import com.ziroom.qa.quality.defende.provider.result.RestResultVo;
import com.ziroom.qa.quality.defende.provider.vo.TestResultVo;
import com.ziroom.qa.quality.defende.provider.vo.dailyreport.TestTopicTypeVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@Api(value="所属项目",tags={"所属项目"})
@RequestMapping("/topic")
@RestController
@Slf4j
public class TestTopicController {

    @Autowired
    private TestTopicService testTopicService;

    @ApiOperation("分页查询")
    @PostMapping(value = "/queryListByPage",produces = { "application/json;charset=UTF-8" })
    public RestResultVo<Page<TestTopic>> queryListByPage(@RequestHeader String userName, @RequestBody Pagination<TestTopic> pagination){
        RestResultVo restResultVo;
        try {
            Page<TestTopic> page = testTopicService.queryListByPage(pagination);
            restResultVo = RestResultVo.fromData(page);
        } catch (Exception e) {
            log.error("/topic/queryListByPage queryVo=={} 分页查询失败!", JSON.toJSONString(pagination),e);
            restResultVo = RestResultVo.fromErrorMessage(e.getMessage());
        }
        return restResultVo;
    }

    @ApiOperation("查询项目信息下拉菜单")
    @GetMapping(value = "/queryTopicDropdownList")
    public RestResultVo<List<TestTopic>> queryTopicDropdownList(@RequestHeader String userName){
        RestResultVo restResultVo;
        try {
            List<TestTopic> list = testTopicService.queryDropdownList(userName);
            restResultVo = RestResultVo.fromData(list);
        } catch (Exception e) {
            log.error("/topic/queryDropdownList queryDropdownList失败!",e);
            restResultVo = RestResultVo.fromErrorMessage(e.getMessage());
        }
        return restResultVo;
    }

//    @OperateLogAnnotation(moduleName = OperateLogModuleConstants.TOPIC, option = OperateLogTypeConstants.PAGING_QUERY)
    @ApiOperation("查询当前登陆人的项目")
    @PostMapping(value = "/queryLoginListByPage",produces = { "application/json;charset=UTF-8" })
    public RestResultVo<Page<TestTopic>> queryLoginListByPage(@RequestHeader String userName,
                                                              @RequestBody Pagination<TestTopic> pagination){
        RestResultVo restResultVo;
        try {
            TestTopic testTopic = pagination.getSearchObj();
            testTopic.setTopicMaster(userName);
            testTopic.setTopicParticipant(userName);
            pagination.setSearchObj(testTopic);
            Page<TestTopic> page = testTopicService.queryListByPage(pagination);
            restResultVo = RestResultVo.fromData(page);
        } catch (Exception e) {
            log.error("/topic/queryMyListByPage queryVo=={} 查询当前登陆人的项目失败!", JSON.toJSONString(pagination),e);
            restResultVo = RestResultVo.fromErrorMessage(e.getMessage());
        }
        return restResultVo;
    }

//    @OperateLogAnnotation(moduleName = OperateLogModuleConstants.TOPIC, option = OperateLogTypeConstants.SAVE)
    @ApiOperation("新增信息")
    @PostMapping("/saveTestTopic")
    public RestResultVo saveTestTopic(@RequestHeader String userName, @RequestBody TestTopic testTopic){
        RestResultVo restResultVo;
        try {
            testTopic.setCreateUser(userName);
            testTopic.setUpdateUser(userName);
            testTopic.setCreateTime(LocalDateTime.now());
            testTopic.setUpdateTime(LocalDateTime.now());
            TestResultVo resultVo = testTopicService.saveTestTopic(testTopic);
            if(resultVo.getFlag()){
                restResultVo = RestResultVo.fromData(resultVo.getData());
            }else{
                restResultVo = RestResultVo.fromErrorMessage(resultVo.getMsgRes());
            }
        } catch (Exception e) {
            log.error("/topic/saveTestTopic testTopic=={} 新增信息失败!", JSON.toJSONString(testTopic),e);
            restResultVo = RestResultVo.fromErrorMessage(e.getMessage());
        }
        return restResultVo;
    }

//    @OperateLogAnnotation(moduleName = OperateLogModuleConstants.TOPIC, option = OperateLogTypeConstants.UPDATE)
    @ApiOperation("修改信息")
    @PostMapping("/updateTestTopic")
    public RestResultVo updateTestTopic(@RequestHeader String userName, @RequestBody TestTopic testTopic){
        RestResultVo restResultVo;
        try {
            testTopic.setUpdateUser(userName);
            testTopic.setUpdateTime(LocalDateTime.now());
            TestResultVo resultVo = testTopicService.updateTestTopic(testTopic);
            if(resultVo.getFlag()){
                restResultVo = RestResultVo.fromData(resultVo.getMsgRes());
            }else{
                restResultVo = RestResultVo.fromErrorMessage(resultVo.getMsgRes());
            }
        } catch (Exception e) {
            log.error("/topic/updateTestTopic testTopic=={} 修改信息失败!", JSON.toJSONString(testTopic),e);
            restResultVo = RestResultVo.fromErrorMessage(e.getMessage());
        }
        return restResultVo;
    }

//    @OperateLogAnnotation(moduleName = OperateLogModuleConstants.TOPIC, option = OperateLogTypeConstants.DETAIL)
    @ApiOperation("根据id获取详情")
    @GetMapping("/findInfoById")
    public RestResultVo findInfoById(@RequestHeader String userName, @RequestParam Long id){
        RestResultVo restResultVo;
        try {
            TestTopic testTopic = testTopicService.findInfoById(id);
            restResultVo = RestResultVo.fromData(testTopic);
        } catch (Exception e) {
            log.error("/topic/findInfoById id=={} 根据id获取详情失败!", id,e);
            restResultVo = RestResultVo.fromErrorMessage(e.getMessage());
        }
        return restResultVo;
    }

    @ApiOperation("初始化信息")
    @GetMapping("/initCreateInfo")
    public RestResultVo initCreateInfo(@RequestHeader String userName){
        RestResultVo restResultVo;
        try {
            List<TestTopicTypeVo> testTopicTypeVoList = testTopicService.initCreateInfo();
            restResultVo = RestResultVo.fromData(testTopicTypeVoList);
        } catch (Exception e) {
            log.error("/topic/initCreateInfo userName=={} 初始化信息失败!",userName,e);
            restResultVo = RestResultVo.fromErrorMessage(e.getMessage());
        }
        return restResultVo;
    }

//    @OperateLogAnnotation(moduleName = OperateLogModuleConstants.TOPIC, option = OperateLogTypeConstants.DETELE)
    @ApiOperation("批量删除")
    @GetMapping("/batchDeleteByIdList")
    public RestResultVo batchDeleteByIdList(@RequestHeader String userName, @RequestParam List<Long> idList){
        RestResultVo restResultVo;
        try {
            TestResultVo resultVo = testTopicService.batchDeleteByIdList(idList);
            if (resultVo.getFlag()) {
                restResultVo = RestResultVo.fromSuccess(resultVo.getMsgRes());
            } else {
                restResultVo = RestResultVo.fromErrorMessage(resultVo.getMsgRes());
            }
        } catch (Exception e) {
            log.error("/topic/batchDeleteByIdList idList=={} 批量删除失败!", idList,e);
            restResultVo = RestResultVo.fromErrorMessage(e.getMessage());
        }
        return restResultVo;
    }

    @ApiOperation("校验项目名称是否重复")
    @GetMapping("/checkNewTopicName")
    public RestResultVo checkNewTopicName(@RequestHeader String userName, @RequestParam(value = "id",required = false) Long id, @RequestParam String topicName){
        RestResultVo restResultVo;
        try {
            TestResultVo resultVo = testTopicService.checkNewTopicName(id,topicName);
            if(resultVo.getFlag()){
                restResultVo = RestResultVo.fromData(resultVo.getMsgRes());
            }else{
                restResultVo = RestResultVo.fromErrorMessage(resultVo.getMsgRes());
            }
        } catch (Exception e) {
            log.error("/topic/checkNewTopicName id==={}，topicName=={} 批量删除失败!", id,topicName,e);
            restResultVo = RestResultVo.fromErrorMessage(e.getMessage());
        }
        return restResultVo;
    }

}
